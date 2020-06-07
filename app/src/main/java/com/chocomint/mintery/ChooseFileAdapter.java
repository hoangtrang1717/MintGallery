package com.chocomint.mintery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ChooseFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
            view = inflater.inflate(R.layout.item_choose_header, parent, false);

            return new ChooseFileAdapter.HolderHeaderView(view);
        }
        else {
            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.item_choose_image, parent, false);

            return new ChooseFileAdapter.HolderImageView(view);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ChooseFileAdapter.HolderHeaderView) {
            ((HolderHeaderView) holder).all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!compoundButton.isPressed()) {
                        return;
                    }
                    for (int i = position + 1; i < allMedia.size(); i++) {
                        if (allMedia.get(i).path.compareTo("nothing") == 0) {
                            break;
                        }
                        chooseFileCallback.chooseFile(i, b);
                        notifyItemChanged(i);
                    }
                }
            });

            ((HolderHeaderView) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((HolderHeaderView) holder).all.performClick();
                    for (int i = position + 1; i < allMedia.size(); i++) {
                        if (allMedia.get(i).path.compareTo("nothing") == 0) {
                            break;
                        }
                        chooseFileCallback.chooseFile(i, ((HolderHeaderView) holder).all.isChecked());
                        notifyItemChanged(i);
                    }
                }
            });
            ((HolderHeaderView) holder).number.setText(String.valueOf(allMedia.get(position).id));

            if (allMedia.get(position).id == allMedia.get(position).countDate) {
                ((HolderHeaderView) holder).all.setChecked(true);
            } else {
                ((HolderHeaderView) holder).all.setChecked(false);
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date today = new Date();
            String temp = today.getDate() == allMedia.get(position).dateModified.getDate() ? "Today" : formatter.format(allMedia.get(position).dateModified);
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

            ((HolderImageView) holder).radioButton.bringToFront();
            ((HolderImageView) holder).radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!compoundButton.isPressed()) {
                        return;
                    }
                    chooseFileCallback.chooseFile(position, b);
                    if (b) {
                        allMedia.get(allMedia.get(position).countDate).changeCountDate(1);
                    } else {
                        allMedia.get(allMedia.get(position).countDate).changeCountDate(-1);
                    }
                    notifyItemChanged(allMedia.get(position).countDate);
                }
            });

            ((HolderImageView) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((HolderImageView) holder).radioButton.performClick();
                    chooseFileCallback.chooseFile(position, ((HolderImageView) holder).radioButton.isChecked());
                    if (((HolderImageView) holder).radioButton.isChecked()) {
                        allMedia.get(allMedia.get(position).countDate).changeCountDate(1);
                    } else {
                        allMedia.get(allMedia.get(position).countDate).changeCountDate(-1);
                    }
                    notifyItemChanged(allMedia.get(position).countDate);
                }
            });

            if (chooseFileCallback.findChooseFile(allMedia.get(position).id) >= 0) {
                ((HolderImageView) holder).radioButton.setChecked(true);
            } else {
                ((HolderImageView) holder).radioButton.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return allMedia.size();
    }

    public static class HolderHeaderView extends RecyclerView.ViewHolder {

        TextView time;
        ConstraintLayout view;
        CheckBox all;
        TextView number;

        public HolderHeaderView(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.date_modified);
            view = itemView.findViewById(R.id.item_choose_holder);
            all = itemView.findViewById(R.id.radio_All);
            number = itemView.findViewById(R.id.quantity);
        }
    }

    public static class HolderImageView extends RecyclerView.ViewHolder {

        TextView time;
        ImageView thumbnail;
        CheckBox radioButton;
        SquareLayout view;

        public HolderImageView(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.video_time);
            thumbnail = itemView.findViewById(R.id.image_item);
            view = itemView.findViewById(R.id.item_holder);
            radioButton = itemView.findViewById(R.id.radio_choose);
        }
    }

    private class NotifyChange extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            notifyItemChanged(integers[0]);
            return null;
        }
    }
}
