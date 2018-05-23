package com.example.mauricio.testapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.BaseColumns;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.rdc.R;

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
import java.util.Scanner;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends Activity {

    private LocationManager locationMangaer=null;
    private LocationListener locationListener=null;
    private FusedLocationProviderClient client;

    private Button getData = null;
    private EditText start = null;
    private EditText ziel = null;
    private URL url = null;
    private TextView loc = null;
    private String googleUrl1 = null;
    private String googleUrl2 = null;
    private String timeInput = null;
    private EditText dep = null;
    private Button homeButton = null;
    private double lat = 47.331403;
    private double lon = 11.181802;
    private SQLiteDatabase db;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS Data (" +
                    "id INTEGER primary key AUTOINCREMENT ," +
                    "startort text not null," +
                    "zielort text not null," +
                    "dep_time text not null)";

    private static final String SQL_DROP_Table = "DROP TABLE IF EXISTS Data";

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
        final DBManager dbManager = new DBManager(this);
        db = dbManager.getReadableDatabase();
        getData = findViewById(R.id.getData);
        start = findViewById(R.id.startort);
        ziel = findViewById(R.id.zielort);
        loc = findViewById(R.id.editTextLocation);
        dep = findViewById(R.id.editText2);
        homeButton = findViewById(R.id.homeButton);
        getDataFromDB();

        client = LocationServices.getFusedLocationProviderClient(this);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if( location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();

                        }

                    }
                });
            }
        });


        getData.setOnClickListener(new OnClickListener() {
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
                    db.execSQL(SQL_CREATE_ENTRIES);
                    ContentValues values = new ContentValues();
                    values.put("Startort", start.getText().toString());
                    values.put("Zielort", ziel.getText().toString());
                    values.put("dep_time", dep.getText().toString());

                    long newRowId = db.insert("Data", null, values);
                    cal.add(Calendar.MINUTE, -5);
                    startAlarm(cal);
                    //getDataFromDB();
//                    loc.setText((int) newRowId);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    public void getDataFromDB() {
        db = this.openOrCreateDatabase("SchulDB.db",SQLiteDatabase.OPEN_READWRITE,null);
        db.execSQL(SQL_CREATE_ENTRIES);
        Cursor c;
        c = db.rawQuery("Select * from Data", new String[] {});

        if (c != null && c.moveToFirst()) {
            start.setText(c.getString(c.getColumnIndexOrThrow("startort")));
            ziel.setText(c.getString(c.getColumnIndexOrThrow("zielort")));
            dep.setText(c.getString(c.getColumnIndexOrThrow("dep_time")));
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

    public JSONObject retrieveURLData() {

        try {

            Scanner scan = new Scanner(url.openStream());
            String str = new String();
            while (scan.hasNext())
                str += scan.nextLine();
            scan.close();
            // build a JSON object
            JSONObject obj = new JSONObject(str);
            if (! obj.getString("status").equals("OK"))
                return obj;
        } catch (IOException ioe){
            ioe.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }



    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[] {ACCESS_FINE_LOCATION}, 1);
    }

}