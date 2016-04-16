package com.example.swasth.swasthtabletapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class FeedbackSummary extends AppCompatActivity {

    private TextView creditsTextView;
    private User user;
    private int selected;
    private SharedPreferences preferences;
    private Locale myLocale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("lang_info", Context.MODE_PRIVATE);
        selected = preferences.getInt("key_lang", 0);

        selectLanguage(selected);
        setContentView(R.layout.activity_feedback_summary);

        user = new User(getApplicationContext());
        //creditsTextView = (TextView) findViewById(R.id.creditsTextView);
        // int credits = user.getCredits();

        //String display = String.format("₹ %s", getResources().getString(R.string.credits));
        //creditsTextView.setText(display);

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        }, 4000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, 4000);
    }


    // select the language of choice
    private void selectLanguage(int position) {
        switch (position) {
            case 1:
                setLocale("hi");
                break;
            default:
                setLocale("en");
                break;
        }
    }


    public void setLocale(String lang) {
        Configuration config = getBaseContext().getResources().getConfiguration();

        myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //onConfigurationChanged(config);  //not getting overridden
    }

}
