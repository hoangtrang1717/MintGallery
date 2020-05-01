package com.example.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gallery.Sort.SortByModified;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class PhotoFragment extends Fragment {
    Context context;
    GridView gridView;
    ArrayList<File> arrayList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout_photo = (LinearLayout) inflater.inflate(R.layout.fragment_photo_layout, null);
        context=this.getContext();
        arrayList = imageReader(Environment.getExternalStorageDirectory());
        gridView = (GridView) layout_photo.findViewById(R.id.gridview);
        final ImageAdapter imageAdapter =new ImageAdapter(arrayList, context);
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i= new Intent(context.getApplicationContext(),FullImageActivity.class);
                i.putExtra("id",arrayList.get(position).toString());
                i.putExtra("position",position);
                i.putExtra("list",arrayList);
                startActivity(i);
            }
        });

        return layout_photo;
    }

    private ArrayList<File> imageReader(File root) {
        ArrayList<File> b = new ArrayList<>();
        final File[] files = root.listFiles();
        final int filesCount =files.length;
        System.out.println("HIHI"+filesCount);
        for(int i =0;i<filesCount;i++){
            if(files[i].isDirectory()){
                b.addAll(imageReader(files[i]));
            }
            else{
                if(files[i].getName().endsWith(".jpg") || files[i].getName().endsWith(".png")
                        ||files[i].getName().endsWith(".jpeg")){
                    System.out.println(files[i].getPath());
                    b.add(files[i]);
                }
            }
        }
        Collections.sort(b, Collections.<File>reverseOrder(new SortByModified()));
        return b;
    }
}
