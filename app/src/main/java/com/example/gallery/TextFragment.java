package com.example.gallery;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.gallery.Adapter.ColorAdapter;
import com.example.gallery.Interface.TextFragmentListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TextFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorApdapterListener {

    int colorSelected = Color.parseColor("#000000"); // default text color

    TextFragmentListener listener;
    RecyclerView recyclerView_color;
    Button btnDone;
    EditText editAddText;
    static TextFragment instance;
    public static TextFragment getInstance(){
        if(instance==null)
        {
            instance=new TextFragment();
        }
        return instance;
    }
    public void setListner(TextFragmentListener listener){
        this.listener=listener;
    }
    public TextFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_text, container, false);
        editAddText = (EditText) itemView.findViewById(R.id.editAddText);
        btnDone =(Button) itemView.findViewById(R.id.btnDone);
        recyclerView_color = (RecyclerView) itemView.findViewById(R.id.recyclerColor);
        recyclerView_color.setHasFixedSize(true);
        recyclerView_color.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        ColorAdapter colorAdapter = new ColorAdapter(getContext(),this);
        recyclerView_color.setAdapter(colorAdapter);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddTextButtonClick(editAddText.getText().toString(),colorSelected);
            }
        });
        return itemView;
    }

    @Override
    public void onColorSelected(int color) {
        colorSelected = color;
    }
}
