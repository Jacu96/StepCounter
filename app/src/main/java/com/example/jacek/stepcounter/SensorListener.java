package com.example.jacek.stepcounter;

import android.app.Service;
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


public class SensorListener extends Service implements SensorEventListener {

    private static float steps;
    private static float yesterdaySteps;
    private static float sinceBoot;
    private static boolean bootFlag = false;
    public SharedPreferences StepsPrefs;
    public SharedPreferences.Editor StepsPrefsEditor;
    private SensorManager sensorManager;

    public static void setBootFlag() {
        bootFlag = true;
    }
    
    public static void resetSteps() {
        yesterdaySteps = sinceBoot;
        steps = 0;
    }

    public static int getSteps() {
        return (int) steps;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            //Jeśli samplingPeriodIs= sensorManager.SENSOR_DELAY_NORMAL zamiast 0
            // to liczy po kilkanaście kroków na raz
            sensorManager.registerListener(this, countSensor, 0);
        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }

        StepsPrefs = getSharedPreferences("com.example.jacek.stepcounter", Context.MODE_PRIVATE);
        StepsPrefsEditor = StepsPrefs.edit();
        if (bootFlag) {
            yesterdaySteps = StepsPrefs.getFloat("yesterdaySteps", 0)
                    -StepsPrefs.getFloat("sinceBoot", 0);
            StepsPrefsEditor.putFloat("yesterdaySteps", yesterdaySteps);
            StepsPrefsEditor.putFloat("sinceBoot", 0);
            StepsPrefsEditor.commit();
            bootFlag = false;

        } else {
            yesterdaySteps = StepsPrefs.getFloat("yesterdaySteps", 0);
            sinceBoot = StepsPrefs.getFloat("sinceBoot", 0);
        }
        steps = sinceBoot - yesterdaySteps;
        sendStepsToMainActivity(steps);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        sinceBoot = event.values[0];
        steps = sinceBoot - yesterdaySteps;
        sendStepsToMainActivity(steps);

        StepsPrefsEditor.putFloat("yesterdaySteps", yesterdaySteps);
        StepsPrefsEditor.putFloat("sinceBoot", sinceBoot);
        StepsPrefsEditor.commit();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void sendStepsToMainActivity(float value) {
        Intent intent = new Intent();
        intent.setAction(MainActivity.BROADCAST_ACTION);
        intent.putExtra("data", value);
        sendBroadcast(intent);
    }

}
