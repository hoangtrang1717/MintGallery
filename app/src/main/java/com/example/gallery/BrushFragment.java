package com.example.gallery;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.Adapter.ColorAdapter;
import com.example.gallery.Interface.BrushFragmentListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BrushFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorApdapterListener {

    SeekBar seekBar_brush_size, seekBar_opacity_size;
    RecyclerView recyclerView_color;
    ToggleButton btnBrushState;
    ColorAdapter colorAdapter;
    BrushFragmentListener listener;
    static BrushFragment instance;
    public static BrushFragment getInstance(){
        if(instance==null)
        {
            instance=new BrushFragment();
        }
        return instance;
    }

    public void setListener(BrushFragmentListener listener) {
        this.listener = listener;
    }

    public BrushFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_brush, container, false);

        seekBar_brush_size = (SeekBar)itemView.findViewById(R.id.seekbar_brush_size);
        seekBar_opacity_size = (SeekBar)itemView.findViewById(R.id.seekbar_brush_opacity);
        btnBrushState = (ToggleButton) itemView.findViewById(R.id.btnBrushState);
        recyclerView_color = (RecyclerView) itemView.findViewById(R.id.recyclerViewBrush);
        recyclerView_color.setHasFixedSize(true);
        recyclerView_color.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        colorAdapter = new ColorAdapter(getContext(),this);
        recyclerView_color.setAdapter(colorAdapter);

        //event
        seekBar_opacity_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listener.onBrushOpacityChangedListener(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar_brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listener.onBrushSizeChangedListener(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnBrushState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.onBrushStateChangedListener(isChecked);
            }
        });

        return itemView;
    }

    @Override
    public void onColorSelected(int color) {
        listener.onBrushColorChangedListener(color);
    }
}
