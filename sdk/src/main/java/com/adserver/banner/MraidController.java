package com.adserver.banner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.adserver.AdListener;
import com.adserver.Position;
import com.adserver.mraid.BrowserActivity;
import com.adserver.mraid.MraidBridge;
import com.adserver.mraid.MraidUtilities;
import com.adserver.mraid.MraidWebViewClient;
import com.adserver.mraid.OrientationProperties;
import com.adserver.mraid.Rect;
import com.adserver.mraid.ResizeProperties;
import com.adserver.mraid.Size;
import com.adserver.mraid.constants.Events;
import com.adserver.mraid.constants.Orientations;
import com.adserver.mraid.constants.States;
import com.adserver.utils.HttpClient;
import com.adserver.utils.Utils;
import com.adserver.utils.webviews.HtmlWebView;

public class MraidController extends BannerController implements MraidBridge.Listener {

    private static final String TAG = BrowserActivity.class.getName();

    private HtmlWebView webViewExpanded;
    private MraidBridge mraidBridge;
    private MraidBridge mraidBridgeExpanded;

    private boolean windowIsFullscreen;

    private Rect defaultRect;

    public MraidController(@NonNull Context context, FrameLayout container, AdListener adListener) {
        super(context, container, adListener);

        this.adListener = adListener;
        this.container = container;
        mraidBridge = new MraidBridge(this, context, (Activity) context);


        int flags = activity.getWindow().getAttributes().flags;
        windowIsFullscreen = (flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    @SuppressLint("ClickableViewAccessibility")
    public HtmlWebView createWebView() {

        HtmlWebView webView = super.createWebView();
        mraidBridge.activeWebView = webView;

        webView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mraidBridge.activeWebView != null) {
                    webViewLayoutChanged(v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom);
                }
            }
        });

        return webView;
    }

    private void webViewLayoutChanged(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        Log.i(TAG, String.format("onLayoutChanged left=%d right=%d top=%d bottom=%d", left, right, top, bottom));

        int x = MraidUtilities.convertPixelsToDp(left, context);
        int y = MraidUtilities.convertPixelsToDp(top, context);
        int width = MraidUtilities.convertPixelsToDp(right, context) - x;
        int height = MraidUtilities.convertPixelsToDp(bottom, context) - y;

        int[] xy = new int[2];
        webView.getLocationOnScreen(xy);
        Rect screenRect = new Rect(MraidUtilities.convertPixelsToDp(xy[0], context), MraidUtilities.convertPixelsToDp(xy[1], context), width, height);
        if (defaultRect == null) {
            defaultRect = screenRect;
            mraidBridge.setMRAIDDefaultPosition(defaultRect);
        }

        mraidBridge.setMRAIDCurrentPosition(screenRect);
        mraidBridge.setMRAIDSizeChanged();
    }

    @Override
    public void setContent(String body) {
        webView.loadHtml(body);
    }

    @Override
    protected WebViewClient getWebClient() {
        return new MraidWebViewClient() {
            @Override
            protected boolean getIsClicked() {
                return isClicked();
            }

            @Override
            protected void onClick() {
                adListener.onClicked();
                adListener.onLeavingApplication();
            }

            @Override
            protected void onMraidUrl(String url) {
                mraidBridge.handleEndpoint(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mraidBridge.initialize();

                mraidBridge.setMRAIDState(States.DEFAULT);
                mraidBridge.setMRAIDIsVisible(true);
                mraidBridge.fireMRAIDEvent(Events.VIEWABLE_CHANGE, "true");
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

    protected void removeFromParent() {
        if (mraidBridge != null && mraidBridge.state.equals(States.RESIZED)) {
            setSize(new Size(defaultRect.width, defaultRect.height));
            mraidBridge.setMRAIDState(States.DEFAULT);
        }
        Utils.removeFromParent(container);
    }

    protected void reposition() {
        if (mraidBridge.state.equals(States.EXPANDED)) {
            setFullScreen();
        } else {
            webView.setLayoutParams(currentWebViewLayout);
            container.setLayoutParams(currentContainerLayout);
        }
    }

    public void open(String url) {
        adListener.onClicked();
    }

    public void close() {

        if (webViewExpanded == null && !windowIsFullscreen) {
            ((BannerView) container).destroy();
        }

        if (webViewExpanded != null) {
            mraidBridgeExpanded = null;
            Utils.removeFromParent(webViewExpanded);
            webViewExpanded = null;
            container.setLayoutParams(currentContainerLayout);
            mraidBridge.setMRAIDState(States.DEFAULT);
        }

        if (windowIsFullscreen) {
            (activity).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            webView.setLayoutParams(currentWebViewLayout);
            container.setLayoutParams(currentContainerLayout);
            windowIsFullscreen = false;
        }

        if (closeButton != null) {
            closeButton.removeAllViews();
            Utils.removeFromParent(closeButton);
            closeButton = null;
        }

        adListener.onClosed();
    }

    public void expand(String url) {
        if (url == null) {
            if (mraidBridge.expandProperties == null || !mraidBridge.expandProperties.useCustomClose) {
                addCloseButton(webViewExpanded, Position.TOP_RIGHT);
            }
            setFullScreen();
            adListener.onExpanded();
        } else {
            HttpClient.ResultListener listener = new HttpClient.ResultListener() {
                @Override
                public void GetCallback(String str) {
                    final String body = str;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String updatedBody = body;
                            updatedBody = Utils.validateHTMLStructure(updatedBody, false);
                            createExpandedWebView(updatedBody);
                            addCloseButton(webViewExpanded, Position.TOP_RIGHT);
                            adListener.onExpanded();
                            closeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    close();
                                }
                            });
                        }
                    });
                }
            };
            new HttpClient(listener).execute(url);
        }
    }

    private void createExpandedWebView(String body) {
        webViewExpanded = super.createWebView();
        webViewExpanded.setBackgroundColor(Color.WHITE);

        webViewExpanded.loadHtml(body);

        if (body.contains(MraidBridge.MRAID_JS)) {
            mraidBridgeExpanded = new MraidBridge(this, context, activity);
            mraidBridgeExpanded.activeWebView = webViewExpanded;
            mraidBridgeExpanded.initialize();
        }

        container.addView(webViewExpanded);
        setFullScreen();
    }

    public void resize(ResizeProperties properties) {
        int[] xyPos = new int[2];
        webView.getLocationOnScreen(xyPos);

        int offsetX = MraidUtilities.convertDpToPixel(properties.offsetX, context);
        int offsetY = MraidUtilities.convertDpToPixel(properties.offsetY, context);
        int width = MraidUtilities.convertDpToPixel(properties.width, context);
        int height = MraidUtilities.convertDpToPixel(properties.height, context);

        android.graphics.Rect rect = new android.graphics.Rect();

        ViewGroup root = activity.findViewById(android.R.id.content);
        root.getDrawingRect(rect);

        Rect destinationRect = new Rect(xyPos[0] + offsetX, xyPos[1] + offsetY, width, height);
        if (!properties.allowOffscreen) {
            if (destinationRect.x < 0) {
                destinationRect.x = 0;
            }
            if (destinationRect.x + destinationRect.width > rect.width()) {
                destinationRect.x = rect.width() - destinationRect.width;
            }
            if (destinationRect.y < 0) {
                destinationRect.y = 0;
            }
            if (destinationRect.y + destinationRect.height > rect.height()) {
                destinationRect.y = rect.height() - destinationRect.height;
            }
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerParams.setMargins(destinationRect.x, destinationRect.y, 0, 0);

        webView.setLayoutParams(params);
        container.setLayoutParams(containerParams);
        mraidBridge.setMRAIDCurrentPosition(destinationRect);
        adListener.onResized();
    }

    public void onLeavingApplication() {
        adListener.onLeavingApplication();
    }

    public void reportDOMSize(Size size) {
       /* if (defaultSize == null) {
            defaultSize = size;
        } */
    }

    public void setOrientationProperties(OrientationProperties properties) {
        if (properties.forceOrientation != null) {
            if (properties.forceOrientation.equals(Orientations.LANDSCAPE)) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            if (properties.forceOrientation.equals(Orientations.PORTRAIT)) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            if (properties.forceOrientation.equals(Orientations.NONE)) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        }
    }

    private void setFullScreen() {
        if (!windowIsFullscreen) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            windowIsFullscreen = true;
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
        webView.setLayoutParams(params);

        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
        containerParams.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        container.setLayoutParams(containerParams);
    }
}
