package com.example.swasth.swasthtabletapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String SERVER_ADDRESS = "http://swasth-india.esy.es/swasth/center_user_login.php";
    public static final String KEY_CARDNO = "cardno";
    public static String JSON_ADDRESS = "http://swasth-india.esy.es/swasth/center_user_login.php";
    public static String FEEDBACK_ADDRESS = "";
    String cardNo;
    //    UserLocalStore userLocalStore;
    //    User user;
    Spinner languageSpinner;

    private Feedback[] feedback;

    private int selected;
    private String[] languages = {"English", "हिन्दी"};
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private EditText cardNoEditText;
    private Patient[] patientList;
    private static  int CARD_NO;
    private Button fillFeedbackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("lang_info", Context.MODE_PRIVATE);
        selected = preferences.getInt("key_lang", 0);
        FEEDBACK_ADDRESS = "http://swasth-india.esy.es/swasth/jsontest.php?choice=" + selected;
        getJsonQuestions();
        setContentView(R.layout.activity_main);

        // language spinner
        languageSpinner = (Spinner) findViewById(R.id.languageSpinner);
        languageSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);
        languageSpinner.setSelection(selected);

        cardNoEditText = (EditText) findViewById(R.id.cardNoEditText);

        cardNoEditText.getText().clear();

        fillFeedbackButton = (Button) findViewById(R.id.fillFeedbackButton);
        fillFeedbackButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cardNoEditText = (EditText) findViewById(R.id.cardNoEditText);
        cardNoEditText.getText().clear();
    }

    @Override
    public void onClick(View v) {
        CARD_NO = Integer.parseInt(cardNoEditText.getText().toString());

        cardNo = cardNoEditText.getText().toString();
        if (!cardNo.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putString("card_no", cardNo);
            login();
//            Intent intent = new Intent(this, FeedbackActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
        } else
            Toast.makeText(this, "Enter a card number!", Toast.LENGTH_SHORT).show();
    }

    private void login() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_ADDRESS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(getApplicationContext(), "Login Response 1......." + response, Toast.LENGTH_LONG).show();
                Log.i("TEST", "Response..." + response);
                if (response.contains("Failure")) {
                    Toast.makeText(getApplicationContext(), "Invalid Credentials......." + response, Toast.LENGTH_LONG).show();
                } else if (!response.isEmpty()) {
                    Log.i("TEST", "Inside success...");

                    try {
                        Log.i("TEST", "Inside try...");


                        Gson gson = new GsonBuilder().create();
                        patientList = gson.fromJson(response, Patient[].class);
                        Log.i("mits ", " " + patientList.length);


//                        Gson gson = new GsonBuilder().create();
//                        feedback = gson.fromJson(response, Feedback[].class);
//                        Log.i("mits ", " " + feedback.length);

//                    }){
//                        @Override
//                        protected Map<String, String> getParams ()throws AuthFailureError {
//                            Map<String, String> params = new HashMap<>();
//                            params.put("choice", Integer.toString(choice));
//                            return params;
//                        }
//                    } ;

//                        Intent intent = new Intent(MainActivity.this, FamilyListActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelableArray("patient_list", patientList);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                        //finish();


                        Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                        Bundle bundle = new Bundle();
                        intent.putExtra("card_no",cardNo);
                        bundle.putParcelableArray("feedback", feedback);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    } catch (Exception ex) {

                    }
//                    Toast.makeText(getApplicationContext(), "Login Response 2......." + response, Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Errror......." + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CARDNO, cardNo);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        editor = preferences.edit();
        //String languageName = languages[position];
        switch (position) {
            case 1:
                selected = 1;
                setLocale("hi");
                break;

            default:
                selected = 0;
                setLocale("en");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    private void getJsonQuestions() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, FEEDBACK_ADDRESS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("TEST", "Response..." + response);

                Gson gson = new GsonBuilder().create();
                feedback = gson.fromJson(response, Feedback[].class);
                Log.i("mits ", " " + feedback.length);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error......." + error, Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
        //((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        fillFeedbackButton.setText(R.string.fill_feedback_button);
        cardNoEditText.setHint(R.string.enter_card_number_edittext);
        this.onConfigurationChanged(config);

        editor.clear();
        editor.putInt("key_lang", selected);
        editor.apply();
    }
}