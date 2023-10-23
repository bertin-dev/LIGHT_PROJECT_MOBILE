package com.adsyst.light_project_mobile.ui.starting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.local_storage.provider.PrefManager;
import com.adsyst.light_project_mobile.local_translation.LocaleHelper;
import com.adsyst.light_project_mobile.ui.authentication.LoginStep1;

public class SplashScreen extends AppCompatActivity {

    private Button btnStart;
    private ProgressBar simpleProgressBar;
    private PrefManager prefManager;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        context = this;
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashScreen.this, Onboarding.class);
                startActivity(intent);
            }
        });

        simpleProgressBar = findViewById(R.id.simpleProgressBar);
        //int progressValue = simpleProgressBar.getProgress(); // get progress value from the progress bar
        //Toast.makeText(this, "" + progressValue, Toast.LENGTH_SHORT).show();



        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            private int time = 0;

            @Override
            public void run()
            {
                // do stuff then
                // can call h again after work!
                time += 1;
                Log.d("TimerExample", "Going for... " + time);
                simpleProgressBar.setProgress(time);
                if(time==100){

                    // Checking for first time launch
                    prefManager = new PrefManager(context);
                    if (!prefManager.isFirstTimeLaunch()) {
                        prefManager.setFirstTimeLaunch(false);
                        startActivity(new Intent(SplashScreen.this, LoginStep1.class));
                    }else {
                        prefManager.setFirstTimeLaunch(false);
                        Intent intent = new Intent(SplashScreen.this, Onboarding.class);
                        startActivity(intent);
                    }
                    finish();
                }
                h.postDelayed(this, 10);
            }
        }, 10); // 1 second delay (takes millis)

    }


    /**
     * attachBaseContext(Context newBase) methode callback permet de verifier la langue au demarrage de la page login
     * @param newBase
     * @since 2020
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}