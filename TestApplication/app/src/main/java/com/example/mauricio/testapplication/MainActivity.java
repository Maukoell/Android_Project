package com.example.mauricio.testapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import org.json.*;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Scanner;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rdc.R;

public class MainActivity extends Activity {

    private LocationManager locationMangaer=null;
    private LocationListener locationListener=null;

    private Button getData = null;
    private EditText start = null;
    private EditText ziel = null;
    private URL url = null;
    private TextView loc = null;
    private String googleUrl = null;


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
                googleUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=" + start.getText() + "&destination=" + ziel.getText() + "&mode=transit&transit_mode=train&key=AIzaSyDwCmvGloqh5i8eL08cFWJMiWaYOPSK8B4";
                //googleUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=Seefeld_Bahnhof&destination=Innsbruck_Hauptbahnhof&mode=transit&transit_mode=train&key=AIzaSyDwCmvGloqh5i8eL08cFWJMiWaYOPSK8B4";
                try {
                    JSONObject jsn = readJsonFromUrl(googleUrl);
                    JSONArray arr = jsn.getJSONArray("routes");
                    jsn = arr.getJSONObject(0);
                    JSONArray arr2 = jsn.getJSONArray("legs");
                    JSONObject arr3 = arr2.getJSONObject(0);
                    JSONObject arr4 = arr3.getJSONObject("departure_time");
                    String s = arr4.getString("text");

                    loc.setText(s);
                    //loc.setText(jsn.getJSONObject("routes").getJSONObject("legs").getJSONObject("arrival_time").getString("text"));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

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

}