package com.adserver.utils.webviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.adserver.Adserver;
import com.adserver.utils.Utils;

public class BaseWebView extends WebView {
    private static boolean sDeadlockCleared = false;
    protected boolean mIsDestroyed;

    public BaseWebView(Context context) {
        /*
         * Important: don't allow any WebView subclass to be instantiated using
         * an Activity context, as it will leak on Froyo devices and earlier.
         */
        super(context.getApplicationContext());

        restrictDeviceContentAccess();

        setWebContentsDebuggingEnabled(true);

        if (!sDeadlockCleared) {
            clearWebViewDeadlock(getContext());
            sDeadlockCleared = true;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        CookieManager cookieManager = CookieManager.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(this, Adserver.getInstance().canCollectPersonalInformation);
        }
    }

    @Override
    public void destroy() {
        if (mIsDestroyed) {
            return;
        }

        mIsDestroyed = true;

        // Needed to prevent receiving the following error on Android versions using WebViewClassic
        // https://code.google.com/p/android/issues/detail?id=65833.
        Utils.removeFromParent(this);

        // Even after removing from the parent, WebViewClassic can leak because of a static
        // reference from HTML5VideoViewProcessor. Removing children fixes this problem.
        removeAllViews();
        super.destroy();
    }

    /*
     * Disabling file access and content access prevents advertising creatives from
     * detecting the presence of, or reading, files on the device filesystem.
     */
    private void restrictDeviceContentAccess() {
        getSettings().setAllowFileAccess(false);
        getSettings().setAllowContentAccess(false);
        getSettings().setAllowFileAccessFromFileURLs(false);
        getSettings().setAllowUniversalAccessFromFileURLs(false);
    }

    /**
     * This fixes https://code.google.com/p/android/issues/detail?id=63754,
     * which occurs on KitKat devices. When a WebView containing an HTML5 video is
     * is destroyed it can deadlock the WebView thread until another hardware accelerated WebView
     * is added to the view hierarchy and restores the GL context. Since we need to use WebView
     * before adding it to the view hierarchy, this method clears the deadlock by adding a
     * separate invisible WebView.
     * <p>
     * This potential deadlock must be cleared anytime you attempt to access a WebView that
     * is not added to the view hierarchy.
     */
    private void clearWebViewDeadlock(@NonNull final Context context) {
        if (VERSION.SDK_INT == VERSION_CODES.KITKAT) {
            // Create an invisible WebView
            final WebView webView = new WebView(context.getApplicationContext());
            webView.setBackgroundColor(Color.TRANSPARENT);

            // For the deadlock to be cleared, we must load content and add to the view hierarchy. Since
            // we don't have an activity context, we'll use a system window.
            webView.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = 1;
            params.height = 1;
            // Unlike other system window types TYPE_TOAST doesn't require extra permissions
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
            params.format = PixelFormat.TRANSPARENT;
            params.gravity = Gravity.START | Gravity.TOP;
            final WindowManager windowManager =
                    (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            windowManager.addView(webView, params);
        }
    }
}
