package com.terry.samples.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.terry.samples.service.JobService;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            startScheduleService(context);
        }
    }

    private void startScheduleService(Context context) {
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
                .setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
                        1000 * 5,
                        PendingIntent.getService(context, 0,
                                new Intent(context, JobService.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));
    }
}
