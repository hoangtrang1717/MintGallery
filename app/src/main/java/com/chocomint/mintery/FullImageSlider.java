package com.chocomint.mintery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class FullImageSlider extends PagerAdapter {
    Context context;
    ArrayList<Media> arrayList;
    LayoutInflater layoutInflater;
    String data;

    public FullImageSlider(Context c, ArrayList<Media> list, String path){
        this.context = c;
        this.data = path;
        this.arrayList = list;
        layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (arrayList.contains(object)) {
            return arrayList.indexOf(object);
        } else {
            return POSITION_NONE;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==((ConstraintLayout) object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        View itemview = layoutInflater.inflate(R.layout.full_image_layout, container, false);
        final PhotoView fullImage = (PhotoView) itemview.findViewById(R.id.image);
        fullImage.setMaximumScale(5);
        fullImage.setMinimumScale(-5);

        Glide.with(context.getApplicationContext()).load(arrayList.get(position).path).into(fullImage);

        fullImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    container.callOnClick();
                }
            });
        container.addView(itemview);
        return itemview;
    }
}
