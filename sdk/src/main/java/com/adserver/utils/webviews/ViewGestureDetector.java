package com.adserver.utils.webviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;

public class ViewGestureDetector extends GestureDetector {
    @NonNull
    private GestureListener mGestureListener;

    public ViewGestureDetector(@NonNull Context context) {
        this(context, new GestureListener());
    }

    private ViewGestureDetector(Context context, @NonNull GestureListener gestureListener) {
        super(context, gestureListener);

        mGestureListener = gestureListener;

        setIsLongpressEnabled(false);
    }

    @SuppressLint("ClickableViewAccessibility")
    public static ViewGestureDetector createInstance(WebView webView) {

        final ViewGestureDetector viewGestureDetector = new ViewGestureDetector(webView.getContext());

        new ViewGestureDetector(webView.getContext());

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                viewGestureDetector.onTouchEvent(event);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        return viewGestureDetector;
    }

    public boolean isClicked() {
        return mGestureListener.isClicked();
    }

    /**
     * Track user interaction in a separate class
     */
    static class GestureListener extends GestureDetector.SimpleOnGestureListener {
        boolean mIsClicked = false;

        boolean isClicked() {
            return mIsClicked;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mIsClicked = true;
            return super.onSingleTapUp(e);
        }
    }
}
