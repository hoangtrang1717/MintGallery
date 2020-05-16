package com.example.gallery;

import android.graphics.Color;
import android.graphics.Typeface;
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
import com.example.gallery.Adapter.FontAdapter;
import com.example.gallery.Interface.TextFragmentListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TextFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorApdapterListener, FontAdapter.FontAdapterClickListener {

    int colorSelected = Color.parseColor("#000000"); // default text color

    TextFragmentListener listener;
    RecyclerView recyclerView_color, recyclerView_font;
    Button btnDone;
    EditText editAddText;
    Typeface typefaceSelected = Typeface.DEFAULT;
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

        recyclerView_font = (RecyclerView) itemView.findViewById(R.id.recyclerFont);
        recyclerView_font.setHasFixedSize(true);
        recyclerView_font.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        ColorAdapter colorAdapter = new ColorAdapter(getContext(),this);
        recyclerView_color.setAdapter(colorAdapter);

        FontAdapter fontAdapter = new FontAdapter(getContext(),this);
        recyclerView_font.setAdapter(fontAdapter);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddTextButtonClick(typefaceSelected,editAddText.getText().toString(),colorSelected);
            }
        });
        return itemView;
    }

    @Override
    public void onColorSelected(int color) {
        colorSelected = color;
    }

    @Override
    public void onFontSelected(String fontName) {
        typefaceSelected = Typeface.createFromAsset(getContext().getAssets(),new StringBuilder("fonts/")
                .append(fontName).toString());
    }
}
