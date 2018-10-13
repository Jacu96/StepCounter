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
    int i=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        previousButton=(Button) findViewById(R.id.previousButton);
        nextButton=(Button) findViewById(R.id.nextButton);
        backButton=(Button) findViewById(R.id.backButton);
        tv_steps = (TextView) findViewById(R.id.tv_steps);
        tv_label = (TextView) findViewById(R.id.tv_label);
        //TODO wstawic zamiast slowa date brana z bazy danych
        tv_label.setText("(DATA):");

        tv_steps.setText("steps");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("History.backButton ", "Button pressed");
                finish();
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database db=Database.getInstance(context);

                steps=db.getSteps(i);//ToDo liczba ktokow z bazy danych
                calendar.setTimeInMillis(db.getDate(i));
                tv_label.setText(calendar.get(Calendar.DAY_OF_MONTH)+"."+
                        calendar.get(Calendar.MONTH)+"."+
                        calendar.get(Calendar.YEAR)+" H:"+
                        calendar.get(Calendar.HOUR_OF_DAY)+" M:"+
                        calendar.get(Calendar.MINUTE)+" S:"+
                        calendar.get(Calendar.SECOND));
                i--;
                tv_steps.setText("steps");
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database db=Database.getInstance(context);

                steps=db.getSteps(i);//ToDo liczba ktokow z bazy danych
                calendar.setTimeInMillis(0/*TODO warosc z bazy danych*/);
                tv_label.setText(calendar.get(Calendar.DAY_OF_MONTH)+"."+
                                calendar.get(Calendar.MONTH)+"."+
                                calendar.get(Calendar.YEAR)+" H:"+
                                calendar.get(Calendar.HOUR_OF_DAY)+" M:"+
                                calendar.get(Calendar.MINUTE)+" S:"+
                                calendar.get(Calendar.SECOND));
                i++;
                tv_steps.setText(steps+"");
            }
        });

    }
}
