package com.example.gallery;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;

public class FullImageSlider extends PagerAdapter {
    Context context;
    ArrayList<ImageInformation> arrayList;
    LayoutInflater layoutInflater;
    String data;
    public FullImageSlider(Context c,ArrayList<ImageInformation> list, String data){
        this.context=c;
        this.data=data;
        this.arrayList=list;
        layoutInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==((LinearLayout)object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemview = layoutInflater.inflate(R.layout.full_image_layout,container,false);
        PhotoView fullImage = (PhotoView) itemview.findViewById(R.id.image);
        fullImage.setMaximumScale(5);

        //fullImage.setImageURI(Uri.parse(data));
        //fullImage.setImageURI(Uri.parse(arrayList.get(position).toString()));
        //fullImage.setImageURI(Uri.parse(arrayList.get(position).toString()));
        Glide.with(context.getApplicationContext()).load(arrayList.get(position).getThumb()).into(fullImage);

        /*Intent i= new Intent(context.getApplicationContext(),FullImageActivity.class);
        i.putExtra("curposition",position);
        context.startActivity(i);*/

        fullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Slider clicked", Toast.LENGTH_LONG).show();
            }
        });
        container.addView(itemview);
        return itemview;
    }
}
