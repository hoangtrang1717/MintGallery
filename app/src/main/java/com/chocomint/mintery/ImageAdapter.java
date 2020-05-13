package com.chocomint.mintery;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.HolderView> {
    private ArrayList<Media> allMedia;
    private Context mContext;

    public ImageAdapter(Context mContext, ArrayList<Media> data) {
        this.mContext = mContext;
        this.allMedia = data;
    }

    @NonNull
    @Override
    public HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.item_image, parent, false);

        return new HolderView(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull HolderView holder, final int position) {
        Glide.with(holder.thumbnail.getContext()).load(allMedia.get(position).path).centerCrop().into(holder.thumbnail);
        if (allMedia.get(position).type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            Long duration = allMedia.get(position).duration;
            long hour = TimeUnit.MILLISECONDS.toHours(duration);
            String durationText = "";
            if (hour > 0) {
                durationText = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(duration),
                        TimeUnit.MILLISECONDS.toMinutes(duration) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
            } else {
                durationText = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(duration) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
            }
            holder.time.setText(durationText);
            holder.time.setVisibility(View.VISIBLE);
            holder.time.bringToFront();
        } else {
            holder.time.setVisibility(View.INVISIBLE);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (allMedia.get(0).type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                    intent = new Intent(mContext, FullImageActivity.class);
                } else {
                    intent = new Intent(mContext, FullVideoActivity.class);
                }
                intent.putExtra("id", allMedia.get(position).path);
                intent.putExtra("position", position);
                intent.putExtra("list", allMedia);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allMedia.size();
    }

    public static class HolderView extends RecyclerView.ViewHolder {

        TextView time;
        ImageView thumbnail;
        SquareLayout view;

        public HolderView(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.video_time);
            thumbnail = itemView.findViewById(R.id.image_item);
            view = itemView.findViewById(R.id.item_holder);
        }
    }
}
