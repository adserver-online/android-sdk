package com.adserver.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.webkit.WebView;

public class DeviceInfo {

    public String language;
    public String manufacturer;
    public String model;
    public String osName;
    public String userAgent;
    public String osVersion;
    public int screenWidth;
    public int screenHeight;
    public float density;
    public double dpi;
    public Boolean isPhone;
    public Boolean isTablet;

    public DeviceInfo(Context context) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();

        userAgent = new WebView(context).getSettings().getUserAgentString();
        language = context.getResources().getConfiguration().locale.getLanguage();

        manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;
        osName = "Android";

        osVersion = Build.VERSION.RELEASE;

        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        density = displayMetrics.density;

        float yInches = displayMetrics.heightPixels / displayMetrics.ydpi;
        float xInches = displayMetrics.widthPixels / displayMetrics.xdpi;
        dpi = Math.sqrt(xInches * xInches + yInches * yInches);
        dpi = (double) Math.round(dpi * 100) / 100;

        isTablet = dpi >= 6.5;
        isPhone = !isTablet;
    }
}
