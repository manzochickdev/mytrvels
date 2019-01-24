package kr.changhan.mytravels;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("Notification", "hello word");

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                //example for large icon
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle("My Travels")
                .setContentText(context.getString(R.string.remind))
                .setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        i,
                        PendingIntent.FLAG_ONE_SHOT
                );
        // example for blinking LED
        builder.setLights(0xFFb71c1c, 1000, 2000);
        builder.setContentIntent(pendingIntent);
        manager.notify(12345, builder.build());

    }
}
