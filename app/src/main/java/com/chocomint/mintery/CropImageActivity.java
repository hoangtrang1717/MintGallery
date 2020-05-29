package com.chocomint.mintery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class CropImageActivity extends AppCompatActivity {

    LinearLayout btnCrop11, btnCrop32, btnCrop23, btnCrop43, btnCrop34, btnCrop169, btnCrop916, btnCropCustom;
    CropImageView cropImageView;
    Toolbar cropToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        getView();

        Uri uri = Uri.parse(getIntent().getStringExtra("uri"));
        cropImageView.setImageUriAsync(uri);

        cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                if (result.isSuccessful()) {
                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                    Bitmap bitmap = result.getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                    byte[] byteArray = bStream.toByteArray();
                    Intent intent = new Intent();
                    intent.putExtra("bitmap", byteArray);
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                } else {
                    Toast.makeText(CropImageActivity.this, "An unexpected error has occured. Try agian  later.", Toast.LENGTH_LONG).show();
                }
            }
        });
        cropImageView.getCroppedImageAsync();

        btnCrop11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.setAspectRatio(1, 1);
                cropImageView.setFixedAspectRatio(true);
                //btnCrop11.setFo
            }
        });
        btnCrop23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.setAspectRatio(2, 3);
                cropImageView.setFixedAspectRatio(true);
            }
        });
        btnCrop32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.setAspectRatio(3, 2);
                cropImageView.setFixedAspectRatio(true);
            }
        });
        btnCrop34.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.setAspectRatio(3, 4);
                cropImageView.setFixedAspectRatio(true);
            }
        });
        btnCrop43.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.setAspectRatio(4, 3);
                cropImageView.setFixedAspectRatio(true);
            }
        });
        btnCrop169.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.setAspectRatio(16, 9);
                cropImageView.setFixedAspectRatio(true);
            }
        });
        btnCrop916.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.setAspectRatio(9, 16);
                cropImageView.setFixedAspectRatio(true);
            }
        });
        btnCropCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.setFixedAspectRatio(false);
            }
        });
    }

    private void getView() {
        btnCrop11 = findViewById(R.id.crop_11);
        btnCrop23 = findViewById(R.id.crop_23);
        btnCrop32 = findViewById(R.id.crop_32);
        btnCrop34 = findViewById(R.id.crop_34);
        btnCrop43 = findViewById(R.id.crop_43);
        btnCrop169 = findViewById(R.id.crop_169);
        btnCrop916 = findViewById(R.id.crop_916);
        btnCropCustom = findViewById(R.id.crop_custom);
        cropImageView = findViewById(R.id.crop_image_view);
        cropToolbar = (Toolbar) findViewById(R.id.crop_toolbar);
        cropToolbar.setTitle("");
        setSupportActionBar(cropToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crop_menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.crop_crop:
                cropImageView.getCroppedImageAsync();
                return true;
            case R.id.flip_horizontal:
                cropImageView.flipImageHorizontally();
                return true;
            case R.id.flip_vertical:
                cropImageView.flipImageVertically();
                return true;
            case R.id.crop_rotate:
                cropImageView.rotateImage(90);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
