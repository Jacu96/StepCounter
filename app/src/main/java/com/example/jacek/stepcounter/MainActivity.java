package com.example.jacek.stepcounter;


import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import android.content.IntentFilter;


public class MainActivity extends AppCompatActivity {


    public static final String BROADCAST_ACTION = "com.example.jacek.stepcounter";
    private static float steps = 0;
    private final String TAG = "MainActivity";
    private UpdateView updateView;
    private TextView tv_steps;
    private TextView tv_info;
    private Button historyButton;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        historyButton=(Button) findViewById(R.id.historyButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_steps = (TextView) findViewById(R.id.tv_steps);
        tv_info = (TextView) findViewById(R.id.tv_info);

        tv_info.setText("Steps");

        Intent sensorListenerIntent = new Intent(this, SensorListener.class);
        Intent newDayServiceIntent= new Intent(this, NewDayService.class);
        startService(sensorListenerIntent);
        startService(newDayServiceIntent);

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, History.class);
                startActivity(myIntent);
            }
        });




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


    class UpdateView extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
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
