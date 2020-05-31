package com.chocomint.mintery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chocomint.mintery.Interface.EditImageFragmentListener;

public class EditImageFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private EditImageFragmentListener listener;
    SeekBar seekBar_brightness, seekBar_contrast, seekBar_saturation;
    static EditImageFragment instance;

    public void setListener(EditImageFragmentListener listener) {
        this.listener = listener;
    }

    public static EditImageFragment getInstance(){
        if(instance == null){
            instance = new EditImageFragment();
        }
        return  instance;
    }
    public EditImageFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_edit_image, container, false);

        seekBar_brightness = (SeekBar) itemView.findViewById(R.id.seekbar_brightness);
        seekBar_contrast = (SeekBar) itemView.findViewById(R.id.seekbar_constrast);
        seekBar_saturation = (SeekBar) itemView.findViewById(R.id.seekbar_saturation);

        seekBar_brightness.setMax(200);
        seekBar_brightness.setProgress(100);

        seekBar_contrast.setMax(20);
        seekBar_contrast.setProgress(0);

        seekBar_saturation.setMax(30);
        seekBar_saturation.setProgress(10);

        seekBar_saturation.setOnSeekBarChangeListener(this);
        seekBar_contrast.setOnSeekBarChangeListener(this);
        seekBar_brightness.setOnSeekBarChangeListener(this);
        return itemView;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if(listener != null){
            if(seekBar.getId() == R.id.seekbar_brightness){
                listener.onBrightnessChanged(progress - 10);
            }
            else if(seekBar.getId() == R.id.seekbar_constrast){
                progress += 10;
                float value = .10f * progress;
                listener.onContrastChanged(value);
            }
            else if(seekBar.getId() == R.id.seekbar_saturation){
                float value = .10f * progress;
                listener.onSaturationChanged(value);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(listener != null){
            listener.onEditStarted();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(listener != null){
            listener.onEditCompleted();
        }
    }
}
