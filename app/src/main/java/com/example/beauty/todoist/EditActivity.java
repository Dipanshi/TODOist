package com.example.beauty.todoist;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
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

public class EditActivity extends AppCompatActivity {
    EditText edittask;
    EditText editdescription;
    TextView edittime;
    TextView editdate;
    private int  Day,Year,Month,mins,hours;
    static final int Date_Id=0;
    static final int Time_Id=1;
    public static final int EDIT_SUCESS = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        edittask = (EditText) findViewById(R.id.setTask);
        editdescription = (EditText) findViewById(R.id.setDescription);
        edittime = (TextView) findViewById(R.id.settime);
        editdate = (TextView) findViewById(R.id.setdate);
        Day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        Year = Calendar.getInstance().get(Calendar.YEAR);
        Month = Calendar.getInstance().get(Calendar.MONTH);
        mins = Calendar.getInstance().get(Calendar.MINUTE);
        hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        Intent i = getIntent();
        long id = i.getLongExtra(constant.KEY_TODO_ID, -1L);
        if (id > -1) {
            TodoOpenHelper openHelper = TodoOpenHelper.getInstance(getApplicationContext());
            SQLiteDatabase db = openHelper.getReadableDatabase();


            Cursor cursor = db.query(Contract.TODO_TABLE_NAME, null,
                    Contract.TODO_ID + " = ?", new String[]{id + ""}
                    , null, null, null);
            if (cursor.moveToFirst()) {
                String task = cursor.getString(cursor.getColumnIndex(Contract.TODO_TASK));
                String description = cursor.getString(cursor.getColumnIndex(Contract.TODO_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndex(Contract.TODO_DATE));
                String time = cursor.getString(cursor.getColumnIndex(Contract.TODO_TIME));
                editdate.setText(date);
                edittime.setText(time);
                edittask.setText(task);
                editdescription.setText(description);
            }
        }
                editdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(Date_Id);

                    }
                });
                edittime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(Time_Id);
                    }
                });
            }
    public void display_DATE(){
        editdate.setText(Day+"/"+(Month+1)+"/"+Year+"");
    }
    public void display_TIME(){
         int hour = hours % 12;
          if (hour == 0)
            hour = 12;
        edittime.setText(String.format("%02d:%02d %s", hour, mins,hours<12?"am":"pm"));
    }
    private DatePickerDialog.OnDateSetListener mDate=
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Month = month;
                    Year = year;
                    Day = dayOfMonth;
                    display_DATE();
                }
            };
    private TimePickerDialog.OnTimeSetListener mTime=
            new TimePickerDialog.OnTimeSetListener(){
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mins=minute;
                    hours=hourOfDay;
                    display_TIME();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case Date_Id:
                return new DatePickerDialog(this,mDate,Year,Month,Day);
            case Time_Id:
                return new TimePickerDialog(this,mTime,hours,mins,false);
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.editsave){
            Calendar calendar = Calendar.getInstance();
            Calendar current = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, Day);
            calendar.set(Calendar.MONTH, Month);
            calendar.set(Calendar.YEAR, Year);
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, mins);
            calendar.set(Calendar.SECOND,0);
            String Task= edittask.getText().toString();
            String Description = editdescription.getText().toString();
            String Date=editdate.getText().toString();
            String Time=edittime.getText().toString();
            if(Task.matches("")){
                Toast.makeText(this,"Enter the task title",Toast.LENGTH_LONG).show();
            }
            else if(Description.matches("")){
                Toast.makeText(this,"Enter the Task Description",Toast.LENGTH_LONG).show();
            }
            else if (calendar.compareTo(current) <= 0) {
                Toast.makeText(this, "Date or Time is Expired", Toast.LENGTH_LONG).show();
            } else {
                Intent i=getIntent();
                long ID = i.getLongExtra(constant.KEY_TODO_ID, -1L);
                TodoOpenHelper openHelper = TodoOpenHelper.getInstance(getApplicationContext());
                SQLiteDatabase db = openHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.TODO_TASK,Task);
                contentValues.put(Contract.TODO_DESCRIPTION,Description);
                contentValues.put(Contract.TODO_DATE,Date);
                contentValues.put(Contract.TODO_TIME,Time);
                db.update(Contract.TODO_TABLE_NAME,contentValues,Contract.TODO_ID + " = ? ",new String[]{ID +""});
                Intent result = new Intent();
                result.putExtra(constant.KEY_TODO_ID,ID);
                setResult(EDIT_SUCESS, result);
                finish();
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                alarmIntent.putExtra(constant.KEY_TASK,Task);
                alarmIntent.putExtra(constant.KEY_DESCRIPTION,Description);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,(int)ID,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
                Log.i("work","alarm cancel");
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Log.i("ALARM SET", "RING");




            }


        }

        return super.onOptionsItemSelected(item);
    }


}


