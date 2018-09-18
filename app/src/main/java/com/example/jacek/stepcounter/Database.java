package com.example.jacek.stepcounter;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;


public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="History.db";
    public static final String TABLE_NAME="History";
    public static final String ID="ID";
    public static final String DATE="Date";
    public static final String STEPS="Steps";




    public Database (Context context,String name, SQLiteDatabase.CursorFactory factory, int version){
        super (context,name,factory,version);
    }
    public Database(Context context){
        super(context,DATABASE_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+DATE+" INTEGER, "+STEPS+" INTEGER )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    public boolean putData (int date, int steps){
        SQLiteDatabase sqLiteDatabase= this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(DATE, date);
        contentValues.put(STEPS, steps);
        long result= sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        if(result==-1) return false;
        else return true;

    }
}
