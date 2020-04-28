package com.example.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.gallery.Adapter.FilterViewPagerAdapter;
import com.example.gallery.Interface.EditImageFragmentListener;
import com.example.gallery.Interface.FilterListFragmentListener;
import com.example.gallery.Utils.BitmapUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class EditImageActivity extends AppCompatActivity implements FilterListFragmentListener, EditImageFragmentListener {
    public static final int PERMISSION_PICK_IMAGE = 100;
    public static String path = null;
    public static int CurPosition;
    ArrayList<File> arrayList;

    ImageView imageView;
    TabLayout tabLayout;
    ViewPager viewPager;

    CoordinatorLayout coordinatorLayout;

    Bitmap originalBitmap, filteredBitmap, finalBitmap;

    FilterListFragment filterListFragment;
    EditImageFragment editImageFragment;

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;
    OutputStream outputStream;
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_filter_main_layout);

        Toolbar toolbar =  findViewById(R.id.toolbarFilter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit");

        arrayList = (ArrayList<File>) getIntent().getSerializableExtra("array");
        path=getIntent().getExtras().getString("path");
        System.out.println("EditPath"+path);
        CurPosition=getIntent().getExtras().getInt("pos");

        imageView = (ImageView)findViewById(R.id.imageFilter);
        tabLayout=(TabLayout) findViewById(R.id.filterTabs);
        viewPager = (ViewPager) findViewById(R.id.filterViewPager);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator);
        loadImg();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadImg() {
        originalBitmap = BitmapUtils.getBitmapFromGallery(this,path,300,300);
        filteredBitmap =originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        imageView.setImageBitmap(originalBitmap);
    }

    private void setupViewPager(ViewPager viewPager) {
        FilterViewPagerAdapter adapter = new FilterViewPagerAdapter(getSupportFragmentManager(),1);

        filterListFragment = new FilterListFragment();
        filterListFragment.setListener(this);

        editImageFragment=new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filterListFragment,"FILTER");
        adapter.addFragment(editImageFragment,"EDIT");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal =brightness;
        Filter myFilter =new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imageView.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal =saturation;
        Filter myFilter =new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imageView.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onContrastChanged(float contrast) {
        saturationFinal =contrast;
        Filter myFilter =new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        imageView.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));

        finalBitmap=myFilter.processFilter(bitmap);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        resetControl();
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        imageView.setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap=filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_image_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id =item.getItemId();
        switch (id){
            case R.id.action_save:
                saveImageToGallery();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImageToGallery(){
        BitmapDrawable drawable =(BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap =drawable.getBitmap();
        File filepath = Environment.getExternalStorageDirectory();
        File dir = new File(filepath.getAbsolutePath()+"/Mintery/");
        dir.mkdir();
        final File file = new File(dir,System.currentTimeMillis()+".png");
        arrayList.add(file);
        System.out.println(arrayList);
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for(int i=0;i<arrayList.size();i++){
            if(arrayList.get(i).getPath().equals(file.getPath())){
                CurPosition = i;
            }
        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        Toast.makeText(getApplicationContext(),"Save successfully",Toast.LENGTH_SHORT).show();
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Snackbar snackbar = Snackbar.make(coordinatorLayout,"Image saved to Gallery",Snackbar.LENGTH_LONG)
                .setAction("OPEN", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openImage(file);
                    }
                });
        snackbar.show();
    }

    private void openImage(File file) {
        Intent i= new Intent(EditImageActivity.this,FullImageActivity.class);
        i.putExtra("list",arrayList);
        i.putExtra("id",arrayList.get(CurPosition).getPath());
        i.putExtra("position",CurPosition);
        startActivity(i);
    }

    private void resetControl() {
        if(editImageFragment!=null){
            editImageFragment.resetControls();
        }
        brightnessFinal=0;
        saturationFinal=1.0f;
        contrastFinal=1.0f;
    }
}
