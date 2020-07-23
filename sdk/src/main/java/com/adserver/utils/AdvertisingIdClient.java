package com.adserver.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

/**
 * Detect google advertising id
 */
public final class AdvertisingIdClient {

    private final static String TAG = AdvertisingIdClient.class.getSimpleName();

    private final static String DNT_AD_ID = "00000000-0000-0000-0000-000000000000";

    @NonNull
    public static AdInfo getAdvertisingIdInfo(Context context) {
        try {
            Context appContext = context.getApplicationContext();
            com.google.android.gms.ads.identifier.AdvertisingIdClient.Info result =
                    com.google.android.gms.ads.identifier.AdvertisingIdClient
                            .getAdvertisingIdInfo(
                                    appContext == null
                                            ? context
                                            : appContext);

            boolean dnt = result.isLimitAdTrackingEnabled();
            String id = dnt ? DNT_AD_ID : result.getId();

            if (TextUtils.isEmpty(id)) {
                id = "";
                Log.d(TAG, "getId() returned empty id.");
            }

            return new AdInfo(id, dnt);

        } catch (GooglePlayServicesRepairableException e) {
            Log.d(TAG,
                    e.toString() +
                            " MESSAGE: " +
                            e.getMessage() +
                            " CONNECTION_STATUS_CODE: " +
                            e.getConnectionStatusCode()
            );
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d(TAG,
                    e.toString() +
                            " MESSAGE: " +
                            e.getMessage() +
                            " ERROR_CODE: " +
                            e.errorCode
            );
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        return new AdInfo("", false);
    }

    public static final class AdInfo {

        private String mAdvertisingId;
        private boolean mLimitAdTrackingEnabled;

        private AdInfo() {
        }

        private AdInfo(String advertisingId, boolean limitAdTrackingEnabled) {
            mAdvertisingId = advertisingId;
            mLimitAdTrackingEnabled = limitAdTrackingEnabled;
        }

        public String getId() {
            return mAdvertisingId;
        }

        public boolean isLimitAdTrackingEnabled() {
            return mLimitAdTrackingEnabled;
        }
    }
}