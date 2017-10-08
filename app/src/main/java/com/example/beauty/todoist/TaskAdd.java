package com.example.beauty.todoist;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.IDNA;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Locale;


public class TaskAdd extends AppCompatActivity {
    EditText SetTask;
    EditText SetDescription;
    TextView SetTime;
    TextView SetDate;

    public static final int ADD_SUCCESS = 1;
    private int Day, Year, Month, mins, hours, hour;
    static final int Date_Id = 0;
    static final int Time_Id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);
        SetTask = (EditText) findViewById(R.id.setTask);
        SetDescription = (EditText) findViewById(R.id.setDescription);
        SetTime = (TextView) findViewById(R.id.settime);
        SetDate = (TextView) findViewById(R.id.setdate);
        Calendar Cal = Calendar.getInstance();
        Day = Cal.get(Calendar.DAY_OF_MONTH);
        Year = Cal.get(Calendar.YEAR);
        Month = Cal.get(Calendar.MONTH);
        hours = Cal.get(Calendar.HOUR_OF_DAY);
        mins = Cal.get(Calendar.MINUTE);
        SetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(Date_Id);


            }
        });
        SetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(Time_Id);
            }
        });
    }

    public void display_DATE() {
        SetDate.setText(Day + "/" + (Month + 1) + "/" + Year + "");
    }

    public void display_TIME() {
        hour = hours % 12;
        if (hour == 0)
            hour = 12;
        SetTime.setText(String.format("%02d:%02d %s", hour, mins,hours<12?"am":"pm"));
    }

    private DatePickerDialog.OnDateSetListener mDate =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Month = month;
                    Year = year;
                    Day = dayOfMonth;
                    display_DATE();
                }
            };
    private TimePickerDialog.OnTimeSetListener mTime =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mins = minute;
                    hours = hourOfDay;
                    display_TIME();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case Date_Id:
                return new DatePickerDialog(this, mDate, Year, Month, Day);
            case Time_Id:
                return new TimePickerDialog(this,mTime,hours,mins,false);
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addtaskmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save) {
            Calendar calendar = Calendar.getInstance();
            Calendar current = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, Day);
            calendar.set(Calendar.MONTH, Month);
            calendar.set(Calendar.YEAR, Year);
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, mins);
            calendar.set(Calendar.SECOND,0);
            String task = SetTask.getText().toString();
            String description = SetDescription.getText().toString();
            String date = SetDate.getText().toString();
            String time = SetTime.getText().toString();
            if(task.matches("")){
                Toast.makeText(TaskAdd.this,"Enter the task title",Toast.LENGTH_LONG).show();
            }
            else if(description.matches("")){
                Toast.makeText(TaskAdd.this,"Enter the Task Description",Toast.LENGTH_LONG).show();
            }
             else if (calendar.compareTo(current) <= 0) {
                Toast.makeText(TaskAdd.this, "Date or Time is Expired", Toast.LENGTH_LONG).show();
            } else {

                TodoOpenHelper openHelper = TodoOpenHelper.getInstance(getApplicationContext());
                SQLiteDatabase db = openHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.TODO_TASK, task);
                contentValues.put(Contract.TODO_DESCRIPTION, description);
                contentValues.put(Contract.TODO_DATE, date);
                contentValues.put(Contract.TODO_TIME, time);
                long Id = db.insert(Contract.TODO_TABLE_NAME, null, contentValues);
                Intent result = new Intent();
                result.putExtra(constant.KEY_TODO_ID, Id);
                setResult(ADD_SUCCESS, result);
                finish();
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                alarmIntent.putExtra(constant.KEY_TASK,task);
                alarmIntent.putExtra(constant.KEY_DESCRIPTION,description);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) Id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Log.i("ALARM SET", "RING");


            }

        }
        return super.onOptionsItemSelected(item);
    }





}




