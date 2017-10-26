package com.louco.btproject;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "BtTag";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket btSocket = null;

    private boolean startFindDevices = false;
    private boolean SecritMe = false;
    private AdRVBtDevices adapterRv = new AdRVBtDevices(new bindDevicesBt());
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @BindView(R.id.bt_btOn)
    Button buttonBT;
    @BindView(R.id.bt_Discover)
    Button buttonDiscover;
    @BindView(R.id.bt_Discovery_devices)
    Button buttonDiscoveryDevices;
    @BindView(R.id.rv_list_devices)
    RecyclerView rvListDevices;

    private final BroadcastReceiver brOnBt = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            receivertest(intent);
        }
    };

    private void receivertest(Intent intent) {
        String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.d(TAG, "onReceive: STATE OFF");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                    break;
                case BluetoothAdapter.STATE_ON:
                    startFindDevices = true;
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
            String name = device.getName();
            adapterRv.addDevicesList(device);
            Log.d(TAG, "deviceName = " + name + " deviceHardwareAddress = " + device.getAddress());
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
        switch (state) {
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
        Log.d(TAG, action);
        if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (device.getBondState()) {
                case BluetoothDevice.BOND_BONDED:
                    Log.d(TAG, "BOND_BONDED");
                    break;
                case BluetoothDevice.BOND_BONDING:
                    Log.d(TAG, "BOND_BONDING");
                    break;
                case BluetoothDevice.BOND_NONE:
                    Log.d(TAG, "BOND_NONE");
                    break;
            }
        }
    }

    private class bindDevicesBt implements AdRVBtDevices.onClickRV {

        @Override
        public void onClick(BluetoothDevice device) {
            Log.d(TAG, "createBond  " + device.getName());
            bluetoothAdapter.cancelDiscovery();
            try {
                if (btSocket != null) {
                    btSocket.close();
                    btSocket = null;
                }
                btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
                btSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        PermissionAccess();

        rvListDevices.setLayoutManager(new LinearLayoutManager(this));
        rvListDevices.setAdapter(adapterRv);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        buttonBT.setOnClickListener(v -> enableDisableBT());
        buttonDiscover.setOnClickListener(v -> DiscoverBt());
        buttonDiscoveryDevices.setOnClickListener(v -> FindDevices());

        IntentFilter intFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(brOnBt, intFilter);

        IntentFilter intentFilterBond = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBcReceiver3, intentFilterBond);
    }

    private void PermissionAccess() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(brOnBt);
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
        if (btSocket!=null)
            try {
            if (SecritMe) {
                btSocket.getOutputStream().write("S".getBytes());
                SecritMe=false;
            } else {
                btSocket.getOutputStream().write("W".getBytes());
                SecritMe=true;
            }
            } catch (IOException e) {
                e.printStackTrace();
            }


        {
        /*Log.d(TAG, "DiscoverBt");
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        startActivity(intent);

        IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBcReceiver2, intentFilter);*/
        }

    }

    private void FindDevices() {
        Log.d(TAG, "FindDevices");
        adapterRv.clearDevicesList();
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "cancelDiscovery");
        }

//        checkBTPermissions();
        bluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(brOnBt, filter);
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
