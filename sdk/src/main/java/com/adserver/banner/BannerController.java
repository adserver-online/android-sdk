package com.adserver.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.adserver.AdController;
import com.adserver.AdListener;
import com.adserver.Position;
import com.adserver.mraid.Size;
import com.adserver.utils.Utils;
import com.adserver.utils.webviews.BaseWebViewClient;
import com.adserver.utils.webviews.HtmlWebView;
import com.adserver.utils.webviews.ViewGestureDetector;

public class BannerController extends AdController {

    protected FrameLayout container;

    protected boolean absolutePositioned = false;
    protected String absolutePosition = Position.BOTTOM_CENTER;

    protected ViewGroup.LayoutParams currentWebViewLayout;
    protected ViewGroup.LayoutParams currentContainerLayout;

    public BannerController(@NonNull Context context, FrameLayout container, AdListener adListener) {
        super(context);

        this.adListener = adListener;
        this.container = container;
    }

    @SuppressLint("ClickableViewAccessibility")
    public HtmlWebView createWebView() {

        webView = new HtmlWebView(context);
        webView.setWebViewClient(getWebClient());
        gestureDetector = ViewGestureDetector.createInstance(webView);

        return webView;
    }

    @Override
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
        };
    }

    protected void setSize(Size rect) {

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rect.width, context.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rect.height, context.getResources().getDisplayMetrics());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        webView.setLayoutParams(params);
        currentWebViewLayout = params;

        if (absolutePositioned) {
            FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            int marginLeft = 0, marginTop = 1, marginBottom = 0, marginRight = 0;

            int grav = Gravity.NO_GRAVITY;
            if (absolutePosition.contains("top")) {
                grav = grav | Gravity.TOP;
            }
            if (absolutePosition.contains("bottom")) {
                grav = grav | Gravity.BOTTOM;
            }
            if (absolutePosition.contains("left")) {
                grav = grav | Gravity.START;
            }
            if (absolutePosition.contains("right")) {
                grav = grav | Gravity.END;
            }

            if (absolutePosition.equals(Position.CENTER)) {
                grav = grav | Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
            } else if (absolutePosition.contains("center")) {
                if (!absolutePosition.contains("top") && !absolutePosition.contains("bottom")) {
                    grav = grav | Gravity.CENTER_VERTICAL;
                }
                if (!absolutePosition.contains("left") && !absolutePosition.contains("right")) {
                    grav = grav | Gravity.CENTER_HORIZONTAL;
                }
            }
            if (absolutePosition.contains("status-bar")) {
                marginTop += getStatusBarHeight();
            }

            containerParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
            containerParams.gravity = grav;

            currentContainerLayout = containerParams;
            container.setLayoutParams(containerParams);
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void show() {

        currentWebViewLayout = webView.getLayoutParams();
        currentContainerLayout = container.getLayoutParams();

        container.removeAllViews();
        container.addView(webView);

        if (absolutePositioned) {
            addToRoot();
        }

        adListener.onDisplayed();
    }

    protected void addToRoot() {
        container.setFitsSystemWindows(true);
        ViewGroup root = activity.findViewById(android.R.id.content);
        Utils.removeFromParent(container);
        root.addView(container);
        reposition();
        container.bringToFront();
    }

    protected void reposition() {
        webView.setLayoutParams(currentWebViewLayout);
        container.setLayoutParams(currentContainerLayout);
    }
}
