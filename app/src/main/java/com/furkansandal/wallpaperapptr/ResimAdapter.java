package com.furkansandal.wallpaperapptr;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
public class ResimAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<WpUrl> padisahArrayList;
    Context context;
    public ResimAdapter(Context context,Activity activity, ArrayList<WpUrl> padisahArrayList) {

        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.padisahArrayList = padisahArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return padisahArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return padisahArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.activity_gridview, null);

        ImageView padisahResim = (ImageView) convertView.findViewById(R.id.icon);
        WpUrl padisah = padisahArrayList.get(position);
        //TextView padisahIsim = (TextView) convertView.findViewById(R.id.textView);
        //padisahIsim.setText(padisah.getUrl());
        //padisahResim.setImageResource();

        Glide.with(context).load(padisah.getUrl()).into(padisahResim);
        return convertView;
    }
}