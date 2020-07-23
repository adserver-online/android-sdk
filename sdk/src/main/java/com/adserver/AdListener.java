package com.adserver;

public abstract class AdListener {
    /**
     * Called when an ad is successfully fetched.
     */
    public void onFetchSucceeded() {
    }

    /**
     * Called when an ad fetch fails.
     */
    public void onFetchFailed(ErrorCode code) {
    }

    /**
     * Called when the interstitial goes to full screen or banner shown.
     */
    public void onDisplayed() {
    }

    /**
     * Called when the interstitial web view is finished loading.
     */
    public void onReady() {
    }

    /**
     * MRAID Expand called.
     */
    public void onExpanded() {
    }

    /**
     * Called before an ad causes another application to open. E.G. Web Browser.
     */
    public void onLeavingApplication() {
    }

    /**
     * MRAID Resize called.
     */
    public void onResized() {
    }

    /**
     * Called when an ad is closed. (MRAID/Interstitials)
     */
    public void onClosed() {
    }

    /**
     * Called when the ad is clicked.
     */
    public void onClicked() {
    }
}
