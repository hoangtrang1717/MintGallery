package com.chocomint.mintery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chocomint.mintery.videoTrimmer.utils.FileUtils;
import com.github.rubensousa.previewseekbar.PreviewLoader;
import com.github.rubensousa.previewseekbar.PreviewView;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class FullVideoActivity extends AppCompatActivity {
    Toolbar video_toolbar;
    ViewPager slider;
    FullImageSlider imageSlider;
    ArrayList<Media> arrayList;
    BottomAppBar bottomAppBar;
    int CurrentPosition;
    FavoriteDatabase favoriteDatabase;
    Menu menuVideo;
    private final int REQUEST_WRITE_EXTERNAL = 2;

    ImageButton shareBtn, deleteBtn, trimBtn, pauseBtn;
    boolean isPlaying;
    private final int REQUEST_WRITE_EXTERNAL_DELETE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_layout);

        getView();
        favoriteDatabase = new FavoriteDatabase(FullVideoActivity.this);

        isPlaying = true;
        final String path = getIntent().getExtras().getString("id");
        slider = (ViewPager) findViewById(R.id.video_viewpaprer);
        arrayList = (ArrayList<Media>) getIntent().getSerializableExtra("list");
        CurrentPosition = getIntent().getExtras().getInt("position");
        //
        int temp = 0;
        for(int i = 0; i < CurrentPosition; i++)
        {
            if(arrayList.get(i).path.compareTo("nothing") == 0) {
                temp = temp + 1;
            }
        }
        CurrentPosition = CurrentPosition - temp;
        //
        arrayList.removeIf(new Predicate<Media>() {
            @Override
            public boolean test(Media media) {
                if (media.path.compareTo("nothing") == 0) {
                    return true;
                }
                return false;
            }
        });
        //
        imageSlider = new FullImageSlider(FullVideoActivity.this, arrayList, path);
        slider.setAdapter(imageSlider);
        slider.setCurrentItem(CurrentPosition);
        slider.setOffscreenPageLimit(1);
        imageSlider.setCurrentVideo(CurrentPosition);

        slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideActionBar();
            }
        });

        slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                imageSlider.stopVideo(CurrentPosition);
                CurrentPosition = position;
                imageSlider.playVideo(position);
                if (!isPlaying) {
                    pauseBtn.setImageResource(R.drawable.ic_pause);
                }
                setFavoriteIcon();
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseVideo();
                new ShareThread().execute();
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPlaying = imageSlider.pauseVideo(CurrentPosition);
                if (isPlaying) {
                    pauseBtn.setImageResource(R.drawable.ic_pause);
                } else {
                    pauseBtn.setImageResource(R.drawable.ic_play);
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    showDialogDelete();
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_DELETE);
                }
            }
        });

        trimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(FullVideoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL);
                }
                if (ContextCompat.checkSelfPermission(FullVideoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), TrimmerActivity.class);
                intent.putExtra("EXTRA_VIDEO_PATH", FileUtils.getPath(getApplicationContext(), Uri.fromFile(new File(path))));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favoriteDatabase.close();
    }

    private void getView() {
        shareBtn = findViewById(R.id.image_share);
        deleteBtn = findViewById(R.id.image_delete);
        trimBtn = findViewById(R.id.video_trim);
        pauseBtn = findViewById(R.id.video_pause);
        bottomAppBar = findViewById(R.id.full_video_bottomtab);
        video_toolbar = (Toolbar) findViewById(R.id.video_toolbar);
        this.setSupportActionBar(video_toolbar);
        this.getSupportActionBar().setTitle("");
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseVideo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuVideo = menu;
        getMenuInflater().inflate(R.menu.video_menu_toolbar, menu);
        setFavoriteIcon();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        pauseVideo();
        switch (item.getItemId()) {
            case R.id.detail:
                Intent intent = new Intent(this, MediaDetailActivity.class);
                Bundle bundle = new Bundle();
                Media media = new Media(arrayList.get(CurrentPosition));
                bundle.putSerializable("media", media);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.img_favorite:
                new FavoriteThread().execute(arrayList.get(CurrentPosition).isFavorite);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_DELETE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showDialogDelete();
                }
            }
        }
    }

    private void setFavoriteIcon() {
        MenuItem menuItem =  menuVideo.findItem(R.id.img_favorite);
        if (arrayList.get(CurrentPosition).isFavorite) {
            menuItem.setIcon(R.drawable.ic_heart_sharp);
        } else {
            menuItem.setIcon(R.drawable.ic_heart_outline);
        }
    }

    public void hideActionBar() {
        if (getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
            bottomAppBar.setVisibility(View.INVISIBLE);
        } else {
            getSupportActionBar().show();
            bottomAppBar.setVisibility(View.VISIBLE);
        }
    }

    private void pauseVideo() {
        if (isPlaying) {
            isPlaying = imageSlider.pauseVideo(CurrentPosition);
            pauseBtn.setImageResource(R.drawable.ic_play);
        }
    }

    private void showDialogDelete() {
        pauseVideo();
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(FullVideoActivity.this);
        myAlertDialog.setTitle("Delete Video");
        myAlertDialog.setMessage("Do you want to delete it?");
        myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                int delete = getContentResolver().delete(Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(CurrentPosition).id)), null, null);
                if (delete > 0) {
                    imageSlider.stopVideo(CurrentPosition);
                    arrayList.remove(CurrentPosition);
                    if (arrayList.size() < 1) {
                        onBackPressed();
                    }
                    imageSlider.notifyDataSetChanged();
                    pauseBtn.setImageResource(R.drawable.ic_pause);
                }
            }});
        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }});
        myAlertDialog.show();
    }

    private class ShareThread extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(CurrentPosition).id)));
                shareIntent.setType("video/*");
                startActivity(Intent.createChooser(shareIntent, "Share this video"));
            } catch (ActivityNotFoundException e) {
                Log.d("Error share", e.getMessage());
                return false;
            }
            return true;
        }
    }

    private class FavoriteThread extends AsyncTask <Boolean, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            boolean isDone;
            if (booleans != null && booleans[0]) {
                isDone = favoriteDatabase.deleteFavorite(arrayList.get(CurrentPosition).id, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            } else {
                isDone = favoriteDatabase.insertFavorite(arrayList.get(CurrentPosition).id, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            }
            return isDone;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                MenuItem menuItem =  menuVideo.findItem(R.id.img_favorite);
                if(arrayList.get(CurrentPosition).setFavorite()) {
                    menuItem.setIcon(R.drawable.ic_heart_sharp);
                } else {
                    menuItem.setIcon(R.drawable.ic_heart_outline);
                }
                pauseBtn.setImageResource(R.drawable.ic_pause);
                imageSlider.notifyDataSetChanged();
            } else {
                Toast.makeText(FullVideoActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();
            }
        }
    }
}
