package com.example.swasth.swasthtabletapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;
import java.util.TreeMap;

public class FeedbackEditTextActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText queryEditText;
    private Button cancelButton;
    private Button sendButton;
    private int score;
    public static final String SERVER_ADDRESS = "http://swasth-india.esy.es/swasth/insert_medical_feedback.php";

    Map<String, String> sortedMap;
 User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_edit_text);
        user = new User(getApplicationContext());
        score = getIntent().getIntExtra("score", 0);
        queryEditText = (EditText) findViewById(R.id.queryEditText);
       // cancelButton = (Button) findViewById(R.id.cancelButton);
        sendButton = (Button) findViewById(R.id.sendButton);
        sortedMap = new TreeMap<>(user.getFeedback());
        sendButton.setOnClickListener(this);
       // cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendButton:
                String message = queryEditText.getText().toString();
                if (!message.isEmpty()) {
                    // store message in DB
                    //Toast.makeText(getApplicationContext(),"Button Clicked 2", Toast.LENGTH_LONG).show();

                    sortedMap.put("feedback", message);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_ADDRESS, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            user.updateCredits(response);
                            Toast.makeText(getApplicationContext(),"Your total credits: " + response, Toast.LENGTH_LONG).show();
                            Log.d("TEST", "Edit Text Response..." + response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("TEST","Error..." + error);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            return sortedMap;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    requestQueue.add(stringRequest);
                    Intent i = new Intent(FeedbackEditTextActivity.this, MainActivity.class);
                    String temp = getResources().getString(R.string.toast_thankyou);
                    Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                    startActivity(i);
                }
                else
                    Toast.makeText(FeedbackEditTextActivity.this, getResources().getText(R.string.toast_write_message), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
