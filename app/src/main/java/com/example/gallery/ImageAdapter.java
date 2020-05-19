package com.example.gallery;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    ArrayList<ImageInformation> list;
    Context context;

    public ImageAdapter(ArrayList<ImageInformation> l,Context c)
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
        /*
        ImageView imageView = new ImageView(context);
        Glide.with(context)
                .asBitmap()
                .load(list.get(position).getThumb())
                .override(240,240)
                .skipMemoryCache(false)
                .into(imageView);
        return imageView;
         */
        CheckableLayout l;
        ImageView i;

        if (View == null) {
            i = new ImageView(context);
            i.setScaleType(ImageView.ScaleType.FIT_CENTER);
            i.setLayoutParams(new ViewGroup.LayoutParams(240, 240));
            l = new CheckableLayout(context);
            l.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT,
                    GridView.LayoutParams.WRAP_CONTENT));
            l.addView(i);
        } else {
            l = (CheckableLayout) View;
            i = (ImageView) l.getChildAt(0);
        }

        Glide.with(context)
                .asBitmap()
                .load(list.get(position).getThumb())
                .override(240,240)
                .skipMemoryCache(false)
                .into(i);
        return l;
    }

    public class CheckableLayout extends FrameLayout implements Checkable {
        private boolean mChecked;

        public CheckableLayout(Context context) {
            super(context);
        }

        public void setChecked(boolean checked) {
            mChecked = checked;
            setBackgroundDrawable(checked ?
                    getResources().getDrawable(R.drawable.emoji_261d)
                    : null);
        }

        public boolean isChecked() {
            return mChecked;
        }

        public void toggle() {
            setChecked(!mChecked);
        }

    }
}
