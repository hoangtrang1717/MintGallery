package com.chocomint.mintery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Predicate;

import static java.io.File.separator;

public class MainActivity extends AppCompatActivity implements CallbackFunction {

    ImageButton photoTabbar, videoTabbar;
    FloatingActionButton albumTabbar;
    Fragment photoFrag, videoFrag, albumFrag;
    Toolbar mainToolbar;
    TextView toolBarText;
    ArrayList<Media> photoList, videoList, albumList;
    ArrayList<String> favoriteList;
    FavoriteDatabase favoriteDatabase;
    boolean isOpenedCamera;
    File photoFile;
    Uri photoURI;
    boolean isHasNewPhoto, isHasNewVideo;
    Menu toolbarMenu;
    String albumFolder;
    EditText albumTitle;

    final int PHOTO_FRAG = 1;
    final int ALBUM_FRAG = 2;
    final int VIDEO_FRAG = 3;
    int currentFrag;

    private final int REQUEST_READ_EXTERNAL = 1;
    private final int REQUEST_WRITE_EXTERNAL = 2;
    private final int REQUEST_CAMERA = 3;
    private final int REQUEST_TAKE_PHOTO = 4;
    private final int REQUEST_TAKE_VIDEO = 5;
    private final int REQUEST_FULL_IMAGE = 6;
    private final int REQUEST_FULL_VIDEO = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getView();
        isOpenedCamera = false;
        isHasNewPhoto = false;
        isHasNewVideo = false;
        currentFrag = PHOTO_FRAG;
        setSupportActionBar(mainToolbar);
        favoriteDatabase = new FavoriteDatabase(MainActivity.this);

        // set listener cho tabbar
        albumTabbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFrag = ALBUM_FRAG;
                toolBarText.setText("Albums");
                albumFrag = new AlbumFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", albumList);
                albumFrag.setArguments(bundle);
                videoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
                photoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, albumFrag, "album").commit();
            }
        });

        videoTabbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFrag = VIDEO_FRAG;
                videoFrag = new VideoFragment();
                toolbarMenu.setGroupVisible(R.id.camera_group, true);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", videoList);
                videoFrag.setArguments(bundle);
                toolBarText.setText("Videos");
                videoTabbar.setColorFilter(getResources().getColor(R.color.primary));
                photoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, videoFrag, "video").commit();
            }
        });

        photoTabbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFrag = PHOTO_FRAG;
                photoFrag = new PhotoFragment();
                toolbarMenu.setGroupVisible(R.id.camera_group, true);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", photoList);
                photoFrag.setArguments(bundle);
                toolBarText.setText("Photos");
                photoTabbar.setColorFilter(getResources().getColor(R.color.primary));
                videoTabbar.setColorFilter(Color.argb(60, 0, 0,0));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, photoFrag, "photo").commit();
            }
        });
    }

    private void getView() {
        albumTabbar = findViewById(R.id.album_tabbar);
        videoTabbar = findViewById(R.id.video_tabbar);
        photoTabbar = findViewById(R.id.photo_tabbar);
        mainToolbar = findViewById(R.id.main_toolbar);
        toolBarText = findViewById(R.id.toolbar_text);
        photoFrag = new PhotoFragment();
        videoFrag = new VideoFragment();
        albumFrag = new AlbumFragment();
        photoList = new ArrayList<>();
        videoList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL)) {
            new LoadImageAndVideo().execute();
        }
    }

    @Override
    protected void onDestroy() {
        favoriteDatabase.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbarMenu = menu;
        if (currentFrag == ALBUM_FRAG) {
            getMenuInflater().inflate(R.menu.album_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.toolbar_menu, menu);
            toolbarMenu.setGroupVisible(R.id.camera_group, true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera_toolbar:
                if (checkPermission(Manifest.permission.CAMERA, REQUEST_CAMERA) && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_EXTERNAL)) {
                    isOpenedCamera = true;
                    if (currentFrag == PHOTO_FRAG) {
                        dispatchTakePictureIntent();
                    } else if (currentFrag == VIDEO_FRAG) {
                        dispatchTakeVideoIntent();
                    }
                }
                return true;
            case R.id.about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                return true;
            case R.id.createFolder_toolbar:
                if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_EXTERNAL)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Create album");
                View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.input_dialog, null, false);
                albumTitle = (EditText) viewInflated.findViewById(R.id.albumFolder);
                builder.setView(viewInflated);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        albumFolder = albumTitle.getText().toString();
                        try {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.welcome_img);
                            SavePhoto savePhoto = new SavePhoto(bitmap, MainActivity.this, "welcomeImg", "image/jpg", MainActivity.this, albumFolder);
                            savePhoto.saveImagetoAlbum();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }


                    }
                });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO: {
                isOpenedCamera = false;
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                        SavePhoto savePhoto = new SavePhoto(bitmap, MainActivity.this, null, "image/jpg", this);
                        savePhoto.saveImage();
                        deleteTempFile();
                        isHasNewPhoto = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "An unexpected error has occured. Try again later.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    deleteTempFile();
                }
                break;
            }
            case REQUEST_TAKE_VIDEO: {
                isOpenedCamera = false;
                if (resultCode == RESULT_OK) {
                    isHasNewVideo = true;
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "You have cancelled capture video.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "An unexpected error has occured. Try again later.", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case REQUEST_FULL_IMAGE: {
                if (resultCode == RESULT_OK) {
                    isHasNewPhoto = true;
                }
                break;
            }
            default: return;
        }
    }

    @Override
    public void onAddPhotoSuccess() { }

    private class LoadImageAndVideo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL)) {
                fetchImageFromGallery();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            switch (currentFrag) {
                case PHOTO_FRAG:
                    photoTabbar.callOnClick();
                    break;
                case VIDEO_FRAG:
                    videoTabbar.callOnClick();
                    break;
                case ALBUM_FRAG:
                    albumTabbar.callOnClick();
                    break;
                default:
                    photoTabbar.callOnClick();
            }
            super.onPostExecute(aVoid);
        }
    }

    public boolean checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{permission}, requestCode);
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
            case REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_EXTERNAL) && !isOpenedCamera) {
                        isOpenedCamera = true;
                        dispatchTakePictureIntent();
                    }
                    break;
                }
            }
            case REQUEST_WRITE_EXTERNAL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPermission(Manifest.permission.CAMERA, REQUEST_CAMERA) && !isOpenedCamera) {
                        isOpenedCamera = true;
                        dispatchTakePictureIntent();
                    }
                    break;
                }
            }
            default: return;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Mintery_" + timeStamp + "_";
        File storageDir = getExternalCacheDir();
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private void deleteTempFile() {
        if (!photoFile.delete()) {
            Log.d("Error delete temp file", "Cannot delete");
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = null;
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(this,
                            "com.chocomint.mintery.provider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            } catch (IOException ex) {
                Toast.makeText(this, "An unexpected error has occured. Try again later.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
        }
    }

    private void fetchImageFromGallery() {
        if (photoList != null) {
            photoList.clear();
        } else {
            photoList = new ArrayList<>();
        }
        if (videoList != null) {
            videoList.clear();
        } else {
            videoList = new ArrayList<>();
        }
        if (albumList != null) {
            albumList.clear();
        } else {
            albumList = new ArrayList<>();
        }
        if (favoriteList != null) {
            favoriteList.clear();
        } else {
            favoriteList = new ArrayList<>();
        }

        try {
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            String[] imageColumns = { MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATE_MODIFIED,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.SIZE,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.MIME_TYPE
            };

            Cursor imagecursor = this.getApplication().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageColumns,
                    null,
                    null, // Selection args (none).
                    MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC" // Sort order.
            );

            int count = imagecursor.getCount();

            String[] videoColumns = { MediaStore.Video.VideoColumns._ID,
                    MediaStore.Video.VideoColumns.DATE_MODIFIED,
                    MediaStore.Video.VideoColumns.DURATION,
                    MediaStore.Video.VideoColumns.DATA,
                    MediaStore.Video.VideoColumns.DISPLAY_NAME,
                    MediaStore.Video.VideoColumns.SIZE,
                    MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Video.VideoColumns.MIME_TYPE,
                    MediaStore.Video.VideoColumns.MINI_THUMB_MAGIC
            };

            Cursor videocursor = this.getApplication().getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    videoColumns,
                    null,
                    null, // Selection args (none).
                    MediaStore.Video.VideoColumns.DATE_MODIFIED + " DESC" // Sort order.
            );

            int videoCount = videocursor.getCount();

            if (photoList != null && count > 0) {
                photoList.clear();
                // get all photo
                int image_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
                int date_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED);
                int data_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                int size_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE);
                int name_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
                int album_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
                int mime_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.MIME_TYPE);

                Date prev_date = new Date();
                int countPhotoInDate = 0;
                int positionOfDate = 0;
                for (int i = 0; i < count; i++) {
                    imagecursor.moveToPosition(i);
                    int id = imagecursor.getInt(image_column_index);
                    boolean favorite = favoriteDatabase.readFavorite(id, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);

                    Long currDate = imagecursor.getLong(date_column_index);
                    calendar.setTimeInMillis(currDate*1000L);
                    Date day = calendar.getTime();

                    if (i == 0) {
                        photoList.add(new Media(0, "nothing", MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, day, Long.getLong("123"), "nothing", "nothing", "nothing", false));
                        countPhotoInDate++;
                    } else {
                        if (prev_date.getDate() != day.getDate()) {
                            photoList.get(positionOfDate).setId(countPhotoInDate);
                            positionOfDate = photoList.size();
                            photoList.add(new Media(0, "nothing", MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, day, Long.getLong("123"), "nothing", "nothing", "nothing", false));
                            countPhotoInDate = 1;
                        } else {
                            countPhotoInDate++;
                        }
                    }

                    prev_date = day;

                    Long size = imagecursor.getLong(size_column_index);
                    String sizeStr = String.format("%.2f", (float) size / (1024 * 1024));
                    String name = imagecursor.getString(name_column_index);

                    String filePath = imagecursor.getString(data_column_index);
                    String album = imagecursor.getString(album_column_index);
                    Media media = new Media(id, filePath, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, day, (long) 0, name, sizeStr, album, favorite);
                    media.setMimeType(imagecursor.getString(mime_column_index));
                    media.setCountDate(positionOfDate);
                    photoList.add(media);
                    if (favorite == true) {
                        favoriteList.add(filePath);
                    }
                }
                if (photoList.get(1).type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                    Media recentsMedia = new Media(photoList.get(1).path, "Recents");
                    albumList.add(recentsMedia);
                }
                
                photoList.get(positionOfDate).setId(countPhotoInDate);
                imagecursor.close();
            }
            if (videoList != null && videoCount > 0) {
                videoList.clear();
                //get all video
                int video_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID);
                int video_duration_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION);
                int video_date_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_MODIFIED);
                int video_data_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA);
                int video_size_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.SIZE);
                int video_name_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DISPLAY_NAME);
                int video_album_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME);
                int mime_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.MIME_TYPE);
                int thumb_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.MINI_THUMB_MAGIC);

                Date prev_date = new Date();
                int countVideoInDate = 0;
                int positionOfDate = 0;
                for (int i = 0; i < videoCount; i++) {
                    videocursor.moveToPosition(i);
                    int id = videocursor.getInt(video_column_index);
                    boolean favorite = favoriteDatabase.readFavorite(id, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);

                    Long currDate = videocursor.getLong(video_date_column_index);
                    calendar.setTimeInMillis(currDate*1000L);
                    Date day = calendar.getTime();
                    if (i == 0) {
                        videoList.add(new Media(0, "nothing", MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, day, Long.getLong("123"), "nothing", "nothing", "nothing", false));
                        countVideoInDate++;
                    } else {
                        if (prev_date.getDate() != day.getDate()) {
                            videoList.get(positionOfDate).setId(countVideoInDate);
                            positionOfDate = videoList.size();
                            videoList.add(new Media(0, "nothing", MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, day, Long.getLong("123"), "nothing", "nothing", "nothing", false));
                            countVideoInDate = 1;
                        } else {
                            countVideoInDate++;
                        }
                    }

                    prev_date = day;

                    Long size = videocursor.getLong(video_size_column_index);
                    String sizeStr = String.format("%.2f", (float) size / (1024 * 1024));
                    String name = videocursor.getString(video_name_column_index);

                    Long duration = videocursor.getLong(video_duration_column_index);

                    String filePath = videocursor.getString(video_data_column_index);
                    String album = videocursor.getString(video_album_column_index);
                    Media media = new Media(id, filePath, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, day, duration, name, sizeStr, album, favorite);
                    media.setMimeType(videocursor.getString(mime_column_index));
                    media.setCountDate(positionOfDate);
                    videoList.add(media);
                    if (favorite == true) {
                        favoriteList.add(filePath);
                    }
                }
                videoList.get(positionOfDate).setId(countVideoInDate);
                videocursor.close();
            }


            if (favoriteList.size() > 0) {
                String favoriteThumbnail = favoriteList.get(0);
                Media favoriteMedia = new Media(favoriteThumbnail, "Favorites");
                albumList.add(favoriteMedia);
            }
            if (android.os.Build.VERSION.SDK_INT >= 29) {
                String[] albumColumn = { MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME};

                String mediaQuery = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
                        " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
                Cursor  albumCursor = this.getApplication().getContentResolver().query(
                        MediaStore.Files.getContentUri("external"),
                        albumColumn,
                        mediaQuery,
                        null, // Selection args (none).
                        null
                );

                int album_column_index_1 = albumCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME);
                int albumCount = albumCursor.getCount();

                ArrayList<String> albumName = new ArrayList<>();
                for (int i = 0; i < albumCount; i++) {
                    albumCursor.moveToPosition(i);
                    albumName.add(albumCursor.getString(album_column_index_1));
                }

                HashSet<String> uniqueAlbum = new HashSet<>(albumName);
                for (String name: uniqueAlbum) {
                    String[] query = {"MAX(" + MediaStore.Files.FileColumns.DATE_MODIFIED + ")",
                            MediaStore.Files.FileColumns.DATA
                    };
                    Cursor filter = this.getApplication().getContentResolver().query(
                            MediaStore.Files.getContentUri("external"),
                            query,
                            "(" + mediaQuery + ") AND " + MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME + " = ?",
                            new String[]{name},
                            null // Sort order.
                    );

                    int data_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);

                    filter.moveToPosition(0);
                    String filePath = filter.getString(data_column_index_1);
                    Media media = new Media(filePath, name);
                    albumList.add(media);
                    filter.close();
                }
                albumCursor.close();
            } else {
                String[] albumColumn = { "DISTINCT " + MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME};

                String mediaQuery = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
                        " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
                Cursor  albumCursor = this.getApplication().getContentResolver().query(
                        MediaStore.Files.getContentUri("external"),
                        albumColumn,
                        mediaQuery,
                        null, // Selection args (none).
                        null
                );

                int album_column_index_1 = albumCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME);
                int albumCount = albumCursor.getCount();

                for (int i = 0; i < albumCount; i++) {
                    albumCursor.moveToPosition(i);
                    String name = albumCursor.getString(album_column_index_1);

                    String[] query = {"MAX(" + MediaStore.Files.FileColumns.DATE_MODIFIED + ")",
                            MediaStore.Files.FileColumns.DATA
                    };
                    Cursor filter = this.getApplication().getContentResolver().query(
                            MediaStore.Files.getContentUri("external"),
                            query,
                            "(" + mediaQuery + ") AND " + MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME + " = ?",
                            new String[]{name},
                            null // Sort order.
                    );

                    int data_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);

                    filter.moveToPosition(0);
                    String filePath = filter.getString(data_column_index_1);
                    Media media = new Media(filePath, name);
                    albumList.add(media);
                    filter.close();
                }

                albumCursor.close();
            }


        } catch (Exception e) {
            Log.d("Error getting data", e.getMessage());
            e.printStackTrace();
        }
    }
}
