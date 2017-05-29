package com.hp.epilepsy.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.MedicationDairy;

public class NotifyService extends Service {
    public NotifyService() {

    }

    @Override
    public void onCreate() {

        try {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("My notification")
                            .setSound(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.notifiy))
                            .setContentText("Hello World!");
            Intent resultIntent = new Intent(this, MedicationDairy.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            int mNotificationId = 001;
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
