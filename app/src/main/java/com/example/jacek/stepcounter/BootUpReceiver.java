package com.example.jacek.stepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_LOCKED_BOOT_COMPLETED)) {
            SensorListener.setBootFlag();
            Intent sensorListenerIntent = new Intent(context, SensorListener.class);
            Intent newDayServiceIntent = new Intent(context, NewDayService.class);
            context.startService(sensorListenerIntent);
            context.startService(newDayServiceIntent);
        }
    }
}
