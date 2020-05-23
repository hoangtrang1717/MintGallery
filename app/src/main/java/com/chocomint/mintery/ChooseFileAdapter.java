package com.chocomint.mintery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ChooseFileAdapter extends RecyclerView.Adapter<ChooseFileAdapter.HolderView> {
    private static final int ITEM_VIEW_TYPE_HEADER = 1;
    private static final int ITEM_VIEW_TYPE_ITEM = 2;
    private ArrayList<Media> allMedia;
    private Context mContext;
    private ChooseFileCallback chooseFileCallback;

    public ChooseFileAdapter(Context mContext, ArrayList<Media> data, ChooseFileCallback chooseFileCallback) {
        this.mContext = mContext;
        this.allMedia = data;
        this.chooseFileCallback = chooseFileCallback;
    }

    public boolean isHeader(int position) {
        return allMedia.get(position).id == 0;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public ChooseFileAdapter.HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_VIEW_TYPE_HEADER) {
            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.item_header, parent, false);

            return new ChooseFileAdapter.HolderView(view);
        }
        else {
            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.item_choose_image, parent, false);

            return new ChooseFileAdapter.HolderView(view);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final ChooseFileAdapter.HolderView holder, final int position) {
        if (isHeader(position)) {
            if(position == 0)
            {
                Date today = Calendar.getInstance().getTime();
                Calendar cal1 = Calendar.getInstance(TimeZone.getDefault());
                cal1.setTime(today);

                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                cal.setTime(allMedia.get(position).dateModified);

                int year = cal.get(Calendar.YEAR); int month = cal.get(Calendar.MONTH); int day = cal.get(Calendar.DAY_OF_MONTH);
                int year1 = cal1.get(Calendar.YEAR); int month1 = cal1.get(Calendar.MONTH); int day1 = cal1.get(Calendar.DAY_OF_MONTH);

                if(year == year1 && month == month1 && day == day1)
                {
                    holder.time.setText("Today");
                    holder.time.setVisibility(View.VISIBLE);
                    holder.time.bringToFront();
                    return;
                }
            }
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            cal.setTime(allMedia.get(position).dateModified);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH); month = month + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            String temp = day + "/" + month + "/" + year;
            holder.time.setText(temp);
            holder.time.setVisibility(View.VISIBLE);
            holder.time.bringToFront();
            return;
        }

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

        holder.radioButton.bringToFront();

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.radioButton.setChecked(!holder.radioButton.isChecked());
            }
        });

        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                chooseFileCallback.chooseFile(position, b);
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
        CheckBox radioButton;
        RelativeLayout view;

        public HolderView(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.video_time);
            thumbnail = itemView.findViewById(R.id.image_item);
            view = itemView.findViewById(R.id.item_holder);
            radioButton = itemView.findViewById(R.id.radio_choose);
        }
    }
}
