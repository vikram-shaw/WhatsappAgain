package com.mevikram.fireapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] progName;
    private final Integer[] progImage;

    public MyListAdapter(Activity context,String[] progName,Integer[] progImage){
        super(context,R.layout.activity_image_list,progName);
        this.context=context;
        this.progImage=progImage;
        this.progName=progName;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.activity_image_list,null,true);
        TextView txtTitile =rowView.findViewById(R.id.tvName);
        ImageView imageView=rowView.findViewById(R.id.ivIcon);
        txtTitile.setText(progName[position]);
        imageView.setImageResource(progImage[position]);
        return rowView;
    }
}
