package org.androidtown.schedule;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ohji1 on 2017-07-19.
 */

public class AlarmReceiver extends BroadcastReceiver
{
    String INTENT_ACTION = Intent.ACTION_BOOT_COMPLETED;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String shedule_title = intent.getStringExtra("shedule_title");
        int hour  = intent.getIntExtra("hour", 1);
        int minute = intent.getIntExtra("minute",0);


        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, SecondActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);

        builder.setSmallIcon(R.drawable.ic_star).setTicker("HETT").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle("Schedule Alarm").setContentText("schedule: " + shedule_title +", "+ hour+ "h " + minute+ "m ")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

        notificationmanager.notify(1, builder.build());

    }
}