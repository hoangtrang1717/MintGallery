package com.example.gallery;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;

public class FullImageActivity extends AppCompatActivity {
    ViewPager slider;
    Toolbar img_toolbar;
    //ImageView fullImage;
    FullImageSlider imageSlider;
    ArrayList<File> arrayList;
    int CurrentPosition;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);
        img_toolbar = (Toolbar) findViewById(R.id.image_toolbar);
        setSupportActionBar(img_toolbar);
        String data = getIntent().getExtras().getString("id");
        //fullImage.setImageURI(Uri.parse(data));
        slider =(ViewPager) findViewById(R.id.image_viewpaprer);
        arrayList = (ArrayList<File>) getIntent().getSerializableExtra("list");
        CurrentPosition = getIntent().getExtras().getInt("position");
        imageSlider =new FullImageSlider(FullImageActivity.this,arrayList,data);
        slider.setAdapter(imageSlider);
        slider.setCurrentItem(CurrentPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_menu_toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()){
            case R.id.info:
                Toast.makeText(FullImageActivity.this, "Info clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.edit:
                Toast.makeText(FullImageActivity.this, "Edit clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.img_favorite:
                Toast.makeText(FullImageActivity.this, "Search clicked", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
