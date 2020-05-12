package com.chocomint.mintery;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
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
    ExoPlayer exoPlayer;

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
        return view ==((CoordinatorLayout) object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        releasePlayer();
        ((ViewPager) container).removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemview = null;
        if (arrayList.get(position).type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            itemview = layoutInflater.inflate(R.layout.full_image_layout, container, false);
            PhotoView fullImage = (PhotoView) itemview.findViewById(R.id.image);
            fullImage.setMaximumScale(5);

            Glide.with(context.getApplicationContext()).load(arrayList.get(position).path).into(fullImage);

            fullImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Slider clicked", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            itemview = layoutInflater.inflate(R.layout.full_video_layout, container, false);
            PlayerView playerView = itemview.findViewById(R.id.video_view);
            releasePlayer();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(itemview.getContext(), new DefaultTrackSelector());
            playerView.setPlayer(exoPlayer);

            String userAgent = Util.getUserAgent(itemview.getContext(), "VideoPlayer");
            ExtractorMediaSource mediaSource = new ExtractorMediaSource(Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(position).id)),
                    new DefaultDataSourceFactory(itemview.getContext(), userAgent),
                    new DefaultExtractorsFactory(),
                    null,
                    null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);

        }

        container.addView(itemview);
        return itemview;
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
