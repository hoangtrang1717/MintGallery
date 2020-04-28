package com.example.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    }
}
