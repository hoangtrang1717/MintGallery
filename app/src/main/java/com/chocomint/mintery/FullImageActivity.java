package com.chocomint.mintery;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Predicate;

public class FullImageActivity extends AppCompatActivity implements CallbackFunction {

    ViewPager slider;
    Toolbar img_toolbar;
    FullImageSlider imageSlider;
    ArrayList<Media> arrayList;
    BottomAppBar bottomTab;
    int CurrentPosition;
    Menu menuImage;
    FavoriteDatabase favoriteDatabase;
    AlertDialog dialog;

    ImageButton cropBtn, editBtn, shareBtn, deleteBtn;

    final int SET_WALLPAPER = 1;
    final int SET_LOCKSCREEN = 2;
    final int SET_ALL = 3;

    final int REQUEST_READ_WRITE_EXTERNAL = 123;
    final int REQUEST_WRITE_EXTERNAL = 124;
    private final int REQUEST_EDIT_IMAGE = 6;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);

        getView();
        favoriteDatabase = new FavoriteDatabase(FullImageActivity.this);

        String path = getIntent().getExtras().getString("id");
        slider = (ViewPager) findViewById(R.id.image_viewpaprer);
        arrayList = (ArrayList<Media>) getIntent().getSerializableExtra("list");
        CurrentPosition = getIntent().getExtras().getInt("position");

        //
        int temp = 0;
        for(int i = 0; i < CurrentPosition; i++)
        {
            if(arrayList.get(i).path.compareTo("nothing") == 0) {
                temp = temp + 1;
            }
        }
        CurrentPosition = CurrentPosition - temp;
        //
        arrayList.removeIf(new Predicate<Media>() {
            @Override
            public boolean test(Media media) {
                if (media.path.compareTo("nothing") == 0) {
                    return true;
                }
                return false;
            }
        });
        //
        imageSlider = new FullImageSlider(FullImageActivity.this, arrayList, path);
        slider.setAdapter(imageSlider);
        slider.setCurrentItem(CurrentPosition);

        slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportActionBar().isShowing()) {
                    getSupportActionBar().hide();
                    bottomTab.setVisibility(View.INVISIBLE);
                    slider.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
                } else {
                    getSupportActionBar().show();
                    bottomTab.setVisibility(View.VISIBLE);
                    slider.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
                }
            }
        });

        slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                CurrentPosition = position;
                setFavoriteIcon();
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(FullImageActivity.this, CropImageActivity.class);
                    intent.putExtra("uri", Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(CurrentPosition).id)).toString());
                    startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_WRITE_EXTERNAL);
                }
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayList.get(CurrentPosition).mimeType.contains("gif")) {
                    Toast.makeText(FullImageActivity.this, "Cannot edit gif.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(FullImageActivity.this, EditImageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("path", arrayList.get(CurrentPosition).path);
                    startActivityForResult(intent, REQUEST_EDIT_IMAGE);
                }
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ShareThread().execute();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    showDialogDelete();
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favoriteDatabase.close();
    }

    private void getView() {
        cropBtn = findViewById(R.id.image_crop);
        editBtn = findViewById(R.id.image_edit);
        shareBtn = findViewById(R.id.image_share);
        deleteBtn = findViewById(R.id.image_delete);
        bottomTab = findViewById(R.id.full_image_bottomtab);

        img_toolbar = (Toolbar) findViewById(R.id.image_toolbar);
        this.setSupportActionBar(img_toolbar);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuImage = menu;
        getMenuInflater().inflate(R.menu.image_menu_toolbar, menu);
        setFavoriteIcon();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detail:
                Intent intent = new Intent(this, MediaDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("media", arrayList.get(CurrentPosition));
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.img_favorite:
                new FavoriteThread().execute(arrayList.get(CurrentPosition).isFavorite);
                return true;
            case R.id.wallpaper:
                AlertDialog.Builder builder = new AlertDialog.Builder(FullImageActivity.this);
                View view = getLayoutInflater().inflate(R.layout.set_wallpaper_dialog, null);
                final Button setWallpaper = view.findViewById(R.id.button_set_wallpaper);
                final Button setLockscreen = view.findViewById(R.id.button_set_lockscreen);
                final Button setAll = view.findViewById(R.id.button_set_all);
                setWallpaper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new SetWallpaperThread().execute(SET_WALLPAPER);
                        dialog.dismiss();
                    }
                });
                setLockscreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new SetWallpaperThread().execute(SET_LOCKSCREEN);
                        dialog.dismiss();
                    }
                });
                setAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new SetWallpaperThread().execute(SET_ALL);
                        dialog.dismiss();
                    }
                });
                builder.setView(view);
                dialog = builder.create();
                dialog.show();
                return true;
            case R.id.about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setFavoriteIcon() {
        MenuItem menuItem =  menuImage.findItem(R.id.img_favorite);
        if (arrayList.get(CurrentPosition).isFavorite) {
            menuItem.setIcon(R.drawable.ic_heart_sharp);
        } else {
            menuItem.setIcon(R.drawable.ic_heart_outline);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_WRITE_EXTERNAL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CropImage.activity(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(CurrentPosition).id)))
                            .start(FullImageActivity.this);
                }
            }
            case REQUEST_WRITE_EXTERNAL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showDialogDelete();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    onBackPressed();
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.d("Error crop image", "");
                }
                break;
            }
            case REQUEST_EDIT_IMAGE: {
                if (resultCode == RESULT_OK) {
                    onBackPressed();
                }
            }
            default: return;
        }
    }

    @Override
    public void onAddPhotoSuccess() {
        setResult(RESULT_OK);
        onBackPressed();
    }

    private void showDialogDelete() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(FullImageActivity.this);
        myAlertDialog.setTitle("Delete Photo");
        myAlertDialog.setMessage("Do you want to delete it?");
        myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                int delete = getContentResolver().delete(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(CurrentPosition).id)), null, null);
                if (delete > 0) {
                    arrayList.remove(CurrentPosition);
                    if (arrayList.size() < 1) {
                        onBackPressed();
                    }
                    imageSlider.notifyDataSetChanged();
                }
            }});
        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }});
        myAlertDialog.show();
    }

    private class ShareThread extends AsyncTask <Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(CurrentPosition).id)));
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, "Share this image"));
            } catch (ActivityNotFoundException e) {
                return false;
            }
            return true;
        }
    }

    private class SetWallpaperThread extends AsyncTask <Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... integers) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(FullImageActivity.this);
            Bitmap bmap2 = BitmapFactory.decodeFile(arrayList.get(CurrentPosition).path);

            try {
                if (integers[0] == SET_WALLPAPER) {
                    wallpaperManager.setBitmap(bmap2, null, true, WallpaperManager.FLAG_SYSTEM);
                } else if (integers[0] == SET_LOCKSCREEN) {
                    wallpaperManager.setBitmap(bmap2, null, true, WallpaperManager.FLAG_LOCK);
                } else if (integers[0] == SET_ALL) {
                    wallpaperManager.setBitmap(bmap2, null, true, WallpaperManager.FLAG_SYSTEM);
                    wallpaperManager.setBitmap(bmap2, null, true, WallpaperManager.FLAG_LOCK);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    private class FavoriteThread extends AsyncTask <Boolean, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            boolean isDone;
            if (booleans != null && booleans[0]) {
                isDone = favoriteDatabase.deleteFavorite(arrayList.get(CurrentPosition).id, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            } else {
                isDone = favoriteDatabase.insertFavorite(arrayList.get(CurrentPosition).id, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            }
            return isDone;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                MenuItem menuItem =  menuImage.findItem(R.id.img_favorite);
                if(arrayList.get(CurrentPosition).setFavorite()) {
                    menuItem.setIcon(R.drawable.ic_heart_sharp);
                } else {
                    menuItem.setIcon(R.drawable.ic_heart_outline);
                }
                imageSlider.notifyDataSetChanged();
            } else {
                Toast.makeText(FullImageActivity.this, "An unexpected error has occured. Try again later.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
