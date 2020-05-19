package com.chocomint.mintery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;

public class DeleteMainActivity extends AppCompatActivity {

    ImageButton photoTabbar, videoTabbar;
    FloatingActionButton albumTabbar;
    Fragment photoFrag, videoFrag, albumFrag;
    Toolbar mainToolbar;
    TextView toolBarText;
    ArrayList<Media> arrayList, photoList, videoList;

    final int PHOTO_FRAG = 1;
    final int ALBUM_FRAG = 2;
    final int VIDEO_FRAG = 3;
    int currentFrag;

    int type, position;

    private final int REQUEST_READ_EXTERNAL = 1;
    private final int REQUEST_WRITE_EXTERNAL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_delete_main);

        getView();
        setSupportActionBar(mainToolbar);

        // set listener cho tabbar
        albumTabbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        videoTabbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        photoTabbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getView() {
        albumTabbar = findViewById(R.id.album_tabbar);
        videoTabbar = findViewById(R.id.video_tabbar);
        photoTabbar = findViewById(R.id.photo_tabbar);
        mainToolbar = findViewById(R.id.main_toolbar);
        toolBarText = findViewById(R.id.toolbar_text);
        type = getIntent().getIntExtra("type", MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
        arrayList = (ArrayList<Media>) getIntent().getSerializableExtra("fullList");
        position = getIntent().getIntExtra("position", 0);
        if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            currentFrag = PHOTO_FRAG;
            photoFrag = new PhotoFragment();
            Bundle bundle = new Bundle();
            bundle.putString("from", "DELETE");
            bundle.putSerializable("list", arrayList);
            photoFrag.setArguments(bundle);
            toolBarText.setText("Photos");
            photoTabbar.setColorFilter(getResources().getColor(R.color.primary));
            videoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, photoFrag, "photo").commit();

        } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            currentFrag = VIDEO_FRAG;
            videoFrag = new VideoFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("list", arrayList);
            bundle.putString("from", "DELETE");
            videoFrag.setArguments(bundle);
            toolBarText.setText("Videos");
            videoTabbar.setColorFilter(getResources().getColor(R.color.primary));
            photoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, videoFrag, "video").commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_toolbar:
                if (currentFrag == PHOTO_FRAG) {
                    PhotoFragment photoFragment = (PhotoFragment) getSupportFragmentManager().findFragmentByTag("photo");
                    photoFragment.SharePhoto();
                } else if (currentFrag == VIDEO_FRAG) {
                    VideoFragment photoFragment = (VideoFragment) getSupportFragmentManager().findFragmentByTag("video");
                    photoFragment.ShareVideo();
                }
                return true;
            case R.id.delete_toolbar:
                if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_EXTERNAL)) {
                    deleteFiles();
                }
                return true;
            case R.id.about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteFiles() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(DeleteMainActivity.this);
        myAlertDialog.setTitle(currentFrag == PHOTO_FRAG ? "Delete photos" : "Delete videos");
        myAlertDialog.setMessage("Do you want to delete all of them?");
        myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (currentFrag == PHOTO_FRAG) {
                    PhotoFragment photoFragment = (PhotoFragment) getSupportFragmentManager().findFragmentByTag("photo");
                    photoFragment.DeletePhotos();
                } else if (currentFrag == VIDEO_FRAG) {
                    VideoFragment videoFragment = (VideoFragment) getSupportFragmentManager().findFragmentByTag("video");
                    videoFragment.DeleteVideos();
                }
            }
        });
        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) { }
        });
        myAlertDialog.show();
    }

    public boolean checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(DeleteMainActivity.this, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{permission}, requestCode);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    deleteFiles();
                }
            }
            default: return;
        }
    }
}
