package com.example.beauty.todoist;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by BEAUTY on 9/19/2017.
 */

 public class CustomAdapter extends ArrayAdapter<Tasklist> {
    Context mContext;
    ArrayList<Tasklist> mTasks;
    Deleteonclicklistener mDeleteclicklistener;
    Editonclicklistener mEditclicklistener;
    Viewonclicklistener mViewclicklistener;

    public CustomAdapter(@NonNull Context context, ArrayList<Tasklist> Tasks,Deleteonclicklistener deleteonclicklistener,Editonclicklistener editonclicklistener,Viewonclicklistener viewonclicklistener) {
        super(context, 0);
        mContext = context;
        mTasks = Tasks;
        mDeleteclicklistener=deleteonclicklistener;
        mEditclicklistener=editonclicklistener;
        mViewclicklistener=viewonclicklistener;
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewholder;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.tasklist_layout,null);
            viewholder = new ViewHolder();
            TextView Task=(TextView)convertView.findViewById(R.id.taskname);
            TextView Description=(TextView)convertView.findViewById(R.id.description);
            TextView Date=(TextView)convertView.findViewById(R.id.date);
            TextView Time=(TextView)convertView.findViewById(R.id.time);
            Button Delete=(Button)convertView.findViewById(R.id.delete);
            Button View=(Button)convertView.findViewById(R.id.view);
            Button Edit=(Button)convertView.findViewById(R.id.edit);
            viewholder.Date=Date;
            viewholder.Time=Time;
            viewholder.Description=Description;
            viewholder.Edit=Edit;
            viewholder.Task=Task;
            viewholder.View=View;
            viewholder.Delete=Delete;
            convertView.setTag(viewholder);
        }
        viewholder=(ViewHolder)convertView.getTag();
        viewholder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteclicklistener.onDeleteClicked(position,v);


            }
        });
        viewholder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mEditclicklistener.onEditClicked(position,v);

            }
        });
        viewholder.View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              mViewclicklistener.onViewClicked(position,v);
            }
        });
        Tasklist Task =mTasks.get(position);
        viewholder.Task.setText(Task.getTask());
        viewholder.Description.setText(Task.getDescription());
        viewholder.Time.setText(Task.getTime());
        viewholder.Date.setText(Task.getDate());
        return convertView;
    }

    static class ViewHolder {
        TextView Task;
        TextView Description;
        TextView Date;
        TextView Time;
        Button Delete;
        Button View;
        Button Edit;

    }

    static interface Deleteonclicklistener {
        void onDeleteClicked( int position, View v );
    }


    static  interface Editonclicklistener {
        void onEditClicked(int postion,View v);
    }

    static interface Viewonclicklistener {
        void onViewClicked(int postion,View v);
    }
}
