package com.chocomint.mintery.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitmapUtils {
    public static Bitmap getBitmapFromAsset(Context context, String fileName, int width, int height){
        AssetManager assetManager = context.getAssets();
        InputStream inputStream;
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            inputStream =assetManager.open(fileName);
            options.inSampleSize =calculateInSampleSize(options,width,height);
            options.inJustDecodeBounds=false;
            return BitmapFactory.decodeStream(inputStream,null,options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmapFromGallery(Context context, String picturePath,int width,int height){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath,options);
        options.inSampleSize =calculateInSampleSize(options,width,height);
        options.inJustDecodeBounds=false;
        System.out.println("size"+picturePath);
        return BitmapFactory.decodeFile(picturePath,options);
    }

    public static Bitmap applyOverlay(Context context, Bitmap sourceImage, int overlayDrawableResourceId){
        Bitmap bitmap = null;
        try{
            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();
            Resources r = context.getResources();

            Drawable imageAsDrawable =  new BitmapDrawable(r, sourceImage);
            Drawable[] layers = new Drawable[2];

            layers[0] = imageAsDrawable;
            layers[1] = new BitmapDrawable(r, BitmapUtils.decodeSampledBitmapFromResource(r, overlayDrawableResourceId, width, height));
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            bitmap = BitmapUtils.drawableToBitmap(layerDrawable);
        }catch (Exception ex){}
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    public static String insertImage(ContentResolver cr, Bitmap src, String title, String description) throws IOException {
        ContentValues values =new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME,title);
        values.put(MediaStore.Images.Media.DESCRIPTION,description);
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED,System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN,System.currentTimeMillis());
        Uri uri =null;
        String stringUri = null;
        try{
            uri =cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
            if(src!=null){
                OutputStream outputStream = cr.openOutputStream(uri);
                try {
                    src.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                }finally{
                    outputStream.close();

                }
                long id = ContentUris.parseId(uri);
                Bitmap minithumb = MediaStore.Images.Thumbnails.getThumbnail(cr,id,MediaStore.Images.Thumbnails.MINI_KIND,null);
                storeThumbnail(cr,minithumb,id,50f,50f, MediaStore.Images.Thumbnails.MICRO_KIND);
            }
            else {
                cr.delete(uri,null,null);
                uri=null;
            }
        }catch (FileNotFoundException e) {
            if(uri!=null){
                cr.delete(uri,null,null);
                uri=null;
            }
        }
        if(uri!=null){
            stringUri =uri.toString();
        }
        return  stringUri;
    }

    private static final Bitmap storeThumbnail(ContentResolver cr, Bitmap soucre, long id, float width, float height, int microKind) {
        Matrix matrix = new Matrix();
        float scaleX= width/soucre.getWidth();
        float scaleY = height/soucre.getHeight();
        matrix.setScale(scaleX,scaleY);
        Bitmap thumb= Bitmap.createBitmap(soucre,0,0,soucre.getWidth(),soucre.getHeight(),matrix,true);
        ContentValues contentValues = new ContentValues(4);
        contentValues.put(MediaStore.Images.Thumbnails.KIND,microKind);
        contentValues.put(MediaStore.Images.Thumbnails.IMAGE_ID,id);
        contentValues.put(MediaStore.Images.Thumbnails.HEIGHT,height);
        contentValues.put(MediaStore.Images.Thumbnails.WIDTH,width);

        Uri uri = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,contentValues);
        try{
            OutputStream outputStream =cr.openOutputStream(uri);
            thumb.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.close();
            return  thumb;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
