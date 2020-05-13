package com.chocomint.mintery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.exifinterface.media.ExifInterface;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

public class MediaDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    Media media;
    TextView date, size, title, path, resolution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);

        getView();

        toolbar = findViewById(R.id.media_detail_toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        new LoadDetail().execute();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        date.setText(simpleDateFormat.format(media.dateModified));
        path.setText(media.path);
    }

    private void getView() {
        media = (Media) getIntent().getSerializableExtra("media");
        date = findViewById(R.id.text_modified);
        size = findViewById(R.id.text_size);
        path = findViewById(R.id.text_path);
        title = findViewById(R.id.text_title);
        resolution = findViewById(R.id.text_resolution);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadDetail extends AsyncTask<Void, String, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(media.id));
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uri);
                ExifInterface exifInterface = new ExifInterface(inputStream);
                int width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
                int height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
                publishProgress(width + " x " + height);
//                String gpsInfo = exifInterface.getAttribute(ExifInterface.TAG_GPS_AREA_INFORMATION);
////                Log.d("latlong", "" + latLong);
//                Log.d("width", String.valueOf(width));
//                Log.d("height", String.valueOf(height));
//                Log.d("position", gpsInfo != null ? gpsInfo : "Không có");
            } catch (IOException e) {
                Log.d("error load detail", e.getMessage());
                return false;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ignored) {}
                }
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0] != null) {
                resolution.setText(values[0]);
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean) {
                Toast.makeText(MediaDetailActivity.this, "Đã có lỗi xảy ra khi đọc thông tin", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aBoolean);
        }
    }
}
