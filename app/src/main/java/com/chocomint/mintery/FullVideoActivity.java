package com.chocomint.mintery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import com.github.rubensousa.previewseekbar.PreviewLoader;
import com.github.rubensousa.previewseekbar.PreviewView;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.material.bottomappbar.BottomAppBar;

import java.util.ArrayList;

public class FullVideoActivity extends AppCompatActivity {
    Context context = this;
    ViewPager slider;
    Toolbar video_toolbar;
    FullImageSlider imageSlider;
    ArrayList<Media> arrayList;
    BottomAppBar bottomAppBar;
    int CurrentPosition;
    PreviewTimeBar seekBar;
    ImageView imagePreview;
    FrameLayout previewFrame;

    ImageButton shareBtn, deleteBtn, trimBtn, pauseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_layout);

        getView();

        String path = getIntent().getExtras().getString("id");
        slider = (ViewPager) findViewById(R.id.video_viewpaprer);
        arrayList = (ArrayList<Media>) getIntent().getSerializableExtra("list");
        CurrentPosition = getIntent().getExtras().getInt("position");
        imageSlider = new FullImageSlider(FullVideoActivity.this, arrayList, path);
        slider.setAdapter(imageSlider);
        slider.setCurrentItem(CurrentPosition);

        slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportActionBar().isShowing()) {
                    getSupportActionBar().hide();
                    bottomAppBar.setVisibility(View.INVISIBLE);
                    slider.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
                } else {
                    getSupportActionBar().show();
                    bottomAppBar.setVisibility(View.VISIBLE);
                    slider.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
                }
            }
        });

        slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                imageSlider.onStopVideo();
            }

            @Override
            public void onPageSelected(int position) {
                CurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ShareThread().execute();
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageSlider.onPauseVideo()) {
                    pauseBtn.setImageResource(R.drawable.ic_pause);
                } else {
                    pauseBtn.setImageResource(R.drawable.ic_play);
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( view.getContext(), "Hit delete", Toast.LENGTH_LONG).show();
            }
        });

        trimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( view.getContext(), "Hit trim", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onPause() {
        imageSlider.onPauseVideo();
        super.onPause();
    }

    @Override
    protected void onStop() {
        imageSlider.onStopVideo();
        super.onStop();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detail:
                Intent intent = new Intent(this, MediaDetailActivity.class);
                intent.putExtra("media", arrayList.get(CurrentPosition));
                startActivity(intent);
                return true;
            case R.id.img_favorite:
                Toast.makeText(FullVideoActivity.this, "Favorite clicked", Toast.LENGTH_LONG).show();
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
}
