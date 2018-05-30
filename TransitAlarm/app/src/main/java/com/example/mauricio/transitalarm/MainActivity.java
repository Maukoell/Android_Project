package com.example.mauricio.transitalarm;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FusedLocationProviderClient client;

    private Button getData = null;
    private EditText start = null;
    private EditText ziel = null;
    private URL url = null;
    private TextView loc = null;
    private String googleUrl1 = null;
    private String googleUrl2 = null;
    private String timeInput = null;
    private TextView latlong = null;
    private EditText dep = null;
    private Button homeButton = null;
    private double lat = 47.331403;
    private double lon = 11.181802;
    private SQLiteDatabase db;
    private final Context ACTIVITY_CONTEXT = this;
    private final Activity MAIN_ACTIVITY = this;
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS UIData (" +
                    "id INTEGER primary key AUTOINCREMENT ," +
                    "startort text not null," +
                    "zielort text not null," +
                    "dep_time text not null," +
                    "lat REAL not null," +
                    "long REAL not null)";
    private static final String SQL_CREATE_DATA =
            "CREATE TABLE IF NOT EXISTS Data (" +
                    "id int primary key," +
                    "startort text not null," +
                    "zielort text not null," +
                    "dep_time text not null," +
                    "lat REAL not null," +
                    "long REAL not null," +
                    "delay int not null)";
    private static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

    private static final String SQL_DROP_Table = "DROP TABLE IF EXISTS UIData";
    private static final String SQL_DROP_DATA = "DROP TABLE IF EXISTS Data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);
        final DBManager dbManager = new DBManager(this);
        db = dbManager.getReadableDatabase();
        getData = findViewById(R.id.getData);
        latlong = findViewById(R.id.latlong);
        start = findViewById(R.id.startort);
        ziel = findViewById(R.id.zielort);
        loc = findViewById(R.id.editTextLocation);
        dep = findViewById(R.id.editText2);
        homeButton = findViewById(R.id.homeButton);
        getDataFromDB();

        latlong.setText("Lat: " + lat +" Long: " + lon);

        client = LocationServices.getFusedLocationProviderClient(this);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ACTIVITY_CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ACTIVITY_CONTEXT, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    getLocationPermission();
                    return;
                }
                client.getLastLocation()
                        .addOnSuccessListener(MAIN_ACTIVITY, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    lon = location.getLongitude();
                                    lat = location.getLatitude();
                                    latlong.setText("Lat: " + lat + " Long: " + lon);
                                    Toast toast = Toast.makeText(MAIN_ACTIVITY, "Home Location set",Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        });
            }
        });

        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeInput = dep.getText().toString();
                final String[] timeParts=timeInput.split(":");
                Calendar cal=Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
                // cal.add(Calendar.HOUR, -1);
                cal.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
                cal.set(Calendar.SECOND,0);
                final Date myDate=cal.getTime();
                long l = 0;
                l = myDate.getTime()/1000;
                googleUrl1 = "https://maps.googleapis.com/maps/api/directions/json?origin=" + lat + "," + lon + "&destination=" + start.getText() + "&mode=transit&transit_mode=train&key=AIzaSyDwCmvGloqh5i8eL08cFWJMiWaYOPSK8B4";
                googleUrl2 = "https://maps.googleapis.com/maps/api/directions/json?origin=" + start.getText() + "&destination=" + ziel.getText() + "&departure_time=" + l + "&mode=transit&transit_mode=train&key=AIzaSyDwCmvGloqh5i8eL08cFWJMiWaYOPSK8B4";
                //googleUrl2 = "https://maps.googleapis.com/maps/api/directions/json?origin=Seefeld_Bahnhof&destination=Innsbruck_Hauptbahnhof&mode=transit&transit_mode=train&key=AIzaSyDwCmvGloqh5i8eL08cFWJMiWaYOPSK8B4";
                try {
                    JSONObject jsn2 = readJsonFromUrl(googleUrl1);
                    JSONArray arr11 = jsn2.getJSONArray("routes");
                    jsn2 = arr11.getJSONObject(0);
                    JSONArray arr12 = jsn2.getJSONArray("legs");
                    JSONObject arr13 = arr12.getJSONObject(0);
                    JSONObject arr14 = arr13.getJSONObject("duration");
                    String s1 = arr14.getString("text");
                    String[] parts1 = s1.split(" ");
                    int min = Integer.parseInt(parts1[0]);

                    JSONObject jsn1 = readJsonFromUrl(googleUrl2);
                    JSONArray arr21 = jsn1.getJSONArray("routes");
                    jsn1 = arr21.getJSONObject(0);
                    JSONArray arr22 = jsn1.getJSONArray("legs");
                    JSONObject arr23 = arr22.getJSONObject(0);
                    JSONObject arr24 = arr23.getJSONObject("departure_time");
                    Long s2 = arr24.getLong("value");
                    Date d2 = new Date(s2*1000);

                    cal.set(Calendar.MINUTE, d2.getMinutes());
                    cal.set(Calendar.HOUR, d2.getHours());
                    cal.add(Calendar.MINUTE, -min);
                    d2 = cal.getTime();
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");

                    loc.setText(dateFormatter.format(d2));

                    SQLiteDatabase db = dbManager.getWritableDatabase();
                    db.execSQL(SQL_DROP_Table);
                    //db.execSQL(SQL_DROP_DATA);
                    //db.execSQL(SQL_CREATE_DATA);
                    db.execSQL(SQL_CREATE_ENTRIES);
                    ContentValues values = new ContentValues();
                    values.put("Startort", start.getText().toString());
                    values.put("Zielort", ziel.getText().toString());
                    values.put("dep_time", dep.getText().toString());
                    values.put("lat", lat);
                    values.put("long", lon);

                    long newRowId = db.insert("UIData", null, values);
                    values.put("delay", 0);
                    long rowId = db.insert("Data", null, values);
                    Calendar newCal = Calendar.getInstance();
                    if (cal.get(Calendar.HOUR_OF_DAY) < newCal.get(Calendar.HOUR_OF_DAY) ) {
                        cal.add(Calendar.MINUTE,-5);
                        cal.add(Calendar.DAY_OF_YEAR, 1);
                    }else if (cal.get(Calendar.HOUR_OF_DAY) == newCal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.MINUTE) < newCal.get(Calendar.MINUTE)) {
                        cal.add(Calendar.DAY_OF_YEAR,1);
                        cal.add(Calendar.MINUTE,-5);
                    }
                    startAlarm(cal);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_db) {
            Intent myIntent = new Intent(this, InsertActivity.class);
            startActivity(myIntent);
        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    public void getDataFromDB() {
        db = this.openOrCreateDatabase("SchulDB.db", SQLiteDatabase.OPEN_READWRITE,null);
        db.execSQL(SQL_CREATE_ENTRIES);
        Cursor c;
        c = db.rawQuery("Select * from UIData", new String[] {});

        if (c != null && c.moveToFirst()) {
            start.setText(c.getString(c.getColumnIndexOrThrow("startort")));
            ziel.setText(c.getString(c.getColumnIndexOrThrow("zielort")));
            dep.setText(c.getString(c.getColumnIndexOrThrow("dep_time")));
            lat = c.getDouble(c.getColumnIndexOrThrow("lat"));
            lon = c.getDouble(c.getColumnIndexOrThrow("long"));
            c.close();
        }

    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public void startAlarm(Calendar c) {
        cancelAlarm(c);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(), pendingIntent);
        Toast toast = Toast.makeText(this, "Alarm set",Toast.LENGTH_SHORT);
        toast.show();
    }

    public void cancelAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
    }
}
