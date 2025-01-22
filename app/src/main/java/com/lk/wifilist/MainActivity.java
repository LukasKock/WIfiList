package com.lk.wifilist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView wifiList;
    WifiManager wifiManager;
    private String[] PERMISSIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiList = findViewById(R.id.wifiList);

        PERMISSIONS = new String[]{
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
        Button buttonScan = findViewById(R.id.scanBtn);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        buttonScan.setOnClickListener(view -> {
            if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 1);
            } else {
                permissionsGranted();
            }
        });
        wifiList.setOnItemClickListener((adapterView, view, i, l) ->
                Toast.makeText(MainActivity.this, "Text:" + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "ACCESS WIFI STATE Permission is granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "ACCESS WIFI STATE Permission is denied", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "ACCESS COARSE LOCATION Permission is granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "ACCESS COARSE LOCATION Permission is denied", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "ACCESS FINE LOCATION Permission is granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "ACCESS FINE LOCATION Permission is denied", Toast.LENGTH_SHORT).show();
            }
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[1] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                permissionsGranted();
            }


        }
    }

    private boolean hasPermissions(Context context, String... PERMISSIONS) {

        if (context != null && PERMISSIONS != null) {

            for (String permission : PERMISSIONS) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void permissionsGranted() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<ScanResult> networks = wifiManager.getScanResults();

        ArrayList<String> deviceList = new ArrayList<>();

        //Adding networks to deviceList and Cutting unnecessary strings
        for (int i=0; i<networks.size();i++) {
            String[] separated = (networks.get(i).toString()).split(", BSSID:");
            deviceList.add(separated[0]);
        }
        //removing empty SSIDs
        for(int i=0; i<deviceList.size();i++){
            if((deviceList.get(i).equals("SSID: ")) || (deviceList.get(i).equals("SSID: \"\"")) || (deviceList.get(i).equals("SSID:"))){
                deviceList.remove(i);
                i = 0;
            }
        }
        //removing repeated SSID (Because they have different BSSID - Will it work later?)
        for(int i = 0; i<deviceList.size(); i++){
            for(int b = 0;b<deviceList.size();b++){
                if (b != i) {
                    if((deviceList.get(i)).equals(deviceList.get(b))){
                        deviceList.remove(b);
                        b = 0;
                    }
                }
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList.toArray());
        wifiList.setAdapter(arrayAdapter);
    }
}