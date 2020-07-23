package com.adserver.interstitial;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.adserver.AdController;
import com.adserver.AdListener;
import com.adserver.utils.webviews.BaseWebViewClient;
import com.adserver.utils.webviews.HtmlWebView;
import com.adserver.utils.webviews.ViewGestureDetector;

public class InterstitialController extends AdController {

    /* Possible Memory leak but some big networks use it :-/ */
    protected static InterstitialController INSTANCE_FOR_ACTIVITY;
    int closeTimer = 0;
    boolean hasShown = false;
    boolean isReady = false;

    public InterstitialController(@NonNull Context context, AdListener adListener) {
        super(context);

        this.adListener = adListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    public HtmlWebView createWebView() {

        webView = new HtmlWebView(context);
        webView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        webView.setWebViewClient(getWebClient());
        gestureDetector = ViewGestureDetector.createInstance(webView);

        return webView;
    }

    protected void setReady() {
        isReady = true;
        adListener.onReady();
    }

    public boolean getReady() {
        return isReady;
    }

    protected void setShown() {
        hasShown = true;
    }

    public boolean getShown() {
        return hasShown;
    }

    public void show() {
        if (isReady) {
            Intent intent = new Intent(context, InterstitialActivity.class);

            setExtras(intent);

            INSTANCE_FOR_ACTIVITY = this;

            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                INSTANCE_FOR_ACTIVITY = null;
            }

            adListener.onDisplayed();
        }
    }

    protected void setExtras(Intent intent) {
        intent.putExtra(InterstitialActivity.EXTRA_CLOSE_TIMER, closeTimer);
    }

    public void setContent(String body) {
        webView.loadHtml(body);
    }

    protected WebViewClient getWebClient() {

        return new BaseWebViewClient() {

            @Override
            protected boolean getIsClicked() {
                return isClicked();
            }

            @Override
            protected void onClick() {
                adListener.onClicked();
                adListener.onLeavingApplication();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setReady();
                    }
                }, 150);
            }
        };
    }
}
