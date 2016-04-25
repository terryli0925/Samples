package com.terry.samples.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.terry.samples.R;
import com.terry.samples.activity.MainActivity;
import com.terry.samples.adapter.BaseRVAdapter;
import com.terry.samples.adapter.BaseRVAdapterHelper;
import com.terry.samples.model.Bucket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by terry on 2016/4/21.
 */
public class ImageExploreFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private BaseRVAdapter mBucketListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_explore, container, false);
        initView(view);
        getBucketList();
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getBucketList() {
        ArrayList<Bucket> list = new ArrayList<>();

        // which image properties are we querying
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.Media.DATA
        };

        // We want to order the albums by reverse chronological order. We abuse the
        // "WHERE" parameter to insert a "GROUP BY" clause into the SQL statement.
        // The template for "WHERE" parameter is like:
        //    SELECT ... FROM ... WHERE (%s)
        // and we make it look like:
        //    SELECT ... FROM ... WHERE (1) GROUP BY 1,(2)
        // The "(1)" means true. The "1,(2)" means the first two columns specified
        // after SELECT. Note that because there is a ")" in the template, we use
        // "(2" to match it.
        String groupBy = "1) GROUP BY 1,(2";
        String orderBy = "MAX(" + MediaStore.Images.ImageColumns.DATE_TAKEN + ") DESC";

        Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, groupBy, null, orderBy);

        if (cursor != null && cursor.moveToFirst()) {
            String bucketName;
            int photoCount;
            do {
                bucketName = cursor.getString(cursor.getColumnIndex(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                photoCount = getPhotoCountByBucket(bucketName);
                list.add(new Bucket(bucketName, photoCount));
            } while (cursor.moveToNext());
            cursor.close();

        }

        setAdapter(list);
    }

    private int getPhotoCountByBucket(String bucketName) {
        String searchParams = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "= " + bucketName;
        // Method One
//        Cursor photoCursor = getActivity().getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
//                searchParams, null, null);

        // Method Two
        Cursor photoCursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?", new String[]{bucketName}, null);
//        Log.i(TAG, "" + photoCursor.getColumnCount());

        int photoCount = 0;
        if (photoCursor != null) {
            photoCount = photoCursor.getCount();
            photoCursor.close();
        }

        return photoCount;
    }

    private void setAdapter(List<Bucket> list) {
        mBucketListAdapter = new BaseRVAdapter<Bucket, BaseRVAdapterHelper>(getActivity(),
                R.layout.adapter_bucket_list_item, null) {

            @Override
            protected void convert(BaseRVAdapterHelper holder, Bucket item, int position) {
                holder.getTextView(R.id.name).setText(item.getName());
                holder.getTextView(R.id.photo_count).setText(String.valueOf(item.getPhotoCount()));
            }
        };
        mRecyclerView.setAdapter(mBucketListAdapter);
        mBucketListAdapter.setOnItemClickListener(new BaseRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Bucket bucket = (Bucket) mBucketListAdapter.getItem(position);
                ((MainActivity) getActivity()).replaceFragment(
                        ImageListFragment.newInstance(bucket.getName()), true);
            }
        });

        mBucketListAdapter.addAll(list);
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
