package com.chocomint.mintery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.HolderView> {
    private ArrayList<Media> allMedia;
    private Context mContext;

    public AlbumAdapter(Context mContext, ArrayList<Media> data) {
        this.mContext = mContext;
        this.allMedia = data;
    }

    @NonNull
    @Override
    public HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.album_item, parent, false);

        return new HolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderView holder, final int position) {
        Glide.with(holder.thumbnail.getContext()).load(allMedia.get(position).path).centerCrop().apply(bitmapTransform( new BlurTransformation(10, 2))).into(holder.thumbnail);
        String albumTitle = allMedia.get(position).album;
        holder.title.setText(albumTitle);
        holder.title.setVisibility(View.VISIBLE);
        holder.title.bringToFront();

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(mContext, AlbumDetailActivity.class);
//                if (allMedia.get(0).type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
//                    intent = new Intent(mContext, FullImageActivity.class);
//                } else {
//                    intent = new Intent(mContext, FullVideoActivity.class);
//                }
                intent.putExtra("title", allMedia.get(position).album);
//                intent.putExtra("position", position);
//                intent.putExtra("list", allMedia);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allMedia.size();
    }

    public static class HolderView extends RecyclerView.ViewHolder {

        TextView title;
        ImageView thumbnail;
        SquareLayout view;

        public HolderView(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.album_title);
            thumbnail = itemView.findViewById(R.id.thumbnail_image);
            view = itemView.findViewById(R.id.item_holder);
        }
    }

}
