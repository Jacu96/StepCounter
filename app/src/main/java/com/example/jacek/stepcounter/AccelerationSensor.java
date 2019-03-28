package com.example.jacek.stepcounter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class AccelerationSensor extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private float x;
    private float y;
    private float z;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service", "on create");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        x = sensorEvent.values[0];
        y = sensorEvent.values[1];
        z = sensorEvent.values[2];
        sendStepsToMainActivity(x, y, z);

        Log.d("Acceleratio", "onSensorChanged");
        if (MainActivity.stopServices) {
            sensorManager.unregisterListener(this);
            stopSelf();
            onDestroy();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void sendStepsToMainActivity(float x, float y, float z) {
        Intent intent = new Intent();
        intent.setAction(MainActivity.BROADCAST_ACTION);
        intent.putExtra("x", x);
        intent.putExtra("y", y);
        intent.putExtra("z", z);
        intent.putExtra("whichService", 1);
        sendBroadcast(intent);
    }

}

