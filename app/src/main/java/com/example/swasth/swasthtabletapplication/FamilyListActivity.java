package com.example.swasth.swasthtabletapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;

public class FamilyListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static String JSON_ADDRESS = "http://swasth-india.esy.es/swasth/center_user_login.php";
    public static String FEEDBACK_ADDRESS = "";
    private ListView patientListView;
    private FamilyListAdapter familyListAdapter;
    private Patient[] patientList;
    private Feedback[] feedback;
    private int selected;
    private SharedPreferences preferences;
    private Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("lang_info", Context.MODE_PRIVATE);
        selected = preferences.getInt("key_lang", 0);
        selectLanguage(selected);

        setContentView(R.layout.activity_family_list);

        SharedPreferences editor = getSharedPreferences("lang_info", Context.MODE_PRIVATE);
        int selected = editor.getInt("key_lang", 1);
        FEEDBACK_ADDRESS = "http://swasth-india.esy.es/swasth/jsontest.php?choice=" + selected;

        Bundle bundle = getIntent().getExtras();
        getJsonQuestions(selected);


        Parcelable[] parcelable = bundle.getParcelableArray("patient_list");
        Patient[] patientList = new Patient[parcelable.length];
        System.arraycopy(parcelable, 0, patientList, 0, parcelable.length);


        if (patientList != null) {
            patientListView = (ListView) findViewById(R.id.patientListView);
            //send the array of family details
            familyListAdapter = new FamilyListAdapter(this, patientList);
            patientListView.setAdapter(familyListAdapter);
            patientListView.setOnItemClickListener(this);

        } else
            Toast.makeText(getApplicationContext(), "ERROR" + patientList, Toast.LENGTH_LONG).show();

    }


    private void getJsonQuestions(final int choice) {

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


//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("choice", Integer.toString(choice));
//                return params;
//            }
//        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, FeedbackActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArray("feedback", feedback);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1) {
//            if (resultCode == Activity.RESULT_OK) {
//                setContentView(R.layout.activity_feedback_summary);
//                new Timer().schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        finish();
//                    }
//                }, 4000);
//
//                finish();
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                //do nothing
//            }
//        }
//    }

    // select the language of the app
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
