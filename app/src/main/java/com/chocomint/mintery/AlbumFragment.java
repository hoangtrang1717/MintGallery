package com.chocomint.mintery;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {

    Context context;
    RecyclerView recyclerView;
    ArrayList<String> albumList;
    ArrayList<String> fileChoose;
    ImageAdapter adapter;
    ChooseFileAdapter chooseFileAdapter;
    String from;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layout_photo = null;
        fileChoose = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            from = bundle.getString("from", "");
            albumList = (ArrayList<String>) bundle.getSerializable("list");
            albumList.add("Favorites");
            albumList.add("Recents");
            if (albumList != null && albumList.size() > 0) {
                layout_photo = (LinearLayout) inflater.inflate(R.layout.fragment_album_layout, null);
                recyclerView = layout_photo.findViewById(R.id.video_recycle);
                if (from.compareTo("DELETE") == 0) {
                    //chooseFileAdapter = new ChooseFileAdapter(getActivity(), albumList, this);
                    recyclerView.setAdapter(chooseFileAdapter);
                } else {
                    //adapter = new ImageAdapter(getActivity(), albumList);
                    //recyclerView.setAdapter(adapter);
                    Log.e("ALBUM", albumList.toString());
                }
                recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
            } else {
                layout_photo = (ConstraintLayout) inflater.inflate(R.layout.video_no_item, null);
            }
        }

        return layout_photo;
    }
}
