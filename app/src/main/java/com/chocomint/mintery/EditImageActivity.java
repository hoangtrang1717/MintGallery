package com.chocomint.mintery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class EditImageActivity extends AppCompatActivity {

    Toolbar editToolbar;
    String path;
    int CurPosition;

    PhotoEditorView imageView;
    ImageView viewOfEdit;
    PhotoEditor photoEditor;

    LinearLayout btnFiltersList, btnEditList, btnBrush, btnEmoji, btnSticker, btnText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        getView();

        path = getIntent().getExtras().getString("path");
        CurPosition = getIntent().getExtras().getInt("pos");

        imageView = (PhotoEditorView) findViewById(R.id.photoEditorView);
        viewOfEdit = imageView.getSource();
        Glide.with(this).load(path).into(viewOfEdit);

        Typeface mTextPoppin = ResourcesCompat.getFont(this, R.font.poppins);
        photoEditor = new PhotoEditor.Builder(this, imageView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(mTextPoppin)
                .setDefaultEmojiTypeface(Typeface.createFromAsset(getAssets(),"NotoColorEmoji.ttf"))
                .build();

        btnEditList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditImageActivity.this, "Press edit", Toast.LENGTH_LONG).show();
            }
        });

        btnFiltersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditImageActivity.this, "Press filter", Toast.LENGTH_LONG).show();
            }
        });

        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditImageActivity.this, "Press text", Toast.LENGTH_LONG).show();
            }
        });

        btnBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditImageActivity.this, "Press brush", Toast.LENGTH_LONG).show();
            }
        });

        btnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditImageActivity.this, "Press emoji", Toast.LENGTH_LONG).show();
            }
        });

        btnSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditImageActivity.this, "Press sticker", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getView() {
        btnFiltersList = findViewById(R.id.edit_filter);
        btnEditList = findViewById(R.id.edit_edit);
        btnBrush = findViewById(R.id.edit_brush);
        btnEmoji = findViewById(R.id.edit_emoji);
        btnSticker = findViewById(R.id.edit_sticker);
        btnText = findViewById(R.id.edit_text);
        editToolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        editToolbar.setTitle("");
        setSupportActionBar(editToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_image_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_undo:
                Toast.makeText(this, "Press undo", Toast.LENGTH_LONG).show();
                return true;
            case R.id.edit_redo:
                Toast.makeText(this, "Press redo", Toast.LENGTH_LONG).show();
                return true;
            case R.id.edit_reset:
                Toast.makeText(this, "Press rest", Toast.LENGTH_LONG).show();
                return true;
            case R.id.edit_save:
                Toast.makeText(this, "Press save", Toast.LENGTH_LONG).show();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
