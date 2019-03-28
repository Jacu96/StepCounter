package com.example.jacek.stepcounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "History.db";
    public static final String TABLE_NAME = "History";
    public static final String ID = "ID";
    public static final String DATE = "Date";
    public static final String STEPS = "Steps";
    private static Database instance;


    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static synchronized Database getInstance(final Context c) {
        if (instance == null) {
            instance = new Database(c.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," + DATE + " INTEGER, " + STEPS + " INTEGER )");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public void putData(long date, int steps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE, date);
        contentValues.put(STEPS, steps);
        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public int getSteps(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        int steps;
        String sqlQuery = "SELECT " + STEPS + " FROM " + TABLE_NAME + " WHERE " + ID + " =" + id;
        Cursor c = db.rawQuery(sqlQuery, null);
        c.moveToFirst();
        steps = c.getInt(0);
        c.close();
        db.close();
        return steps;
    }

    public long getDate(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        long date;
        String sqlQuery = "SELECT " + DATE + " FROM " + TABLE_NAME + " WHERE " + ID + " =" + id;
        Cursor c = db.rawQuery(sqlQuery, null);
        c.moveToFirst();
        date = c.getLong(0);
        c.close();
        db.close();
        return date;
    }

    public int getLastID() {
        SQLiteDatabase db = this.getReadableDatabase();
        int lastId;
        String sqlQuery2 = "SELECT count(*) FROM " + TABLE_NAME;
        Cursor c = db.rawQuery(sqlQuery2, null);
        c.moveToFirst();
        lastId = c.getInt(0);
        c.close();
        db.close();
        return lastId;
    }
}
