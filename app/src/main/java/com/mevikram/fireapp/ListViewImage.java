package com.mevikram.fireapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class ListViewImage extends AppCompatActivity {

    ListView lv;
    Context context;
    ArrayList progList;

    public static Integer [] progImage={R.mipmap.ic_launcher,R.mipmap.ic_launcher};
    public static String[] progNames={"Launcher","Checker"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_image);

        MyListAdapter adapter =new MyListAdapter(this,progNames,progImage);
        lv=findViewById(R.id.lvPeople);
        lv.setAdapter(adapter);
    }
}
