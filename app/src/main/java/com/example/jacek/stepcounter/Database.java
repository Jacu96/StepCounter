package com.example.jacek.stepcounter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import java.util.concurrent.atomic.AtomicInteger;
import android.util.Log;

import static java.sql.Types.NULL;


public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "History.db";
    private final String TAG = "Database";
    public static final String TABLE_NAME = "History";
    public static final String ID = "ID";
    public static final String DATE = "Date";
    public static final String STEPS = "Steps";
    //private static final AtomicInteger openCounter = new AtomicInteger();
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
        Log.d(TAG+".onCreate", "Database created");
    }

    //todo zamienic na cos co czaje
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
        sqLiteDatabase.close();
    }

    public int getSteps(long id){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        int steps;
        String sqlQuery="SELECT "+STEPS+" FROM "+TABLE_NAME+" WHERE "+ID+" ="+id;
        Cursor c=sqLiteDatabase.rawQuery(sqlQuery,null);
        c.moveToFirst();
        steps=c.getInt(0);
        c.close();
        sqLiteDatabase.close();
        return steps;
    }

    public long getDate(long id){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        long date;
        String sqlQuery="SELECT "+DATE+" FROM "+TABLE_NAME+" WHERE "+ID+" ="+id;
        Cursor c=sqLiteDatabase.rawQuery(sqlQuery,null);
        c.moveToFirst();
        date=c.getLong(0);
        c.close();
        sqLiteDatabase.close();
        return date;
    }

    public long getLastID(){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        long lastId;
        String sqlQuery2="SELECT count(*) FROM "+TABLE_NAME;
        Cursor c = sqLiteDatabase.rawQuery (sqlQuery2,null);
        c.moveToFirst();
        lastId = c.getLong(0);
        c.close();
        sqLiteDatabase.close();
        return lastId;
    }




}
