package com.example.beauty.todoist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.beauty.todoist.constant.KEY_TODO_ID;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Tasklist> Tasks;
    CustomAdapter adapter;
    String task;
    String description;
    String time;
    String date;
    int Day,Month,Hour,mins,year;
    public final static int REQUEST_ADD = 2;
    public final static int REQUEST_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        Tasks = new ArrayList<>();
        TodoOpenHelper openHelper = TodoOpenHelper.getInstance(getApplicationContext());
        final SQLiteDatabase db = openHelper.getReadableDatabase();

        Cursor cursor = db.query(Contract.TODO_TABLE_NAME, null, null, null, null, null, null);


        while (cursor.moveToNext()) {
            String task = cursor.getString(cursor.getColumnIndex(Contract.TODO_TASK));
            String description = cursor.getString(cursor.getColumnIndex(Contract.TODO_DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndex(Contract.TODO_DATE));
            String time = cursor.getString(cursor.getColumnIndex(Contract.TODO_TIME));
            int id = cursor.getInt(cursor.getColumnIndex(Contract.TODO_ID));
            Tasklist Todoitem = new Tasklist(task, description, date, time, id);
            Tasks.add(Todoitem);


        }

        cursor.close();


        adapter = new CustomAdapter(this, Tasks, new CustomAdapter.Deleteonclicklistener() {
            @Override
            public void onDeleteClicked(final int position, View v) {
                final long id = Tasks.get(position).getId();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete this task?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TodoOpenHelper openHelper = TodoOpenHelper.getInstance(getApplicationContext());
                        SQLiteDatabase db = openHelper.getWritableDatabase();
                        db.delete(Contract.TODO_TABLE_NAME, Contract.TODO_ID + " =? ", new String[]{id + ""});
                        Tasks.remove(Tasks.get(position));
                        adapter.notifyDataSetChanged();
                        alarmcancel();
                    }

                    public void alarmcancel() {
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        Intent alarmIntent = new Intent(MainActivity.this,AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,(int)id,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pendingIntent);

                    }

                });
                builder.setNegativeButton("No", null);

                AlertDialog dialog = builder.create();
                dialog.show();

            }

        }, new CustomAdapter.Editonclicklistener() {
            @Override
            public void onEditClicked(int position, View v) {
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                final long Id = Tasks.get(position).getId();
                i.putExtra(constant.KEY_TODO_ID, Id);
                i.putExtra("hour",Hour);
                i.putExtra("min",mins);
                i.putExtra("month",Month);
                i.putExtra("day",Day);
                i.putExtra("year",year);
                startActivityForResult(i, REQUEST_EDIT);


            }
        }, new CustomAdapter.Viewonclicklistener() {
            @Override
            public void onViewClicked(int position, View v) {
                Intent i = new Intent(MainActivity.this, ViewActivity.class);
                final long Id = Tasks.get(position).getId();
                i.putExtra(constant.KEY_TODO_ID, Id);
                startActivity(i);

            }
        });

        {

            listView.setAdapter(adapter);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.add){
            Intent add=new Intent(this,TaskAdd.class);
            startActivityForResult(add,REQUEST_ADD);
                   }
         else if (id == R.id.call){

            Intent call = new Intent();
            call.setAction(Intent.ACTION_DIAL);
            call.setData(Uri.parse("tel:9999999"));
            startActivity(call);
        } else if (id == R.id.website) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            String url = "http://google.com";
            intent.setData(Uri.parse(url));
            startActivity(intent);

        } else if (id == R.id.sms) {

            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, "DOWNLOAD our app");
            Intent chooser = Intent.createChooser(share, "Share App");
            startActivity(chooser);
        } else if (id == R.id.mail) {
            Intent feedback = new Intent();
            feedback.setAction(Intent.ACTION_SENDTO);
            feedback.setData(Uri.parse("mailto:dipanshitewari.25@gmail.com"));
            feedback.putExtra(Intent.EXTRA_TEXT, "FEEDBACK");
            if (feedback.resolveActivity(getPackageManager()) != null) {
                startActivity(feedback);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD) {
            if (resultCode == TaskAdd.ADD_SUCCESS) {
                long id = data.getLongExtra(KEY_TODO_ID, -1L);
                Day=data.getIntExtra("day",1);
                year=data.getIntExtra("year",1);
                Month= data.getIntExtra("month",1);
                Hour =data.getIntExtra("hour",1);
                mins =data.getIntExtra("min",1);
                if (id > -1) {
                    TodoOpenHelper openHelper = TodoOpenHelper.getInstance(getApplicationContext());
                    SQLiteDatabase db = openHelper.getReadableDatabase();


                    Cursor cursor = db.query(Contract.TODO_TABLE_NAME, null,
                            Contract.TODO_ID + " = ?", new String[]{id + ""}
                            , null, null, null);
                    if (cursor.moveToFirst()) {
                        task = cursor.getString(cursor.getColumnIndex(Contract.TODO_TASK));
                        description = cursor.getString(cursor.getColumnIndex(Contract.TODO_DESCRIPTION));
                        date = cursor.getString(cursor.getColumnIndex(Contract.TODO_DATE));
                        time = cursor.getString(cursor.getColumnIndex(Contract.TODO_TIME));

                        Tasklist Todoitem = new Tasklist(task, description, date, time, (int) id);
                        Tasks.add(Todoitem);

                    }

                }

            }
        }
            if (requestCode == REQUEST_EDIT) {
                if (resultCode == EditActivity.EDIT_SUCESS) {
                    long id = data.getLongExtra(KEY_TODO_ID, -1L);
                    if (id > -1) {

                        TodoOpenHelper openHelper = TodoOpenHelper.getInstance(getApplicationContext());
                        final SQLiteDatabase db = openHelper.getReadableDatabase();

                        Cursor cursor = db.query(Contract.TODO_TABLE_NAME, null, null, null, null, null, null);


                        Tasks.clear();

                        while (cursor.moveToNext()) {
                            String task = cursor.getString(cursor.getColumnIndex(Contract.TODO_TASK));
                            String description = cursor.getString(cursor.getColumnIndex(Contract.TODO_DESCRIPTION));
                            String date = cursor.getString(cursor.getColumnIndex(Contract.TODO_DATE));
                            String time = cursor.getString(cursor.getColumnIndex(Contract.TODO_TIME));
                            int idd = cursor.getInt(cursor.getColumnIndex(Contract.TODO_ID));
                            Tasklist Todoitem = new Tasklist(task, description, date, time, idd);
                            Tasks.add(Todoitem);


                        }

                        adapter.notifyDataSetChanged();

                    }


                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }








