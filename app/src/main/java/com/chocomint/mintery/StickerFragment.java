package com.chocomint.mintery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chocomint.mintery.Adapter.StickerAdapter;
import com.chocomint.mintery.Interface.AddStickerListener;

public class StickerFragment extends Fragment implements StickerAdapter.StickerAdapterListener {

    RecyclerView recyclerView;
    static StickerFragment instance;

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
    public StickerFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_sticker,container,false);

        recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerViewSticker);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        recyclerView.setAdapter(new StickerAdapter(getContext(),this));

        return itemView;
    }

    @Override
    public void onStickerSelected(int sticker) {
        listener.onAddSticker(sticker);
    }
}
