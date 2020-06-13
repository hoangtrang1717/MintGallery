package com.chocomint.mintery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class AlbumDetailActivity extends AppCompatActivity {
    ArrayList<Media> arrayList;
    Toolbar mainToolbar;
    TextView toolBarText;
    Fragment mediaFrag;
    FavoriteDatabase favoriteDatabase;
    String path, albumTitle;

    final int REQUEST_READ_WRITE_EXTERNAL = 123;
    final int REQUEST_WRITE_EXTERNAL_DELETE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_album_detail);
        getView();
        setSupportActionBar(mainToolbar);
        favoriteDatabase = new FavoriteDatabase(AlbumDetailActivity.this);
    }

    @Override
    protected void onResume() {
        albumTitle = getIntent().getExtras().getString("title");
        path = getIntent().getExtras().getString("path");
        toolBarText.setText(albumTitle);
        new LoadDataThread().execute(albumTitle);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        favoriteDatabase.close();
    }

    @Override
    protected void onDestroy() {
        favoriteDatabase.close();
        super.onDestroy();
    }

    private void getView() {
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolBarText = (TextView) findViewById(R.id.toolbar_text);
        mediaFrag = new AlbumDetailFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_detail_menu, menu);
        if (albumTitle.equals("Favorites") || albumTitle.equals("Recents")) {
            MenuItem item = menu.findItem(R.id.delete_album);
            item.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_album: {
                File directory = new File(String.valueOf(Paths.get(path).getParent()));
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    deleteDir(directory);
                    onBackPressed();
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_DELETE);
                }
            }
            return true;
            case R.id.about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < arrayList.size(); i++) {
                    int delete = 0;
                    if (arrayList.get(i).type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                        delete = getContentResolver().delete(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(i).id)), null, null);
                    } else if (arrayList.get(i).type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                        delete = getContentResolver().delete(Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(i).id)), null, null);
                    }
                    if (delete > 0) {
                        arrayList.remove(i);
                    }
            }
        }
        return dir.delete();
    }

    private class LoadDataThread extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {

            arrayList = new ArrayList<Media>();
            if (strings[0].equals("Favorites")){
                String[] query = { MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DATE_MODIFIED,
                        MediaStore.Images.ImageColumns.DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.SIZE
                };
                Cursor filter = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        query,
                        null,
                        null,
                        MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC" // Sort order.
                );

                int image_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
                int date_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED);
                int data_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                int size_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE);
                int name_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);

                int countquery = filter.getCount();
                Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                for (int i = 0; i < countquery; i++) {
                    filter.moveToPosition(i);
                    int id = filter.getInt(image_column_index_1);
                    boolean favorite = favoriteDatabase.readFavorite(id, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                    Long currDate = filter.getLong(date_column_index_1);
                    calendar.setTimeInMillis(currDate*1000L);
                    Date day = calendar.getTime();

                    Long size = filter.getLong(size_column_index_1);
                    String sizeStr = String.format("%.2f", (float) size / (1024 * 1024));
                    String name = filter.getString(name_column_index_1);

                    String filePath = filter.getString(data_column_index_1);
                    if (favorite == true) {
                        Media media = new Media(id, filePath, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, day, (long) 0, name, sizeStr, strings[0], favorite);
                        arrayList.add(media);
                    }
                }

                String[] videoColumns = { MediaStore.Video.VideoColumns._ID,
                        MediaStore.Video.VideoColumns.DATE_MODIFIED,
                        MediaStore.Video.VideoColumns.DURATION,
                        MediaStore.Video.VideoColumns.DATA,
                        MediaStore.Video.VideoColumns.DISPLAY_NAME,
                        MediaStore.Video.VideoColumns.SIZE,
                };

                Cursor videocursor = getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        videoColumns,
                        null,
                        null,
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

                    boolean favorite = favoriteDatabase.readFavorite(id, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                    Long currDate = videocursor.getLong(video_date_column_index);
                    calendar.setTimeInMillis(currDate*1000L);
                    Date day = calendar.getTime();

                    Long size = videocursor.getLong(video_size_column_index);
                    String sizeStr = String.format("%.2f", (float) size / (1024 * 1024));
                    String name = videocursor.getString(video_name_column_index);

                    Long duration = videocursor.getLong(video_duration_column_index);

                    String filePath = videocursor.getString(video_data_column_index);
                    Media media = new Media(id, filePath, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, day, duration, name, sizeStr, strings[0], favorite);
                    arrayList.add(media);
                }
                videocursor.close();
            } else if (strings[0].equals("Recents")) {
                String[] query = { MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DATE_MODIFIED,
                        MediaStore.Images.ImageColumns.DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.SIZE
                };
                Cursor filter = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        query,
                        MediaStore.Images.ImageColumns.DATE_MODIFIED + ">" + (System.currentTimeMillis() / 1000 - 7*60*60*24),
                        null,
                        MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC" // Sort order.
                );

                int image_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
                int date_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED);
                int data_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                int size_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE);
                int name_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);

                int countquery = filter.getCount();
                Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                for (int i = 0; i < countquery; i++) {
                    filter.moveToPosition(i);
                    int id = filter.getInt(image_column_index_1);
                    boolean favorite = favoriteDatabase.readFavorite(id, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                    Long currDate = filter.getLong(date_column_index_1);
                    calendar.setTimeInMillis(currDate*1000L);
                    Date day = calendar.getTime();

                    Long size = filter.getLong(size_column_index_1);
                    String sizeStr = String.format("%.2f", (float) size / (1024 * 1024));
                    String name = filter.getString(name_column_index_1);

                    String filePath = filter.getString(data_column_index_1);
                    Media media = new Media(id, filePath, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, day, (long) 0, name, sizeStr, strings[0], favorite);
                    arrayList.add(media);
                }

                String[] videoColumns = { MediaStore.Video.VideoColumns._ID,
                        MediaStore.Video.VideoColumns.DATE_MODIFIED,
                        MediaStore.Video.VideoColumns.DURATION,
                        MediaStore.Video.VideoColumns.DATA,
                        MediaStore.Video.VideoColumns.DISPLAY_NAME,
                        MediaStore.Video.VideoColumns.SIZE,
                };

                Cursor videocursor = getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        videoColumns,
                        MediaStore.Video.VideoColumns.DATE_MODIFIED + ">" + (System.currentTimeMillis() / 1000 - 7*60*60*24),
                        null,
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

                    boolean favorite = favoriteDatabase.readFavorite(id, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                    Long currDate = videocursor.getLong(video_date_column_index);
                    calendar.setTimeInMillis(currDate*1000L);
                    Date day = calendar.getTime();

                    Long size = videocursor.getLong(video_size_column_index);
                    String sizeStr = String.format("%.2f", (float) size / (1024 * 1024));
                    String name = videocursor.getString(video_name_column_index);

                    Long duration = videocursor.getLong(video_duration_column_index);

                    String filePath = videocursor.getString(video_data_column_index);
                    Media media = new Media(id, filePath, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, day, duration, name, sizeStr, strings[0], favorite);
                    arrayList.add(media);
                }
                videocursor.close();
            } else {
                String[] query = { MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DATE_MODIFIED,
                        MediaStore.Images.ImageColumns.DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.SIZE
                };
                Cursor filter = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        query,
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " = ?",
                        new String[] {strings[0]},
                        MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC" // Sort order.
                );

                int image_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
                int date_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED);
                int data_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                int size_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE);
                int name_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);

                int countquery = filter.getCount();
                Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                for (int i = 0; i < countquery; i++) {
                    filter.moveToPosition(i);
                    int id = filter.getInt(image_column_index_1);

                    Long currDate = filter.getLong(date_column_index_1);
                    boolean favorite = favoriteDatabase.readFavorite(id, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                    calendar.setTimeInMillis(currDate*1000L);
                    Date day = calendar.getTime();

                    Long size = filter.getLong(size_column_index_1);
                    String sizeStr = String.format("%.2f", (float) size / (1024 * 1024));
                    String name = filter.getString(name_column_index_1);

                    String filePath = filter.getString(data_column_index_1);
                    Media media = new Media(id, filePath, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, day, (long) 0, name, sizeStr, strings[0], favorite);
                    arrayList.add(media);
                }

                String[] videoColumns = { MediaStore.Video.VideoColumns._ID,
                        MediaStore.Video.VideoColumns.DATE_MODIFIED,
                        MediaStore.Video.VideoColumns.DURATION,
                        MediaStore.Video.VideoColumns.DATA,
                        MediaStore.Video.VideoColumns.DISPLAY_NAME,
                        MediaStore.Video.VideoColumns.SIZE,
                };

                Cursor videocursor = getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        videoColumns,
                        MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME + " = ?",
                        new String[] {strings[0]},
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
                    boolean favorite = favoriteDatabase.readFavorite(id, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                    Long currDate = videocursor.getLong(video_date_column_index);
                    calendar.setTimeInMillis(currDate*1000L);
                    Date day = calendar.getTime();

                    Long size = videocursor.getLong(video_size_column_index);
                    String sizeStr = String.format("%.2f", (float) size / (1024 * 1024));
                    String name = videocursor.getString(video_name_column_index);

                    Long duration = videocursor.getLong(video_duration_column_index);

                    String filePath = videocursor.getString(video_data_column_index);
                    Media media = new Media(id, filePath, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, day, duration, name, sizeStr, strings[0], favorite);
                    arrayList.add(media);
                }
                videocursor.close();
            }


            Collections.sort(arrayList, new SortByModified());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("list", arrayList);
            mediaFrag = new AlbumDetailFragment();
            mediaFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, mediaFrag, "media").commit();
        }
    }
}
