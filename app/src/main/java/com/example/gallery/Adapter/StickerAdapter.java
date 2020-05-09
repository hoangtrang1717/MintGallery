package com.example.gallery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.R;

import java.util.ArrayList;
import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerViewHolder> {
    Context context;
    List<Integer> stickerList;
    StickerAdapterListener listener;

    int row_selected = -1;
    public StickerAdapter(Context context, StickerAdapterListener listener){
        this.context=context;
        this.stickerList=getStickerList();
        this.listener = listener;
    }

    private List<Integer> getStickerList() {
        List<Integer> result = new ArrayList<>();
        result.add(R.drawable.sticker1);
        result.add(R.drawable.sticker2);
        result.add(R.drawable.sticker3);
        result.add(R.drawable.sticker4);
        result.add(R.drawable.sticker5);
        result.add(R.drawable.sticker6);
        return  result;
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.sticker_item,parent,false);

        return new StickerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        if(row_selected == position){
            holder.sticker_check.setVisibility(View.VISIBLE);
        }else{
            holder.sticker_check.setVisibility(View.INVISIBLE);
        }

        holder.sticker.setImageResource(stickerList.get(position));
    }

    @Override
    public int getItemCount() {
        return stickerList.size();
    }

    public class StickerViewHolder extends  RecyclerView.ViewHolder{
        ImageView sticker_check, sticker;
        public StickerViewHolder(@NonNull View itemView) {
            super(itemView);
            sticker_check =(ImageView) itemView.findViewById(R.id.sticker_check);
            sticker =(ImageView) itemView.findViewById(R.id.sticker);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onStickerSelected(stickerList.get(getAdapterPosition()));

                    row_selected = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface StickerAdapterListener {
        void onStickerSelected(int sticker);
    }

}
