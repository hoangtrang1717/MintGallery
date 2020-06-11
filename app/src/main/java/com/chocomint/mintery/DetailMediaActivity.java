package com.chocomint.mintery;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.ArrayList;

public class DetailMediaActivity extends AppCompatActivity implements CallbackFunction {

    Context context = this;
    ViewPager slider;
    Toolbar media_toolbar;
    FullImageSlider imageSlider;
    ArrayList<Media> arrayList;
    BottomAppBar mediaBottomTab;
    int CurrentPosition;
    Menu mediaMenu;
    FavoriteDatabase favoriteDatabase;

    ImageButton firstBtn, secondBtn, thirdBtn, fourthBtn;

    boolean isPlaying;

    final int REQUEST_READ_WRITE_EXTERNAL = 123;
    int type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getExtras().getInt("type");

        setContentView(R.layout.detail_item_layout);
        getView();
        if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            firstBtn.setImageResource(R.drawable.ic_scissors);
            secondBtn.setImageResource(R.drawable.ic_pause);
        }

        favoriteDatabase = new FavoriteDatabase(DetailMediaActivity.this);
        String path = getIntent().getExtras().getString("id");
        slider = (ViewPager) findViewById(R.id.media_viewpaprer);
        arrayList = (ArrayList<Media>) getIntent().getSerializableExtra("list");
        CurrentPosition = getIntent().getExtras().getInt("position");
        imageSlider = new FullImageSlider(DetailMediaActivity.this, arrayList, path);
        slider.setAdapter(imageSlider);
        slider.setCurrentItem(CurrentPosition);
        if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            isPlaying = true;
            slider.setOffscreenPageLimit(1);
            imageSlider.setCurrentVideo(CurrentPosition);
        }

        slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportActionBar().isShowing()) {
                    getSupportActionBar().hide();
                    mediaBottomTab.setVisibility(View.GONE);
                    slider.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
                } else {
                    getSupportActionBar().show();
                    mediaBottomTab.setVisibility(View.VISIBLE);
                    slider.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
                }
            }
        });

        slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (arrayList.get(position).type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                    type = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
                    setVideo(position);
                } else if (arrayList.get(position).type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                    type = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
                    setImage(position);
                }
                setFavoriteIcon();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        firstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                    Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(CurrentPosition).id));
                    if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        CropImage.activity(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(CurrentPosition).id)))
                                .start(DetailMediaActivity.this);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_WRITE_EXTERNAL);
                    }
                } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                    pauseVideo();
                    Toast.makeText(view.getContext(), "Hit trim", Toast.LENGTH_LONG).show();
                }
            }
        });

        secondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                    Toast.makeText(view.getContext(), "Hit edit", Toast.LENGTH_LONG).show();
                } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                    isPlaying = imageSlider.pauseVideo(CurrentPosition);
                    if (isPlaying) {
                        secondBtn.setImageResource(R.drawable.ic_pause);
                    } else {
                        secondBtn.setImageResource(R.drawable.ic_play);
                    }
                }
            }
        });

        thirdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                    new ShareThread().execute();
                } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                    pauseVideo();
                    new DetailMediaActivity.ShareVideoThread().execute();
                }
            }
        });

        fourthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    CropImage.activity(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(CurrentPosition).id)))
                            .start(DetailMediaActivity.this);
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_WRITE_EXTERNAL);
                }
                if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                    AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(DetailMediaActivity.this);
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
                        }
                    });
                    myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    myAlertDialog.show();
                } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                    pauseVideo();
                    AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(DetailMediaActivity.this);
                    myAlertDialog.setTitle("Delete Video");
                    myAlertDialog.setMessage("Do you want to delete it?");
                    myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(CurrentPosition).id));
                            int delete = getContentResolver().delete(uri, null, null);

                            if (delete > 0) {

                                arrayList.remove(CurrentPosition);
                                if (arrayList.size() < 1) {
                                    onBackPressed();
                                }
                                imageSlider.stopVideo(CurrentPosition);
                                imageSlider.notifyDataSetChanged();
                            }

                        }
                    });
                    myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    myAlertDialog.show();
                }
            }
        });
    }

    protected void setVideo(int position) {
        if (arrayList.get(CurrentPosition).type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            imageSlider.stopVideo(CurrentPosition);
        } else {
            firstBtn.setImageResource(R.drawable.ic_scissors);
            secondBtn.setImageResource(R.drawable.ic_pause);
        }
        isPlaying=true;
        CurrentPosition = position;
        slider.setCurrentItem(CurrentPosition);
        slider.setOffscreenPageLimit(1);
        imageSlider.setCurrentVideo(CurrentPosition);
        imageSlider.playVideo(CurrentPosition);
        if (!isPlaying) {
            secondBtn.setImageResource(R.drawable.ic_play);
        }
        setFavoriteIcon();
    }

    protected void setImage(int position) {
        if (arrayList.get(CurrentPosition).type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            imageSlider.stopVideo(CurrentPosition);
        }
        firstBtn.setImageResource(R.drawable.ic_crop);
        secondBtn.setImageResource(R.drawable.ic_sliders);
        CurrentPosition = position;
        slider.setCurrentItem(CurrentPosition);
        setFavoriteIcon();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favoriteDatabase.close();
    }

    private void getView() {
        firstBtn = findViewById(R.id.firstIcon);
        secondBtn = findViewById(R.id.secondIcon);
        thirdBtn = findViewById(R.id.thirdIcon);
        fourthBtn = findViewById(R.id.fouthIcon);
        mediaBottomTab = findViewById(R.id.full_media_bottomtab);

        media_toolbar = (Toolbar) findViewById(R.id.media_toolbar);
        this.setSupportActionBar(media_toolbar);
        this.getSupportActionBar().setTitle("");
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mediaMenu = menu;
        getMenuInflater().inflate(R.menu.video_menu_toolbar, menu);
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
        MenuItem menuItem =  mediaMenu.findItem(R.id.img_favorite);
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
                            .start(DetailMediaActivity.this);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    String uriToString = resultUri.toString();
                    try {
                        Bitmap bitmap = null;
                        if (Build.VERSION.SDK_INT < 28) {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                        } else {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), resultUri);
                            bitmap = ImageDecoder.decodeBitmap(source);
                        }
                        String format = uriToString.substring(uriToString.lastIndexOf('.') + 1);
                        SavePhoto savePhoto = new SavePhoto(bitmap, this.getBaseContext(), null, "image/jpg", this);
                        savePhoto.saveImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("Error create new photo", e.getMessage());
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Log.d("Error crop image", error.getMessage());
                }
                break;
            }
            default: return;
        }
    }

    @Override
    public void onAddPhotoSuccess() {
        onBackPressed();
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

    private class ShareVideoThread extends AsyncTask <Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(arrayList.get(CurrentPosition).id)));
                shareIntent.setType("video/*");
                startActivity(Intent.createChooser(shareIntent, "Share this video"));
            } catch (ActivityNotFoundException e) {
                Log.d("Error share", e.getMessage());
                return false;
            }
            return true;
        }
    }

    private class FavoriteThread extends AsyncTask <Boolean, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            boolean isDone;
            Log.e("Type", String.valueOf(arrayList.get(CurrentPosition).type));
            if (arrayList.get(CurrentPosition).type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
                if (booleans != null && booleans[0]) {
                    isDone = favoriteDatabase.deleteFavorite(arrayList.get(CurrentPosition).id, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                } else {
                    isDone = favoriteDatabase.insertFavorite(arrayList.get(CurrentPosition).id, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                }
            } else {
                if (booleans != null && booleans[0]) {
                    isDone = favoriteDatabase.deleteFavorite(arrayList.get(CurrentPosition).id, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                } else {
                    isDone = favoriteDatabase.insertFavorite(arrayList.get(CurrentPosition).id, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                }
            }
            return isDone;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                MenuItem menuItem =  mediaMenu.findItem(R.id.img_favorite);
                if(arrayList.get(CurrentPosition).setFavorite()) {
                    menuItem.setIcon(R.drawable.ic_heart_sharp);
                } else {
                    menuItem.setIcon(R.drawable.ic_heart_outline);
                }
                imageSlider.notifyDataSetChanged();
            } else {
                Toast.makeText(DetailMediaActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void hideActionBar() {
        if (getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
            mediaBottomTab.setVisibility(View.INVISIBLE);
        } else {
            getSupportActionBar().show();
            mediaBottomTab.setVisibility(View.VISIBLE);
        }
    }

    private void pauseVideo() {
        if (isPlaying) {
            isPlaying = imageSlider.pauseVideo(CurrentPosition);
            secondBtn.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseVideo();
    }
}
