package com.example.gallery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gallery.Sort.SortByModified;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class PhotoFragment extends Fragment {
    Context context;
    GridView gridView;
    ArrayList<ImageInformation> arrayList;
    Uri uri;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout_photo = (LinearLayout) inflater.inflate(R.layout.fragment_photo_layout, null);
        context=this.getContext();
        //arrayList = imageReader(Environment.DIRECTORY_PICTURES.endsWith(".png"));
        //File file = new File(getPath(uri));
        arrayList = new ArrayList<>();
        fetchImageFromGallery();
        final ImageAdapter adapter = new ImageAdapter(arrayList,getActivity());
        gridView = (GridView) layout_photo.findViewById(R.id.gridview);
        //gridView.setAdapter(adapter);
       // gridView.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final ImageAdapter adapter = new ImageAdapter(arrayList,getActivity());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(adapter);
                        gridView.setAdapter(adapter);
                        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);                  //
                        gridView.setMultiChoiceModeListener(new MultiChoiceModeListener());           //
                    }
                });
            }
        }).start();
        //gridView = (GridView) layout_photo.findViewById(R.id.gridview);
        //final ImageAdapter imageAdapter =new ImageAdapter(arrayList, context);
        //gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i= new Intent(context.getApplicationContext(),FullImageActivity.class);
                i.putExtra("id", arrayList.get(position).getPath());
                i.putExtra("position",position);
                i.putExtra("list",arrayList);
                startActivity(i);
            }
        });

        return layout_photo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        arrayList = new ArrayList<>();
        fetchImageFromGallery();
        final ImageAdapter adapter = new ImageAdapter(arrayList,getActivity());
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getActivity(),
                "Activity onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.camera:
                startActivity(new Intent(context, CameraActivity.class));
                Toast.makeText(getActivity(), "Camera clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.bin:
                Toast.makeText(getActivity(), "Bin clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_search:
                Toast.makeText(getActivity(), "Search clicked", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<File> imageReader(File root) {
        ArrayList<File> b = new ArrayList<>();
        final File[] files = root.listFiles();
        final int filesCount =files.length;
        System.out.println("HIHI"+filesCount);
        for(int i =0;i<filesCount;i++){
            if(files[i].isDirectory()){
                b.addAll(imageReader(files[i]));
            }
            else{
                if(files[i].getName().endsWith(".jpg") || files[i].getName().endsWith(".png")
                        ||files[i].getName().endsWith(".jpeg")){
                    System.out.println(files[i].getPath());
                    b.add(files[i]);
                }
            }
        }
        Collections.sort(b, Collections.<File>reverseOrder(new SortByModified()));
        return b;
    }

    private void fetchImageFromGallery() {
        try {
            Uri uri;
            Cursor cursor;
            int column_index_data, column_index_folder_name,column_index_name, column_id, thumb,column_index_date, column_index_size;
            long currDate, size;
            String absolutePathImage = null;
            String name = null;
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            System.out.println(uri);
            String[] projection = {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails.DATA,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.DATE_ADDED};

            String orderBy = MediaStore.Images.Media.DATE_ADDED;
            cursor = getActivity().getApplication().getContentResolver()
                    .query(uri, projection, null, null, orderBy + " DESC");
            assert cursor != null;
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            column_index_date = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
            column_index_size = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            column_index_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            //column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
            //column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            thumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
            if (cursor.moveToFirst()) {
                do {
                    absolutePathImage = cursor.getString(column_index_data);
                    name = cursor.getString(column_index_name);
                    currDate = cursor.getLong(column_index_date);
                    size =cursor.getLong(column_index_size);
                    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                    calendar.setTimeInMillis(currDate*1000L);
                    Date day = calendar.getTime();
                    ImageInformation imageInformation = new ImageInformation();

                    imageInformation.setSelected(false);
                    imageInformation.setPath(absolutePathImage);
                    imageInformation.setName(name);
                    imageInformation.setThumb(cursor.getString(thumb));
                    imageInformation.setSize(size);
                    imageInformation.setDateTaken(day);
                    System.out.println(name);
                    arrayList.add(imageInformation);
                } while (cursor.moveToNext());
            }
            System.out.println(arrayList.size());
            for (int i = 0; i < arrayList.size(); i++) {
                System.out.println(arrayList.get(i).getSize());
            }
        }catch (Exception e) {
            System.out.println("aaaaaaaaaaa");
            e.printStackTrace();
        }
    }

    public class MultiChoiceModeListener implements GridView.MultiChoiceModeListener
    {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Select Items");
            mode.setSubtitle("One item selected");
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                              boolean checked) {
            int selectCount = gridView.getCheckedItemCount();
            switch (selectCount) {
                case 1:
                    mode.setSubtitle("One item selected");
                    break;
                default:
                    mode.setSubtitle("" + selectCount + " items selected");
                    break;
            }
        }
    }
}
