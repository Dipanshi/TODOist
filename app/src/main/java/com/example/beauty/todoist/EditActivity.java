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
    private int  day,yeaR,month,min,hour;
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
        Intent i=getIntent();
        hour=i.getIntExtra("hour",60);
        min=i.getIntExtra("min",60);
        day=i.getIntExtra("day",1);
        month=i.getIntExtra("month",1);
        yeaR=i.getIntExtra("year",1);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.YEAR,yeaR);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,min);
        Intent intent = getIntent();
        long id = intent.getLongExtra(constant.KEY_TODO_ID, -1L);
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
        editdate.setText(day+"/"+(month+1)+"/"+yeaR+"");
    }
    public void display_TIME(){
         int hours = hour % 12;
          if (hours == 0)
            hours = 12;
        edittime.setText(String.format("%02d:%02d %s", hours, min,hour<12?"am":"pm"));
    }
    private DatePickerDialog.OnDateSetListener mDate=
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int Month, int dayOfMonth) {
                    month = Month;
                    yeaR = year;
                    day = dayOfMonth;
                    display_DATE();
                }
            };
    private TimePickerDialog.OnTimeSetListener mTime=
            new TimePickerDialog.OnTimeSetListener(){
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    min=minute;
                    hour=hourOfDay;
                    display_TIME();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case Date_Id:
                return new DatePickerDialog(this,mDate,yeaR,month,day);
            case Time_Id:
                return new TimePickerDialog(this,mTime,hour,min,false);
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
            Calendar current = Calendar.getInstance();
            current.get(Calendar.DAY_OF_MONTH);
            current.get(Calendar.YEAR);
            current.get(Calendar.MONTH);
            current.get(Calendar.HOUR_OF_DAY);
            current.get(Calendar.MINUTE);
            Calendar cal= Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.YEAR, yeaR);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);
            cal.set(Calendar.SECOND,0);
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
            else if (cal.compareTo(current) <= 0) {
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
                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                Log.i("ALARM SET", "RING");




            }


        }

        return super.onOptionsItemSelected(item);
    }


}


