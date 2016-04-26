package com.terry.samples.service;

import android.app.IntentService;
import android.content.Intent;

import com.terry.samples.utils.LogUtils;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class JobService extends IntentService {

    private static final String TAG = "JobService";

    public JobService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtils.print(TAG, "JobService running");
    }
}
