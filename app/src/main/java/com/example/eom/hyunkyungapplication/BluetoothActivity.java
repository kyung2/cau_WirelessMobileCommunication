/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.example.eom.hyunkyungapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class BluetoothActivity extends AppCompatActivity {

    public static final String TAG = "BTActivity";
    public static final int REQUEST_ENABLE_BT = 1;
    // Whether the Log Fragment is currently shown
    BluetoothAdapter mBluetoothAdapter;
    ConnectThread thread;
    ArrayList<String> mArrayAdapter = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluethooth);
        Intent intent = getIntent();
        int cupsize = intent.getIntExtra("cupsize",1);
        int soju_ratio = intent.getIntExtra("sojuratio",5);
        String to_send = ""+cupsize;
        if(soju_ratio==10){
            to_send+="a";
        }else{
            to_send+=soju_ratio;
        }
        to_send+="|";
        Log.d(TAG,to_send);

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
                Log.d("Bluetooth",device.getName() + "\n" + device.getAddress());
//                if(device.getName().equals("DEVICENAME")) {
                    thread = new ConnectThread(device, to_send);
                    thread.start();
//                }
            }
        }else{
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            thread.cancel();
            unregisterReceiver(mReceiver);
        }catch (Exception e){
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        try {
            thread.cancel();
            unregisterReceiver(mReceiver);
        }catch (Exception e){
        }
    }

    private class ConnectThread extends Thread {
        private  BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final String to_send;
        public ConnectThread(BluetoothDevice device,String to_send) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            mmDevice = device;
            this.to_send = to_send;
            // Get a BluetoothSocket to connect with the given BluetoothDevice
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();
            while(true) {
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
                mmSocket.close();
            } catch (IOException e) { }
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
    // Register the BroadcastReceiver
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
