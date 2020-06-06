package com.chocomint.mintery;

import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class Media implements Serializable {
    int id;
    String path;
    int type;
    Date dateModified;
    Long duration;
    String name;
    String size;
    String album;
    PlayerManager playerManager;
    String mimeType;
    boolean isFavorite;

    public Media(int id, String path, int type, Date dateModified, Long duration, String name, String size, String album, boolean isFavorite) {
        this.id = id;
        this.path = path;
        this.type = type;
        this.dateModified = dateModified;
        this.duration = duration;
        this.name = name;
        this.size = size;
        this.album = album;
        this.isFavorite = isFavorite;
    }

    public Media(Media m) {
        this.id = m.id;
        this.path = m.path;
        this.type = m.type;
        this.dateModified = m.dateModified;
        this.duration = m.duration;
        this.name = m.name;
        this.size = m.size;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean setFavorite() {
        this.isFavorite = !this.isFavorite;
        return this.isFavorite;
    }

    public void setPlayerManager(final PlayerView playerView, PreviewTimeBar previewTimeBar, final ImageView imageView, Uri uri) {
        playerManager = new PlayerManager(playerView, previewTimeBar, imageView);
        playerManager.play(uri);
    }

    public void playVideo() {
        playerManager.onStart();
    }

    public void stopVideo() {
        playerManager.onStop();
    }

    public boolean pauseVideo() {
        return playerManager.pause();
    }
}
