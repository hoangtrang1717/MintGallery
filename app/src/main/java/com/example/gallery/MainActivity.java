package com.example.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    Toolbar mainToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPemission();
        mainToolBar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolBar);
        mainToolBar.setTitle("");
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, new PhotoFragment()).commit();
    }
    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(MainActivity.this,
                "onResume", Toast.LENGTH_SHORT).show();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, new PhotoFragment()).commit();
    }

    private static final int REQUEST_PERMISSION = 1234;
    private static final int REQUEST_READ_PERMISSION = 1;
    private static final int REQUEST_WRITE_PERMISSION = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 3;

    private static final String[] PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    protected void checkPemission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*requestPermissions(PERMISSION,REQUEST_PERMISSION);
            return;*/
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
                }
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                }
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,
                            "READ Permission Granted", Toast.LENGTH_SHORT).show();
                }
            case 2:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this,
                            "WRITE Permission Granted", Toast.LENGTH_SHORT).show();
                }
            case 3:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this,
                            "CAMERA Permission Granted", Toast.LENGTH_SHORT).show();
                }

        }
    }

    /*private ActivityManager.MemoryInfo getMemoryInfo(){
        ActivityManager activityManager =(ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }*/



    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_photo:
                    selectedFragment = new PhotoFragment();
                case R.id.nav_album:
                    Toast.makeText(MainActivity.this, "Album clicked", Toast.LENGTH_LONG).show();
                case R.id.nav_favorite:
                    Toast.makeText(MainActivity.this, "Favorite clicked", Toast.LENGTH_LONG).show();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, selectedFragment).commit();
            return true;
        }
    };

}
