package com.chocomint.mintery.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.chocomint.mintery.R;

import java.util.ArrayList;
import java.util.List;

public class FontAdapter extends RecyclerView.Adapter<FontAdapter.FontViewHolder> {
    Context context;
    FontAdapterClickListener listener;
    List<String> fontList;

    int row_selected = 0;

    public FontAdapter(Context context, FontAdapterClickListener listener){
        this.context = context;
        this.listener = listener;
        fontList = loadFontList();
    }

    private List<String> loadFontList(){
        List<String> result = new ArrayList<>();
        result.add("poppins.ttf");
        result.add("bangers.ttf");
        result.add("bungee_inline.ttf");
        result.add("bungee_outline.ttf");
        result.add("dancing_script.ttf");
        result.add("lobster.ttf");
        result.add("playfair_display.ttf");
        return result;
    }
    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.font_item, parent, false);
        return new FontViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FontViewHolder holder, int position) {
        if (row_selected == position){
            holder.cardView.setBackground(context.getDrawable(R.drawable.font_item_background));
        } else {
            holder.cardView.setBackground(context.getDrawable(R.drawable.font_item_bgunchoose));
        }
        Typeface typeface =Typeface.createFromAsset(context.getAssets(),new StringBuilder("fonts/")
                .append(fontList.get(position)).toString());
        holder.txtFontDemo.setTypeface(typeface);
    }

    @Override
    public int getItemCount() {
        return fontList.size();
    }

    public class FontViewHolder extends RecyclerView.ViewHolder{
        TextView txtFontDemo;
        ConstraintLayout cardView;
        public FontViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFontDemo = (TextView) itemView.findViewById(R.id.txtFontDemo);
            cardView = itemView.findViewById(R.id.font_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    row_selected = getAdapterPosition();
                    listener.onFontSelected(fontList.get(row_selected));
                    notifyDataSetChanged();
                }
            });

        }
    }

    public interface FontAdapterClickListener {
        public void onFontSelected(String fontName);
    }
}
