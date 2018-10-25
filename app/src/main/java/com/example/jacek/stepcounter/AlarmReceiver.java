package com.example.jacek.stepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        NewDayService.setOkToResetFlag();
        Intent newDayServiceIntent = new Intent(context, NewDayService.class);
        context.startService(newDayServiceIntent);


    }
}
