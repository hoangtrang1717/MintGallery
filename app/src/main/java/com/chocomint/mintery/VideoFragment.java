package com.chocomint.mintery;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;

public class VideoFragment extends Fragment implements ChooseFileCallback {
    Context context;
    RecyclerView recyclerView;
    ArrayList<Media> arrayList, videoList;
    ArrayList<String> fileChoose;
    ImageAdapter adapter;
    ChooseFileAdapter chooseFileAdapter;
    String from;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout_photo = (LinearLayout) inflater.inflate(R.layout.fragment_video_layout, null);
        recyclerView = layout_photo.findViewById(R.id.video_recycle);

        fileChoose = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            from = bundle.getString("from", "");
            arrayList = (ArrayList<Media>) bundle.getSerializable("list");
            videoList = (ArrayList<Media>) arrayList.clone();
            videoList.removeIf(new Predicate<Media>() {
                @Override
                public boolean test(Media media) {
                    if (media.type != MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                        return true;
                    }
                    return false;
                }
            });
        }

        if (from.compareTo("DELETE") == 0) {
            chooseFileAdapter = new ChooseFileAdapter(getActivity(), videoList, arrayList, this);
            recyclerView.setAdapter(chooseFileAdapter);
        } else {
            adapter = new ImageAdapter(getActivity(), videoList, arrayList);
            recyclerView.setAdapter(adapter);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));

        return layout_photo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void chooseFile(int position, boolean add) {
        if (add) {
            fileChoose.add(String.valueOf(videoList.get(position).id));
        } else {
            fileChoose.remove(String.valueOf(videoList.get(position).id));
        }
    }

    public void ShareVideo() {
        new VideoFragment.ShareThread().execute();
    }

    private class ShareThread extends AsyncTask <Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (fileChoose != null && fileChoose.size() > 0) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    ArrayList<Uri> files = new ArrayList<>();
                    for (String id : fileChoose) {
                        files.add(Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id));
                    }
                    shareIntent.putExtra(Intent.EXTRA_STREAM, files);
                    shareIntent.setType("video/*");
                    startActivity(Intent.createChooser(shareIntent, "Share videos"));
                } else {
                    Toast.makeText(getContext(), "You did not choose any video", Toast.LENGTH_LONG).show();
                }
            } catch (ActivityNotFoundException e) {
                return false;
            }
            return true;
        }
    }

    public void DeleteVideos() {
        new VideoFragment.DeleteThread().execute();
    }

    private class DeleteThread extends AsyncTask <Void, Boolean, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                for (String id : fileChoose) {
                    getActivity().getContentResolver().delete(Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id), null, null);
                }
            } catch (Throwable e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                getActivity().onBackPressed();
            } else {
                Toast.makeText(getContext(), "Error. Try again later", Toast.LENGTH_LONG);
            }
        }
    }
}

