package com.adserver.rtb.providers;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.adserver.rtb.request.models.App;

import java.util.List;

public class AppInfoProvider {

    private String appBundle;
    private String appName;
    private List<String> categories;
    private String keywords;
    private int privacyPolicy;

    public AppInfoProvider(Context context) {
        this.appBundle = context.getApplicationContext().getPackageName();
        this.appName = getApplicationName(context);
    }

    private String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public App getApp() {
        App app = new App();
        app.name = getAppName();
        app.bundle = getAppBundle();
        app.cat = getCategories();
        app.keywords = getKeywords();

        return app;
    }

    private String getAppBundle() {
        return appBundle;
    }

    private String getAppName() {
        return appName;
    }

    private List<String> getCategories() {
        return categories;
    }

    private String getKeywords() {
        return keywords;
    }
}
