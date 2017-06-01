package com.example.eom.hyunkyungapplication;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuAdapter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    SeekBar seekbar;
    TextView soju;
    TextView beer;
    TextView cup;
    //hello

    int ratio_soju = 5;
    int ratio_beer = 5;

    int cup_size = 0;
    int soju_time = 0;
    int beer_time = 0;

    public static final int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;

    ProgressDialog dialog;

    BluetoothAdapter mBluetoothAdapter;
    ConnectThread thread;
    ArrayList<String> mArrayAdapter = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        seekbar = (SeekBar)findViewById(R.id.seekBar);
        soju = (TextView)findViewById(R.id.textView4);
        beer = (TextView)findViewById(R.id.textView6);
        cup = (TextView)findViewById(R.id.textView);

        final ImageButton cup_small = (ImageButton)findViewById(R.id.imageButton1) ;
        ImageButton cup_medium = (ImageButton)findViewById(R.id.imageButton2) ;
        ImageButton cup_large = (ImageButton)findViewById(R.id.imageButton3) ;

        mHandler = new Handler();


        Button payment = (Button)findViewById(R.id.button) ;

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                ratio_soju = progress;
                ratio_beer = 10 - progress;
                soju.setText("   "+ progress + "");
                beer.setText("   "+ ratio_beer + "");
                Log.d("data","cupsze is "+cup_size+" sojuratio is "+ratio_soju);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        cup_small.setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View view) {
                cup_size = 1;
                soju_time = 50;
                beer_time = 50;
                cup.setText("50ml 잔을 선택");
//계싼 알고리즘 사용해서 ~~ 이렇구 저렇구~~
            }
        }) ;

        cup_medium.setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View view) {
                cup_size = 2;
                soju_time = 50;
                beer_time = 50;

                cup.setText("180ml 잔을 선택");
            }
        }) ;


        cup_large.setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View view) {
                cup_size = 3;
                soju_time = 50;
                beer_time = 50;

                cup.setText("300ml 잔을 선택");
            }
        }) ;

        payment.setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View view) {
                // nfc 페이지
                if (cup_size == 0) {
                    Toast.makeText(MainActivity.this, "컵 사이즈를 선택해 주세요", Toast.LENGTH_SHORT).show();
                } else {
//                    Intent intent =new Intent(MainActivity.this, BluetoothActivity.class);
//                    intent.putExtra("cupsize",cup_size);
//                    intent.putExtra("sojuratio",ratio_soju);
//                    Log.d("data","cupsze is "+cup_size+" sojuratio is "+ratio_soju);
//                    startActivity(intent);
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        // Device does not support Bluetooth
                    }
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        // Loop through paired devices
                        for (BluetoothDevice device : pairedDevices) {
                            // Add the name and address to an array adapter to show in a ListView
                            mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                            Log.d("Bluetooth", device.getName() + "\n" + device.getAddress());
//                            if (device.getName().equals("DEVICENAME")) {

                                String to_send = "" + cup_size;
                                if (ratio_soju == 10) {
                                    to_send += "a";
                                } else {
                                    to_send += ratio_soju;
                                }
                                to_send += "|";

                                thread = new ConnectThread(device, to_send);
                                thread.start();
//                            }
                        }
                    } else {
                        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
                    }

                }

            }
        }) ;


    }
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final String to_send;
        public ConnectThread(BluetoothDevice device,String to_send) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            mmDevice = device;
            this.to_send = to_send;
            // Get a BluetoothSocket to connect with the given BluetoothDevice
        }
         boolean aBoolean = true;

        public void run() {
            // Cancel discovery because it will slow down the connection

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    dialog = ProgressDialog.show(MainActivity.this, "",
                            "주문중입니다.", true, true, new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    thread.cancel();
                                    Toast.makeText(MainActivity.this,"주문을 취소했습니다.",Toast.LENGTH_SHORT).show();
                                    Log.d("dialog","canceled");
                                }
                            });
                }
            });
            mBluetoothAdapter.cancelDiscovery();
            while(aBoolean) {
                BluetoothSocket tmp = null;
                try {
                    tmp = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mmSocket = tmp;
                try {
                    // Connect the device through the socket. This will block
                    // until it succeeds or throws an exception
                    mmSocket.connect();
                    mmSocket.getOutputStream().write(to_send.getBytes());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog!=null&&dialog.isShowing()){
                                dialog.dismiss();
                            }
                            Toast.makeText(MainActivity.this,"주문이 성공했습니다.",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    return;
                } catch (IOException connectException) {
                    // Unable to connect; close the socket and get out
                    connectException.printStackTrace();
                    try {
                        mmSocket.close();
                    } catch (IOException closeException) {
                        closeException.printStackTrace();
                    }
                }
            }

            // Do work to manage the connection (in a separate thread)
//            manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                aBoolean=false;
                mmSocket.close();
                if (dialog!=null&&dialog.isShowing()){
                    dialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        thread.cancel();
    }


}

