package com.example.mauricio.testapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.rdc.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static com.example.mauricio.testapplication.MainActivity.readJsonFromUrl;

public class AlertReceiver extends BroadcastReceiver {

    private String googleUrl1;
    private String googleUrl2;
    private int lat;
    private int lon;
    private String start;
    private String ziel;
    private String timeInput;
    private Calendar dep;
    private Calendar go;
    private SQLiteDatabase db;

    @Override
    public void onReceive(Context context, Intent intent) {
        String[] s = DBManager.getDataFromDB(context);
        start = s[0];
        ziel = s[1];
        timeInput = s[2];
        getTime();
        createNotificationChannel(context);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,"Test")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Test")
                .setContentText("Test Notification 123 @ !§%")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        String depString = dep.get(Calendar.HOUR_OF_DAY) + ":" + dep.get(Calendar.MINUTE) ;
        String goString = go.get(Calendar.HOUR_OF_DAY) + ":" + go.get(Calendar.MINUTE);
        mBuilder.setContentTitle("Dein Zug fährt um " + depString + " ab");
        mBuilder.setContentText("Geh um " + goString +" los um deinen Zug zu erreichen");
        notificationManager.notify(1, mBuilder.build());
    }



    private void getTime() {
        final String[] timeParts=timeInput.split(":");
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        // cal.add(Calendar.HOUR, -1);
        cal.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        cal.set(Calendar.SECOND,0);
        final Date myDate=cal.getTime();
        long l = 0;
        l = myDate.getTime()/1000;
        googleUrl1 = "https://maps.googleapis.com/maps/api/directions/json?origin=" + lat + "," + lon + "&destination=" + start + "&mode=transit&transit_mode=train&key=AIzaSyDwCmvGloqh5i8eL08cFWJMiWaYOPSK8B4";
        googleUrl2 = "https://maps.googleapis.com/maps/api/directions/json?origin=" + start + "&destination=" + ziel + "&departure_time=" + l + "&mode=transit&transit_mode=train&key=AIzaSyDwCmvGloqh5i8eL08cFWJMiWaYOPSK8B4";
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
            dep = cal;
            cal.add(Calendar.MINUTE, -min);
            go = cal;
            d2 = cal.getTime();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (JSONException e) {
        e.printStackTrace();
    }}

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel 1";
            String description = "Notification Channel 1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Test", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
