package com.example.gallery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gallery.Adapter.EmojiAdapter;
import com.example.gallery.Interface.EmojiFragmentListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;


public class EmojiFragment extends BottomSheetDialogFragment implements EmojiAdapter.EmojiAdapterListner {

    RecyclerView recyclerView;
    static EmojiFragment instance;

    EmojiFragmentListener listner;

    public void setListner(EmojiFragmentListener listner) {
        this.listner = listner;
    }

    public static  EmojiFragment getInstance(){
        if(instance==null){
            instance = new EmojiFragment();
        }
        return instance;
    }

    public EmojiFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_emoji, container, false);

        recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerViewEmoji);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),5));

        EmojiAdapter adapter= new EmojiAdapter(getContext(), PhotoEditor.getEmojis(getContext()),this);
        recyclerView.setAdapter(adapter);
        return itemView;
   }

    @Override
    public void onEmojiItemSelected(String emoji) {
        listner.onEmojiSelected(emoji);
    }
}
