package com.adserver.utils.webviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adserver.Adserver;

public class HtmlWebView extends BaseWebView {

    final static String TAG = HtmlWebView.class.getName();

    @NonNull
    private final ViewGestureDetector viewGestureDetector;

    @SuppressLint("SetJavaScriptEnabled")
    public HtmlWebView(Context context) {
        super(context);

        disableScrollingAndZoom();
        getSettings().setJavaScriptEnabled(true);

        viewGestureDetector = new ViewGestureDetector(context);

        setBackgroundColor(Color.TRANSPARENT);
        initializeOnTouchListener();
    }

    @Override
    public void loadUrl(@Nullable final String url) {
        if (url == null) {
            return;
        }

        if (url.startsWith("javascript:")) {
            super.loadUrl(url);
            return;
        }

        Log.d(TAG, "Loading url: " + url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void stopLoading() {
        if (mIsDestroyed) {
            Log.d(TAG, HtmlWebView.class.getSimpleName() + "#stopLoading() called after destroy()");
            return;
        }

        final WebSettings webSettings = getSettings();
        if (webSettings == null) {
            Log.d(TAG, HtmlWebView.class.getSimpleName() + "#getSettings() returned null");
            return;
        }

        webSettings.setJavaScriptEnabled(false);
        super.stopLoading();
        webSettings.setJavaScriptEnabled(true);
    }

    private void disableScrollingAndZoom() {
        setHorizontalScrollBarEnabled(false);
        setHorizontalScrollbarOverlay(false);
        setVerticalScrollBarEnabled(false);
        setVerticalScrollbarOverlay(false);
        getSettings().setSupportZoom(false);
    }

    public void loadHtml(String html) {
        loadDataWithBaseURL(Adserver.getInstance().serverSchema + "://" + Adserver.getInstance().getServerDomain(), html,
                "text/html", "utf-8", null);
    }

    void initializeOnTouchListener() {
        setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                viewGestureDetector.onTouchEvent(event);

                // We're not handling events if the current action is ACTION_MOVE
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
    }
}
