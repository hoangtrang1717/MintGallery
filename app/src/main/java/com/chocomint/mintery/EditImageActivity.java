package com.chocomint.mintery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chocomint.mintery.Interface.AddStickerListener;
import com.chocomint.mintery.Interface.BrushFragmentListener;
import com.chocomint.mintery.Interface.EditImageFragmentListener;
import com.chocomint.mintery.Interface.EmojiFragmentListener;
import com.chocomint.mintery.Interface.FilterListFragmentListener;
import com.chocomint.mintery.Interface.TextFragmentListener;
import com.chocomint.mintery.Utils.BitmapUtils;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.FileNotFoundException;
import java.io.OutputStream;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;

public class EditImageActivity extends AppCompatActivity implements EditImageFragmentListener, TextFragmentListener, BrushFragmentListener, EmojiFragmentListener, AddStickerListener, FilterListFragmentListener, CallbackFunction {

    Toolbar editToolbar;
    String path;

    PhotoEditorView imageView;
    ImageView viewOfEdit;
    PhotoEditor photoEditor;

    LinearLayout btnFiltersList, btnEditList, btnBrush, btnEmoji, btnSticker, btnText;
    Fragment editImageFragment, editBrushFragment, editTextFragment, editEmojiFragment, editStickerFragment, editFilterFragment;

    private final String EDIT_IMAGE_FRAG = "editImageFrag";
    private final String EDIT_FILTER_FRAG = "editFilterFrag";
    private final String EDIT_TEXT_FRAG = "editTextFrag";
    private final String EDIT_EMOJI_FRAG = "editEmojiFrag";
    private final String EDIT_STICKER_FRAG = "editStickerFrag";
    private final String EDIT_BRUSH_FRAG = "editBrushFrag";

    private final int EDIT_BRUSH_BRUSH = 1;
    private final int EDIT_BRUSH_ERASER = 2;
    int currentBrush;
    float currentSize;

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;
    String editFr;
    Bitmap originalBitmap, filteredBitmap, finalBitmap;
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        path = getIntent().getExtras().getString("path");
        editFr = null;
        currentBrush = EDIT_BRUSH_BRUSH;
        currentSize = 1;
        getView();

        imageView = (PhotoEditorView) findViewById(R.id.photoEditorView);
        viewOfEdit = imageView.getSource();
        Glide.with(this).load(path).into(viewOfEdit);

        Typeface mTextPoppin = ResourcesCompat.getFont(this, R.font.poppins);
        photoEditor = new PhotoEditor.Builder(this, imageView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(mTextPoppin)
                .setDefaultEmojiTypeface(Typeface.createFromAsset(getAssets(),"NotoColorEmoji.ttf"))
                .build();
        loadImg();

        btnEditList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editFr == EDIT_IMAGE_FRAG) {
                    editFr = null;
                    getSupportFragmentManager().beginTransaction().remove(editImageFragment).commit();
                } else {
                    editFr = EDIT_IMAGE_FRAG;
                    getSupportFragmentManager().beginTransaction().replace(R.id.edit_frag, editImageFragment, EDIT_IMAGE_FRAG).commit();
                }
            }
        });

        btnFiltersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editFr == EDIT_FILTER_FRAG) {
                    editFr = null;
                    getSupportFragmentManager().beginTransaction().remove(editFilterFragment).commit();
                } else {
                    editFr = EDIT_FILTER_FRAG;
                    getSupportFragmentManager().beginTransaction().replace(R.id.edit_frag, editFilterFragment, EDIT_FILTER_FRAG).commit();
                }
            }
        });

        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editFr == EDIT_TEXT_FRAG) {
                    editFr = null;
                    getSupportFragmentManager().beginTransaction().remove(editTextFragment).commit();
                } else {
                    editFr = EDIT_TEXT_FRAG;
                    getSupportFragmentManager().beginTransaction().replace(R.id.edit_frag, editTextFragment, EDIT_TEXT_FRAG).commit();
                }
            }
        });

        btnBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editFr == EDIT_BRUSH_FRAG) {
                    editFr = null;
                    photoEditor.setBrushDrawingMode(false);
                    getSupportFragmentManager().beginTransaction().remove(editBrushFragment).commit();
                } else {
                    editFr = EDIT_BRUSH_FRAG;
                    photoEditor.setBrushDrawingMode(true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.edit_frag, editBrushFragment, EDIT_BRUSH_FRAG).commit();
                }
            }
        });

        btnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editFr == EDIT_EMOJI_FRAG) {
                    editFr = null;
                    getSupportFragmentManager().beginTransaction().remove(editEmojiFragment).commit();
                } else {
                    editFr = EDIT_EMOJI_FRAG;
                    getSupportFragmentManager().beginTransaction().replace(R.id.edit_frag, editEmojiFragment, EDIT_EMOJI_FRAG).commit();
                }
            }
        });

        btnSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editFr == EDIT_STICKER_FRAG) {
                    editFr = null;
                    getSupportFragmentManager().beginTransaction().remove(editStickerFragment).commit();
                } else {
                    editFr = EDIT_STICKER_FRAG;
                    getSupportFragmentManager().beginTransaction().replace(R.id.edit_frag, editStickerFragment, EDIT_STICKER_FRAG).commit();
                }
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
        editImageFragment = EditImageFragment.getInstance();
        ((EditImageFragment) editImageFragment).setListener(this);
        editBrushFragment = BrushFragment.getInstance();
        ((BrushFragment) editBrushFragment).setListener(this);
        editTextFragment = TextFragment.getInstance();
        ((TextFragment) editTextFragment).setListener(this);
        editEmojiFragment = EmojiFragment.getInstance();
        ((EmojiFragment) editEmojiFragment).setListener(this);
        editStickerFragment = StickerFragment.getInstance();
        ((StickerFragment) editStickerFragment).setListener(this);
        editFilterFragment = FilterListFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        editFilterFragment.setArguments(bundle);
        ((FilterListFragment) editFilterFragment).setListener(this);
    }

    private void loadImg() {
        originalBitmap = BitmapUtils.getBitmapFromGallery(this, path,300,300);
        filteredBitmap =originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        imageView.getSource().setImageBitmap(originalBitmap);
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
                photoEditor.undo();
                return true;
            case R.id.edit_redo:
                photoEditor.redo();
                return true;
            case R.id.edit_reset:
                while (photoEditor.undo());
                return true;
            case R.id.edit_save:
                photoEditor.saveAsBitmap(new OnSaveBitmap() {
                    @Override
                    public void onBitmapReady(Bitmap saveBitmap) {
                        try {
                            new SavePhoto(saveBitmap, EditImageActivity.this, null, "image/jpg", EditImageActivity.this).saveImage();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(EditImageActivity.this, "An unexpected error has occured.", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAddPhotoSuccess() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override
    public void onAddSticker(int sticker) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), sticker);
        photoEditor.addImage(Bitmap.createScaledBitmap(bitmap, 300, 300, false));
        editFr = null;
        getSupportFragmentManager().beginTransaction().remove(editStickerFragment).commit();
    }

    @Override
    public void onBrushSizeChangedListener(float size) {
        currentSize = size;
        if (currentBrush == EDIT_BRUSH_BRUSH) {
            photoEditor.setBrushSize(size);
        } else if (currentBrush == EDIT_BRUSH_ERASER) {
            photoEditor.setBrushEraserSize(size);
            photoEditor.brushEraser();
        }
    }

    @Override
    public void onBrushOpacityChangedListener(int opacity) {
        photoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushColorChangedListener(int color) {
        photoEditor.setBrushColor(color);
    }

    @Override
    public void onBrushStateChangedListener(boolean isErazer) {
        if (isErazer) {
            currentBrush = EDIT_BRUSH_ERASER;
            photoEditor.setBrushEraserSize(currentSize);
            photoEditor.brushEraser();
        }
        else {
            currentBrush = EDIT_BRUSH_BRUSH;
            photoEditor.setBrushDrawingMode(true);
            photoEditor.setBrushSize(currentSize);
        }
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imageView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imageView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onContrastChanged(float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        imageView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onEditStarted() { }

    @Override
    public void onEditCompleted() {

    }

    @Override
    public void onEmojiSelected(String emoji) {
        photoEditor.addEmoji(emoji);
        editFr = null;
        getSupportFragmentManager().beginTransaction().remove(editEmojiFragment).commit();
    }

    @Override
    public void onAddTextButtonClick(String text, TextStyleBuilder textStyleBuilder) {
        photoEditor.addText(text, textStyleBuilder);
        editFr = null;
        getSupportFragmentManager().beginTransaction().remove(editTextFragment).commit();
    }

    @Override
    public void onFilterSelected(Filter filter) {
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        imageView.getSource().setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap=filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);
    }
}
