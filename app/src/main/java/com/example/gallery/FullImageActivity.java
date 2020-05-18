package com.example.gallery;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.lang.Object;

import static com.example.gallery.R.id.info;

public class FullImageActivity extends AppCompatActivity {
    Context context = this;
    ViewPager slider;
    Toolbar img_toolbar;
    //ImageView fullImage;
    FullImageSlider imageSlider;
    ArrayList<ImageInformation> arrayList;
    int CurrentPosition;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);
        img_toolbar = (Toolbar) findViewById(R.id.image_toolbar);
        setSupportActionBar(img_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        String data = getIntent().getExtras().getString("id");
        //fullImage.setImageURI(Uri.parse(data));
        slider =(ViewPager) findViewById(R.id.image_viewpaprer);
        arrayList = (ArrayList<ImageInformation>) getIntent().getSerializableExtra("list");
        CurrentPosition = getIntent().getExtras().getInt("position");
        System.out.println("hihi"+arrayList);
        imageSlider =new FullImageSlider(FullImageActivity.this,arrayList,data);
        slider.setAdapter(imageSlider);
        slider.setCurrentItem(CurrentPosition);
        slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) { CurrentPosition = position; }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_menu_toolbar, menu);
        int i;
        for(i = 0; i < menu.size(); i++)
        {
            if(menu.getItem(i).getItemId() == R.id.img_favorite)
            {
                MenuItem item = menu.getItem(i);
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(arrayList.get(CurrentPosition).getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(exif.getAttribute("UserComment").compareTo("Favorite") == 0) {
                    item.setIcon(R.drawable.ic_favorite_bottom_nav);
                }
            }
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()){
            case info:
                ShowImageDetail();
                Toast.makeText(FullImageActivity.this, "Detail" , Toast.LENGTH_LONG).show();
                return true;
            case R.id.edit:
                EditImage();
                Toast.makeText(FullImageActivity.this, "Edit clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.delete:
                deleteFiles(arrayList.get(CurrentPosition).getPath());
                Toast.makeText(FullImageActivity.this, "Delete clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.wallpaper:
                setWallpaper();
                Toast.makeText(FullImageActivity.this, "Wallpaper clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.img_favorite:
                Favor(item);
                //Toast.makeText(FullImageActivity.this, "Favorite clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.home:
            case android.R.id.home:
                //finish();
                startActivity(new Intent(FullImageActivity.this,MainActivity.class));
                //startActivity(new Intent(getApplicationContext(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void Favor(MenuItem item) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(arrayList.get(CurrentPosition).getPath());
        } catch (IOException e) {
            e.printStackTrace(); return;
        }
        if(exif.getAttribute("UserComment").compareTo("Favorite") == 0) {
            exif.setAttribute("UserComment", "Not favorite");
            item.setIcon(R.drawable.ic_favorite);
        }
        else if(exif.getAttribute("UserComment").compareTo("Not favorite") == 0) {
            exif.setAttribute("UserComment", "Favorite");
            item.setIcon(R.drawable.ic_favorite_bottom_nav);
        }
        else
        {
            exif.setAttribute("UserComment", "Favorite");
            item.setIcon(R.drawable.ic_favorite_bottom_nav);
        }
        try {
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace(); return;
        }
        Toast.makeText(FullImageActivity.this, exif.getAttribute("UserComment"), Toast.LENGTH_LONG).show();
    }

    /*private void DeleteImage() {
        String imgPath = arrayList.get(CurrentPosition).getPath();
        String imgName= arrayList.get(CurrentPosition).getName();
        File dir = new File(imgPath);
        dir.mkdir();
        Toast.makeText(FullImageActivity.this, imgPath + imgName, Toast.LENGTH_LONG).show();

        final File temp = new File(dir, imgName);
        boolean succ = temp.delete();
        if(succ == false)
        {
            Toast.makeText(FullImageActivity.this, "Delete failed", Toast.LENGTH_LONG).show();
            return;
        }
        arrayList.get(CurrentPosition);
        if(arrayList.size() == 1)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return;
        }
        if(CurrentPosition - 1 >= 0) {
            slider.setCurrentItem(CurrentPosition - 1);
        }
        else {
            slider.setCurrentItem(CurrentPosition + 1);
        }
    }*/

    public void deleteFiles(final String path) {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(FullImageActivity.this);
        myAlertDialog.setTitle("Delete Image");
        myAlertDialog.setMessage("Do you want to delete it?");
        myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                File file = new File(path);

                if (file.exists()) {
                    String deleteCmd = "rm -r " + path;
                    Runtime runtime = Runtime.getRuntime();
                    try {
                        runtime.exec(deleteCmd);
                    } catch (IOException e) {

                    }
                }
                arrayList.remove(arrayList.get(CurrentPosition));
                MediaScannerConnection.scanFile(getApplicationContext(),
                        new String[] {file.getAbsolutePath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });
                finish();
                startActivity(getIntent());            }});
        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
            }});
        myAlertDialog.show();
    }
    public void ShowImageDetail(){
        Date lastModifiedDate = arrayList.get(CurrentPosition).getDateTaken();
        String lastModified = lastModifiedDate.toString();
        String imgName= arrayList.get(CurrentPosition).getName();
        String imgPath =arrayList.get(CurrentPosition).getPath();
        double imgSize = (double)arrayList.get(CurrentPosition).getSize()/(double)(1024*1024);

        Intent myIntentA1A2;
        myIntentA1A2 = new Intent(FullImageActivity.this, ImageDetail.class);
        Bundle myBundle1 = new Bundle();
        myBundle1.putString("date", lastModified);
        myBundle1.putString("name", imgName);
        myBundle1.putString("path", imgPath);
        myBundle1.putDouble("size", imgSize);
        myIntentA1A2.putExtras(myBundle1);
        startActivityForResult(myIntentA1A2, 1122);
    }

    public void EditImage(){
        Intent i= new Intent(FullImageActivity.this,EditImageActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("path",arrayList.get(CurrentPosition).getPath());
        i.putExtra("array",arrayList);
        i.putExtra("pos",CurrentPosition);
        startActivity(i);
    }

    private void setWallpaper() {
        /*Bitmap bitmap = BitmapFactory.decodeFile(arrayList.get(CurrentPosition).getPath());
        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
        try{
            manager.setBitmap(bitmap);
            Toast.makeText(this, "Wallpaper set!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }*/

        Bitmap bmap2 = BitmapFactory.decodeFile(arrayList.get(CurrentPosition).getPath());
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        Bitmap bitmap = Bitmap.createScaledBitmap(bmap2, width, height, true);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(FullImageActivity.this);
        try {
            wallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
