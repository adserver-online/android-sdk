package com.adserver;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class AdView extends FrameLayout {

    public AdRequest adRequest;

    public AdView(@NonNull Context context) {
        super(context);
    }

    public AdView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdRequest(AdRequest adRequest) {
        this.adRequest = adRequest;
    }

    public void checkIfSdkOk() {
        if (!Adserver.getInstance().getIsInitialized()) {
            throw new IllegalStateException("The SDK is not yet configured for this application. Please make sure you call 'Adserver.initialize(this);' before making any ad call.");
        }
    }

    protected void checks(AdRequest adRequest) {
        checkIfSdkOk();

        if (adRequest.getZoneID() == 0) {
            throw new IllegalStateException("Zone ID required");
        }
    }
}
