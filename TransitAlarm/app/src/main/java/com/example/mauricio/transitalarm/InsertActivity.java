package com.example.mauricio.transitalarm;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class InsertActivity extends ListActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ArrayList<String> results = new ArrayList<String>();
    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    private SQLiteDatabase db;
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_inserts);

        contactList = new ArrayList<>();


        openAndQueryDatabase();
        displayResultList();

    }

    @SuppressLint("SetTextI18n")
    private void displayResultList() {
        TextView tView = new TextView(this);

        getListView().addHeaderView(tView);

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, results));
        getListView().setTextFilterEnabled(true);

    }
    private void openAndQueryDatabase() {

        DBManager dbManager = new DBManager(this);
        db = dbManager.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT startort, zielort, dep_time, lat, long, delay FROM " +
                "Data", null);

        if (c != null ) {
            if (c.moveToFirst()) {
                do {
                    String startort = c.getString(c.getColumnIndex("startort"));
                    String zielort = c.getString(c.getColumnIndex("zielort"));
                    String dep_time = c.getString(c.getColumnIndex("dep_time"));
                    double lat = c.getDouble(c.getColumnIndex("lat"));
                    double lon = c.getDouble(c.getColumnIndex("long"));
                    int delay = c.getInt(c.getColumnIndex("delay"));
                    results.add("Startort: " + startort + ", Zielort: " + zielort + ", Departure Time: " + dep_time + ", Latitude: " +
                    lat + ", Longitude: " + lon + ", Delay:" + delay);
                }while (c.moveToNext());
            }
        }


}}
