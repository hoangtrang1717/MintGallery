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

        colorAdapter = new ColorAdapter(getContext(),genColorList(),this);
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

    private List<Integer> genColorList() {
        List<Integer> colorList = new ArrayList<>();
        /*facebook pallette*/
        colorList.add(Color.parseColor("#3b5998"));
        colorList.add(Color.parseColor("#8b9dc3"));
        colorList.add(Color.parseColor("#dfe3ee"));
        colorList.add(Color.parseColor("#f7f7f7"));
        colorList.add(Color.parseColor("#ffffff"));
        /*Puple pallette*/
        colorList.add(Color.parseColor("#efbbff"));
        colorList.add(Color.parseColor("#d896ff"));
        colorList.add(Color.parseColor("#be29ec"));
        colorList.add(Color.parseColor("#800080"));
        colorList.add(Color.parseColor("#660066"));
        /*Blue gray pallette*/
        colorList.add(Color.parseColor("#6e7f80"));
        colorList.add(Color.parseColor("#536872"));
        colorList.add(Color.parseColor("#708090"));
        colorList.add(Color.parseColor("#536878"));
        colorList.add(Color.parseColor("#36454f"));
        /*pastel rainbow*/
        colorList.add(Color.parseColor("#a8e6cf"));
        colorList.add(Color.parseColor("#dcedc1"));
        colorList.add(Color.parseColor("#ffd3b6"));
        colorList.add(Color.parseColor("#ffaaa5"));
        colorList.add(Color.parseColor("#ff8b94"));
        /*rainbow dash*/
        colorList.add(Color.parseColor("#ee4035"));
        colorList.add(Color.parseColor("#f37736"));
        colorList.add(Color.parseColor("#fdf498"));
        colorList.add(Color.parseColor("#7bc043"));
        colorList.add(Color.parseColor("#0392cf"));
        return colorList;
    }

    @Override
    public void onColorSelected(int color) {
        listener.onBrushColorChangedListener(color);
    }
}
