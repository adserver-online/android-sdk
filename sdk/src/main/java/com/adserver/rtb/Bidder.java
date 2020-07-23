package com.adserver.rtb;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.adserver.AdRequest;
import com.adserver.AdResponse;
import com.adserver.Adserver;
import com.adserver.BuildConfig;
import com.adserver.mraid.BrowserActivity;
import com.adserver.rtb.providers.AppInfoProvider;
import com.adserver.rtb.providers.DeviceInfoProvider;
import com.adserver.rtb.providers.GeoProvider;
import com.adserver.rtb.providers.UserInfoProvider;
import com.adserver.rtb.request.BidRequestFactory;
import com.adserver.rtb.request.models.BidRequest;
import com.adserver.rtb.response.models.BidResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Bidder {

    private static final String TAG = BrowserActivity.class.getName();
    private Context mContext;
    private RTBService mRtbService;
    private String mRtbDomain;
    private String mRtbSchema;
    private BidRequestFactory mBidRequestFactory;
    private AdRequest mAdRequest;

    public Bidder(Context context, AdRequest adRequest, BidRequestFactory bidRequestFactory, final ResponseListener responseListener) {

        mBidRequestFactory = bidRequestFactory;
        mAdRequest = adRequest;
        mContext = context;

        Adserver sdk = Adserver.getInstance();
        mRtbDomain = sdk.serverDomain;
        mRtbSchema = sdk.serverSchema;

        Log.d(TAG, "Requesting ad from server...");

        BidRequest bidRequest = createBidRequest();
        Call<BidResponse> callRtb = getRTBService().getBid(adRequest.getZoneID(), bidRequest);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(bidRequest);
        Log.d("RTB request: ", json);

        callRtb.enqueue(new Callback<BidResponse>() {
            @Override
            public void onResponse(@NonNull Call<BidResponse> call, @NonNull Response<BidResponse> response) {

                if (response.code() == 200) {

                    if (BuildConfig.DEBUG) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());

                        Log.d(TAG, "RTB response: " + json);
                    }

                    AdResponse adResponse = mBidRequestFactory.parseBidResponse(response.body());
                    responseListener.onSuccess(adResponse);

                } else if (response.code() == 204) {
                    Log.d(TAG, "RTB empty response (204)");
                } else {
                    Log.d(TAG, "RTB error response:" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<BidResponse> call, @NonNull Throwable t) {
                Log.d("RTB fail", t.getMessage());
                responseListener.onError(t);
            }
        });
    }

    protected BidRequest createBidRequest() {

        GeoProvider geoProvider = new GeoProvider(mContext, mAdRequest.getLocation());
        AppInfoProvider appInfoProvider = new AppInfoProvider(mContext);
        DeviceInfoProvider deviceInfoProvider = new DeviceInfoProvider(mContext, geoProvider);
        UserInfoProvider userInfoProvider = new UserInfoProvider(mContext);

        return mBidRequestFactory.createBidRequest(
                mAdRequest,
                appInfoProvider.getApp(),
                deviceInfoProvider.getDevice(),
                userInfoProvider.getUser()
        );
    }

    private RTBService getRTBService() {

        if (mRtbService == null) {
            String baseUrl = this.mRtbSchema + "://" + this.mRtbDomain;

            Gson gson = new GsonBuilder()
                    .create();

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson));

            mRtbService = builder.build().create(RTBService.class);
        }

        return mRtbService;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public interface ResponseListener {
        void onSuccess(AdResponse response);

        void onError(Throwable throwable);
    }
}
