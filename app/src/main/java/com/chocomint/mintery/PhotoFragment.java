package com.chocomint.mintery;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PhotoFragment extends Fragment implements ChooseFileCallback {
    Context context;
    RecyclerView recyclerView;
    ArrayList<Media> photoList;
    ImageAdapter adapter;
    ChooseFileAdapter chooseFileAdapter;
    String from;
    ArrayList<String> fileChoose;
    ArrayList<String> fileChoosePath;

    final int REQUEST_SHARE = 1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout_photo = null;

        fileChoose = new ArrayList<>();
        fileChoosePath = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            from = bundle.getString("from", "");
            photoList = (ArrayList<Media>) bundle.getSerializable("list");
            if (photoList != null && photoList.size() > 0) {
                layout_photo = (LinearLayout) inflater.inflate(R.layout.fragment_photo_layout, null);
                recyclerView = layout_photo.findViewById(R.id.photo_reycle);
                if (from.compareTo("DELETE") == 0) {
                    chooseFileAdapter = new ChooseFileAdapter(getActivity(), photoList, this);
                    recyclerView.setAdapter(chooseFileAdapter);

                    final GridLayoutManager manager = new GridLayoutManager(this.getActivity(), 4);
                    recyclerView.setLayoutManager(manager);
                    manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return chooseFileAdapter.isHeader(position) ? manager.getSpanCount() : 1;

                        }
                    });
                } else {
                    adapter = new ImageAdapter(getActivity(), photoList);
                    recyclerView.setAdapter(adapter);

                    final GridLayoutManager manager = new GridLayoutManager(this.getActivity(), 4);
                    recyclerView.setLayoutManager(manager);
                    manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return adapter.isHeader(position) ? manager.getSpanCount() : 1;

                        }
                    });
                }
            } else {
                layout_photo = (ConstraintLayout) inflater.inflate(R.layout.photo_no_item, null);
            }
        }

        return layout_photo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void chooseFile(int position, boolean add) {
        if (add) {
            if (fileChoose.indexOf(String.valueOf(photoList.get(position).id)) < 0) {
                fileChoose.add(String.valueOf(photoList.get(position).id));
                fileChoosePath.add(photoList.get(position).path);
            }
        } else {
            fileChoose.remove(String.valueOf(photoList.get(position).id));
            fileChoosePath.remove(photoList.get(position).path);
        }
    }

    @Override
    public int findChooseFile(int id) {
        return fileChoose.indexOf(String.valueOf(id));
    }

    public void SharePhoto() {
        new PhotoFragment.ShareThread().execute();
    }

    private class ShareThread extends AsyncTask <Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (fileChoose != null && fileChoose.size() > 0) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    ArrayList<Uri> files = new ArrayList<>();
                    for (String id : fileChoose) {
                        files.add(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id));
                    }
                    shareIntent.putExtra(Intent.EXTRA_STREAM, files);
                    shareIntent.setType("image/*");
                    startActivityForResult(Intent.createChooser(shareIntent, "Share images"), REQUEST_SHARE);
                } else {
                    return false;
                }
            } catch (ActivityNotFoundException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean && fileChoose.size() <= 0) {
                Toast.makeText(getContext(), "You did not choose any photo", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void DeletePhotos() {
        new PhotoFragment.DeleteThread().execute();
    }

    private class DeleteThread extends AsyncTask <Void, Boolean, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                for (String id : fileChoose) {
                    getActivity().getContentResolver().delete(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id), null, null);
                }
            } catch (Throwable e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                if (fileChoose.size() <= 0) {
                    Toast.makeText(getContext(), "You did not choose any photo", Toast.LENGTH_LONG);
                }
                getActivity().onBackPressed();
            } else {
                Toast.makeText(getContext(), "Error. Try again later", Toast.LENGTH_LONG);
            }
        }
    }
    public void CollagePhoto() {
        new PhotoFragment.CollageThread().execute();
    }
    private class CollageThread extends AsyncTask <Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (fileChoose != null && fileChoose.size() >= 2 && fileChoose.size() <= 9 ) {
                    Intent collageIntent = new Intent(getActivity(),CollageImageActivity.class);
                    collageIntent.putExtra("files", fileChoosePath);
                    startActivity(collageIntent);
                    getActivity().finish();
                } else {
                    return false;
                }
            } catch (ActivityNotFoundException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                if(fileChoose != null && fileChoose.size() >= 10 ) {
                    Toast.makeText(getContext(), "You must choose at most 9 images.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "You must choose at least 2 images.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_SHARE:
                getActivity().onBackPressed();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
