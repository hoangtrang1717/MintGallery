package com.chocomint.mintery;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;

public class FullImageSlider extends PagerAdapter {
    Context context;
    ArrayList<Media> arrayList;
    LayoutInflater layoutInflater;
    String data;
    PlayerManager playerManager;
    int currentVideo;

    public FullImageSlider(Context c, ArrayList<Media> list, String path){
        this.context = c;
        this.data = path;
        this.arrayList = list;
        layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCurrentVideo(int position) {
        currentVideo = position;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (arrayList.contains(object)) {
            return arrayList.indexOf(object);
        } else {
            return POSITION_NONE;
        }
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
            final PlayerView playerView = itemview.findViewById(R.id.video_view);
            PreviewTimeBar previewTimeBar = playerView.findViewById(R.id.exo_progress);
            ImageView imageView = playerView.findViewById(R.id.image_preview);
            Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(position).id));
            arrayList.get(position).setPlayerManager(playerView, previewTimeBar, imageView, uri);
            if (position == this.currentVideo) {
                arrayList.get(position).playVideo();
            }
        }

        container.addView(itemview);
        return itemview;
    }

    public void playVideo(int position) {
        arrayList.get(position).playVideo();
    }

    public void stopVideo(int position) {
        arrayList.get(position).stopVideo();
    }

    public boolean pauseVideo(int position) {
        return arrayList.get(position).pauseVideo();
    }
}
