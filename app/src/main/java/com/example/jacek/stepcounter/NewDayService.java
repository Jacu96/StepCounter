package com.example.jacek.stepcounter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.support.annotation.Nullable;
import java.util.Calendar;


public class NewDayService extends Service {

    private static boolean okToResetFlag = false;


public static void setOkToResetFlag(){
    okToResetFlag=true;
}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(okToResetFlag) {
            putDataInDatabase();
            SensorListener.resetSteps();
        }
        setAlarm(nextDayMidnight());
        stopSelf();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private long nextDayMidnight(){
        long date;
        //one day = 86400000ms
        long oneDay=86400000;
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 0);

        date=c.getTimeInMillis()+oneDay;
        return date;
    }

    private void setAlarm(long alarmTime){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        alarmManager.cancel(pendingIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,alarmTime, pendingIntent);
    }

    private void putDataInDatabase(){
        Database db = Database.getInstance(this);
        db.putData(System.currentTimeMillis(), SensorListener.getSteps());
        db.close();
    }














}
