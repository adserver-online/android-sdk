package com.adserver.mraid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;

import com.adserver.Position;
import com.adserver.mraid.constants.CalendarEvent;
import com.adserver.mraid.constants.Events;
import com.adserver.mraid.constants.Features;
import com.adserver.mraid.constants.NativeEndpoints;
import com.adserver.mraid.constants.States;
import com.adserver.utils.PhotoDownloader;
import com.adserver.utils.Utils;
import com.adserver.utils.webviews.HtmlWebView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URLDecoder;
import java.util.ArrayList;

public class MraidBridge {

    public static final String MRAID_OPEN = "mraid://open?args=";
    public static final String MRAID_JS = "mraid.js";
    private static final String TAG = BrowserActivity.class.getName();
    private static final String MRAID_VERSION = "2.0";
    public String state = States.LOADING;
    public boolean isInterstitial = false;
    public HtmlWebView activeWebView;
    public OrientationProperties orientationProperties = new OrientationProperties();
    public ExpandProperties expandProperties = new ExpandProperties();
    Activity activity;
    private Listener mraidListener;
    private Context context;
    private ResizeProperties resizeProperties = new ResizeProperties();
    private ArrayList<String> supportedFeatures;

    public MraidBridge(Listener listener, @NonNull Context context, Activity activity) {
        this.mraidListener = listener;
        this.context = context;
        this.activity = activity;
    }

    public void initialize() {

        getSupportedFeatures();

        setMRAIDSupports(Features.CALENDAR);
        setMRAIDSupports(Features.TEL);
        setMRAIDSupports(Features.STORE_PICTURE);
        setMRAIDSupports(Features.INLINE_VIDEO);
        setMRAIDSupports(Features.SMS);
        setMRAIDScreenSize();
        setMRAIDMaxSize();
        setMRAIDVersion(MRAID_VERSION);

        fireMRAIDEvent(Events.READY, null);
        setMRAIDState(States.DEFAULT);
    }

    public void handleEndpoint(String fullUrl) {
        fullUrl = fullUrl.replace("mraid://", "");
        String toFind = "?args=";
        String url;
        String args = null;
        int index = fullUrl.indexOf(toFind);
        if (index > -1) {
            url = fullUrl.substring(0, index);
            args = fullUrl.substring(index + toFind.length());
            args = URLDecoder.decode(args);
        } else {
            url = fullUrl;
        }
        switch (url) {
            case NativeEndpoints.REPORT_JS_LOG:
                android.util.Log.d("MRAID_CONSOLE::", args);
                break;
            case NativeEndpoints.SET_RESIZE_PROPERTIES:
                setResizeProperties(args);
                break;
            case NativeEndpoints.RESIZE:
                resize();
                break;
            case NativeEndpoints.SET_EXPAND_PROPERTIES:
                setExpandProperties(args);
                break;
            case NativeEndpoints.EXPAND:
                expand(args);
                break;
            case NativeEndpoints.SET_ORIENTATION_PROPERTIES:
                setOrientationProperties(args);
                break;
            case NativeEndpoints.OPEN:
                open(args);
                break;
            case NativeEndpoints.CLOSE:
                close();
                break;
            case NativeEndpoints.CREATE_CALENDAR_EVENT:
                createCalendarEvent(args);
                break;
            case NativeEndpoints.PLAY_VIDEO:
                playVideo(args);
                break;
            case NativeEndpoints.REPORT_DOM_SIZE:
                Gson gson = new GsonBuilder().create();
                Size reportedSize = gson.fromJson(args, Size.class);
                if (reportedSize != null && reportedSize.width > 0 && reportedSize.height > 0) {
                    mraidListener.reportDOMSize(reportedSize);
                }
                break;
            case NativeEndpoints.STORE_PICTURE:
                createPhoto(args);
                break;
        }
    }

    private void getSupportedFeatures() {
        // if device should disallow features, do it here
        supportedFeatures = new ArrayList<>();
        if (((TelephonyManager) activeWebView.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {
            supportedFeatures.add(Features.SMS);
            supportedFeatures.add(Features.TEL);
        }
        supportedFeatures.add(Features.CALENDAR);
        supportedFeatures.add(Features.INLINE_VIDEO);
        supportedFeatures.add(Features.STORE_PICTURE);
    }

    private void expand(String url) {
        Log.d(TAG, "MRAID :: expand");
        if (this.state.equals(States.DEFAULT)) {
            mraidListener.expand(url);
        }

        setMRAIDState(States.EXPANDED);
    }

    /* CALLED BY MRAID */

    private void resize() {
        Log.d(TAG, "MRAID :: resize");
        if (resizeProperties == null) {
            fireMRAIDEvent(Events.ERROR, "resize cannot be called before setResizeProperties");
            return;
        }
        if (state.equals(States.DEFAULT) || state.equals(States.RESIZED)) {
            String pos = resizeProperties.customClosePosition != null ? resizeProperties.customClosePosition : Position.TOP_RIGHT;
            if (resizeProperties.allowOffscreen) {
                // make sure the close button would be on screen
                boolean valid = true;
                int[] xypos = new int[2];
                activeWebView.getLocationOnScreen(xypos);
                int btnSize = MraidUtilities.convertDpToPixel(50, context);
                Rect resizedRect = new Rect(xypos[0], xypos[1], MraidUtilities.convertDpToPixel(resizeProperties.width, context), MraidUtilities.convertDpToPixel(resizeProperties.height, context));
                Rect fullscreen = MraidUtilities.getFullScreenRect((Activity) context);
                int offsetX = MraidUtilities.convertDpToPixel(resizeProperties.offsetX, context);
                int offsetY = MraidUtilities.convertDpToPixel(resizeProperties.offsetY, context);


                if (pos.contains("right")) {
                    int btnRight = resizedRect.x + resizedRect.width + offsetX;
                    boolean offscreenLeft = btnRight < btnSize;
                    boolean offscreenRight = btnRight > fullscreen.width;
                    if (offscreenLeft || offscreenRight) {
                        valid = false;
                    }
                }
                if (pos.contains("left")) {
                    int btnLeft = resizedRect.x + offsetX;
                    boolean offscreenLeft = btnLeft < 0;
                    boolean offscreenRight = btnLeft > (fullscreen.width - btnSize);
                    if (offscreenLeft || offscreenRight) {
                        valid = false;
                    }
                }
                if (pos.contains("bottom")) {
                    int btnBottom = resizedRect.y + resizedRect.height + offsetY;
                    boolean offscreenTop = btnBottom < btnSize;
                    boolean offscreenBottom = btnBottom > fullscreen.height;
                    if (offscreenTop || offscreenBottom) {
                        valid = false;
                    }
                }
                if (pos.contains("top")) {
                    int btnTop = resizedRect.y + offsetY;
                    boolean offscreenTop = btnTop < 0;
                    boolean offscreenBottom = btnTop > (fullscreen.height - btnSize);
                    if (offscreenTop || offscreenBottom) {
                        valid = false;
                    }
                }
                if (!valid) {
                    fireMRAIDEvent(Events.ERROR, "Current resize properties would result in the close region being off screen.  Ignoring resize.");
                    return;
                }
            }

            mraidListener.resize(resizeProperties);
            setMRAIDState(States.RESIZED);
        }
    }

    private void open(String url) {
        Log.d(TAG, "MRAID :: open");

        if (url.startsWith("tel://")) {
            String number = url.substring("tel://".length());
            attemptPhoneCall(number);
        } else if (url.startsWith("sms://")) {
            String number = url.substring("sms://".length());
            createSMS(number);
        } else {
            Intent intent = new Intent(context, BrowserActivity.class);
            intent.putExtra("URL", url);
            mraidListener.onLeavingApplication();
            activity.startActivity(intent);
        }

        mraidListener.open(url);
    }

    private void close() {
        Log.d(TAG, "MRAID :: close");
        mraidListener.close();
        ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        if (!isInterstitial) {
            setMRAIDState(States.DEFAULT);
        } else {
            setMRAIDState(States.HIDDEN);
            setMRAIDIsVisible(false);
        }
    }

    private void playVideo(String url) {
        Log.d(TAG, "MRAID :: playVideo -- NOT SUPPORTED YET");
    }

    private void attemptPhoneCall(String number) {
        Log.d(TAG, "MRAID :: callPhone");
        if (Utils.isPermissionGranted(activity, Manifest.permission.CALL_PHONE)) {
            mraidListener.onLeavingApplication();
            MraidUtilities.makePhoneCall(number, context);
        }
    }

    private void createSMS(String number) {
        Log.d(TAG, "MRAID :: createSms");

        if (Utils.isPermissionGranted(activity, Manifest.permission.SEND_SMS)) {
            mraidListener.onLeavingApplication();
            MraidUtilities.sendSMS(number, context);
        }
    }

    private void createPhoto(String args) {
        Log.d(TAG, "MRAID :: storePhoto");

        if (Utils.isPermissionGranted(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new PhotoDownloader((Activity) context).savePhoto(args);
        }
    }

    private void createCalendarEvent(String args) {
        Log.d(TAG, "MRAID :: createCalendarEvent");
        Gson gson = new GsonBuilder().create();
        CalendarEvent calendarEvent = gson.fromJson(args, CalendarEvent.class);
        if (calendarEvent != null && Utils.isPermissionGranted(activity, Manifest.permission.WRITE_CALENDAR)) {
            MraidUtilities.writeCalendarEvent(calendarEvent, (Activity) context);
        }
    }

    private void setExpandProperties(String args) {
        Log.d(TAG, "MRAID :: setExpandProperties");
        Gson gson = new GsonBuilder().create();
        ExpandProperties newProperties = gson.fromJson(args, ExpandProperties.class);
        if (newProperties != null) {
            // only replace properties which are present.  (deserialization defaults nullable types to null when not provided in the string)
            if (args.contains("useCustomClose")) {
                expandProperties.useCustomClose = newProperties.useCustomClose;
            }
            if (args.contains("width")) {
                expandProperties.width = newProperties.width;
            }
            if (args.contains("isModal")) {
                expandProperties.isModal = newProperties.isModal;
            }
            if (args.contains("height")) {
                expandProperties.height = newProperties.height;
            }
        }
    }

    private void setResizeProperties(String args) {
        Log.d(TAG, "MRAID :: setResizeProperties");
        Gson gson = new GsonBuilder().create();
        ResizeProperties newProperties = gson.fromJson(args, ResizeProperties.class);
        if (newProperties != null) {
            // only replace properties which are present.  (deserialization defaults nullable types to null when not provided in the string)
            if (args.contains("allowOffscreen")) {
                resizeProperties.allowOffscreen = newProperties.allowOffscreen;
            }
            if (args.contains("customClosePosition")) {
                resizeProperties.customClosePosition = newProperties.customClosePosition;
            }
            if (args.contains("height")) {
                resizeProperties.height = newProperties.height;
            }
            if (args.contains("width")) {
                resizeProperties.width = newProperties.width;
            }
            if (args.contains("offsetX")) {
                resizeProperties.offsetX = newProperties.offsetX;
            }
            if (args.contains("offsetY")) {
                resizeProperties.offsetY = newProperties.offsetY;
            }
        }
    }

    private void setOrientationProperties(String args) {
        Log.d(TAG, "MRAID :: setOrientationProperties");
        Gson gson = new GsonBuilder().create();
        OrientationProperties newProperties = gson.fromJson(args, OrientationProperties.class);
        if (newProperties != null) {
            if (args.contains("allowOrientationChange")) {
                orientationProperties.allowOrientationChange = newProperties.allowOrientationChange;
            }
            if (args.contains("forceOrientation")) {
                orientationProperties.forceOrientation = newProperties.forceOrientation;
            }
            mraidListener.setOrientationProperties(newProperties);
        }
    }

    /*
        ========= MRAID JS Methods =========
     */
    public void setMRAIDState(String state) {
        this.state = state;
        String js = "window.mraid.setState(\"" + state + "\");";
        executeJavascript(js);
    }

    public void fireMRAIDEvent(String event, String args) {
        String script = "window.mraid.fireEvent('" + event + "'" + (args != null ? ", " + args : "") + ");";
        executeJavascript(script);
    }

    public void setMRAIDSupports(String feature) {
        boolean supports = supportedFeatures.indexOf(feature) > -1;
        String script = String.format("window.mraid.setSupports(\"%s\", %b);", feature, supports);
        executeJavascript(script);
    }

    public void setMRAIDSizeChanged() {
        int width = MraidUtilities.convertPixelsToDp(activeWebView.getWidth(), activeWebView.getContext());
        int height = MraidUtilities.convertPixelsToDp(activeWebView.getHeight(), activeWebView.getContext());
        fireMRAIDEvent(Events.SIZE_CHANGE, String.format("{ width:\"%d\", height:\"%d\"}", width, height));
    }

    public void setMRAIDVersion(String version) {
        String script = "window.mraid.setVersion(\"" + version + "\");";
        executeJavascript(script);
    }

    private void setMRAIDMaxSize() {
        //TODO if we will support non-fullscreen apps, determine the allowable space. not recommended by IAB
        DisplayMetrics displayMetrics = activeWebView.getContext().getResources().getDisplayMetrics();
        int dpHeight = Math.round(displayMetrics.heightPixels / displayMetrics.density);
        int dpWidth = Math.round(displayMetrics.widthPixels / displayMetrics.density);
        @SuppressLint("DefaultLocale") String script = String.format("window.mraid.setMaxSize({width:%d, height:%d});", dpWidth, dpHeight);
        executeJavascript(script);
    }

    public void setMRAIDScreenSize() {
        DisplayMetrics displayMetrics = activeWebView.getContext().getResources().getDisplayMetrics();
        int dpHeight = Math.round(displayMetrics.heightPixels / displayMetrics.density);
        int dpWidth = Math.round(displayMetrics.widthPixels / displayMetrics.density);

        @SuppressLint("DefaultLocale") String script = String.format("window.mraid.setScreenSize({width:%d, height:%d});", dpWidth, dpHeight);
        executeJavascript(script);
    }

    public void setMRAIDCurrentPosition(Rect pos) {
        String js = String.format("window.mraid.setCurrentPosition({x:%s, y:%s, width:%s, height:%s});", pos.x, pos.y, pos.width, pos.height);
        executeJavascript(js);
    }

    public void setMRAIDDefaultPosition(Rect pos) {
        String js = String.format("window.mraid.setDefaultPosition({x:%s, y:%s, width:%s, height:%s});", pos.x, pos.y, pos.width, pos.height);
        executeJavascript(js);
    }

    public void setMRAIDIsVisible(boolean visible) {
        String js = "window.mraid.setIsViewable(" + visible + ");";
        executeJavascript(js);
    }

    protected void executeJavascript(String script) {
        activeWebView.loadUrl("javascript:" + script);
    }

    public interface Listener {
        void open(String url);

        void close();

        void expand(String url);

        void resize(ResizeProperties properties);

        void reportDOMSize(Size size);

        void setOrientationProperties(OrientationProperties properties);

        void onLeavingApplication();
    }
}
