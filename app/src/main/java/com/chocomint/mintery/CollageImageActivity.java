package com.chocomint.mintery;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.ArrayMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chocomint.mintery.Adapter.PuzzleAdapter;
import com.chocomint.mintery.Collage.ProcessActivity;
import com.chocomint.mintery.CollageLayout.straight.StraightLayoutHelper;
import com.chocomint.mintery.Utils.PuzzleUtils;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.slant.SlantPuzzleLayout;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class CollageImageActivity extends AppCompatActivity {
    private static final String TAG = "CollageImageActivity";

    private RecyclerView puzzleList;

    private PuzzleAdapter puzzleAdapter;

    private List<Bitmap> bitmaps = new ArrayList<>();
    private ArrayMap<String, Bitmap> arrayBitmaps = new ArrayMap<>();

    private ArrayList<String> selectedPath = new ArrayList<>();

    private List<Target> targets = new ArrayList<>();

    private int deviceWidth;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collage_image_main_layout);

        selectedPath = getIntent().getStringArrayListExtra("files");
        for(int i = 0;i<selectedPath.size();i++){
            Bitmap bitmap = BitmapFactory.decodeFile(selectedPath.get(i));
            bitmaps.add(bitmap);
        }
        deviceWidth = getResources().getDisplayMetrics().widthPixels;

        initView();
    }

    private void initView() {
        puzzleList = (RecyclerView) findViewById(R.id.puzzle_list);

        puzzleAdapter = new PuzzleAdapter();
        puzzleList.setAdapter(puzzleAdapter);
        puzzleList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
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
    }

    private void refreshLayout() {
        puzzleList.post(new Runnable() {
            @Override public void run() {
                puzzleAdapter.refreshData(PuzzleUtils.getPuzzleLayouts(bitmaps.size()), bitmaps);
            }
        });
    }
}

