package com.chocomint.mintery;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;

public class FullVideoFragment extends Fragment{
    PlayerView playerView;
    PreviewTimeBar previewTimeBar;
    ImageView imageView;
    PlayerManager playerManager;
    String id;
    Uri uri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout_video = (ConstraintLayout) inflater.inflate(R.layout.full_video_layout, null);

        Bundle bundle = getArguments();
        if (bundle != null) {
            id = String.valueOf(bundle.getInt("id"));
        }

        playerView = layout_video.findViewById(R.id.video_view);
        previewTimeBar = playerView.findViewById(R.id.exo_progress);
        imageView = playerView.findViewById(R.id.image_preview);
        uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
        playerManager = new PlayerManager(playerView, previewTimeBar, imageView);
        playerManager.play(uri);
        playerManager.onStart();

        return layout_video;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        playerManager.pause();
        super.onPause();
    }

    @Override
    public void onStop() {
        playerManager.onStop();
        super.onStop();
    }
}
