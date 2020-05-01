package com.example.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DecimalFormat;

public class ImageDetail extends AppCompatActivity {
    TextView ImgDate, ImgName, ImgPath, ImgSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_info_layout);
        ImgDate = (TextView) findViewById(R.id.ImgDate);
        ImgName = (TextView) findViewById(R.id.ImgName);
        ImgPath = (TextView) findViewById(R.id.ImgPath);
        ImgSize = (TextView) findViewById(R.id.ImgSize);

        Toolbar toolbar =  findViewById(R.id.toolbarImgDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Image Detail");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        Intent myCallerIntent = getIntent();
        Bundle myBundle = myCallerIntent.getExtras();
        ImgDate.setText(myBundle.getString("date"));
        ImgName.setText(myBundle.getString("name"));
        ImgPath.setText(myBundle.getString("path"));
        double size = myBundle.getDouble("size");
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        String mbsize = df.format(size) + "MB";
        ImgSize.setText(mbsize);
        myCallerIntent.putExtras(myBundle);
        setResult(Activity.RESULT_OK, myCallerIntent);
        //startActivity(new Intent(getApplicationContext(),FullImageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.img_detail_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
