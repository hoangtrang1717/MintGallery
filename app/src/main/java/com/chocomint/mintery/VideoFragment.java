package com.chocomint.mintery;

import android.Manifest;
import android.content.Context;
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

public class VideoFragment extends Fragment {
    Context context;
    RecyclerView recyclerView;
    ArrayList<Media> arrayList;
    ImageAdapter adapter;

    private final int REQUEST_READ_EXTERNAL = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout_photo = (LinearLayout) inflater.inflate(R.layout.fragment_video_layout, null);
        recyclerView = layout_photo.findViewById(R.id.video_recycle);
        arrayList = new ArrayList<>();
        adapter = new ImageAdapter(getActivity(), arrayList);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
        recyclerView.setAdapter(adapter);

        return layout_photo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        new LoadImageAndVideo().execute();
        super.onCreate(savedInstanceState);
    }

    private class LoadImageAndVideo extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                fetchVideoFromGallery();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }
    }

    public boolean checkPermission(String permission) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new LoadImageAndVideo().execute();
                }
            }
        }
    }

    private void fetchVideoFromGallery() {
        try {
            String[] videoColumns = { MediaStore.Video.VideoColumns._ID,
                    MediaStore.Video.VideoColumns.DATE_MODIFIED,
                    MediaStore.Video.VideoColumns.DURATION,
                    MediaStore.Video.VideoColumns.DATA
            };

            Cursor videocursor = getActivity().getApplication().getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    videoColumns,
                    null,
                    null, // Selection args (none).
                    MediaStore.Video.VideoColumns.DATE_MODIFIED + " DESC" // Sort order.
            );

            int video_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID);
            int video_duration_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION);
            int video_date_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_MODIFIED);
            int video_data_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA);

            int videoCount = videocursor.getCount();
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            for (int i = 0; i < videoCount; i++) {
                videocursor.moveToPosition(i);
                int id = videocursor.getInt(video_column_index);

                Long currDate = videocursor.getLong(video_date_column_index);
                calendar.setTimeInMillis(currDate*1000L);
                Date day = calendar.getTime();

                Long duration = videocursor.getLong(video_duration_column_index);

                String filePath = videocursor.getString(video_data_column_index);
                arrayList.add(new Media(id, filePath, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, day, duration));
            }
            videocursor.close();
            Collections.sort(arrayList, new SortByModified());
        } catch (Exception e) {
            Log.d("Error getting video", e.getMessage());
            e.printStackTrace();
        }
    }
}

