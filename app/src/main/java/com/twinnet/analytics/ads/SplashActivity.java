package com.twinnet.analytics.ads;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lib.adssdk.Ads;
import com.lib.adssdk.AdsManager.AdsManagerAppOpen;
import com.lib.adssdk.AppUtil;
import com.lib.adssdk.Listner.Ads.AdsInitListner;
import com.lib.adssdk.Listner.Ads.ErrorType;
import com.lib.adssdk.MyApp;
import com.lib.adssdk.TwinnetAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initAds();

    }

    public void initAds() {
        // Initialize SDK
         MyApplication.getInstance().InitSdk();

        Ads.init(this, new AdsInitListner() {
            @Override
            public void onSuccess() {
                try {
                    JSONObject eventProperties = new JSONObject();
                    eventProperties.put("splashActivity", "onSuccess");
                    TwinnetAnalytics.setCustomEvent("SplashInfo", eventProperties);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject data = new JSONObject(AppUtil.GetOtherData());
                    if ("true".equals(data.getString("isShowAppOpenOnSplash"))) {
                        AdsManagerAppOpen.initAd(SplashActivity.this, new AdsInitListner() {
                            @Override
                            public void onSuccess() {
                                nextActivity();
                            }

                            @Override
                            public void failedOnAdsInit(ErrorType error, String msg) {
                                nextActivity();
                            }
                        });
                    } else {
                        nextActivity();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failedOnAdsInit(ErrorType error, String msg) {
                try {
                    JSONObject eventProperties = new JSONObject();
                    eventProperties.put("errorType", error.toString());
                    eventProperties.put("msg", msg);
                    TwinnetAnalytics.setCustomEvent("SplashInfo", eventProperties);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (error == ErrorType.NO_INTERNET) {
                    Toast.makeText(SplashActivity.this, "checkInternet", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SplashActivity.this, "error: " + error + " msg: " + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private final Handler handler = new Handler(Looper.getMainLooper());

    private void nextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        waitForChange(() -> {
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void waitForChange(Runnable callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!MyApp.isMobileAdsInitializeCalled.get()) {
                    handler.postDelayed(this, 100);
                } else {
                    callback.run();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_CANCELED) {
            try {
                JSONObject eventProperties = new JSONObject();
                eventProperties.put("onActivityResult", "Update Canceled");
                TwinnetAnalytics.setCustomEvent("SplashInfo", eventProperties);
            } catch (Exception e) {
                e.printStackTrace();
            }
            nextActivity();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            JSONObject eventProperties = new JSONObject();
            eventProperties.put("clickOnBackPress", "1");
            TwinnetAnalytics.setCustomEvent("SplashInfo", eventProperties);
            super.onBackPressed();
        } catch (Exception e) {
            super.onBackPressed();
            e.printStackTrace();
        }
    }
}