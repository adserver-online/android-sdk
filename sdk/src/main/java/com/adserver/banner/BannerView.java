package com.adserver.banner;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adserver.AdListener;
import com.adserver.AdRequest;
import com.adserver.AdResponse;
import com.adserver.AdView;
import com.adserver.ErrorCode;
import com.adserver.Position;
import com.adserver.mraid.BrowserActivity;
import com.adserver.mraid.Size;
import com.adserver.rtb.Bidder;
import com.adserver.rtb.request.BannerBidRequestFactory;
import com.adserver.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class BannerView extends AdView {

    private static final String TAG = BrowserActivity.class.getName();
    public boolean initializing = false;
    public boolean absolutePositioned = false;
    boolean stopRefreshOnClose = false;
    private BannerController controller;
    private String absolutePosition = Position.BOTTOM_CENTER;
    private Size defaultSize = new Size(320, 50);
    private AdListener adListener;
    private int refreshInterval = 0;
    private Timer refreshHandler;

    public BannerView(@NonNull Context context) {
        super(context);
        this.init();
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    public void init() {
        adListener = new AdListener() {
        };
    }

    public void loadAd(AdRequest adRequest, String position) {
        checks(adRequest);
        loadAdInternal(adRequest, position);
    }

    public void loadAd(AdRequest adRequest) {
        checks(adRequest);
        loadAdInternal(adRequest, Position.BOTTOM_CENTER);
    }

    protected void loadAdInternal(final AdRequest adRequest, final String position) {

        if (initializing) {
            return;
        }

        initializing = true;

        this.absolutePosition = position;
        this.setAdRequest(adRequest);

        defaultSize = new Size(adRequest.getWidth(), adRequest.getHeight());

        new Bidder(getContext(), adRequest, new BannerBidRequestFactory(), getResponseListener());

        if (refreshHandler == null && refreshInterval > 0) {
            int interval = this.refreshInterval * 1000;
            this.refreshHandler = new Timer();
            this.refreshHandler.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    //another version is in Utils.executeOnUIThread
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        public void run() {
                            loadAdInternal(adRequest, position);
                        }
                    });
                }
            }, (long) interval, (long) interval);
        }
    }

    protected void destroy() {
        this.removeAllViews();

        if (refreshHandler != null && stopRefreshOnClose) {
            refreshHandler.cancel();
            refreshHandler = null;
        }
    }

    public void setListener(AdListener listener) {
        this.adListener = listener;
    }

    public void setAutoRefreshInterval(int seconds) {
        this.refreshInterval = seconds;
    }

    public void setAbsolutePositioned(boolean flag) {
        this.absolutePositioned = flag;
    }

    private Bidder.ResponseListener getResponseListener() {
        return new Bidder.ResponseListener() {
            @Override
            public void onSuccess(AdResponse response) {

                String body = response.html;

                if (body == null || body.equals("")) {
                    adListener.onFetchFailed(ErrorCode.NO_INVENTORY);
                    return;
                }

                body = Utils.validateHTMLStructure(body, true);

                adListener.onFetchSucceeded();

                if (response.getIsMraid()) {
                    controller = new MraidController(getContext(), BannerView.this, adListener);
                } else {
                    controller = new BannerController(getContext(), BannerView.this, adListener);
                }

                controller.absolutePosition = absolutePosition;
                controller.absolutePositioned = absolutePositioned;

                controller.createWebView();
                controller.setContent(body);

                if (defaultSize != null) {
                    controller.setSize(defaultSize);
                }

                controller.show();
                initializing = false;
            }

            @Override
            public void onError(Throwable throwable) {
                adListener.onFetchFailed(ErrorCode.NO_INVENTORY);
                initializing = false;
            }
        };
    }
}
