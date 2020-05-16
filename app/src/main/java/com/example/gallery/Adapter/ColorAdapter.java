package com.example.gallery.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.BrushFragment;
import com.example.gallery.R;

import java.util.ArrayList;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {
    Context context;
    List<Integer> colorList;
    ColorApdapterListener listener;
    public ColorAdapter(Context context,  ColorApdapterListener listener){
        this.context=context;
        this.colorList = genColorList() ;
        this.listener = listener;
    }



    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.color_item,parent,false);
        return new ColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        holder.color_section.setCardBackgroundColor(colorList.get(position));
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder{
        public CardView color_section;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            color_section = (CardView)itemView.findViewById(R.id.color_section);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onColorSelected(colorList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface ColorApdapterListener{
        void onColorSelected(int color);
    }

    private List<Integer> genColorList() {
        List<java.lang.Integer> colorList = new ArrayList<>();
        /*facebook pallette*/
        colorList.add(Color.parseColor("#3b5998"));
        colorList.add(Color.parseColor("#8b9dc3"));
        colorList.add(Color.parseColor("#dfe3ee"));
        colorList.add(Color.parseColor("#f7f7f7"));
        colorList.add(Color.parseColor("#ffffff"));
        /*Puple pallette*/
        colorList.add(Color.parseColor("#efbbff"));
        colorList.add(Color.parseColor("#d896ff"));
        colorList.add(Color.parseColor("#be29ec"));
        colorList.add(Color.parseColor("#800080"));
        colorList.add(Color.parseColor("#660066"));
        /*Blue gray pallette*/
        colorList.add(Color.parseColor("#6e7f80"));
        colorList.add(Color.parseColor("#536872"));
        colorList.add(Color.parseColor("#708090"));
        colorList.add(Color.parseColor("#536878"));
        colorList.add(Color.parseColor("#36454f"));
        /*pastel rainbow*/
        colorList.add(Color.parseColor("#a8e6cf"));
        colorList.add(Color.parseColor("#dcedc1"));
        colorList.add(Color.parseColor("#ffd3b6"));
        colorList.add(Color.parseColor("#ffaaa5"));
        colorList.add(Color.parseColor("#ff8b94"));
        /*rainbow dash*/
        colorList.add(Color.parseColor("#ee4035"));
        colorList.add(Color.parseColor("#f37736"));
        colorList.add(Color.parseColor("#fdf498"));
        colorList.add(Color.parseColor("#7bc043"));
        colorList.add(Color.parseColor("#0392cf"));
        return colorList;
    }
}
