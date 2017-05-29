package com.hp.epilepsy.widget.model;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.NotifyService;
import com.hp.epilepsy.widget.adapter.DBAdapter;

import java.util.Calendar;

public class Medication extends Activity {
    DBAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter=new DBAdapter(this);
        setContentView(R.layout.activity_medication);
    }

   public void onclick(View v)
   {
       if(v.getId()==R.id.insert)
       {
         try {
             boolean done = adapter.checkuserAlter("what is your name","Mohsen Saad Mahmoud sroor");
             if (done) {
                 Toast.makeText(getApplicationContext(), "data inserted sucssesfuly", Toast.LENGTH_LONG).show();
             } else {
                 Toast.makeText(getApplicationContext(), "data not inserted ", Toast.LENGTH_LONG).show();
             }
         }catch (Exception Ex)

         {
             Toast.makeText(getApplicationContext(),Ex.toString(), Toast.LENGTH_LONG).show();
         }
       }
       else if(v.getId()==R.id.retrive)
       {
           try {

/*

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
*/


               Intent myIntent = new Intent(this, NotifyService.class);
               AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
               PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);

               Calendar calendar = Calendar.getInstance();
               calendar.set(Calendar.SECOND, 0);
               calendar.set(Calendar.MINUTE, 53);
               calendar.set(Calendar.HOUR, 2);
               calendar.set(Calendar.AM_PM, Calendar.PM);
               calendar.add(Calendar.DAY_OF_MONTH, 1);
               alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);

           }catch (Exception EX)
           {
               Toast.makeText(getApplicationContext(), EX.toString(), Toast.LENGTH_LONG).show();
           }
       }

   }

}
