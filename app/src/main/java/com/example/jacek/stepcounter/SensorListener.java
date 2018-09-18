package com.example.jacek.stepcounter;

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


public class SensorListener extends IntentService implements SensorEventListener {

    private final String TAG = "SensorListener";
    private static float steps;
    private static float yesterdaySteps;
    private static float sinceBoot = 0;
    private SensorManager sensorManager;
    private Calendar calendar = Calendar.getInstance();
    SharedPreferences stepsSP;
    SharedPreferences.Editor stepsSPEditor;
    private static boolean startFlag;
    Database db;


    public SensorListener() {
        super("test-service");
    }

    /**
     * Metoda jest wywoływana przez AlarmReceiver
     * Służy do resetowania licznika kroków
     */
    public static void resetSteps() {
        Log.d("resetSteps", "RESET KURWA"+yesterdaySteps);

        if(startFlag) {
            yesterdaySteps = sinceBoot;
            steps = 0;
            Log.d("resetSteps", "i zresetowal" + yesterdaySteps);
        }
        else {
            Log.d("resetSteps", "nie zresetował" + yesterdaySteps);
            startFlag=true;
        }


    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
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

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);

        startFlag=false;
        startAlarm(calendar);

        stepsSP=getSharedPreferences("com.example.jacek.stepcounter",Context.MODE_PRIVATE);
        stepsSPEditor=stepsSP.edit();
        yesterdaySteps=stepsSP.getFloat("yesterdaySteps",0);

    }

    /**
     * Metoda jest odpowiedzialna za wywołanie alarmu codziennie o godzinie c (północ)
     *
     * @param c
     */
    private void startAlarm(Calendar c) {
        Log.d(TAG, "start alarm"+yesterdaySteps);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        //Hipoteza: requestcode 1 to on prosi o wyslanie i moze w wolnej chwili wysle a 0 to ze wyslac i chuj
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.d(TAG, "start alarm"+yesterdaySteps);


    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sinceBoot = event.values[0];
        steps = event.values[0] - yesterdaySteps;
        Log.d(TAG, "yesterday="+yesterdaySteps);
        Intent intent = new Intent();
        intent.setAction(MainActivity.BROADCAST_ACTION);
        intent.putExtra("data", steps);
        sendBroadcast(intent);

    //pro restarcie appki nadal pamięta kroki dzięki temu shared prefereces
        stepsSPEditor.putFloat("yesterdaySteps",yesterdaySteps);
        stepsSPEditor.commit();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
