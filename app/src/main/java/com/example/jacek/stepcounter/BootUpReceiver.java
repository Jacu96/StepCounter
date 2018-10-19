package com.example.jacek.stepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())||Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.getAction())) {
            Toast.makeText(context,"shutdownReceiver boot completed",Toast.LENGTH_LONG).show();
            SensorListener.setBootFlag();

            Intent sensorListenerIntent = new Intent(context, SensorListener.class);
            context.startService(sensorListenerIntent);
        }
    }
}
