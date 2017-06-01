package com.example.eom.hyunkyungapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TwoButtonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_button);
        findViewById(R.id.button2_two_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent;
                mainIntent= new Intent(TwoButtonActivity.this,MainActivity.class);
                TwoButtonActivity.this.startActivity(mainIntent);
            }
        });
        findViewById(R.id.button3_two_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent;
                mainIntent= new Intent(TwoButtonActivity.this,MainActivity.class);
                TwoButtonActivity.this.startActivity(mainIntent);
            }
        });
    }
}
