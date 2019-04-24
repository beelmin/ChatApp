package com.example.chatroom;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
    private Button send;
    private EditText et1;
    private LinearLayout linearLayout;

    private RequestQueue requestQueue;

    private Handler handler = new Handler();
    private Runnable runnable;
    private int delay = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentUser = getIntent().getStringExtra("user");

        linearLayout = (LinearLayout) findViewById(R.id.info);
        et1 = (EditText) findViewById(R.id.mess);
        et1.setTextColor(Color.WHITE);


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

    @Override
    protected void onResume() {
        super.onResume();

        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                Chat.this.getData();
                handler.postDelayed(runnable,delay);
            }
        },delay);

    }

    @Override
    protected void onPause() {
        super.onPause();

        handler.removeCallbacks(runnable);
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
                            //Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        //Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
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
                    JSONObject objres = new JSONObject(response);
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
        if(where.equals("right")) {
            ll.setGravity(Gravity.RIGHT);
        }else{
            ll.setGravity(Gravity.LEFT);
        }



        TextView tv = new TextView(Chat.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                         LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,7);
        tv.setLayoutParams(params);
        tv.setPadding(5,5,5,5);


        int[] colors_right = {getResources().getColor(R.color.green),getResources().getColor(R.color.green)};
        int[] colors_left = {getResources().getColor(R.color.grey),getResources().getColor(R.color.grey)};

        GradientDrawable gd;

        if(where.equals("right")){
            gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors_right);
            gd.setStroke(2,getResources().getColor(R.color.green));
        }else{
            gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors_left);
            gd.setStroke(2,getResources().getColor(R.color.grey));
        }

        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(10.0f);

        tv.setBackground(gd);
        tv.setText(text);
        tv.setTextColor(Color.WHITE);

        ll.addView(tv);
        this.linearLayout.addView(ll);

    }
}
