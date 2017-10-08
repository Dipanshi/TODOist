package com.example.beauty.todoist;

import java.io.Serializable;

/**
 * Created by BEAUTY on 9/19/2017.
 */

 public class Tasklist implements Serializable {
    private String task;
    private String description;
    private String Time;
    private String Date;
    private int id;



    public Tasklist(String task,String description,String Date,String Time,int id){
        this.task=task;
        this.description=description;
        this.Time=Time;
        this.Date=Date;
        this.id=id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
