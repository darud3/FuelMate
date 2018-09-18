package com.example.pietrzyk.sqlite1;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

public class AlarmReciver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String category = intent.getStringExtra("category");
        int notification_id = intent.getIntExtra("notification_id", -1);

        createNotification(context, category, title, "Field 3", notification_id);
    }

    public void createNotification(Context context, String msg, String msgTxt, String msgAlert, int notification_id){

        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, ReminderListActivity.class),0);


        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgTxt);

        // intent ktory zostanie odpalony kiedy klikniemy notification, tutaj to MainActivity
        mBuilder.setContentIntent(notificationIntent);

        //how person will be notified: dzwiek, wibracja, ledy itd
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);

        //cancel notification when clicked on task bar
        mBuilder.setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notification_id ,mBuilder.build());
    }
}
