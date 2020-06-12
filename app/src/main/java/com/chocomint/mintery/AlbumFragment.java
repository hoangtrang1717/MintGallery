package com.chocomint.mintery;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;


public class AlbumFragment extends Fragment {
    Context context;
    RecyclerView recyclerView;
    ArrayList<Media> albumList;
    AlbumAdapter adapter;
    ChooseFileAdapter chooseFileAdapter;
    String from;
    ArrayList<String> fileChoose;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout_photo = null;

        fileChoose = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            from = bundle.getString("from", "");
            albumList = (ArrayList<Media>) bundle.getSerializable("list");
            if (albumList != null && albumList.size() > 0) {
                layout_photo = (LinearLayout) inflater.inflate(R.layout.fragment_album_layout, null);
                recyclerView = layout_photo.findViewById(R.id.album_recycle);
                adapter = new AlbumAdapter(getActivity(), albumList);
                recyclerView.setAdapter(adapter);

                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            } else {
                layout_photo = (ConstraintLayout) inflater.inflate(R.layout.photo_no_item, null);
            }
        }

        return layout_photo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

}
