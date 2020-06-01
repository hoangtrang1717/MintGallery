package com.chocomint.mintery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chocomint.mintery.Adapter.EmojiAdapter;
import com.chocomint.mintery.Interface.EmojiFragmentListener;

import ja.burhanrashid52.photoeditor.PhotoEditor;

public class EmojiFragment extends Fragment implements EmojiAdapter.EmojiAdapterListner {
    RecyclerView recyclerView;
    static EmojiFragment instance;

    EmojiFragmentListener listner;

    public void setListener(EmojiFragmentListener listner) {
        this.listner = listner;
    }

    public static EmojiFragment getInstance(){
        if(instance == null){
            instance = new EmojiFragment();
        }
        return instance;
    }

    public EmojiFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_emoji, container, false);

        recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerViewEmoji);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),7));

        EmojiAdapter adapter = new EmojiAdapter(getContext(), PhotoEditor.getEmojis(getContext()),this);
        recyclerView.setAdapter(adapter);
        return itemView;
    }

    @Override
    public void onEmojiItemSelected(String emoji) {
        listner.onEmojiSelected(emoji);
    }
}
