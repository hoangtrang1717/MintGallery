package com.chocomint.mintery;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PhotoFragment extends Fragment {
    Context context;
    RecyclerView recyclerView;
    ArrayList<Media> arrayList;
    ImageAdapter adapter;

    private final int REQUEST_READ_EXTERNAL = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout_photo = (LinearLayout) inflater.inflate(R.layout.fragment_photo_layout, null);
        recyclerView = layout_photo.findViewById(R.id.photo_reycle);
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
                fetchImageFromGallery();
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

    private void fetchImageFromGallery() {
        try {
            String[] imageColumns = { MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATE_MODIFIED,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.SIZE,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME
            };

            Cursor imagecursor = getActivity().getApplication().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageColumns,
                    null,
                    null, // Selection args (none).
                    MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC" // Sort order.
            );

            int image_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
            int date_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED);
            int data_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
            int size_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE);
            int name_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);

            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            int count = imagecursor.getCount();
            for (int i = 0; i < count; i++) {
                imagecursor.moveToPosition(i);
                int id = imagecursor.getInt(image_column_index);

                Long currDate = imagecursor.getLong(date_column_index);
                calendar.setTimeInMillis(currDate*1000L);
                Date day = calendar.getTime();

                Long size = imagecursor.getLong(size_column_index);
                String sizeStr = String.format("%.2f", (float) size / (1024 * 1024));
                String name = imagecursor.getString(name_column_index);

                String filePath = imagecursor.getString(data_column_index);
                arrayList.add(new Media(id, filePath, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, day, (long) 0, name, sizeStr));
            }
            imagecursor.close();
            Collections.sort(arrayList, new SortByModified());
        } catch (Exception e) {
            Log.d("Error getting image", e.getMessage());
            e.printStackTrace();
        }
    }
}
