package com.example.jacek.stepcounter;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Calendar;


public class NewDayService extends IntentService {

    private static boolean okToResetFlag = false;

    public NewDayService() {
        super(".NewDayService");
    }

    public static void setOkToResetFlag() {
        okToResetFlag = true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (okToResetFlag) {
            putDataInDatabase();
            SensorListener.resetSteps();
        }
        setAlarm(nextDayMidnight());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private long nextDayMidnight() {
        long date;
        //one day = 86400000ms
        long oneDay = 86400000;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 0);
        date = c.getTimeInMillis() + oneDay;
        return date;
    }

    private void setAlarm(long alarmTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }

    private void putDataInDatabase() {
        Database db = Database.getInstance(this);
        db.putData(System.currentTimeMillis(), SensorListener.getSteps());
        db.close();
    }


}
