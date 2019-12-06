package com.dsitvision.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;



import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    public static final String WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int WRITE_PERM_REQ_CODE = 19;
    private static final int MULTIPLE_PERMISSIONS = 11;

    EditText etUsername;
    TextView tvPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        etUsername = (EditText) findViewById(R.id.et_home_player_name);
        tvPort = (TextView) findViewById(R.id.tv_port_info);

        String userNameHint = getString(R.string.enter_name_hint) + "(default = " + Build
                .MANUFACTURER + ")";
        etUsername.setHint(userNameHint);
        checkPermissions();
        //checkWritePermission();
        printInterfaces();
    }


    @Override
    protected void onResume() {
        super.onResume();
        DBAdapter.getInstance(HomeScreen.this).clearDatabase();
        tvPort.setText(String.format(getString(R.string.port_info), ConnectionUtils.getPort
                (HomeScreen.this)));
    }

    private void printInterfaces() {
        try {
            Enumeration<NetworkInterface> x = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : Collections.list(x)) {
                Log.v("NetWorkInterfaces", "display name: " + ni.getDisplayName());
                Log.v("NetWorkInterfaces", "name: " + ni.getName());
                Log.v("NetWorkInterfaces", "is up and running ? : " + String.valueOf(ni.isUp()));
                Log.v("NetWorkInterfaces", "Loopback?: " + String.valueOf(ni.isLoopback()));
                Log.v("NetWorkInterfaces", "Supports multicast: " + String.valueOf(ni
                        .supportsMulticast()));
                Log.v("NetWorkInterfaces", "is virtual: " + String.valueOf(ni.isVirtual()));
                Log.v("NetWorkInterfaces", "Hardware address: " + Arrays.toString(ni
                        .getHardwareAddress()));
                Log.v("NetWorkInterfaces", "Sub interfaces.....");
                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                for (InetAddress singleNI : Collections.list(inetAddresses)) {
                    Log.v("NetWorkInterfaces", "sub ni inetaddress: " + singleNI.getHostAddress());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void saveUsername() {
        String userName = etUsername.getText().toString();
        if (userName != null && userName.trim().length() > 0) {
            Utility.saveString(HomeScreen.this, TransferConstants.KEY_USER_NAME, userName);
        }
    }

    private void checkWritePermission() {
        boolean isGranted = Utility.checkPermission(WRITE_PERMISSION, this);
        if (!isGranted) {
            Utility.requestPermission(WRITE_PERMISSION, WRITE_PERM_REQ_CODE, this);
        }
    }



    public void startWiFiDirect(View v) {
        if (Utility.isWiFiEnabled(HomeScreen.this)) {
            saveUsername();
            Intent wifiDirectIntent = new Intent(HomeScreen.this, LocalDashWiFiDirect.class);
            startActivity(wifiDirectIntent);
            finish();
        } else {
            NotificationToast.showToast(HomeScreen.this, getString(R.string
                    .wifi_not_enabled_error));
        }
    }


    public void startcall(View v) {
        if (Utility.isWiFiEnabled(HomeScreen.this)) {
            saveUsername();
            Intent wifiDirectIntent = new Intent(HomeScreen.this, CallDeviceList.class);
            startActivity(wifiDirectIntent);
            finish();
        } else {
            NotificationToast.showToast(HomeScreen.this, getString(R.string
                    .wifi_not_enabled_error));
        }
    }

    String[] permissions= new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };
    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode==MULTIPLE_PERMISSIONS)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // permissions granted.
            } else {
                // no permissions granted.
            }
            return;
        }
    }

}
