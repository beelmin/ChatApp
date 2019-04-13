package com.example.chatroom;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Chat extends AppCompatActivity {

    private final String URL = "https://chatroom-android.herokuapp.com/";

    private String currentUser;
    private int currentColor;
    private Button send;
    private EditText et1;
    private LinearLayout linearLayout;
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentUser = getIntent().getStringExtra("user");
        currentColor = getIntent().getIntExtra("color",0);

        linearLayout = (LinearLayout) findViewById(R.id.info);
        et1 = (EditText) findViewById(R.id.mess);

        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                Chat.this.getData();
                handler.postDelayed(this, 300);
            }
        }, 300);


        send = (Button) findViewById(R.id.button1);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                Date date=cal.getTime();
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                String formattedDate=dateFormat.format(date);


                String data = "{"+
                        "\"username\"" + ":\"" + Chat.this.currentUser + "\","+
                        "\"message\"" + ":\"" + Chat.this.et1.getText().toString() + "\","+
                        "\"time\"" + ":\"" + formattedDate + "\""+
                        "}";

                Chat.this.et1.setText("");
                Chat.this.sendData(data);
            }
        });


    }




    private void getData() {


        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonArrayRequest request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{

                            Chat.this.linearLayout.removeAllViews();

                            for(int i=0;i<response.length();i++){

                                JSONObject obj = response.getJSONObject(i);

                                String user = obj.getString("user");
                                String message = obj.getString("mess");
                                String time = obj.getString("time");

                                String text = time + " " + user + " : " + message;


                                if(user.equals(Chat.this.currentUser)) {
                                    Chat.this.newTextView(text,"right");
                                }else{
                                    Chat.this.newTextView(text,"left");
                                }

                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            /*
                            Toast.makeText(getApplicationContext(),
                                    "ovdje sam",
                                    Toast.LENGTH_LONG).show();
                              */
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        /*
                        Toast.makeText(getApplicationContext(),
                                "ovdje sam 2",
                                Toast.LENGTH_LONG).show();

                        */
                    }
                });

        requestQueue.add(request);
    }


    private void sendData(String data) {

        final String savedata= data;

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres=new JSONObject(response);
                    //Toast.makeText(getApplicationContext(),objres.toString(),Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return savedata == null ? null : savedata.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {

                    return null;
                }
            }

        };
        requestQueue.add(stringRequest);

    }


    private void newTextView(String text, String where) {


        LinearLayout ll = new LinearLayout(Chat.this);
        ll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ll.setOrientation(LinearLayout.HORIZONTAL);
        if(where == "right") {
            ll.setGravity(Gravity.RIGHT);
        }else{
            ll.setGravity(Gravity.LEFT);
        }



        TextView tv = new TextView(Chat.this);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        tv.setText(text);
        
        if(where == "right") {
            if(currentColor != 0) {
                tv.setTextColor(currentColor);
            }
        }




        ll.addView(tv);
        this.linearLayout.addView(ll);

    }
}
