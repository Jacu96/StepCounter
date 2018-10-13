package com.example.jacek.stepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.app.IntentService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.support.annotation.Nullable;
import java.util.Calendar;


public class AlarmReceiver extends BroadcastReceiver {
    private final String TAG = "AlarmReceiver";
    private SharedPreferences.Editor stepsSPEditor;



    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "ALARM RECEIVER-wywolanie");
        //wprowadzamy do bazy danych liczbe krokow i date kiedy zostaly one zrobione

        Database db=Database.getInstance(context);
        db.putData(System.currentTimeMillis(),SensorListener.getSteps());
        db.close();

        SensorListener.resetSteps();


        //stepsSP=getSharedPreferences("com.example.jacek.stepcounter",Context.MODE_PRIVATE);
        //stepsSPEditor=stepsSP.edit();
        //yesterdaySteps=stepsSP.getFloat("yesterdaySteps",0);



    }
}
