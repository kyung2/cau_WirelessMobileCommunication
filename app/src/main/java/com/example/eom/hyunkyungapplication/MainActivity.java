package com.example.eom.hyunkyungapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SeekBar seekbar;
    TextView soju;
    TextView beer;
    int cup_size = 0;
    int soju_time = 0;
    int beer_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        seekbar = (SeekBar)findViewById(R.id.seekBar);
        soju = (TextView)findViewById(R.id.textView4);
        beer = (TextView)findViewById(R.id.textView6);

        ImageButton cup_samll = (ImageButton)findViewById(R.id.imageButton1) ;
        ImageButton cup_medium = (ImageButton)findViewById(R.id.imageButton2) ;
        ImageButton cup_large = (ImageButton)findViewById(R.id.imageButton3) ;



        Button payment = (Button)findViewById(R.id.button) ;

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                int tmp = 10-progress;
                soju.setText("   "+ progress + "");
                beer.setText("   "+ tmp + "");
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        cup_samll.setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View view) {
                cup_size = 50;
                soju_time = 50;
                beer_time = 50;
            }
        }) ;

        cup_medium.setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View view) {
                cup_size = 180;
                soju_time = 50;
                beer_time = 50;
            }
        }) ;


        cup_large.setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View view) {
                cup_size = 300;
                soju_time = 50;
                beer_time = 50;
            }
        }) ;

        payment.setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View view) {
                // nfc 페이지
            }
        }) ;


    }
}

