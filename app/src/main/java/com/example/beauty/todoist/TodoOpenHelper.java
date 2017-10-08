package com.example.beauty.todoist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by BEAUTY on 9/22/2017.
 */

class TodoOpenHelper extends SQLiteOpenHelper {

    private static TodoOpenHelper instance;


    public static TodoOpenHelper getInstance(Context context) {
        if(instance == null){
            instance = new TodoOpenHelper(context);
        }
        return instance;
    }

    private TodoOpenHelper(Context context) {
        super(context, "todo_db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        String query = "CREATE TABLE " + Contract.TODO_TABLE_NAME + " ( " +
                Contract.TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.TODO_TASK + " TEXT, " + Contract.TODO_DESCRIPTION+ " TEXT, " + Contract.TODO_DATE + " TEXT, " +
                Contract.TODO_TIME + " TEXT)";


        db.execSQL(query);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
