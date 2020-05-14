package com.chocomint.mintery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.aakira.playermanager.ExoPlayerManager;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.rubensousa.previewseekbar.PreviewView;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class FullImageSlider extends PagerAdapter {
    Context context;
    ArrayList<Media> arrayList;
    LayoutInflater layoutInflater;
    String data;
    PlayerManager playerManager;

    public FullImageSlider(Context c, ArrayList<Media> list, String path){
        this.context = c;
        this.data = path;
        this.arrayList = list;
        layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==((ConstraintLayout) object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        View itemview = null;
        if (arrayList.get(position).type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            itemview = layoutInflater.inflate(R.layout.full_image_layout, container, false);
            final PhotoView fullImage = (PhotoView) itemview.findViewById(R.id.image);
            fullImage.setMaximumScale(5);
            fullImage.setMinimumScale(-5);

            Glide.with(context.getApplicationContext()).load(arrayList.get(position).path).into(fullImage);

            fullImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    container.callOnClick();
                }
            });
        } else {
            itemview = layoutInflater.inflate(R.layout.full_video_layout, container, false);
            PlayerView playerView = itemview.findViewById(R.id.video_view);
            PreviewTimeBar previewTimeBar = playerView.findViewById(R.id.exo_progress);
            ImageView imageView = playerView.findViewById(R.id.image_preview);
            playerManager = new PlayerManager(playerView, previewTimeBar, imageView);
            playerManager.play(Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(position).id)));
            playerManager.onStart();

            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    container.callOnClick();
                }
            });


        }

        container.addView(itemview);
        return itemview;
    }

    public void onStopVideo() {
        playerManager.onStop();
    }

    public boolean onPauseVideo() {
        return playerManager.pause();
    }
}
