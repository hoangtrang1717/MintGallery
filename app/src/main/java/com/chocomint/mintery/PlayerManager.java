package com.chocomint.mintery;

import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.rubensousa.previewseekbar.PreviewLoader;
import com.github.rubensousa.previewseekbar.PreviewView;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class PlayerManager implements PreviewLoader {

    private PlayerView playerView;
    private ExoPlayer player;
    private PreviewTimeBar previewTimeBar;
    private ImageView imageView;
    private Uri uri;

    private Player.EventListener eventListener = new Player.DefaultEventListener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_READY && playWhenReady) {
                previewTimeBar.hidePreview();
            }
        }
    };

    public PlayerManager(final PlayerView playerView,
                         PreviewTimeBar previewTimeBar, final ImageView imageView) {
        this.playerView = playerView;
        this.imageView = imageView;
        this.previewTimeBar = previewTimeBar;
        this.previewTimeBar.setPreviewLoader(this);
        this.previewTimeBar.addOnPreviewChangeListener(new PreviewView.OnPreviewChangeListener() {
            @Override
            public void onStartPreview(PreviewView previewView, int progress) {
                player.setPlayWhenReady(false);
            }

            @Override
            public void onStopPreview(PreviewView previewView, int progress) {
                player.seekTo(progress);
                player.setPlayWhenReady(true);
            }

            @Override
            public void onPreview(PreviewView previewView, int progress, boolean fromUser) { }
        });
    }

    public void play(Uri uri) {
        this.uri = uri;
    }

    public boolean pause() {
        player.setPlayWhenReady(!player.getPlayWhenReady());
        return player.getPlayWhenReady();
    }

    public void onStart() {
        if (Util.SDK_INT > 23) {
            createPlayers();
            player.setPlayWhenReady(true);
        }
    }

    public void onResume() {
        if (Util.SDK_INT <= 23) {
            createPlayers();
        }
    }

    public void onStop() {
        if (Util.SDK_INT > 23) {
            releasePlayers();
        }
    }

    private void releasePlayers() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void createPlayers() {
        if (player != null) {
            player.release();
        }
        player = createFullPlayer();
        playerView.setPlayer(player);
    }

    private ExoPlayer createFullPlayer() {
        TrackSelection.Factory videoTrackSelectionFactory
                = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        ExoPlayer player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(playerView.getContext()),
                trackSelector, loadControl);
        player.setPlayWhenReady(true);
        String userAgent = Util.getUserAgent(playerView.getContext(), "VideoPlayer");
        ExtractorMediaSource mediaSource = new ExtractorMediaSource(uri,
                new DefaultDataSourceFactory(playerView.getContext(), userAgent),
                new DefaultExtractorsFactory(),
                null,
                null);
        player.prepare(mediaSource);
        player.addListener(eventListener);
        return player;
    }

    @Override
    public void loadPreview(long currentPosition, long max) {
        player.setPlayWhenReady(false);
        long interval = currentPosition * 1000;
        RequestOptions options = new RequestOptions().frame(interval);
        Glide.with(playerView.getContext()).asBitmap()
                .load(uri)
                .apply(options)
                .into(imageView);

    }
}