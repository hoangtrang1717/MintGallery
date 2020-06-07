package com.chocomint.mintery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.exifinterface.media.ExifInterface;

import android.location.Address;
import android.location.Geocoder;
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
import java.util.List;
import java.util.Locale;

public class MediaDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    Media media;
    TextView date, size, title, path, location;

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

        if (media.type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            new LoadDetail().execute();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        date.setText(simpleDateFormat.format(media.dateModified));
        path.setText(media.path);
        title.setText(media.name);
        size.setText(media.size + " MB");
    }

    private void getView() {
        media = (Media) getIntent().getExtras().getSerializable("media");
        date = findViewById(R.id.text_modified);
        size = findViewById(R.id.text_size);
        path = findViewById(R.id.text_path);
        title = findViewById(R.id.text_title);
        location = findViewById(R.id.text_location);
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
                double[] latlong = exifInterface.getLatLong();

                if (Geocoder.isPresent() && latlong != null && latlong.length > 0) {
                    Geocoder geocoder = new Geocoder(MediaDetailActivity.this, Locale.getDefault());
                    List<Address> address = geocoder.getFromLocation(latlong[0], latlong[1], 1);
                    if (address != null && address.size() > 0) {
                        Address address1 = address.get(0);
                        publishProgress(address1.getAddressLine(address1.getMaxAddressLineIndex()));
                    }
                }
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
                location.setText(values[0]);
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
