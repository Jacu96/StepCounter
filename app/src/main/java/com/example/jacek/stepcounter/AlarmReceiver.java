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


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "ALARM RECEIVER-wywolanie");
        //wprowadzamy do bazy danych liczbe krokow i date kiedy zostaly one zrobione
        //nowe podejscie
        NewDayService.setOkToResetFlag();
        Intent newDayServiceIntent = new Intent(context, NewDayService.class);
        context.startService(newDayServiceIntent);


    }
}
