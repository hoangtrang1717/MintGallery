package com.example.gallery;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    ArrayList<File> list;
    Context context;

    public ImageAdapter(ArrayList<File> l,Context c)
    {
        context=c;
        list=l;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View View, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        Glide.with(context.getApplicationContext()).load(getItem(position).toString())
                .override(240,240)
                .centerCrop()
                .into(imageView);
        return imageView;
    }
}
