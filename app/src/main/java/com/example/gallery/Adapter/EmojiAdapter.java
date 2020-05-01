package com.example.gallery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.EmojiFragment;
import com.example.gallery.R;

import java.util.ArrayList;
import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconTextView;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder> {
    Context context;
    List<String> emojiList;
    EmojiAdapterListner listner;

    public EmojiAdapter(Context context, List<String> emojis, EmojiAdapterListner listner) {
        this.context = context;
        this.emojiList = emojis;
        this.listner = listner;
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.emoji_item,parent,false);

        return new EmojiViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder holder, int position) {
        holder.txtEmoji.setText(emojiList.get(position));
    }

    @Override
    public int getItemCount() {
        return emojiList.size();
    }

    public class EmojiViewHolder extends RecyclerView.ViewHolder{
        EmojiconTextView txtEmoji;

        public EmojiViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEmoji = (EmojiconTextView) itemView.findViewById(R.id.txtEmoji);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.onEmojiItemSelected(emojiList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface EmojiAdapterListner{
        void onEmojiItemSelected(String emoji);
    }

}
