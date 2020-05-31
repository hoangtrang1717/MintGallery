package com.chocomint.mintery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.chocomint.mintery.Interface.BrushFragmentListener;

public class BrushFragment extends Fragment {
    SeekBar seekBar_brush_size;
    RadioGroup brushGroup;
    BrushFragmentListener listener;
    LinearLayout pickColor;
    ColorPickerDialog colorPickerDialog;
    static BrushFragment instance;
    public static BrushFragment getInstance(){
        if(instance == null)
        {
            instance = new BrushFragment();
        }
        return instance;
    }

    public void setListener(BrushFragmentListener listener) {
        this.listener = listener;
    }

    public BrushFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_brush, container, false);

        seekBar_brush_size = (SeekBar)itemView.findViewById(R.id.seekbar_brush_size);
        brushGroup = itemView.findViewById(R.id.radio_group);
        pickColor = itemView.findViewById(R.id.pick_color);

        colorPickerDialog = ColorPickerDialog.createColorPickerDialog(getContext(), R.style.CustomColorPicker);
        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                String alpha = hexVal.substring(1, 3);
                int opacity = Integer.parseInt(alpha, 16) * 100 / 255;
                listener.onBrushOpacityChangedListener(opacity);
                listener.onBrushColorChangedListener(color);
            }
        });

        pickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorPickerDialog.show();
            }
        });

        brushGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioChoose) {
                if (radioChoose == R.id.radio_brush) {
                    pickColor.setVisibility(View.VISIBLE);
                    listener.onBrushStateChangedListener(false);
                } else if (radioChoose == R.id.radio_eraser) {
                    pickColor.setVisibility(View.GONE);
                    listener.onBrushStateChangedListener(true);
                }
            }
        });

        seekBar_brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listener.onBrushSizeChangedListener(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        return itemView;
    }
}
