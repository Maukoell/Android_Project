package com.example.mauricio.transitalarm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SchulDB.db";
    private static final String SQL_CREATE_UIDATA =
            "CREATE TABLE IF NOT EXISTS UIData (" +
                    "id int primary key," +
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

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS UIData";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        onCreate(context.openOrCreateDatabase("SchulDB.db", SQLiteDatabase.OPEN_READWRITE,null));
    }

    public static Object[] getDataFromDB(Context context) {
        SQLiteDatabase db = context.openOrCreateDatabase("SchulDB.db", SQLiteDatabase.OPEN_READWRITE,null);
        db.execSQL(SQL_CREATE_UIDATA);
        db.execSQL(SQL_CREATE_DATA);
        Cursor c;
        c = db.rawQuery("Select * from Data", new String[] {});
        Object[] s = {"","","",0,0};
        if (c != null && c.moveToFirst()) {
            s[0] = c.getString(c.getColumnIndexOrThrow("startort"));
            s[1] = c.getString(c.getColumnIndexOrThrow("zielort"));
            s[2] = c.getString(c.getColumnIndexOrThrow("dep_time"));
            s[3] = c.getDouble(c.getColumnIndexOrThrow("lat"));
            s[4] = c.getDouble(c.getColumnIndexOrThrow("long"));
            c.close();
        }
        return s;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_UIDATA);
        db.execSQL(SQL_CREATE_DATA);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}