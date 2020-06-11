package com.chocomint.mintery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chocomint.mintery.Adapter.PuzzleAdapter;
import com.chocomint.mintery.Collage.ProcessActivity;
import com.chocomint.mintery.CollageLayout.straight.StraightLayoutHelper;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.slant.SlantPuzzleLayout;

import java.util.ArrayList;
import java.util.List;

public class CollageImageActivity extends AppCompatActivity {

    private RecyclerView puzzleList;

    private PuzzleAdapter puzzleAdapter;

    private List<Bitmap> bitmaps = new ArrayList<>();
    private ArrayMap<String, Bitmap> arrayBitmaps = new ArrayMap<>();

    private ArrayList<String> selectedPath;
    private ImageButton btnBack;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collage_image_main_layout);

        btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                onBackPressed();
            }
        });

        selectedPath = new ArrayList<>();
        selectedPath = getIntent().getStringArrayListExtra("files");
        for(String path: selectedPath){
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            bitmaps.add(bitmap);
        }

        initView();
    }

    private void initView() {
        puzzleList = (RecyclerView) findViewById(R.id.puzzle_list);

        puzzleAdapter = new PuzzleAdapter();
        puzzleList.setAdapter(puzzleAdapter);
        puzzleList.setLayoutManager(new GridLayoutManager(this, 2));
        puzzleList.setHasFixedSize(true);
        puzzleAdapter.refreshData(StraightLayoutHelper.getAllThemeLayout(bitmaps.size()), bitmaps);
        puzzleAdapter.setOnItemClickListener(new PuzzleAdapter.OnItemClickListener() {
            @Override public void onItemClick(PuzzleLayout puzzleLayout, int themeId) {
                Intent intent = new Intent(CollageImageActivity.this, ProcessActivity.class);
                intent.putStringArrayListExtra("photo_path", selectedPath);
                if (puzzleLayout instanceof SlantPuzzleLayout) {
                    intent.putExtra("type", 0);
                } else {
                    intent.putExtra("type", 1);
                }
                intent.putExtra("piece_size", selectedPath.size());
                intent.putExtra("theme_id", themeId);

                startActivity(intent);
            }
        });
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        arrayBitmaps.clear();
        arrayBitmaps = null;

        bitmaps.clear();
        bitmaps = null;

        selectedPath.clear();
        selectedPath = null;
    }
}

