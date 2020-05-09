package com.example.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static androidx.core.content.FileProvider.getUriForFile;

public class CameraActivity extends Activity{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_READ_PERMISSION = 1;
    private static final int REQUEST_WRITE_PERMISSION = 2;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    ArrayList<ImageInformation> arr = new ArrayList<>();
    File imageFile;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        else{
            dispatchTakePictureIntent();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                dispatchTakePictureIntent();
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        if(requestCode == REQUEST_WRITE_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }
            else{
                finish();
            }
        }
        if(requestCode == REQUEST_READ_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }
            else{
                finish();
            }
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if(photoFile == null){
                recreate();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
               Uri photoURI = getUriForFile(this,
                       "com.example.gallery.provider",
                       photoFile);
                imageFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                ImageInformation information = new ImageInformation();
                information.setPath(photoFile.getAbsolutePath());
                information.setThumb(photoFile.getAbsolutePath());
                Date date = new Date();
                information.setDateTaken(date);
                arr.add(0, information);

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO ) {
            if (resultCode == RESULT_OK) {
                Intent i = new Intent(this.getApplicationContext(), FullImageActivity.class);
                i.putExtra("id", 1);
                i.putExtra("position", 0);
                i.putExtra("list", arr);
                startActivity(i);
            } else {
                System.out.println(imageFile.exists());
                System.out.println(imageFile.getAbsoluteFile().exists());

                if (imageFile.exists()) {
                    System.out.println(imageFile.getAbsolutePath());
                    System.out.println(imageFile.delete());
                    System.out.println(imageFile.exists());
                }
            }
        }
        finish();
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MINT_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"MintGallery");

        if(!storageDir.exists()){
            boolean success = storageDir.mkdirs();
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        MediaScannerConnection.scanFile(this,
                new String[] {image.getAbsolutePath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

        return image;
    }
}
