package com.example.jacek.stepcounter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Calendar;
import android.content.Context;
import android.widget.Toast;

import java.util.Calendar;

public class History extends AppCompatActivity {
    private TextView tv_label;
    private TextView tv_steps;
    private Button previousButton;
    private Button nextButton;
    private Button backButton;
    private Calendar calendar=Calendar.getInstance();
    private Context context=this;
    private int steps;
    private long recordId;
    private long maxId;
    private static final long previousDay=-1;
    private static final long nextDay=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        previousButton=(Button) findViewById(R.id.previousButton);
        nextButton=(Button) findViewById(R.id.nextButton);
        backButton=(Button) findViewById(R.id.backButton);
        tv_steps = (TextView) findViewById(R.id.tv_steps);
        tv_label = (TextView) findViewById(R.id.tv_label);

        Database db=Database.getInstance(context);
        maxId=db.getLastID();
        if(maxId>0) {
            recordId = maxId;

            steps = db.getSteps(recordId);
            calendar.setTimeInMillis(db.getDate(recordId));
            db.close();

            tv_steps.setText(steps + "");
            tv_label.setText(getTimeLabel(calendar));
        }
        else db.close();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHistoricalStepValue(previousDay);

            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHistoricalStepValue(nextDay);
            }
        });

    }
    private String getTimeLabel(Calendar calendar){
        String time;
            time=calendar.get(Calendar.DAY_OF_MONTH)+"."+ 
                 calendar.get(Calendar.MONTH)+"."+
                 calendar.get(Calendar.YEAR)+" H:"+
                 calendar.get(Calendar.HOUR_OF_DAY)+" M:"+
                 calendar.get(Calendar.MINUTE)+" S:"+
                 calendar.get(Calendar.SECOND);
            return time;
    }
    private void showHistoricalStepValue(long nextOrPreviousDay){
        recordId+=nextOrPreviousDay;
        if(recordId<=maxId && recordId>0) {
            Database db=Database.getInstance(context);
            steps = db.getSteps(recordId);
            calendar.setTimeInMillis(db.getDate(recordId));
            db.close();
        }
        else {
            recordId-=nextOrPreviousDay;
            Toast.makeText(context,"No data from this day", Toast.LENGTH_SHORT).show();
        }
        tv_label.setText(getTimeLabel(calendar));
        tv_steps.setText(steps+"");
    }
}
