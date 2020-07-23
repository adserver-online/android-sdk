package com.adserver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adserver.mraid.MraidUtilities;
import com.adserver.utils.webviews.BaseWebView;
import com.adserver.utils.webviews.HtmlWebView;
import com.adserver.utils.webviews.ViewGestureDetector;

abstract public class AdController {

    @NonNull
    protected final Activity activity;
    @NonNull
    protected final Context context;
    public HtmlWebView webView;
    protected FrameLayout closeButton;
    protected ViewGestureDetector gestureDetector;
    protected AdListener adListener;

    public AdController(@NonNull Context context) {
        this.context = context.getApplicationContext();
        activity = (Activity) context;
    }

    protected void sendAction(String action) {
        Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }

    abstract public BaseWebView createWebView();

    abstract public void setContent(String body);

    public FrameLayout addCloseButton(WebView view, String position) {
        closeButton = new FrameLayout(context);
        int width = MraidUtilities.convertDpToPixel(40, context);
        int height = MraidUtilities.convertDpToPixel(40, context);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        int gravity = Gravity.NO_GRAVITY;
        if (position.contains("top")) {
            gravity = gravity | Gravity.TOP;
        }
        if (position.contains("right")) {
            gravity = gravity | Gravity.END;
        }
        if (position.contains("left")) {
            gravity = gravity | Gravity.START;
        }
        if (position.contains("bottom")) {
            gravity = gravity | Gravity.BOTTOM;
        }
        if (position.equals("center")) {
            gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        } else if (position.contains("center")) {
            gravity = gravity | Gravity.CENTER;
        }
        params.gravity = gravity;
        ViewGroup parent = (ViewGroup) view.getParent();
        parent.addView(closeButton, params);
        closeButton.bringToFront();
        closeButton.setBackgroundColor(0xaa000000);
        TextView tv = new TextView(context);
        FrameLayout.LayoutParams tparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        tparams.gravity = Gravity.CENTER;
        Typeface font = Typeface.create("Droid Sans Mono", Typeface.NORMAL);
        tv.setTextColor(Color.WHITE);
        tv.setTypeface(font);
        tv.setText("X");
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24f);
        closeButton.addView(tv);
        tv.setLayoutParams(tparams);
        tv.bringToFront();

        return closeButton;
    }

    protected boolean isClicked() {
        final ViewGestureDetector gDetector = gestureDetector;
        return gDetector != null && gDetector.isClicked();
    }
}
