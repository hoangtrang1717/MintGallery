package com.chocomint.mintery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Media> allMedia;
    private Context mContext;

    private static final int ITEM_VIEW_TYPE_HEADER = 1;
    private static final int ITEM_VIEW_TYPE_ITEM = 2;

    public ImageAdapter(Context mContext, ArrayList<Media> data) {
        this.mContext = mContext;
        this.allMedia = data;
    }

    public boolean isHeader(int position) {
        return allMedia.get(position).path.compareTo("nothing") == 0;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_VIEW_TYPE_HEADER) {
            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.item_header, parent, false);

            return new ImageAdapter.HolderHeaderView(view);
        }
        else {
            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.item_image, parent, false);

            return new ImageAdapter.HolderImageView(view);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ImageAdapter.HolderHeaderView) {
            if(position == 0)
            {
                Date today = Calendar.getInstance().getTime();
                Calendar cal1 = Calendar.getInstance(TimeZone.getDefault());
                cal1.setTime(today);

                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                cal.setTime(allMedia.get(position).dateModified);

                int year = cal.get(Calendar.YEAR); int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int year1 = cal1.get(Calendar.YEAR); int month1 = cal1.get(Calendar.MONTH);
                int day1 = cal1.get(Calendar.DAY_OF_MONTH);

                if(year == year1 && month == month1 && day == day1)
                {
                    ((HolderHeaderView) holder).time.setText("Today");
                    ((HolderHeaderView) holder).time.setVisibility(View.VISIBLE);
                    ((HolderHeaderView) holder).time.bringToFront();
                    return;
                }
            }
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            cal.setTime(allMedia.get(position).dateModified);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH); month = month + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            String temp = day + "/" + month + "/" + year;
            ((HolderHeaderView) holder).time.setText(temp);
            ((HolderHeaderView) holder).time.setVisibility(View.VISIBLE);
            ((HolderHeaderView) holder).time.bringToFront();
        } else {
            Glide.with(((HolderImageView) holder).thumbnail.getContext()).load(allMedia.get(position).path).centerCrop().into(((HolderImageView) holder).thumbnail);
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
                ((HolderImageView) holder).time.setText(durationText);
                ((HolderImageView) holder).time.setVisibility(View.VISIBLE);
                ((HolderImageView) holder).time.bringToFront();
            } else {
                ((HolderImageView) holder).time.setVisibility(View.INVISIBLE);
            }

            ((HolderImageView) holder).view.setOnClickListener(new View.OnClickListener() {
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

            ((HolderImageView) holder).view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(mContext, DeleteMainActivity.class);
                    intent.putExtra("fullList", allMedia);
                    intent.putExtra("type", allMedia.get(position).type);
                    intent.putExtra("position", position);
                    mContext.startActivity(intent);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return allMedia.size();
    }

    public static class HolderHeaderView extends RecyclerView.ViewHolder {

        TextView time;
        ConstraintLayout view;

        public HolderHeaderView(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.video_time);
            view = itemView.findViewById(R.id.item_holder);
        }
    }

    public static class HolderImageView extends RecyclerView.ViewHolder {

        TextView time;
        ImageView thumbnail;
        SquareLayout view;

        public HolderImageView(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.video_time);
            thumbnail = itemView.findViewById(R.id.image_item);
            view = itemView.findViewById(R.id.item_holder);
        }
    }
}
