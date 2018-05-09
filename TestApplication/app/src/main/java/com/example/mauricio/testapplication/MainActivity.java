package com.example.mauricio.testapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rdc.R;

public class MainActivity extends Activity {

    private LocationManager locationMangaer=null;
    private LocationListener locationListener=null;

    private Button getData = null;
    private EditText start = null;
    private EditText ziel = null;
    private String url = null;
    private EditText loc = null;

    private static final String TAG = "Debug";
    private Boolean flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);

        getData = findViewById(R.id.getData);
        start = findViewById(R.id.startort);
        ziel = findViewById(R.id.zielort);
        loc = findViewById(R.id.editTextLocation);

        getData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + start.getText() + "&destination=" + ziel.getText() + "&mode=transit&transit_mode=train&key=AIzaSyDwCmvGloqh5i8eL08cFWJMiWaYOPSK8B4";
                loc.setText(retrieveURLData());
                //loc.setText(url);
            }
        });

    }

    public String retrieveURLData() {

        try {

            int read;
            char[] chars = new char[1024];
            URL urlPath = new URL(url);
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlPath.openStream()));
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();

        } catch (IOException ioe){
            ioe.printStackTrace();
        }

        return null;
    }

}