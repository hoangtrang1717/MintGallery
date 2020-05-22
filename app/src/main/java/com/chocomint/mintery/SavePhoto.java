package com.chocomint.mintery;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static java.io.File.separator;

public class SavePhoto {
    String mimeType;
    String format;
    Bitmap bitmap;
    Context context;
    String fileName;
    CallbackFunction callbackFunction;

    public SavePhoto(Bitmap bitmap, Context context, String fileName, String mimeType, CallbackFunction callbackFunction) {
        this.bitmap = bitmap;
        this.context = context;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.format = mimeType.substring(mimeType.indexOf('/') + 1);
        this.callbackFunction = callbackFunction;
    }

    public SavePhoto(Context context, String fileName, String mimeType, CallbackFunction callbackFunction) {
        this.context = context;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.format = mimeType.substring(mimeType.indexOf('/') + 1);
        this.callbackFunction = callbackFunction;
    }

    public void saveImage() throws FileNotFoundException {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "Mintery");
            values.put(MediaStore.Images.Media.IS_PENDING, true);
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                saveImageToStream(bitmap, context.getContentResolver().openOutputStream(uri));
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                context.getContentResolver().update(uri, values, null, null);
            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().toString() + separator + "Mintery");

            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = this.fileName == null ? System.currentTimeMillis() + "." + format : this.fileName;
            File file = new File(directory, fileName);
            saveImageToStream(bitmap, new FileOutputStream(file));
            if (file.getAbsolutePath() != null) {
                ContentValues values = contentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
        callbackFunction.onAddPhotoSuccess();
    }

    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                String imFormat = format.compareTo("jpg") == 0 ? "JPEG" : format.toUpperCase();
                bitmap.compress(Bitmap.CompressFormat.valueOf(imFormat), 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
