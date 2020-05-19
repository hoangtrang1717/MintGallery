package com.example.gallery.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.R;

import java.util.ArrayList;
import java.util.List;

public class FontAdapter extends RecyclerView.Adapter<FontAdapter.FontViewHolder> {
    Context context;
    FontAdapterClickListener listener;
    List<String> fontList;

    int row_selected = -1;

    public FontAdapter(Context context, FontAdapterClickListener listener){
        this.context=context;
        this.listener=listener;
        fontList=loadFontList();
    }

    private List<String> loadFontList(){
        List<String> result = new ArrayList<>();
        result.add("Cheque-Black.otf");
        result.add("Cheque-Regular.otf");
        result.add("Rabbits Goody.otf");
        result.add("Mozer-SemiBold.otf");
        result.add("IriskaFreeFont.otf");
        return result;
    }
    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.font_item,parent,false);

        return new FontViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FontViewHolder holder, int position) {
        if(row_selected == position){
            holder.img_check.setVisibility(View.VISIBLE);
        }else{
            holder.img_check.setVisibility(View.INVISIBLE);
        }
        Typeface typeface =Typeface.createFromAsset(context.getAssets(),new StringBuilder("fonts/")
                .append(fontList.get(position)).toString());

     //   holder.txtFontName.setText(fontList.get(position));
        holder.txtFontDemo.setTypeface(typeface);

    }

    @Override
    public int getItemCount() {
        return fontList.size();
    }

    public class FontViewHolder extends RecyclerView.ViewHolder{
        TextView txtFontDemo;
        //TextView txtFontName;
        ImageView img_check;
        public FontViewHolder(@NonNull View itemView) {
            super(itemView);
           // txtFontName =(TextView) itemView.findViewById(R.id.txtFontName);
            txtFontDemo =(TextView) itemView.findViewById(R.id.txtFontDemo);

            img_check =(ImageView) itemView.findViewById(R.id.text_font_check);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFontSelected(fontList.get(getAdapterPosition()));
                    row_selected = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });

        }
    }

    public interface FontAdapterClickListener {
        public void onFontSelected(String fontName);
    }
}
