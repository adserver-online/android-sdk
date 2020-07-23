package com.adserver;

import android.content.Context;
import android.util.Log;

import com.adserver.mraid.BrowserActivity;
import com.adserver.utils.AdvertisingIdClient;

public class Adserver {

    private static final String TAG = BrowserActivity.class.getName();
    private static Adserver instance;
    public boolean canCollectPersonalInformation = true;
    public String serverSchema = "http";
    public String serverDomain = "srv.aso1.net";
    public AdvertisingIdClient.AdInfo adInfo;
    private boolean isInitialized = false;

    public static Adserver getInstance() {
        if (null == instance) {
            instance = new Adserver();
        }

        return instance;
    }

    public static Adserver initialize(Context context) {
        Adserver sdk = Adserver.getInstance();
        sdk.init(context);

        return sdk;
    }

    public boolean getIsInitialized() {
        return isInitialized;
    }

    protected void init(Context context) {
        if (isInitialized) {
            Log.w(TAG, "Repeated SDK initialization");
            return;
        }
        isInitialized = true;

        Log.d(TAG, "Initializing SDK v" + BuildConfig.VERSION_NAME);

        if (null == context) {
            throw new IllegalArgumentException("Context cannot be null.");
        }

        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
    }

    public String getServerDomain() {
        return this.serverDomain;
    }

    public void setServerDomain(String serverDomain) {
        this.serverDomain = serverDomain;
    }
}
