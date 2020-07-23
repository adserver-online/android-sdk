package com.adserver.utils.webviews;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.adserver.Adserver;

abstract public class BaseWebViewClient extends WebViewClient {

    final static String TAG = HtmlWebView.class.getName();

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.d(TAG, "onPageStarted: " + url);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        Log.d(TAG, "Loading URL: " + url);
        super.onLoadResource(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Log.d(TAG, "onReceivedError: " + failingUrl);
        Log.d(TAG, "onReceivedError Error: " + errorCode + ", " + description);
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        Log.d(TAG, "onReceivedError: " + request.getUrl());
        Log.d(TAG, "onReceivedError Error: " + error.getErrorCode() + ", " + error.getDescription());
        super.onReceivedError(view, request, error);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        Log.d(TAG, "onReceivedHttpError: " + request.getUrl());
        Log.d(TAG, "onReceivedHttpError Status: " + errorResponse.getStatusCode());
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        Log.d(TAG, "onReceivedSslError: " + error.getUrl());
        Log.d(TAG, "onReceivedSslError Status: " + error.getPrimaryError());
        super.onReceivedSslError(view, handler, error);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.contains(Adserver.getInstance().getServerDomain())) {
            return false;
        }

        if (getIsClicked()) {
            Log.d(TAG, "Received click interaction, loading intent in default browser. (" + url + ")");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);

            onClick();
            return true;
        } else {
            Log.d(TAG, "Received click interaction, suppressed due to likely false tap event. (" + url + ")");
        }

        return false;
    }

    abstract protected boolean getIsClicked();

    abstract protected void onClick();

    /* TODO Use ChromeWebView ?
    @Override
    public boolean onJsAlert(@NonNull final WebView view, @NonNull final String url,
                             @NonNull final String message, @NonNull final JsResult result) {
        result.confirm();
        return true;
    }

    @Override
    public boolean onJsConfirm(@NonNull final WebView view, @NonNull final String url,
                               @NonNull final String message, @NonNull final JsResult result) {
        result.confirm();
        return true;
    }

    @Override
    public boolean onJsPrompt(@NonNull final WebView view, @NonNull final String url,
                              @NonNull final String message, @NonNull final String defaultValue,
                              @NonNull final JsPromptResult result) {
        result.confirm();
        return true;
    }

    @Override
    public boolean onJsBeforeUnload(@NonNull final WebView view, @NonNull final String url,
                                    @NonNull final String message, @NonNull final JsResult result) {
        result.confirm();
        return true;
    }
     */
}