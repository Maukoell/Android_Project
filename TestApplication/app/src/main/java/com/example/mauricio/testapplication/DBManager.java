package com.example.mauricio.testapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBManager extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SchulDB.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS Data (" +
                    "id int primary key," +
                    "startort text not null," +
                    "zielort text not null," +
                    "dep_time text not null)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS Data";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static String[] getDataFromDB(Context context) {
        SQLiteDatabase db = context.openOrCreateDatabase("SchulDB.db", SQLiteDatabase.OPEN_READWRITE,null);
        db.execSQL(SQL_CREATE_ENTRIES);
        Cursor c;
        c = db.rawQuery("Select * from Data", new String[] {});
        String[] s = {"","",""};
        if (c != null && c.moveToFirst()) {
            s[0] = c.getString(c.getColumnIndexOrThrow("startort"));
            s[1] = c.getString(c.getColumnIndexOrThrow("zielort"));
            s[2] = c.getString(c.getColumnIndexOrThrow("dep_time"));
            c.close();
        }
        return s;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}