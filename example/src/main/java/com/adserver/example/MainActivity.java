package com.adserver.example;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.adserver.AdListener;
import com.adserver.AdRequest;
import com.adserver.Adserver;
import com.adserver.ErrorCode;
import com.adserver.Position;
import com.adserver.banner.BannerView;
import com.adserver.interstitial.InterstitialView;
import com.adserver.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final List<String> REQUIRED_PERMISSIONS = new ArrayList<>();

    static {
        //REQUIRED_PERMISSIONS.add(Manifest.permission.CALL_PHONE);
        //REQUIRED_PERMISSIONS.add(Manifest.permission.SEND_SMS);
    }

    InterstitialView interstitialView;
    BannerView bannerView;
    BannerView bannerViewAbs;
    AdListener bannerListener = new AdListener() {
        @Override
        public void onFetchSucceeded() {
            //Toast.makeText(MainActivity.this, "Fetched", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFetchFailed(ErrorCode code) {
        }

        @Override
        public void onDisplayed() {
            //Toast.makeText(MainActivity.this, "Displayed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onExpanded() {
            Toast.makeText(MainActivity.this, "Expended", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResized() {
            Toast.makeText(MainActivity.this, "Resized", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLeavingApplication() {
            //Toast.makeText(MainActivity.this, "Leaving application", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClosed() {
            Toast.makeText(MainActivity.this, "Closed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClicked() {
            Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        List<String> permissionsToBeRequested = new ArrayList<>();

        for (String permission : REQUIRED_PERMISSIONS) {
            if (!Utils.isPermissionGranted(this, permission)) {
                permissionsToBeRequested.add(permission);
            }
        }

        // Request permissions
        if (!permissionsToBeRequested.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToBeRequested.toArray(
                    new String[0]), 255);
        }

        Adserver sdk = Adserver.initialize(this);

        bannerView = (BannerView) findViewById(R.id.banner_view);
        bannerView.setAutoRefreshInterval(50);
        bannerView.setListener(bannerListener);

        final AdRequest bannerRequest = new AdRequest(54241, 300, 250);

        bannerViewAbs = (BannerView) findViewById(R.id.banner_view_absolute);
        bannerViewAbs.setListener(bannerListener);
        bannerViewAbs.setAbsolutePositioned(true);

        final AdRequest bannerRequestAbs = new AdRequest(54242, 320, 50);
        bannerRequestAbs.setAdSize(320, 50);

        Button bannerButton = (Button) findViewById(R.id.btnReloadBanner);

        bannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerView.loadAd(bannerRequest);
                bannerViewAbs.loadAd(bannerRequestAbs, Position.BOTTOM_CENTER);
            }
        });

        // load ad right away app started
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bannerView.loadAd(bannerRequest);
                bannerViewAbs.loadAd(bannerRequestAbs, Position.BOTTOM_CENTER);
            }
        }, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onGetInterstitialClick(View view) {

        interstitialView = new InterstitialView(this);
        interstitialView.setCloseButtonEnableTimer(3);
        //interstitialView.setBackgroundColor(Color.BLACK);

        AdRequest request = new AdRequest(54253);
        request.setAdSize(240, 400);

        interstitialView.setListener(new AdListener() {
            @Override
            public void onReady() {
                Toast.makeText(MainActivity.this, "Interstitial ready", Toast.LENGTH_SHORT).show();
                super.onReady();
                if (interstitialView.getReady()) {
                    interstitialView.show();
                    Toast.makeText(MainActivity.this, "Interstitial shown", Toast.LENGTH_SHORT).show();
                }
            }
        });

        interstitialView.loadAd(request);
    }
}
