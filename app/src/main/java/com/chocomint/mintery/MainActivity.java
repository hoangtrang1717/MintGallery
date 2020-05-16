package com.chocomint.mintery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;

public class MainActivity extends AppCompatActivity {

    ImageButton photoTabbar, videoTabbar;
    FloatingActionButton albumTabbar;
    Fragment photoFrag, videoFrag, albumFrag;
    Toolbar mainToolbar;
    TextView toolBarText;
    ArrayList<Media> arrayList, photoList, videoList;

    final int PHOTO_FRAG = 1;
    final int ALBUM_FRAG = 2;
    final int VIDEO_FRAG = 3;
    int currentFrag;

    private final int REQUEST_READ_EXTERNAL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getView();
        new MainActivity.LoadImageAndVideo().execute();
        setSupportActionBar(mainToolbar);

        // set listener cho tabbar
        albumTabbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFrag = ALBUM_FRAG;
                toolBarText.setText("Album");
                videoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
                photoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, albumFrag).commit();
            }
        });

        videoTabbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFrag = VIDEO_FRAG;
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", videoList);
                videoFrag.setArguments(bundle);
                toolBarText.setText("Videos");
                videoTabbar.setColorFilter(getResources().getColor(R.color.primary));
                photoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, videoFrag).commit();
            }
        });

        photoTabbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFrag = PHOTO_FRAG;
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", photoList);
                photoFrag.setArguments(bundle);
                toolBarText.setText("Photos");
                photoTabbar.setColorFilter(getResources().getColor(R.color.primary));
                videoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, photoFrag).commit();
            }
        });


    }

    private void getView() {
        albumTabbar = findViewById(R.id.album_tabbar);
        videoTabbar = findViewById(R.id.video_tabbar);
        photoTabbar = findViewById(R.id.photo_tabbar);
        mainToolbar = findViewById(R.id.main_toolbar);
        toolBarText = findViewById(R.id.toolbar_text);
        photoList = new ArrayList<>();
        videoList = new ArrayList<>();
        photoFrag = new PhotoFragment();
        videoFrag = new VideoFragment();
        albumFrag = new AlbumFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera_toolbar:
                Toast.makeText(this, "Hit camera", Toast.LENGTH_LONG).show();
                return true;
            case R.id.about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            photoList = (ArrayList<Media>) arrayList.clone();
            photoList.removeIf(new Predicate<Media>() {
                @Override
                public boolean test(Media media) {
                    if (media.type != MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                        return true;
                    }
                    return false;
                }
            });
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
            photoTabbar.callOnClick();
            super.onPostExecute(aVoid);
        }
    }

    public boolean checkPermission(String permission) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED) {
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
                    new MainActivity.LoadImageAndVideo().execute();
                    break;
                }
            }
            default: return;
        }
    }

    private void fetchImageFromGallery() {
        if (arrayList != null) {
            arrayList.clear();
        } else {
            arrayList = new ArrayList<>();
        }
        try {
            String[] imageColumns = { MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATE_MODIFIED,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.SIZE,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME
            };

            Cursor imagecursor = this.getApplication().getContentResolver().query(
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

            String[] videoColumns = { MediaStore.Video.VideoColumns._ID,
                    MediaStore.Video.VideoColumns.DATE_MODIFIED,
                    MediaStore.Video.VideoColumns.DURATION,
                    MediaStore.Video.VideoColumns.DATA,
                    MediaStore.Video.VideoColumns.DISPLAY_NAME,
                    MediaStore.Video.VideoColumns.SIZE
            };

            Cursor videocursor = this.getApplication().getContentResolver().query(
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
            int video_size_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.SIZE);
            int video_name_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DISPLAY_NAME);

            int videoCount = videocursor.getCount();
            for (int i = 0; i < videoCount; i++) {
                videocursor.moveToPosition(i);
                int id = videocursor.getInt(video_column_index);

                Long currDate = videocursor.getLong(video_date_column_index);
                calendar.setTimeInMillis(currDate*1000L);
                Date day = calendar.getTime();

                Long size = videocursor.getLong(video_size_column_index);
                String sizeStr = String.format("%.2f", (float) size / (1024 * 1024));
                String name = videocursor.getString(video_name_column_index);

                Long duration = videocursor.getLong(video_duration_column_index);

                String filePath = videocursor.getString(video_data_column_index);
                arrayList.add(new Media(id, filePath, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, day, duration, name, sizeStr));
            }
            videocursor.close();

            Collections.sort(arrayList, new SortByModified());
        } catch (Exception e) {
            Log.d("Error getting data", e.getMessage());
            e.printStackTrace();
        }
    }
}
