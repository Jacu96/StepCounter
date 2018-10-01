package com.example.jacek.stepcounter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import java.util.concurrent.atomic.AtomicInteger;
import android.util.Log;


public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "History.db";
    private final String TAG = "DATABASE";
    public static final String TABLE_NAME = "History";
    public static final String ID = "ID";
    public static final String DATE = "Date";
    public static final String STEPS = "Steps";
    private static final AtomicInteger openCounter = new AtomicInteger();
    private static Database instance;


    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static synchronized Database getInstance(final Context c) {
        if (instance == null) {
            instance = new Database(c.getApplicationContext());
        }

        //po co ten counter?
        //openCounter.incrementAndGet();
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (" + ID +
        " INTEGER PRIMARY KEY AUTOINCREMENT," + DATE + " INTEGER, " + STEPS + " INTEGER )");
        Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXonCreate");
    }

    /*@Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }*/

    @Override
    public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            // drop PRIMARY KEY constraint
            db.execSQL("CREATE TABLE " + TABLE_NAME + "2 ("+DATE+" INTEGER, "+STEPS+" INTEGER)");
            db.execSQL("INSERT INTO " + TABLE_NAME + "2 ("+DATE+", "+STEPS+") SELECT "+DATE+", "+STEPS+" FROM " +
                    TABLE_NAME);
            db.execSQL("DROP TABLE " + TABLE_NAME);
            db.execSQL("ALTER TABLE " + TABLE_NAME + "2 RENAME TO " + TABLE_NAME + "");
        }
    }

    public void putData(long date, int steps) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE, date);
        contentValues.put(STEPS, steps);
        sqLiteDatabase.insert(TABLE_NAME,null,contentValues);

        //nie wiadomo czy caly ten long nie jest zbedny
        /*
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if (result == -1) return false;
        else return true;*/
        sqLiteDatabase.close();

    }
    //TODO zrobic metode get ktore daja kroki z konkretnej id
    public int getSteps(int id){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        int steps;
        Cursor c=sqLiteDatabase.rawQuery("SELECT "+STEPS+" FROM "+TABLE_NAME+" WHERE "+ID+" ="+id,null);
        c.moveToFirst();
        steps=c.getInt(0);
        c.close();
        sqLiteDatabase.close();
        return steps;
    }

    public long getDate(int id){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        long date;
        Cursor c=sqLiteDatabase.rawQuery("SELECT "+DATE+" FROM "+TABLE_NAME+" WHERE "+ID+" ="+id,null);
        c.moveToFirst();
        date=c.getLong(0);
        c.close();
        sqLiteDatabase.close();
        return date;
    }



}
