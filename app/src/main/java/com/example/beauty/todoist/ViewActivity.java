package com.example.beauty.todoist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

import static android.R.attr.data;
import static android.R.attr.id;
import static com.example.beauty.todoist.constant.KEY_DATE;
import static com.example.beauty.todoist.constant.KEY_DESCRIPTION;
import static com.example.beauty.todoist.constant.KEY_TASK;
import static com.example.beauty.todoist.constant.KEY_TIME;
import static com.example.beauty.todoist.constant.KEY_TODO_ID;

public class ViewActivity extends AppCompatActivity {
    TextView veiwTask;
    TextView veiwDescripion;
    TextView veiwTime;
    TextView veiwDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        veiwTask = (TextView) findViewById(R.id.viewtask);
        veiwDescripion = (TextView) findViewById(R.id.viewdescription);
        veiwTime = (TextView) findViewById(R.id.time);
        veiwDate = (TextView) findViewById(R.id.date);
        Intent i=getIntent();
        long id = i.getLongExtra(constant.KEY_TODO_ID,-1L);
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
                veiwTask.setText(task);
                veiwDescripion.setText(description);
                veiwDate.setText(date);
                veiwTime.setText(time);

            }

        }

        }
    }










