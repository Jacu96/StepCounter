package com.example.jacek.stepcounter;


import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import android.content.IntentFilter;


public class MainActivity extends AppCompatActivity {


    public static final String BROADCAST_ACTION = "com.example.jacek.stepcounter";
    static float steps = 0;
    private final String TAG = "MainActivity";
    UpdateView updateView;
    private TextView tv_steps;
    private TextView tv_info;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_steps = (TextView) findViewById(R.id.tv_steps);
        tv_info = (TextView) findViewById(R.id.tv_info);
        tv_info.setText("Dzisiaj zrobiłeś już tyle kroków:");


        tv_steps.setText(steps + "");

        Intent sensorListenerIntent = new Intent(this, SensorListener.class);
        startService(sensorListenerIntent);

        IntentFilter filter = new IntentFilter();

        updateView = new UpdateView();
        filter.addAction(BROADCAST_ACTION);
        registerReceiver(updateView, filter);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateView);
    }

    public boolean putData2(int date, int steps){
        database.putData(,this.steps)
    }


    class UpdateView extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                //Ta linijka wyświetla na komputerze "UpdateView.onReceive() called"
                Log.d(TAG, "UpdateView.onReceive() called");
                steps = intent.getExtras().getFloat("data");
                tv_steps.setText("" + steps);
                //Zakładamy 1000 kroków więc steps/10
                progressBar.setProgress((int) steps / 10);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }
}
