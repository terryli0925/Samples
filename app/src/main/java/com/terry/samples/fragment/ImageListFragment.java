package com.terry.samples.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.terry.samples.R;
import com.terry.samples.activity.MainActivity;
import com.terry.samples.adapter.BaseRVAdapter;
import com.terry.samples.adapter.BaseRVAdapterHelper;
import com.terry.samples.model.Photo;
import com.terry.samples.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageListFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String ARG_BUCKET_NAME = "bucket_name";

    private RecyclerView mRecyclerView;

    private String mBucketName;
    private BaseRVAdapter mImageListAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bucketName The name of bucket.
     * @return A new instance of fragment ImageListFragment.
     */
    public static ImageListFragment newInstance(String bucketName) {
        ImageListFragment fragment = new ImageListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BUCKET_NAME, bucketName);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mBucketName = getArguments().getString(ARG_BUCKET_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_list, container, false);
        initView(view);
        getImageList();
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
    }

    private void getImageList() {
        ArrayList<Photo> list = new ArrayList<>();
        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

        Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?", new String[]{mBucketName}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int photoId;
            String photoName;
            String photoPath;
            do {
                photoId = cursor.getInt(cursor.getColumnIndex(
                        MediaStore.Images.Media._ID));
                photoName = cursor.getString(cursor.getColumnIndex(
                        MediaStore.Images.Media.DISPLAY_NAME));
                photoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                LogUtils.print(TAG, photoId + "," + photoName + "," + photoPath);
                list.add(new Photo(photoId, photoName, photoPath));
            } while (cursor.moveToNext());
            cursor.close();
        }

        setAdapter(list);
    }

    private void setAdapter(List<Photo> list) {
        mImageListAdapter = new BaseRVAdapter<Photo, BaseRVAdapterHelper>(getActivity(),
                R.layout.adapter_image_list_item, null) {

            @Override
            protected void convert(BaseRVAdapterHelper holder, Photo item, int position) {
                ImageView imageView = holder.getImageView(R.id.photo);
                Glide.with(getActivity()).load(item.getPath()).override(200, 200)
                        .into(imageView);
            }
        };
        mRecyclerView.setAdapter(mImageListAdapter);
        mImageListAdapter.setOnItemClickListener(new BaseRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                final Photo photo = (Photo) mImageListAdapter.getItem(position);
                LogUtils.print(TAG, "" + position);
            }
        });

        mImageListAdapter.addAll(list);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear(); // Clear menu before inflating new menu
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
