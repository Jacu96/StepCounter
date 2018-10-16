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

    private static float steps;
    private static float yesterdaySteps;
    private static float sinceBoot;
    private static boolean okToResetFlag;
    private static boolean bootFlag = false;
    private final String TAG = "SensorListener";
    public SharedPreferences StepsPrefs;
    public SharedPreferences.Editor StepsPrefsEditor;
    private SensorManager sensorManager;
    private Calendar calendar = Calendar.getInstance();


    public SensorListener() {
        super("test-service");
    }

    public static void setBootFlag() {
        bootFlag = true;
    }

    /**
     * Metoda jest wywoływana przez AlarmReceiver
     * Służy do resetowania licznika kroków
     */
    public static void resetSteps() {
        Log.d("SensorListener.restetS", "RESET " + yesterdaySteps);

        if (okToResetFlag) {
            yesterdaySteps = sinceBoot;
            steps = 0;
            Log.d("SensorListener.restetS", "i zresetowal-YESTERDAY=" + yesterdaySteps);
            Log.d("SensorListener.restetS", "Steps=" + steps);
            Log.d("SensorListener.restetS", "SinceBoot=" + sinceBoot);

        } else {
            Log.d("SensorListener.restetS", "nie zresetował-YESTERDAY=" + yesterdaySteps);
            Log.d("SensorListener.restetS", "Steps=" + steps);
            Log.d("SensorListener.restetS", "SinceBoot=" + sinceBoot);
            okToResetFlag = true;
        }
    }

    public static int getSteps() {
        return (int) steps;
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
        okToResetFlag = false;
        startAlarm(calendar);

        StepsPrefs = getSharedPreferences("com.example.jacek.stepcounter", Context.MODE_PRIVATE);
        StepsPrefsEditor = StepsPrefs.edit();

        if (bootFlag) {
            Database db = Database.getInstance(this);
            long i = db.getLastID();
            yesterdaySteps = StepsPrefs.getFloat("yesterdaySteps", 0)-StepsPrefs.getFloat("sinceBoot", 0);;
            db.close();
            //pewnie potem on wczyta zle z shared preferences wiec bedzie trzeba zrobic
            StepsPrefsEditor.putFloat("yesterdaySteps", yesterdaySteps);
            StepsPrefsEditor.putFloat("sinceBoot", 0);
            StepsPrefsEditor.commit();
            Toast.makeText(this, "ZADZIALAL czujnik resetu", Toast.LENGTH_LONG).show();

            bootFlag = false;
            Toast.makeText(this, "" + yesterdaySteps, Toast.LENGTH_LONG).show();

        } else {
            yesterdaySteps = StepsPrefs.getFloat("yesterdaySteps", 0);
            sinceBoot = StepsPrefs.getFloat("sinceBoot", 0);
        }
        
        steps = sinceBoot - yesterdaySteps;
        sendFloatToMainActivity(steps);
        Intent stepsCurrentValueIntent = new Intent();
        stepsCurrentValueIntent.setAction(MainActivity.BROADCAST_ACTION);
        stepsCurrentValueIntent.putExtra("data", sinceBoot - yesterdaySteps);
        sendBroadcast(stepsCurrentValueIntent);


        Log.d(TAG + ".onCreate", "yesterdaySteps=" + yesterdaySteps);
        Log.d(TAG + ".onCreate", "Steps=" + steps);
        Log.d(TAG + ".onCreate", "SinceBoot=" + sinceBoot);

    }

    /**
     * Metoda jest odpowiedzialna za wywołanie alarmu codziennie o godzinie c (północ)
     *
     * @param c
     */
    private void startAlarm(Calendar c) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);

        //Hipoteza: requestcode 1 to on prosi o wyslanie i moze w wolnej chwili wysle a 0 to ze wyslac i chuj
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sinceBoot = event.values[0];
        steps = sinceBoot - yesterdaySteps;
        /*Intent intent = new Intent();
        intent.setAction(MainActivity.BROADCAST_ACTION);
        intent.putExtra("data", steps);
        sendBroadcast(intent);*/
        //zastapimy metoda sendFloatToMainActivity
        sendFloatToMainActivity(steps);
        Log.d("ONSENSORCHANGED", "yesterdaySteps=" + yesterdaySteps);
        Log.d("ONSENSORCHANGED", "Steps=" + steps);
        Log.d("ONSENSORCHANGED", "SinceBoot=" + sinceBoot);


        //pro restarcie appki nadal pamięta kroki dzięki temu shared prefereces
        // bycmoze wystarczy to robic tylko w on destroy
        StepsPrefsEditor.putFloat("yesterdaySteps", yesterdaySteps);
        StepsPrefsEditor.putFloat("sinceBoot", sinceBoot);
        StepsPrefsEditor.commit();
        //sinceBootStepsSPEditor.putFloat("sinceBoot",sinceBoot);
        // sinceBootStepsSPEditor.commit();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StepsPrefsEditor.putFloat("yesterdaySteps", yesterdaySteps);
        StepsPrefsEditor.putFloat("sinceBoot", sinceBoot);
        StepsPrefsEditor.commit();

    }
    public void sendFloatToMainActivity(float value){
        Intent intent = new Intent();
        intent.setAction(MainActivity.BROADCAST_ACTION);
        intent.putExtra("data", value);
        sendBroadcast(intent);
    }

}
