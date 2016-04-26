package com.terry.samples.fragment;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.terry.samples.R;
import com.terry.samples.activity.MainActivity;
import com.terry.samples.receiver.BootReceiver;
import com.terry.samples.service.JobService;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mStart, mStartP, mStop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mStart = (Button) view.findViewById(R.id.start_service);
        mStartP = (Button) view.findViewById(R.id.start_periodic_service);
        mStop = (Button) view.findViewById(R.id.stop_periodic_service);

        mStart.setOnClickListener(this);
        mStartP.setOnClickListener(this);
        mStop.setOnClickListener(this);
    }

    private void startService() {
        // Start the service
        getActivity().startService(new Intent(getActivity(), JobService.class));
    }

    private void startScheduleService() {
        ((AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE))
                .setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
                        1000 * 5,
                        PendingIntent.getService(getActivity(), 0,
                                new Intent(getActivity(), JobService.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private void stopScheduleService() {
        ((AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE))
                .cancel(PendingIntent.getService(getActivity(), 0,
                        new Intent(getActivity(), JobService.class),
                        PendingIntent.FLAG_UPDATE_CURRENT));
    }

    /**
     * Receiver will not be called unless the application explicitly enables it.
     *
     * @param isEnable
     */
    private void enableBootReceiver(boolean isEnable) {
        ComponentName receiver = new ComponentName(getActivity(), BootReceiver.class);
        PackageManager pm = getActivity().getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                (isEnable) ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
                startService();
                break;
            case R.id.start_periodic_service:
                startScheduleService();
                enableBootReceiver(true);
                break;
            case R.id.stop_periodic_service:
                stopScheduleService();
                enableBootReceiver(false);
                break;
        }
    }
}
