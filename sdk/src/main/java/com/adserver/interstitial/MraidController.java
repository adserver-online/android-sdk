package com.adserver.interstitial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.adserver.AdListener;
import com.adserver.mraid.BrowserActivity;
import com.adserver.mraid.MraidBridge;
import com.adserver.mraid.MraidUtilities;
import com.adserver.mraid.MraidWebViewClient;
import com.adserver.mraid.OrientationProperties;
import com.adserver.mraid.Rect;
import com.adserver.mraid.ResizeProperties;
import com.adserver.mraid.Size;
import com.adserver.mraid.constants.Events;
import com.adserver.mraid.constants.Orientations;
import com.adserver.utils.Utils;
import com.adserver.utils.webviews.HtmlWebView;

public class MraidController extends InterstitialController implements MraidBridge.Listener {

    private static final String TAG = BrowserActivity.class.getName();
    private MraidBridge mraidBridge;

    public MraidController(@NonNull Context context, AdListener adListener) {
        super(context, adListener);

        this.adListener = adListener;

        mraidBridge = new MraidBridge(this, context, (Activity) context);
        mraidBridge.isInterstitial = true;
    }

    @Override
    protected void setShown() {
        super.setShown();
        mraidBridge.setMRAIDIsVisible(true);
        mraidBridge.fireMRAIDEvent(Events.VIEWABLE_CHANGE, "true");
    }

    @Override
    public HtmlWebView createWebView() {
        HtmlWebView webView = super.createWebView();
        mraidBridge.activeWebView = webView;

        return webView;
    }

    protected void setExtras(Intent intent) {
        super.setExtras(intent);
        if (mraidBridge.orientationProperties.forceOrientation != null) {
            intent.putExtra(InterstitialActivity.EXTRA_ORIENTATION, mraidBridge.orientationProperties.forceOrientation);
        }
    }

    @Override
    protected WebViewClient getWebClient() {
        return new MraidWebViewClient() {
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
            protected void onMraidUrl(String url) {
                mraidBridge.handleEndpoint(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mraidBridge.initialize();

                Rect fsRect = MraidUtilities.getFullScreenRectDP(activity);
                mraidBridge.setMRAIDCurrentPosition(new Rect(0, 0, fsRect.width, fsRect.height));

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

    @Override
    public void open(String url) {
        adListener.onClicked();
    }

    @Override
    public void close() {
        if (closeButton != null) {
            closeButton.removeAllViews();
            Utils.removeFromParent(closeButton);
            closeButton = null;
        }

        sendAction(InterstitialActivity.FINISH_ACTION);
        adListener.onClosed();
    }

    @Override
    public void expand(String url) {

    }

    @Override
    public void resize(ResizeProperties properties) {
    }

    @Override
    public void reportDOMSize(Size size) {
    }

    @Override
    public void setOrientationProperties(OrientationProperties properties) {
        if (this.hasShown) {
            if (properties.forceOrientation != null) {
                if (properties.forceOrientation.equals(Orientations.LANDSCAPE)) {
                    sendAction(InterstitialActivity.ORIENTATION_LANDSCAPE_ACTION);
                }
                if (properties.forceOrientation.equals(Orientations.PORTRAIT)) {
                    sendAction(InterstitialActivity.ORIENTATION_PORTRAIT_ACTION);
                }
                if (properties.forceOrientation.equals(Orientations.NONE)) {
                    sendAction(InterstitialActivity.ORIENTATION_UNSPECIFIED_ACTION);
                }
            }
        } else {
            if (properties.forceOrientation != null) {
                Rect fsRect = MraidUtilities.getFullScreenRectDP(this.activity);
                if (properties.forceOrientation.equals(Orientations.LANDSCAPE)) {
                    mraidBridge.setMRAIDCurrentPosition(new Rect(0, 0, Math.max(fsRect.width, fsRect.height), Math.min(fsRect.width, fsRect.height)));
                }
                if (properties.forceOrientation.equals(Orientations.PORTRAIT)) {
                    mraidBridge.setMRAIDCurrentPosition(new Rect(0, 0, Math.min(fsRect.width, fsRect.height), Math.max(fsRect.width, fsRect.height)));
                }
                if (properties.forceOrientation.equals(Orientations.NONE)) {
                    mraidBridge.setMRAIDCurrentPosition(fsRect);
                }
            }
        }
    }

    @Override
    public void onLeavingApplication() {
        adListener.onLeavingApplication();
    }
}
