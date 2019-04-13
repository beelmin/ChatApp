package com.example.chatroom;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {


    private String user;
    private EditText input;
    private Button submit;
    private RadioGroup rg;
    private int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (EditText) findViewById(R.id.input);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.green) {
                    MainActivity.this.color = ContextCompat.getColor(getApplicationContext(), R.color.green);
                }else if(checkedId == R.id.black) {
                    MainActivity.this.color = ContextCompat.getColor(getApplicationContext(), R.color.black);
                }else if(checkedId == R.id.red) {
                    MainActivity.this.color = ContextCompat.getColor(getApplicationContext(), R.color.red);
                }else{
                    MainActivity.this.color = ContextCompat.getColor(getApplicationContext(), R.color.blue);
                }
            }
        });


        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.user = MainActivity.this.input.getText().toString();
                Intent intent = new Intent(MainActivity.this,Chat.class);
                intent.putExtra("user",MainActivity.this.user);
                intent.putExtra("color",MainActivity.this.color);
                startActivity(intent);
            }
        });

    }
}
