package com.chocomint.mintery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ImageButton photoTabbar, videoTabbar;
    FloatingActionButton albumTabbar;
    Fragment photoFrag, videoFrag, albumFrag;
    Toolbar mainToolbar;
    TextView toolBarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getView();
        photoTabbar.setColorFilter(getResources().getColor(R.color.primary));

        setSupportActionBar(mainToolbar);

        // set listener cho tabbar
        albumTabbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolBarText.setText("Album");
                videoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
                photoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, albumFrag).commit();
            }
        });

        videoTabbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolBarText.setText("Videos");
                videoTabbar.setColorFilter(getResources().getColor(R.color.primary));
                photoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, videoFrag).commit();
            }
        });

        photoTabbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolBarText.setText("Photos");
                photoTabbar.setColorFilter(getResources().getColor(R.color.primary));
                videoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, photoFrag).commit();
            }
        });


    }

    private void getView() {
        albumTabbar = findViewById(R.id.album_tabbar);
        videoTabbar = findViewById(R.id.video_tabbar);
        photoTabbar = findViewById(R.id.photo_tabbar);
        mainToolbar = findViewById(R.id.main_toolbar);
        toolBarText = findViewById(R.id.toolbar_text);
        photoFrag = new PhotoFragment();
        videoFrag = new VideoFragment();
        albumFrag = new AlbumFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_photo, photoFrag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera_toolbar:
                Toast.makeText(this, "Hit camera", Toast.LENGTH_LONG).show();
                return true;
            case R.id.about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
