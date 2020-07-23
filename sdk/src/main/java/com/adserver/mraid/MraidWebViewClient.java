
package com.adserver.mraid;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.adserver.Adserver;
import com.adserver.utils.webviews.BaseWebViewClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

abstract public class MraidWebViewClient extends BaseWebViewClient {

    private static final String TAG = BrowserActivity.class.getName();

    private static final String MRAID_INJECTION_JAVASCRIPT = "javascript:" + MraidJsLibrary.JAVASCRIPT_SOURCE;

    @Override
    public WebResourceResponse shouldInterceptRequest(@NonNull final WebView view,
                                                      @NonNull final String url) {
        if (matchesInjectionUrl(url)) {
            return createMraidInjectionResponse();
        } else {
            return super.shouldInterceptRequest(view, url);
        }
    }

    boolean matchesInjectionUrl(@NonNull final String url) {
        final Uri uri = Uri.parse(url.toLowerCase(Locale.US));
        return MraidBridge.MRAID_JS.equals(uri.getLastPathSegment());
    }

    private WebResourceResponse createMraidInjectionResponse() {
        InputStream data = new ByteArrayInputStream(MRAID_INJECTION_JAVASCRIPT.getBytes());
        return new WebResourceResponse("text/javascript", "UTF-8", data);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.contains(Adserver.getInstance().getServerDomain())) {
            return false;
        }

        if (url.contains("mraid://")) {
            onMraidUrl(url);
            return true;
        }

        if (getIsClicked() && (url.startsWith("sms://") || url.startsWith("tel://"))) {
            try {
                url = MraidBridge.MRAID_OPEN + URLEncoder.encode(url, "UTF-8");
                onMraidUrl(url);
                return true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (getIsClicked() && (url.startsWith("http://") || url.startsWith("https://"))) {
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

    abstract protected void onMraidUrl(String url);
}
