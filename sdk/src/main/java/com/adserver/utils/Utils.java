package com.adserver.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class Utils {

    private static Handler mainLooperHandler;

    public static String validateHTMLStructure(String htmlBody, boolean force) {
        // Check for body
        if (force || !htmlBody.contains("<body")) {
            htmlBody = "<body style=\"margin:0;padding:0;width:100%;height:100%;\">\n" + htmlBody + "</body>";
        }

        // Check for header
        String metaString = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\" />";
        if (force || !htmlBody.contains("<head")) {
            htmlBody = "<head><title>Ad</title>\n" + metaString + "\n</head>\n" + htmlBody;
        }

        // Check for html
        if (force || !htmlBody.contains("<html")) {
            htmlBody = "<!DOCTYPE html><html style=\"margin:0;padding:0;width:100%;height:100%;\">\n" + htmlBody + "\n</html>";
        }

        return htmlBody;
    }

    public static void removeFromParent(@Nullable View view) {
        if (view == null || view.getParent() == null) {
            return;
        }

        if (view.getParent() instanceof ViewGroup) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public static boolean isUIThread() {
        return Looper.myLooper() != null && Looper.myLooper() == Looper.getMainLooper();
    }

    public static Handler getMainLooperHandler() {
        if (mainLooperHandler == null) {
            mainLooperHandler = new Handler(Looper.getMainLooper());
        }

        return mainLooperHandler;
    }

    public static void executeOnUIThread(final Runnable r, boolean wait) {
        if (Utils.isUIThread()) {
            r.run();
        } else {
            Runnable runnableWrapper = new Runnable() {
                public void run() {
                    r.run();
                    synchronized (this) {
                        this.notify();
                    }
                }
            };
            synchronized (runnableWrapper) {
                Utils.getMainLooperHandler().post(runnableWrapper);
                if (wait) {
                    try {
                        runnableWrapper.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }

    public static boolean isPermissionGranted(@NonNull final Context context,
                                              @NonNull final String permission) {

        boolean res;
        try {
            res = ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            res = false;
        }

        if (!res) {
            Log.d("Utils", "No permission " + permission);
        }

        return res;
    }
}
