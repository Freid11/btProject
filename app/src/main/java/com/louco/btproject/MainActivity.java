package com.louco.btproject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "BTManager";
    private BluetoothAdapter bluetoothAdapter;
    private Button buttonBT;
    private Button buttonDiscover;
    private Button buttonDiscoveryDevices;
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();

    private final BroadcastReceiver mBcReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            receivertest(intent);
        }
    };

    private void receivertest(Intent intent) {
        String action = intent.getAction();
        if (action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)) {

            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.d(TAG, "onReceive: STATE OFF");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                    break;
            }
        }
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Discovery has found a device. Get the BluetoothDevice
            // object and its info from the Intent.
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            devices.add(device);
            String name = device.getName();
            if(name!=null && name.equals("LoucoPhone")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Log.d(TAG,"createBond");

                    device.createBond();
                }
            }
            Log.d(TAG,"deviceName = "+ name +" deviceHardwareAddress = "+device.getAddress());
        }
    }

    private final BroadcastReceiver mBcReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            receivertest2(intent);
        }
    };

    private void receivertest2(Intent intent) {
        String action = intent.getAction();
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, bluetoothAdapter.ERROR);
        switch(state){
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                Log.d(TAG, "SCAN_MODE_CONNECTABLE_DISCOVERABLE");
                break;
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                Log.d(TAG, "SCAN_MODE_CONNECTABLE");
                break;
            case BluetoothAdapter.SCAN_MODE_NONE:
                Log.d(TAG, "SCAN_MODE_NONE");
                break;
            case BluetoothAdapter.STATE_CONNECTING:
                Log.d(TAG, "STATE_CONNECTING");
                break;
            case BluetoothAdapter.STATE_CONNECTED:
                Log.d(TAG, "STATE_CONNECTED");
                break;

        }
    }

    private final BroadcastReceiver mBcReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            receivertest3(intent);
        }
    };

    private void receivertest3(Intent intent) {
        String action = intent.getAction();
        if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                Log.d(TAG, "BOND_BONDED");
            }
            if (device.getBondState() == BluetoothDevice.BOND_BONDING){
                Log.d(TAG, "BOND_BONDING");
            }
            if(device.getBondState() == BluetoothDevice.BOND_NONE){
                Log.d(TAG, "BOND_NONE");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonBT = (Button) findViewById(R.id.bt_btOn);
        buttonDiscover = (Button) findViewById(R.id.bt_Discover);
        buttonDiscoveryDevices = (Button) findViewById(R.id.bt_Discovery_devices);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        buttonBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDisableBT();
            }
        });
        buttonDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscoverBt();
            }
        });
        buttonDiscoveryDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindDevices();
            }
        });

        IntentFilter intFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBcReceiver, intFilter);

        IntentFilter intentFilterBond = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBcReceiver3, intentFilterBond);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBcReceiver);
        unregisterReceiver(mBcReceiver2);
        unregisterReceiver(mBcReceiver3);
    }

    private void enableDisableBT() {
        if (bluetoothAdapter == null) {
            Log.d(TAG, "Null");
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBT);
            Log.d(TAG, "notEnabled");
        }
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            Log.d(TAG, "Enabled");
        }
    }

    private void DiscoverBt() {
        Log.d(TAG, "DiscoverBt");
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        startActivity(intent);

        IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBcReceiver2, intentFilter);
    }

    private void FindDevices() {
        Log.d(TAG, "FindDevices");
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "cancelDiscovery");
        }
//        checkBTPermissions();
        bluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBcReceiver, filter);
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
   /* private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }*/
}
