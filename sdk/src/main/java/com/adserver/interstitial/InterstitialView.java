package com.adserver.interstitial;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adserver.AdListener;
import com.adserver.AdRequest;
import com.adserver.AdResponse;
import com.adserver.AdView;
import com.adserver.ErrorCode;
import com.adserver.rtb.Bidder;
import com.adserver.rtb.request.InterstitialBidRequestFactory;
import com.adserver.utils.Utils;
import com.adserver.utils.webviews.HtmlWebView;

public class InterstitialView extends AdView {

    private int closeTimer = 0;
    private int backgroundColor = Color.WHITE;

    private InterstitialController controller;
    private AdListener adListener = null;

    public InterstitialView(@NonNull Context context) {
        super(context);
    }

    public InterstitialView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InterstitialView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void loadAd(AdRequest request) {
        checks(request);

        if (controller == null || !controller.getShown()) {
            new Bidder(getContext(), request, new InterstitialBidRequestFactory(), getResponseListener());
        }
    }

    private Bidder.ResponseListener getResponseListener() {

        return new Bidder.ResponseListener() {
            @Override
            public void onSuccess(AdResponse response) {
                String body = response.html;

                if (body == null) {
                    adListener.onFetchFailed(ErrorCode.NO_INVENTORY);
                    return;
                }

                //wrap with centering table
                body = "<table style=\"width:100%;height:100%;\"><tr><td style=\"width:100%;height:100%;vertical-align:center;\">" + body + "</td></tr></table>";

                body = Utils.validateHTMLStructure(body, true);
                adListener.onFetchSucceeded();

                if (response.getIsMraid()) {
                    controller = new MraidController(getContext(), adListener);
                } else {
                    controller = new InterstitialController(getContext(), adListener);
                }

                HtmlWebView webView = controller.createWebView();

                webView.setBackgroundColor(backgroundColor);
                controller.closeTimer = closeTimer;

                controller.setContent(body);
            }

            @Override
            public void onError(Throwable throwable) {
                adListener.onFetchFailed(ErrorCode.NO_INVENTORY);
            }
        };
    }

    public void show() {
        if (controller != null && controller.getReady()) {
            controller.show();
        }
    }

    public boolean getReady() {
        return controller != null && controller.getReady();
    }

    public void setListener(AdListener listener) {
        this.adListener = listener;
    }

    public void setCloseButtonEnableTimer(int seconds) {
        closeTimer = seconds;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
