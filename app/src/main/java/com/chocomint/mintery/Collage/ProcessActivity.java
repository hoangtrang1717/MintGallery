package com.chocomint.mintery.Collage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chocomint.mintery.CallbackFunction;
import com.chocomint.mintery.MainActivity;
import com.chocomint.mintery.SavePhoto;
import com.chocomint.mintery.Utils.PuzzleUtils;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.PuzzlePiece;
import com.xiaopo.flying.puzzle.PuzzleView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import com.chocomint.mintery.R;


public class ProcessActivity extends AppCompatActivity implements View.OnClickListener, CallbackFunction {
    private static final int FLAG_CONTROL_LINE_SIZE = 1;
    private static final int RESULT_LOAD_IMAGE = 2;
    private static final int FLAG_CONTROL_CORNER = 1 << 1;

    private PuzzleLayout puzzleLayout;
    private List<String> bitmapPaint;
    private PuzzleView puzzleView;
    private DegreeSeekBar degreeSeekBar;

    private List<Target> targets = new ArrayList<>();
    private int deviceWidth = 0;

    private int controlFlag;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collage_process_layout);

        deviceWidth = getResources().getDisplayMetrics().widthPixels;

        int type = getIntent().getIntExtra("type", 0);
        int pieceSize = getIntent().getIntExtra("piece_size", 0);
        int themeId = getIntent().getIntExtra("theme_id", 0);
        bitmapPaint = getIntent().getStringArrayListExtra("photo_path");
        puzzleLayout = PuzzleUtils.getPuzzleLayout(type, pieceSize, themeId);

        initView();

        puzzleView.post(new Runnable() {
            @Override public void run() {
                loadPhoto();
            }
        });
    }

    @Override protected void onResume() {
        super.onResume();
    }

    private void loadPhoto() {
        final List<Bitmap> pieces = new ArrayList<>();

        final int count = bitmapPaint.size() > puzzleLayout.getAreaCount() ? puzzleLayout.getAreaCount()
                : bitmapPaint.size();

        for (int i = 0; i < count; i++) {
            final Target target = new Target() {
                @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    pieces.add(bitmap);
                    if (pieces.size() == count) {
                        if (bitmapPaint.size() < puzzleLayout.getAreaCount()) {
                            for (int i = 0; i < puzzleLayout.getAreaCount(); i++) {
                                puzzleView.addPiece(pieces.get(i % count));
                            }
                        } else {
                            puzzleView.addPieces(pieces);
                        }
                    }
                    targets.remove(this);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.get()
                    .load("file:///" + bitmapPaint.get(i))
                    .resize(deviceWidth, deviceWidth)
                    .centerInside()
                    .config(Bitmap.Config.RGB_565)
                    .into(target);

            targets.add(target);
        }
    }

    private void initView() {
        ImageView btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onBackPressed();
            }
        });


        puzzleView = (PuzzleView) findViewById(R.id.puzzle_view);
        degreeSeekBar = (DegreeSeekBar) findViewById(R.id.degree_seek_bar);

        puzzleView.setPuzzleLayout(puzzleLayout);
        puzzleView.setTouchEnable(true);
        puzzleView.setNeedDrawLine(false);
        puzzleView.setNeedDrawOuterLine(false);
        puzzleView.setLineSize(4);
        puzzleView.setLineColor(Color.BLACK);
        puzzleView.setSelectedLineColor(Color.BLACK);
        puzzleView.setHandleBarColor(Color.BLACK);
        puzzleView.setAnimateDuration(300);
        puzzleView.setOnPieceSelectedListener(new PuzzleView.OnPieceSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override public void onPieceSelected(PuzzlePiece piece, int position) {
            }
        });

        // currently the SlantPuzzleLayout do not support padding
        puzzleView.setPiecePadding(10);

        ImageView btnRotate = (ImageView) findViewById(R.id.btn_rotate);
        ImageView btnFlipHorizontal = (ImageView) findViewById(R.id.btn_flip_horizontal);
        ImageView btnFlipVertical = (ImageView) findViewById(R.id.btn_flip_vertical);
        ImageView btnBorder = (ImageView) findViewById(R.id.btn_border);
        ImageView btnCorner = (ImageView) findViewById(R.id.btn_corner);

        btnRotate.setOnClickListener(this);
        btnFlipHorizontal.setOnClickListener(this);
        btnFlipVertical.setOnClickListener(this);
        btnBorder.setOnClickListener(this);
        btnCorner.setOnClickListener(this);

        TextView btnSave = (TextView) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View view) {
                Bitmap saveBitmap = createBitmap(puzzleView);
                try {
                    new SavePhoto(saveBitmap, ProcessActivity.this, null, "image/jpg", ProcessActivity.this).saveImage();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        degreeSeekBar.setCurrentDegrees(puzzleView.getLineSize());
        degreeSeekBar.setDegreeRange(0, 30);
        degreeSeekBar.setScrollingListener(new DegreeSeekBar.ScrollingListener() {
            @Override public void onScrollStart() {

            }

            @Override public void onScroll(int currentDegrees) {
                switch (controlFlag) {
                    case FLAG_CONTROL_LINE_SIZE:
                        puzzleView.setLineSize(currentDegrees);
                        break;
                    case FLAG_CONTROL_CORNER:
                        puzzleView.setPieceRadian(currentDegrees);
                        break;
                }
            }

            @Override public void onScrollEnd() {

            }
        });
    }
    public static Bitmap createBitmap(PuzzleView puzzleView) {
        puzzleView.clearHandling();

        puzzleView.invalidate();

        Bitmap bitmap =
                Bitmap.createBitmap(puzzleView.getWidth(), puzzleView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        puzzleView.draw(canvas);

        return bitmap;
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_rotate:
                puzzleView.rotate(90f);
                break;
            case R.id.btn_flip_horizontal:
                puzzleView.flipHorizontally();
                break;
            case R.id.btn_flip_vertical:
                puzzleView.flipVertically();
                break;
            case R.id.btn_border:
                controlFlag = FLAG_CONTROL_LINE_SIZE;
                puzzleView.setNeedDrawLine(!puzzleView.isNeedDrawLine());
                if (puzzleView.isNeedDrawLine()) {
                    degreeSeekBar.setVisibility(View.VISIBLE);
                    degreeSeekBar.setCurrentDegrees(puzzleView.getLineSize());
                    degreeSeekBar.setDegreeRange(0,30);
                } else {
                    degreeSeekBar.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.btn_corner:
                if (controlFlag == FLAG_CONTROL_CORNER && degreeSeekBar.getVisibility() == View.VISIBLE){
                    degreeSeekBar.setVisibility(View.INVISIBLE);
                    return;
                }
                degreeSeekBar.setCurrentDegrees((int) puzzleView.getPieceRadian());
                controlFlag = FLAG_CONTROL_CORNER;
                degreeSeekBar.setVisibility(View.VISIBLE);
                degreeSeekBar.setDegreeRange(0,100);
                break;
        }
    }

    private void showSelectedPhotoDialog() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @SuppressLint("WrongConstant")
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);
            cursor.close();

            final Target target = new Target() {
                @Override public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    puzzleView.replace(bitmap);
                }

                @SuppressLint("WrongConstant")
                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            Picasso.get()
                    .load("file:///" + path)
                    .resize(deviceWidth, deviceWidth)
                    .centerInside()
                    .config(Bitmap.Config.RGB_565)
                    .into(target);
        }
    }

    @Override
    public void onAddPhotoSuccess() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
