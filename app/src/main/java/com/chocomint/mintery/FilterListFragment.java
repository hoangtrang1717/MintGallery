package com.chocomint.mintery;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chocomint.mintery.Adapter.ThumbnailAdapter;
import com.chocomint.mintery.Interface.FilterListFragmentListener;
import com.chocomint.mintery.Utils.BitmapUtils;
import com.chocomint.mintery.Utils.SpacesItemDecoration;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

public class FilterListFragment extends Fragment implements FilterListFragmentListener {
    RecyclerView recyclerView;
    ThumbnailAdapter adapter;
    List<ThumbnailItem> thumbnailItems;
    String path;

    FilterListFragmentListener listener;

    static FilterListFragment instance;
    public static FilterListFragment getInstance(){
        if (instance == null){
            instance = new FilterListFragment();
        }
        return instance;
    }

    public void setListener(FilterListFragmentListener listener) {
        this.listener = listener;
    }

    public FilterListFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_filter_list, container, false);
        path = getArguments().getString("path");
        thumbnailItems = new ArrayList<>();
        adapter = new ThumbnailAdapter(thumbnailItems,this, getActivity());

        recyclerView =(RecyclerView) itemView.findViewById(R.id.recyclerViewFilter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
        recyclerView.setAdapter(adapter);
        displayThumbnail(null);
        return itemView;
    }

    public void displayThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Bitmap thumpImg;
                if(bitmap == null){
                    thumpImg = BitmapUtils.getBitmapFromGallery(getActivity(), path,100,100);
                }
                else {
                    thumpImg = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                }
                if(thumpImg == null){
                    return;
                }
                ThumbnailsManager.clearThumbs();
                thumbnailItems.clear();

                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumpImg;
                thumbnailItem.filterName ="Normal";
                ThumbnailsManager.addThumb(thumbnailItem);
                List<Filter> filters = FilterPack.getFilterPack(getActivity());

                for(Filter filter : filters){
                    ThumbnailItem tI = new ThumbnailItem();
                    tI.image = thumpImg;
                    tI.filter = filter;
                    tI.filterName = filter.getName();
                    ThumbnailsManager.addThumb(tI);
                }
                thumbnailItems.addAll(ThumbnailsManager.processThumbs(getActivity()));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };
        new Thread(r).start();
    }

    @Override
    public void onFilterSelected(Filter filter) {
        if(listener != null){
            listener.onFilterSelected(filter);
        }
    }
}
