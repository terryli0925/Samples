package com.terry.samples.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by terry on 2016/4/22.
 */
public abstract class QuickRVAdapter<T> extends BaseRVAdapter<T, BaseRVAdapterHelper> {

    /**
     * Create a QuickRVAdapter.
     *
     * @param context     The context.
     * @param layoutResId The layout resource id of each item.
     */
    public QuickRVAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    /**
     * Same as QuickRVAdapter#QuickRVAdapter(Context,int) but with
     * some initialization data.
     *
     * @param context     The context.
     * @param layoutResId The layout resource id of each item.
     * @param data        A new list is created out of this one to avoid mutable list
     */
    public QuickRVAdapter(Context context, int layoutResId, List<T> data) {
        super(context, layoutResId, data);
    }
}
