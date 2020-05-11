package com.chocomint.mintery;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);

        img_toolbar = (Toolbar) findViewById(R.id.image_toolbar);
        this.setSupportActionBar(img_toolbar);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detail:
                Intent intent = new Intent(this, MediaDetailActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Detail", Toast.LENGTH_LONG);
                return true;
            case R.id.img_favorite:
                Toast.makeText(FullImageActivity.this, "Favorite clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.wallpaper:
                new SetWallpaperThread().execute();
                return true;
            case R.id.about_us:
                Toast.makeText(FullImageActivity.this, "About us clicked", Toast.LENGTH_LONG).show();
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
            if (aBoolean) {
                Toast.makeText(getApplicationContext(), "Đã đổi hình nền thành công", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Đổi hình nền không thành công", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aBoolean);
        }
    }

    private boolean setWallpaper() {
        Bitmap bmap2 = BitmapFactory.decodeFile(arrayList.get(CurrentPosition).path);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        Bitmap bitmap = Bitmap.createScaledBitmap(bmap2, width, height, true);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(FullImageActivity.this);
        try {
            wallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
