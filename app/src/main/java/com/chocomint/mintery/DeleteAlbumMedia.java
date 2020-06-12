package com.chocomint.mintery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DeleteAlbumMedia extends AppCompatActivity {

    Fragment mediaFrag;
    Toolbar mainToolbar;
    TextView toolBarText;
    ArrayList<Media> arrayList;

    int currentFrag;
    String albumFolder;
    int position;

    private final int REQUEST_READ_EXTERNAL = 1;
    private final int REQUEST_WRITE_EXTERNAL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_delete_media);

        getView();
        setSupportActionBar(mainToolbar);

    }

    private void getView() {

        mainToolbar = findViewById(R.id.main_toolbar);
        toolBarText = findViewById(R.id.toolbar_text);
        albumFolder = getIntent().getStringExtra("type");
        arrayList = (ArrayList<Media>) getIntent().getSerializableExtra("fullList");
        position = getIntent().getIntExtra("position", 0);
//        if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
//            currentFrag = PHOTO_FRAG;
//            photoFrag = new PhotoFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("from", "DELETE");
//            bundle.putSerializable("list", arrayList);
//            photoFrag.setArguments(bundle);
//            toolBarText.setText("Photos");
//            photoTabbar.setColorFilter(getResources().getColor(R.color.primary));
//            videoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, photoFrag, "photo").commit();
//
//        } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
//            currentFrag = VIDEO_FRAG;
//            videoFrag = new VideoFragment();
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("list", arrayList);
//            bundle.putString("from", "DELETE");
//            videoFrag.setArguments(bundle);
//            toolBarText.setText("Videos");
//            videoTabbar.setColorFilter(getResources().getColor(R.color.primary));
//            photoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, videoFrag, "video").commit();
//        }
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
//        if (currentFrag == PHOTO_FRAG) {
//            PhotoFragment photoFragment = (PhotoFragment) getSupportFragmentManager().findFragmentByTag("photo");
//            photoFragment.DeletePhotos();
//        } else if (currentFrag == VIDEO_FRAG) {
//            VideoFragment videoFragment = (VideoFragment) getSupportFragmentManager().findFragmentByTag("video");
//            videoFragment.DeleteVideos();
//        }
    }

    public boolean checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(DeleteAlbumMedia.this, permission) == PackageManager.PERMISSION_GRANTED) {
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
