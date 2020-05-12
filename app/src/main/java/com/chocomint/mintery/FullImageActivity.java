package com.chocomint.mintery;

import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.io.IOException;
import java.util.ArrayList;

public class FullImageActivity extends AppCompatActivity {

    Context context = this;
    ViewPager slider;
    Toolbar img_toolbar;
    FullImageSlider imageSlider;
    ArrayList<Media> arrayList;
    int CurrentPosition;

    ImageButton cropBtn, editBtn, shareBtn, deleteBtn, trimBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);

        getView();

        String path = getIntent().getExtras().getString("id");
        slider = (ViewPager) findViewById(R.id.image_viewpaprer);
        arrayList = (ArrayList<Media>) getIntent().getSerializableExtra("list");
        CurrentPosition = getIntent().getExtras().getInt("position");
        imageSlider = new FullImageSlider(FullImageActivity.this, arrayList, path);
        slider.setAdapter(imageSlider);
        slider.setCurrentItem(CurrentPosition);

        slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                CurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        img_toolbar = (Toolbar) findViewById(R.id.image_toolbar);
        this.setSupportActionBar(img_toolbar);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        cropBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText( view.getContext(), "Hit crop", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        editBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText( view.getContext(), "Hit edit", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        shareBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText( view.getContext(), "Hit share", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText( view.getContext(), "Hit delete", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        trimBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText( view.getContext(), "Hit trim", Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void getView() {
        cropBtn = findViewById(R.id.image_crop);
        editBtn = findViewById(R.id.image_edit);
        shareBtn = findViewById(R.id.image_share);
        deleteBtn = findViewById(R.id.image_delete);
        trimBtn = findViewById(R.id.video_trim);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (arrayList.get(0).type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            getMenuInflater().inflate(R.menu.image_menu_toolbar, menu);
        } else {
            getMenuInflater().inflate(R.menu.video_menu_toolbar, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detail:
                Intent intent = new Intent(this, MediaDetailActivity.class);
                startActivity(intent);
                return true;
            case R.id.img_favorite:
                Toast.makeText(FullImageActivity.this, "Favorite clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.wallpaper:
                new SetWallpaperThread().execute();
                return true;
            case R.id.about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SetWallpaperThread extends AsyncTask <Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return setWallpaper();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    private boolean setWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(FullImageActivity.this);
        Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(CurrentPosition).id));
        Intent intent = wallpaperManager.getCropAndSetWallpaperIntent(uri);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            return false;
        }
        return true;
    }
}
