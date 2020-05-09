package com.example.gallery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gallery.Adapter.StickerAdapter;
import com.example.gallery.Interface.AddStickerListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class StickerFragment extends BottomSheetDialogFragment implements StickerAdapter.StickerAdapterListener {

    RecyclerView recyclerView;
    Button btnSticker;
    static StickerFragment instance;

    int sticker_selected =-1;

    AddStickerListener listener;

    public void setListener(AddStickerListener listener) {
        this.listener = listener;
    }

    public static StickerFragment getInstance(){
        if(instance == null){
            instance = new StickerFragment();
        }
        return instance;
    }
    public StickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_sticker,container,false);

        recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerViewSticker);
        btnSticker = (Button) itemView.findViewById(R.id.btnAddSticker);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(new StickerAdapter(getContext(),this));
        btnSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddSticker(sticker_selected);
            }
        });
        return itemView;
    }

    @Override
    public void onStickerSelected(int sticker) {
        sticker_selected = sticker;
    }

}
