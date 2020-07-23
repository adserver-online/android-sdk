package com.adserver.interstitial;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adserver.R;
import com.adserver.mraid.constants.Orientations;

import java.util.Timer;
import java.util.TimerTask;

public class InterstitialActivity extends Activity {

    public final static String FINISH_ACTION = "FINISH_ACTION";
    public final static String ORIENTATION_LANDSCAPE_ACTION = "ORIENTATION_LANDSCAPE_ACTION";
    public final static String ORIENTATION_PORTRAIT_ACTION = "ORIENTATION_PORTRAIT_ACTION";
    public final static String ORIENTATION_UNSPECIFIED_ACTION = "ORIENTATION_UNSPECIFIED_ACTION";
    public final static String EXTRA_ORIENTATION = "EXTRA_ORIENTATION";
    public final static String EXTRA_CLOSE_TIMER = "EXTRA_CLOSE_TIMER";
    InterstitialController interstitialController;
    WebView webView;
    LinearLayout layout;
    int closeFadeTime = 155; // milliseconds
    int closeFadeRate = 25; // milliseconds
    int closeFontSize = 14;
    int closeXFontSize = 15;
    Timer countdownTimer;
    Timer fadeTimer;
    float fadeDirection = -1.0f;
    float fadeAmountPerTick;
    boolean closeClickable = false;
    BroadcastReceiver broadcastReceiver;
    private int closeTimer = 0;
    private TextView closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_interstitial);

        interstitialController = InterstitialController.INSTANCE_FOR_ACTIVITY;

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();

                if (action == null) {
                    return;
                }

                switch (action) {
                    case FINISH_ACTION:
                        finish();
                        break;

                    case ORIENTATION_LANDSCAPE_ACTION:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;

                    case ORIENTATION_PORTRAIT_ACTION:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;

                    case ORIENTATION_UNSPECIFIED_ACTION:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        break;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FINISH_ACTION);
        intentFilter.addAction(ORIENTATION_LANDSCAPE_ACTION);
        intentFilter.addAction(ORIENTATION_PORTRAIT_ACTION);
        intentFilter.addAction(ORIENTATION_UNSPECIFIED_ACTION);

        registerReceiver(broadcastReceiver, intentFilter);

        init();
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        super.onDestroy();
    }

    public void init() {
        String orientation = getIntent().getStringExtra(EXTRA_ORIENTATION);
        closeTimer = getIntent().getIntExtra(EXTRA_CLOSE_TIMER, 0);

        if (orientation != null) {
            if (orientation.equals(Orientations.LANDSCAPE)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            if (orientation.equals(Orientations.PORTRAIT)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            if (orientation.equals(Orientations.NONE)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        }

        layout = findViewById(R.id.webViewContainer);
        layout.removeAllViews();

        webView = interstitialController.webView;

        ViewGroup parent = (ViewGroup) webView.getParent();
        if (parent != null) {
            parent.removeView(webView);
            ((Activity) parent.getContext()).finish();
        }

        layout.addView(webView);

        initializeCloseButton();

        interstitialController.setShown();
    }

    void initializeCloseButton() {
        FrameLayout item = findViewById(R.id.btnBackground);

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeClickable) {
                    finish();
                }
            }
        });

        closeButton = new TextView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        closeButton.setLayoutParams(params);
        Typeface font = Typeface.create("Droid Sans Mono", Typeface.NORMAL);
        closeButton.setTextColor(Color.WHITE);
        closeButton.setTypeface(font);
        closeButton.setGravity(Gravity.CENTER);

        if (closeTimer > 0) {
            setCloseButtonText(Integer.toString(closeTimer), closeFontSize);
            countdownTimer = new Timer();
            TimerTask countdown = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateTimer();
                        }
                    });
                }
            };
            countdownTimer.scheduleAtFixedRate(countdown, 0, 1000);
        } else {
            setCloseButtonText("✕", closeXFontSize);
            closeClickable = true;
        }
        layout.bringChildToFront(item);
        item.addView(closeButton);
    }

    void updateTimer() {
        closeTimer -= 1;
        if (closeTimer > 0) {
            setCloseButtonText(Integer.toString(closeTimer), closeFontSize);
        } else {
            setCloseButtonActive();
        }
    }

    void setCloseButtonText(String text, float size) {
        closeButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        closeButton.setText(text);
    }

    void setCloseButtonActive() {
        fadeAmountPerTick = ((float) closeFadeRate / (float) closeFadeTime);
        countdownTimer.cancel();
        countdownTimer = null;
        closeClickable = true;
        fadeTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTimerFade();
                    }
                });
            }
        };
        fadeTimer.scheduleAtFixedRate(task, 0, (long) closeFadeRate);
    }

    void updateTimerFade() {

        if (fadeTimer != null) {
            float a = closeButton.getAlpha();
            a += fadeAmountPerTick * fadeDirection;
            closeButton.setAlpha(a);
            if (closeButton.getAlpha() < 0) {
                fadeDirection = 1.0f;
                setCloseButtonText("✕", closeXFontSize);
            }
            if (closeButton.getAlpha() >= 1) {
                fadeTimer.cancel();
                fadeTimer = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!closeClickable) {
            return;
        }

        super.onBackPressed();
    }
}
