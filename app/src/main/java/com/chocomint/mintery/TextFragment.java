package com.chocomint.mintery;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.chocomint.mintery.Adapter.FontAdapter;
import com.chocomint.mintery.Interface.TextFragmentListener;

import ja.burhanrashid52.photoeditor.TextStyleBuilder;

public class TextFragment extends Fragment implements FontAdapter.FontAdapterClickListener {
    int colorSelected = Color.parseColor("#ff000000");

    TextFragmentListener listener;
    RecyclerView recyclerView_font;
    ImageButton btnDone;
    LinearLayout pickColor;
    ColorPickerDialog colorPickerDialog;
    EditText editAddText;
    Typeface typefaceSelected = Typeface.DEFAULT;
    static TextFragment instance;
    public static TextFragment getInstance(){
        if(instance == null)
        {
            instance = new TextFragment();
        }
        return instance;
    }
    public void setListener(TextFragmentListener listener){
        this.listener = listener;
    }
    public TextFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_text, container, false);
        editAddText = (EditText) itemView.findViewById(R.id.editAddText);
        btnDone = (ImageButton) itemView.findViewById(R.id.btnDone);
        typefaceSelected = Typeface.createFromAsset(getContext().getAssets(), "fonts/poppins.ttf");
        editAddText.setTextColor(colorSelected);

        pickColor = itemView.findViewById(R.id.pick_color_text);

        colorPickerDialog = ColorPickerDialog.createColorPickerDialog(getContext(), R.style.CustomColorPicker);
        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                colorSelected = Color.parseColor(hexVal);
                editAddText.setTextColor(color);
            }
        });

        pickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorPickerDialog.show();
            }
        });

        recyclerView_font = (RecyclerView) itemView.findViewById(R.id.recyclerFont);
        recyclerView_font.setHasFixedSize(true);
        recyclerView_font.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        FontAdapter fontAdapter = new FontAdapter(getContext(),this);
        recyclerView_font.setAdapter(fontAdapter);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editText = editAddText.getText().toString();
                if (editText.isEmpty()) {
                    Toast.makeText(getContext(), "You didn't enter any word", Toast.LENGTH_LONG).show();
                    return;
                }
                TextStyleBuilder textStyleBuilder = new TextStyleBuilder();
                textStyleBuilder.withTextFont(typefaceSelected);
                textStyleBuilder.withTextColor(colorSelected);
                listener.onAddTextButtonClick(editText, textStyleBuilder);
            }
        });
        return itemView;
    }

    @Override
    public void onFontSelected(String fontName) {
        typefaceSelected = Typeface.createFromAsset(getContext().getAssets(),new StringBuilder("fonts/")
                .append(fontName).toString());
        editAddText.setTypeface(typefaceSelected);
    }
}
