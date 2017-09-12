package com.tech.flavor.haveasafej.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tech.flavor.haveasafej.R;
import com.tech.flavor.haveasafej.util.HsjConstant;
import com.tech.flavor.haveasafej.util.HsjHandler;


public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);

       // TextView myTextView = (TextView)findViewById(R.id.title_char);

       // myTextView.setSelected(true);

//        GridLayout ll = (GridLayout) findViewById(R.id.splash_ll_gl);
//
//        HsjHandler.getTitle(ll, this, HsjConstant.APP_NAME, 70);

        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 5000) { //Five second timer
                        sleep(100);
                        waited += 100;
                    }
                } catch (InterruptedException e) { //Any errors that might occur
                    // do nothing
                } finally {
                    finish();
                    Intent pMainActivity = new Intent(getBaseContext(),
                            LogInForm.class);
                    startActivity(pMainActivity);
//	overridePendingTransition(android.R.anim.slide_in_left,
//            android.R.anim.slide_out_right);
                }
            }
        };
        splashThread.start();
    }
    @Override
    public void onBackPressed() {
        return;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

}
