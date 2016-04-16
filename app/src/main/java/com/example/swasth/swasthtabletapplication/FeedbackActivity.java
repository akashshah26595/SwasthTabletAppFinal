package com.example.swasth.swasthtabletapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    public static final String SERVER_ADDRESS = "http://swasth-india.esy.es/swasth/insert_medical_feedback.php";

    public static String credits = "";
    SharedPreferences sharedPreferences;
    int score = 0;
    private int pos = 0;
    private boolean flag = false;
    private TextView progressText;
    private ScrollView feedbackScrollViewLayout;
    private FeedbackAdapter feedbackAdapter;
    private Button previousButton;
    private RadioGroup optionsRadioGroup;
    private Map<String, String> hashMap;
    private ScrollView mainScrollView;
    private Feedback[] feedback;
    private User user;
    private static  int CARD_NO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        user = new User(getApplicationContext());
        progressText = (TextView) findViewById(R.id.progress_text);

        CARD_NO = Integer.parseInt(getIntent().getStringExtra("card_no"));
        Bundle bundle = getIntent().getExtras();
        Parcelable[] parcelable = bundle.getParcelableArray("feedback");
        feedback = new Feedback[parcelable.length];
        System.arraycopy(parcelable, 0, feedback, 0, parcelable.length);

        Button nextButton = (Button) findViewById(R.id.nextButton);
        previousButton = (Button) findViewById(R.id.previousButton);
        //   mainScrollView = (ScrollView) findViewById(R.id.mainScrollView);

//        Bundle bundle = getIntent().getExtras();
//        Parcelable[] parcelable = bundle.getParcelableArray("feedback");
//        Feedback[] feedback = new Feedback[parcelable.length];
//        System.arraycopy(parcelable, 0, feedback, 0, parcelable.length);
        //Log.i("TEST ", feedback[0].question);

        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);

        feedbackScrollViewLayout = (ScrollView) findViewById(R.id.feedbackScrollViewLayout);
        feedbackAdapter = new FeedbackAdapter(this, feedback);

        feedbackScrollViewLayout.addView(feedbackAdapter.getView(pos, null, feedbackScrollViewLayout));
        previousButton.setVisibility(View.GONE);

        progressText.setText((pos + 1) + " of " + feedbackAdapter.getCount());
        hashMap = new HashMap<>(feedback.length);
        optionsRadioGroup = (RadioGroup) findViewById(R.id.optionsRadioGroup);
        optionsRadioGroup.setOnCheckedChangeListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.nextButton:
                nextQuestion();
                break;
            case R.id.previousButton:
                previousQuestion();
                break;
        }
    }


    private void nextQuestion() {
        if (flag) {
            pos++;
            previousButton.setVisibility(View.VISIBLE);

            progressText.setText(String.format("%d of %d", pos + 1, feedbackAdapter.getCount()));
            if (pos >= feedbackAdapter.getCount()) {
                progressText.setText(feedbackAdapter.getCount() + " of " + feedbackAdapter.getCount());

                //hashMap.put("score", String.valueOf(score));
                final Map<String, String> sortedMap = new TreeMap<>(hashMap);
                sortedMap.put("card_no",Integer.toString(CARD_NO));
                sortedMap.put("score",Integer.toString(score));
                //Toast.makeText(this, "Map is: " + sortedMap, Toast.LENGTH_LONG).show();
                pos = feedbackAdapter.getCount() - 1;

                if (score >= feedbackAdapter.getCount() * 3) {

                    sortedMap.put("feedback","No Comments");
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_ADDRESS, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // Toast.makeText(getApplicationContext(), "Feedback Response......." + response, Toast.LENGTH_LONG).show();
                            Log.i("TEST", "Feedback Response......." + response);
                            if (!response.contains("Error")) {
                                String temp = getResources().getString(R.string.toast_thankyou);
                                Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
//                            credits = Integer.valueOf(response);

                                user.updateCredits(response);
                                Log.i("TEST", "User Class Credits:" + user.getCredits());
                                Log.d("TEST", "Edit Text Response..." + response);


                                    Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                                    //intent.putExtra("credits", response);
                                Toast.makeText(getApplicationContext(),"Your total credits: " + response, Toast.LENGTH_LONG).show();

                                startActivity(intent);


                            } else {
                                Log.i("TEST", "Error:" + response);
                            }
//                    Intent intent = new Intent(FeedbackActivity.this, FeedbackSummary.class);
//                    intent.putExtra("summary",sortedMap.toString());
//                    startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Feedback Error......." + error, Toast.LENGTH_LONG).show();
                            Log.i("TEST", "Feedback Error......." + error);

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            return sortedMap;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    requestQueue.add(stringRequest);

                    finish();
                }
                else{
                    Intent i = new Intent(FeedbackActivity.this, FeedbackEditTextActivity.class);
                    user.storeFeedback(sortedMap);
                    i.putExtra("score",score);
                    startActivity(i);
                }
            } else {
                feedbackScrollViewLayout.removeViewAt(0);
                feedbackScrollViewLayout.addView(feedbackAdapter.getView(pos, null, feedbackScrollViewLayout));
                saveRadioState();
                if (!hashMap.containsKey(Integer.toString(pos + 1))) {
                    flag = false;
                }
            }
        } else {
            Toast.makeText(this, getResources().getText(R.string.toast_select_choice), Toast.LENGTH_SHORT).show();
        }
    }


    //on clicking the previous button
    private void previousQuestion() {
        pos--;

        if (pos == 0)
            previousButton.setVisibility(View.GONE);
        progressText.setText((pos + 1) + " of " + feedbackAdapter.getCount());
        if (pos < 0) {
            progressText.setText("1 of " + feedbackAdapter.getCount());
            Toast.makeText(this, "This is the first question!", Toast.LENGTH_SHORT).show();
            pos = 0;
        } else {
            feedbackScrollViewLayout.removeViewAt(0);
            feedbackScrollViewLayout.addView(feedbackAdapter.getView(pos, null, feedbackScrollViewLayout));

            saveRadioState();
            flag = true;
        }
    }

    //track the option selection
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        flag = true;
        switch (checkedId) {
            case R.id.option1:
                hashMap.put(Integer.toString(pos + 1), "1");
                score += 5;
                //Toast.makeText(this, "Hashmap: " + hashMap, Toast.LENGTH_LONG).show();
                break;
            case R.id.option2:
                score += 4;
                //Toast.makeText(this, "Opt 2", Toast.LENGTH_SHORT).show();
                hashMap.put(Integer.toString(pos + 1), "2");
                break;
            case R.id.option3:
                score += 2;
                //Toast.makeText(this, "Opt 3", Toast.LENGTH_SHORT).show();
                hashMap.put(Integer.toString(pos + 1), "3");
                break;
            case R.id.option4:
                score += 1;
                //Toast.makeText(this, "Opt 4", Toast.LENGTH_SHORT).show();
                hashMap.put(Integer.toString(pos + 1), "4");
                break;
            case R.id.option5:
                score += 3;
                //Toast.makeText(this, "Opt 5", Toast.LENGTH_SHORT).show();
                hashMap.put(Integer.toString(pos + 1), "5");
                break;
        }
    }


    //loads saved value of radio button and makes it accept change in radio values
    private void saveRadioState() {
        optionsRadioGroup = (RadioGroup) findViewById(R.id.optionsRadioGroup);

        String val = hashMap.get(Integer.toString(pos + 1));
        if (val != null) {
            if (Integer.parseInt(val) == 1) {
                score -= 5;
                optionsRadioGroup.check(R.id.option1);
            } else if (Integer.parseInt(val) == 2) {
                score -= 4;
                optionsRadioGroup.check(R.id.option2);
            } else if (Integer.parseInt(val) == 3) {
                score -= 2;
                optionsRadioGroup.check(R.id.option3);
            } else if (Integer.parseInt(val) == 4) {
                score -= 1;
                optionsRadioGroup.check(R.id.option4);
            } else {
                score -= 3;
                optionsRadioGroup.check(R.id.option5);
            }
        }
        optionsRadioGroup.setOnCheckedChangeListener(this);
    }


    // on pressing the phone's back button
    @Override
    public void onBackPressed() {
        pos--;
        if (pos < 0) {
            Intent i = new Intent(FeedbackActivity.this, FamilyListActivity.class);
            setResult(Activity.RESULT_CANCELED, i);
            finish();
        } else if (pos >= 0) {
            if (pos == 0)
                previousButton.setVisibility(View.GONE);

            progressText.setText((pos + 1) + " of " + feedbackAdapter.getCount());
            feedbackScrollViewLayout.removeViewAt(0);
            feedbackScrollViewLayout.addView(feedbackAdapter.getView(pos, null, feedbackScrollViewLayout));
            saveRadioState();
            flag = true;
        }
    }
}