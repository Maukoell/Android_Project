package com.example.mauricio.test2.feature;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        Button b = findViewById(R.id.button2);
        final TextView text = findViewById(R.id.textView2);

        final GPSTracker gps = new GPSTracker(this);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps.getLocation();
                text.setText("Latitude: " + gps.getLatitude() + " Longitude: " + gps.getLongitude());
                LatLng destination= new LatLng(210, 321);
                LatLng origin= new LatLng(gps.getLatitude(), gps.getLongitude());
                DirectionsResult result = DirectionsApi.newRequest(getGeoContext())                    .mode(TravelMode.DRIVING).origin(origin)                    .destination(destination).departureTime(now)                    .await();
            }
        });
    }
}
