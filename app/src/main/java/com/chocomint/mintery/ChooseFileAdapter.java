package com.chocomint.mintery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private DaysChosen chosen;

    public ChooseFileAdapter(Context mContext, ArrayList<Media> data, ChooseFileCallback chooseFileCallback) {
        this.mContext = mContext;
        this.allMedia = data;
        this.chooseFileCallback = chooseFileCallback;
        chosen = new DaysChosen();
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
    public ChooseFileAdapter.HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_VIEW_TYPE_HEADER) {
            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.item_choose_header, parent, false);

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
            holder.all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    cal.setTime(allMedia.get(position).dateModified);
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH) + 1;
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    if(b)
                        chosen.AddWholeDay(day, month, year, position, allMedia.get(position).id);
                    else
                        chosen.RemoveWholeDay(day, month, year);
                    notifyDataSetChanged();
                }
            });
            holder.number.setText(String.valueOf(allMedia.get(position).id));
            /*
            holder.undoAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    cal.setTime(allMedia.get(position).dateModified);
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH) + 1;
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    chosen.RemoveWholeDay(day, month, year);
                    notifyDataSetChanged();
                }
            });
            holder.chooseAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    cal.setTime(allMedia.get(position).dateModified);
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH) + 1;
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    chosen.AddWholeDay(day, month, year, position, allMedia.get(position).id);
                    notifyDataSetChanged();
                }
            });
             */
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
                boolean temp = !holder.radioButton.isChecked();
                Toast.makeText(mContext, "YEY", Toast.LENGTH_LONG).show();
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                cal.setTime(allMedia.get(position).dateModified);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                if(temp)
                    chosen.AddSingle(day, month, year, position);
                else
                    chosen.RemoveSingle(day, month, year, position);
                holder.radioButton.setChecked(temp);
            }
        });

        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    System.out.println("ADD: " + position);
                else
                    System.out.println("REMOVE: " + position);
                chooseFileCallback.chooseFile(position, b);
            }
        });

        //Toast.makeText(mContext, "YEY", Toast.LENGTH_LONG).show();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(allMedia.get(position).dateModified);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if(chosen.seeIfChosen(day, month, year, position)) {
            System.out.println("Check " + position);
            holder.radioButton.setChecked(true);
        }
        else {
            System.out.println("UnCheck " + position);
            holder.radioButton.setChecked(false);
        }
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
        CheckBox all;
        TextView number;

        public HolderView(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.video_time);
            thumbnail = itemView.findViewById(R.id.image_item);
            view = itemView.findViewById(R.id.item_holder);
            radioButton = itemView.findViewById(R.id.radio_choose);
            all = itemView.findViewById(R.id.radio_All);
            number = itemView.findViewById(R.id.quantity);
        }
    }

    private class DaysChosen {
        private ArrayList<SingleDay> sdays;
        public DaysChosen() {
            sdays = new ArrayList<>();
        }
        public void AddWholeDay(int d, int m, int y, int pos, int length) {
            for(int i = 0; i < sdays.size(); i++)
            {
                SingleDay temp = sdays.get(i);
                if(temp.day == d && temp.month == m && temp.year == y)
                {
                    temp.position.clear();
                    for(int j = 1; j <= length; j++)
                    {
                        temp.position.add(pos + j);
                    }
                    for(int o = 0; o < sdays.size(); o++)
                    {
                        System.out.println(sdays.get(o).day +"/"+ sdays.get(o).month +"/"+ sdays.get(o).year);
                        for(int j = 0; j < sdays.get(o).position.size(); j++)
                        {
                            System.out.println(sdays.get(o).position.get(j));
                        }
                    }
                    return;
                }
            }
            SingleDay temp = new SingleDay(d, m, y);
            for(int j = 1; j <= length; j++)
            {
                temp.position.add(pos + j);
            }
            sdays.add(temp);

            for(int o = 0; o < sdays.size(); o++)
            {
                System.out.println(sdays.get(o).day +"/"+ sdays.get(o).month +"/"+ sdays.get(o).year);
                for(int h = 0; h < sdays.get(o).position.size(); h++)
                {
                    System.out.println(sdays.get(o).position.get(h));
                }
            }
        }
        public void RemoveWholeDay(int d, int m, int y) {
            for(int i = 0; i < sdays.size(); i++)
            {
                SingleDay temp = sdays.get(i);
                if(temp.day == d && temp.month == m && temp.year == y)
                {
                    sdays.remove(i);
                    System.out.println("REMOVE WHOLE");
                    for(int o = 0; o < sdays.size(); o++)
                    {
                        System.out.println(sdays.get(o).day +"/"+ sdays.get(o).month +"/"+ sdays.get(o).year);
                        for(int j = 0; j < sdays.get(o).position.size(); j++)
                        {
                            System.out.println(sdays.get(o).position.get(j));
                        }
                    }
                    return;
                }
            }
        }
        public boolean seeIfChosen(int d, int m, int y, int p) {
            for(int i = 0; i < sdays.size(); i++)
            {
                SingleDay temp = sdays.get(i);
                if(temp.day == d && temp.month == m && temp.year == y)
                {
                    for(int j = 0; j < temp.position.size(); j++)
                    {
                        if(p == temp.position.get(j))
                            return true;
                    }
                }
            }
            return false;
        }
        public void AddSingle(int d, int m, int y, int p) {
            for(int i = 0; i < sdays.size(); i++)
            {
                SingleDay temp = sdays.get(i);
                if(temp.day == d && temp.month == m && temp.year == y)
                {
                    temp.position.add(p);
                    for(int o = 0; o < sdays.size(); o++)
                    {
                        System.out.println(sdays.get(o).day +"/"+ sdays.get(o).month +"/"+ sdays.get(o).year);
                        for(int h = 0; h < sdays.get(o).position.size(); h++)
                        {
                            System.out.println(sdays.get(o).position.get(h));
                        }
                    }
                    return;
                }
            }
            SingleDay temp = new SingleDay(d, m, y);
            temp.position.add(p);
            sdays.add(temp);

            for(int i = 0; i < sdays.size(); i++)
            {
                System.out.println(sdays.get(i).day +"/"+ sdays.get(i).month +"/"+ sdays.get(i).year);
                for(int j = 0; j < sdays.get(i).position.size(); j++)
                {
                    System.out.println(sdays.get(i).position.get(j));
                }
            }
        }
        public void RemoveSingle(int d, int m, int y, int p) {
            for(int i = 0; i < sdays.size(); i++)
            {
                SingleDay temp = sdays.get(i);
                if(temp.day == d && temp.month == m && temp.year == y)
                {
                    for(int j = 0 ; j < temp.position.size(); j++)
                    {
                        if(p == temp.position.get(j))
                        {
                            int v = temp.position.remove(j);
                            for(int o = 0; o < sdays.size(); o++)
                            {
                                System.out.println(sdays.get(o).day +"/"+ sdays.get(o).month +"/"+ sdays.get(o).year);
                                for(int h = 0; h < sdays.get(o).position.size(); h++)
                                {
                                    System.out.println(sdays.get(o).position.get(h));
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    private class SingleDay {
        public int day;
        public int month;
        public int year;
        public ArrayList<Integer> position;
        public SingleDay(int d, int m, int y) {
            day = d; month = m; year = y;
            position = new ArrayList<>();
        }
    }
}
