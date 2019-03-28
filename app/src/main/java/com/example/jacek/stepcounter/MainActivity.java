package com.example.jacek.stepcounter;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class MainActivity extends AppCompatActivity {

    public static final String BROADCAST_ACTION = "com.example.jacek.stepcounter";
    public static boolean stopServices = false;
    private static float steps;// = 0;
    private TextView tv_info;
    private Button historyButton;
    private ProgressBar progressBar;
    float acccel[];
    //private File accelerations=new File("/storage/emulated/0/counter/accelerations.txt");
    Context context = this;
    OutputStreamWriter outputStreamWriter;
    //  private UpdateView updateView;
    private TextView tv_steps;
    private Button stopButton;
    private Button startButtonWalk;
    private Button startButtonRun;
    private TextView textView0;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private UpdateView1 updateView1;
    private float x;
    private float y;
    private float z;
    private float x2;
    private float y2;
    private float z2;
    private int runNumber = 0;
    private int walkNumber = 0;
    private String runOrWalk;
    private int runOrWalkNumber;
    private String accelerationAndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        historyButton = findViewById(R.id.historyButton);
        progressBar = findViewById(R.id.progressBar);
        tv_steps = findViewById(R.id.tv_steps);
        tv_info = findViewById(R.id.tv_info);

        tv_info.setText("Steps");

        textView0 = findViewById(R.id.tv_info0);
        textView1 = findViewById(R.id.tv_info1);
        textView2 = findViewById(R.id.tv_info2);
        textView3 = findViewById(R.id.tv_info3);
        textView4 = findViewById(R.id.tv_info4);
        textView5 = findViewById(R.id.tv_info5);
        startButtonWalk = findViewById(R.id.startButtonWalk);
        stopButton = findViewById(R.id.stopButton);
        startButtonRun = findViewById(R.id.startButtonRun);


        final Intent accelaerationSensorIntent = new Intent(this, AccelerationSensor.class);
        startService(accelaerationSensorIntent);

        final Intent gravitySensorIntent = new Intent(this, GravitySensor.class);
        startService(gravitySensorIntent);





        Intent sensorListenerIntent = new Intent(this, SensorListener.class);
        Intent newDayServiceIntent= new Intent(this, NewDayService.class);
        stopService(sensorListenerIntent);
        stopService(newDayServiceIntent);
        startService(sensorListenerIntent);
        startService(newDayServiceIntent);


        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, History.class);
                startActivity(myIntent);
            }
        });

        startButtonWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walkNumber++;
                runOrWalkNumber = walkNumber;
                runOrWalk = "walk";
                stopServices = false;
                startService(gravitySensorIntent);
                startService(accelaerationSensorIntent);
                Log.d("Start", "stopServies=" + stopServices);
                //writeToFile("start Walk");
            }
        });
        startButtonRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runNumber++;
                runOrWalkNumber = runNumber;
                runOrWalk = "run";
                stopServices = false;
                startService(gravitySensorIntent);
                startService(accelaerationSensorIntent);
                Log.d("Start", "stopServies=" + stopServices);
                //writeToFile("start Run");
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopServices = true;
                stopService(gravitySensorIntent);
                stopService(accelaerationSensorIntent);
                Log.d("Stop", "stopServies=" + stopServices);
                //writeToFile("stop");
            }
        });


        /*stopButtonRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopServices=true;
                stopService(gravitySensorIntent);
                stopService(accelaerationSensorIntent);
                Log.d("Stop", "stopServies="+stopServices);
                writeToFile("stop Run");

            }
        });*/




        /*IntentFilter filter = new IntentFilter();

        updateView = new UpdateView();
        filter.addAction(BROADCAST_ACTION);
        registerReceiver(updateView, filter);*/

        IntentFilter filter1 = new IntentFilter();

        updateView1 = new UpdateView1();
        filter1.addAction(BROADCAST_ACTION);
        registerReceiver(updateView1, filter1);



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
        unregisterReceiver(updateView1);

    }


    /*class UpdateView extends BroadcastReceiver {
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

    }*/

    float round(float value, int placesAfterComma) {
        value = value * (10 ^ placesAfterComma);
        int cut = (int) value;
        value = (float) cut / (10 ^ placesAfterComma);
        return value;
    }

    float[] accelrationVectorProjectionOntoGravityVector
            (float accelerationX, float accelerationY, float accelerationZ,
             float gravityX, float gravityY, float gravityZ) {
        float projected[] = {
                (gravityX * (accelerationX * gravityX + accelerationY * gravityY + accelerationZ * gravityZ)) / (
                        square(gravityX) + square(gravityY) + square(gravityZ)),
                (gravityY * (accelerationX * gravityX + accelerationY * gravityY + accelerationZ * gravityZ)) / (
                        square(gravityX) + square(gravityY) + square(gravityZ)),
                (gravityZ * (accelerationX * gravityX + accelerationY * gravityY + accelerationZ * gravityZ)) / (
                        square(gravityX) + square(gravityY) + square(gravityZ))};


        return projected;
    }

    float magnitudeOfVerticalAcceletation(float[] vector) {
        return (float) (Math.sqrt(square(vector[0]) + square(vector[1]) + square(vector[2]))) - (float) 9.81;
    }

    float square(float x) {
        return x * x;
    }

    void writeToFile(String text) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter("/storage/emulated/0/counter/" + runOrWalk + runOrWalkNumber + ".txt", true);
            //fw = new FileWriter("/storage/emulated/0/counter/accelerations.txt", true);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            pw.println(text + "\n");
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                pw.close();
                bw.close();
                fw.close();
            } catch (java.io.IOException io) {

            }
        }
    }

    class UpdateView1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getExtras().getInt("whichService")) {
                case 1:
                    try {
                        Log.d("nowy updateview", "acceleration");
                        x = intent.getExtras().getFloat("x");
                        y = intent.getExtras().getFloat("y");
                        z = intent.getExtras().getFloat("z");

                        textView0.setText("" + round(x, 2));
                        textView1.setText("" + round(y, 2));
                        textView2.setText("" + round(z, 2));

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                case 2:
                    try {

                        Log.d("nowy updateview", "gravity");

                        x2 = intent.getExtras().getFloat("x2");
                        y2 = intent.getExtras().getFloat("y2");
                        z2 = intent.getExtras().getFloat("z2");

                        textView3.setText("" + round(x2, 2));
                        textView4.setText("" + round(y2, 2));
                        textView5.setText("" + round(z2, 2));

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                case 3:

                    try {
                        Log.d("upw step", "trajuje");

                        if (steps != intent.getExtras().getFloat("data")) {
                            Log.d("upw step", "powinien ODEBRAC " + intent.getExtras().getFloat("data"));
                            steps = intent.getExtras().getFloat("data");
                            tv_steps.setText("" + steps);
                            //tv_steps.setText("" +intent.getExtras().getFloat("data") );
                            //Zakładamy 1000 kroków więc steps/10
                            progressBar.setProgress((int) steps / 10);

                            Log.d("upw step", "powinien ODEBRAC " + steps);

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
            }


            acccel = accelrationVectorProjectionOntoGravityVector(x, y, z, x2, y2, z2);
            float verticalAcceleration = magnitudeOfVerticalAcceletation(acccel);
            //tv_steps.setText(verticalAcceleration+"");
            accelerationAndTime = verticalAcceleration + "   " + System.currentTimeMillis();

            //tu sie zapisuje do pliku
            writeToFile(accelerationAndTime);


        }

    }


}


