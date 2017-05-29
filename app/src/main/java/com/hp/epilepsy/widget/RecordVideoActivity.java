package com.hp.epilepsy.widget;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.Services.OnClearFromRecentService;
import com.hp.epilepsy.utils.AlarmReceiver;
import com.hp.epilepsy.utils.ApplicationBackgroundCheck;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.model.MedicationReminders;
import com.hp.epilepsy.widget.model.SMS;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;




/**
 * -----------------------------------------------------------------------------
 * Class Name: RecordVideoActivity
 * Created By:Mahmoud
 * Modified By:Shruti and Nikunj
 * Purpose :to record video
 * -----------------------------------------------------------------------------
 */
public class RecordVideoActivity<E> extends Activity implements OnClickListener
{

   String SENT = "SMS_SENT";
   String DELIVERED = "SMS_DELIVERED";
   private Dialog dialog;
   private BroadcastReceiver sendBroadcastReceiver;
   private BroadcastReceiver deliveryBroadcastReceiver;
   private boolean broadcastReceiversRegistered = false;
   public static String MED_TYPE = "MED_TYPE";
   public static String MED_TYPE_FLAG = "MED_TYPE_FLAG";
   public static String MED_ID_Notify = "MED_ID_Notify";
   public static String MED_NAME = "MED_NAME";
   public static String MED_REMINDER_DATE = "MED_REMINDER_DATE";
   public static String MED_REMINDER_TIME = "MED_REMINDER_TIME";
   public static String USER_NAME = "USER_NAME";
   public static String MED_REMINDER_ID = "MED_REMINDER_ID";
   public static String NOTIFICATION_ID = "NOTIFICATION_ID";

   int medId,presNotificationId,appNotificationId;
   String medTypeFlag,title,UserNAme,MedicatioReminderId,MedicationReminderDate,date,time,medication_type,medication_message,type;


   int medReminderId,NotificationID;
   Date datetime;

   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      try {
      //   prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //  statusLocked = prefs.edit().putBoolean("locked", true).commit();

         requestWindowFeature(Window.FEATURE_NO_TITLE);
         setContentView(R.layout.activity_record_video);

         if(!ApplicationBackgroundCheck.isMyServiceRunning(OnClearFromRecentService.class,this));
         {
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
            ApplicationBackgroundCheck.setIncrement(getApplicationContext());
         }
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
         TextView signUp = (TextView)findViewById(R.id.signup_button);
         String htmlString = getString(R.string.sign_up);
         signUp.setText(Html.fromHtml(htmlString));
         signUp.setOnClickListener(this);
         Button button = (Button)findViewById(R.id.login);
         button.setOnClickListener(this);
         ImageView imageView = (ImageView)findViewById(R.id.record_video);
         imageView.setOnClickListener(this);
         imageView = (ImageView)findViewById(R.id.send_sms);
         imageView.setOnClickListener(this);
         sendBroadcastReceiver = new BroadcastReceiver()
         {

            public void onReceive(Context arg0, Intent arg1)
            {
               switch (getResultCode())
               {
               case Activity.RESULT_OK:
                  Toast.makeText(getBaseContext(), getString(R.string.sms_sent), Toast.LENGTH_SHORT).show();
                  break;
               case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                  Toast.makeText(getBaseContext(), getString(R.string.generic_failure), Toast.LENGTH_SHORT).show();
                  break;
               case SmsManager.RESULT_ERROR_NO_SERVICE:
                  Toast.makeText(getBaseContext(), getString(R.string.no_service), Toast.LENGTH_SHORT).show();
                  break;
               case SmsManager.RESULT_ERROR_NULL_PDU:
                  Toast.makeText(getBaseContext(), getString(R.string.null_pdu), Toast.LENGTH_SHORT).show();
                  break;
               case SmsManager.RESULT_ERROR_RADIO_OFF:
                  Toast.makeText(getBaseContext(), getString(R.string.radio_off), Toast.LENGTH_SHORT).show();
                  break;
               }
            }
         };

         deliveryBroadcastReceiver = new BroadcastReceiver()
         {
            public void onReceive(Context arg0, Intent arg1)
            {
               switch (getResultCode())
               {
               case Activity.RESULT_OK:
                  Toast.makeText(getBaseContext(), getString(R.string.sms_delivered), Toast.LENGTH_SHORT).show();
                  break;
               case Activity.RESULT_CANCELED:
                  Toast.makeText(getBaseContext(), getString(R.string.sms_not_delivered), Toast.LENGTH_SHORT).show();
                  break;
               }
            }
         };
      } catch (Exception e) {
         e.printStackTrace();
      }
      Bundle passedData = this.getIntent().getExtras();

      if (passedData != null) {
         if (passedData.containsKey(AlarmReceiver.MED_TYPE)) {
            DBAdapter adapter = new DBAdapter(getApplicationContext());
            medId = (int) passedData.get(AlarmReceiver.MED_ID_Notification);
            medTypeFlag = (String) passedData.get(AlarmReceiver.MED_TYPE_FLAG);
            title = (String) passedData.get(AlarmReceiver.MED_NAME);
            UserNAme = (String) passedData.get(AlarmReceiver.USER_NAME);
            MedicatioReminderId = ((String) passedData.get(AlarmReceiver.MED_TYPE)).substring(3);
            MedicationReminders reminderOBJ = adapter.getMedicationRemindersById(Integer.parseInt(MedicatioReminderId));
            MedicationReminderDate = reminderOBJ.getMedReminderDate();
            datetime = DateTimeFormats.convertStringToDateTimeWithDeafultFormat(MedicationReminderDate);   //error
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            date = dateFormat.format(datetime);
            time = timeFormat.format(datetime);
            LoginActivity.userName = UserNAme;
            medReminderId = (int) passedData.get(AlarmReceiver.MED_REMINDER_ID);
            NotificationID = (int) passedData.get(AlarmReceiver.NOTIFICATION_ID);
         }

         type=(String)passedData.getString("type");
         if(!TextUtils.isEmpty(type))
         {

             if(type.equals("App"))
             {
                medication_type=(String)passedData.getString("type");
                medication_message=(String)passedData.get(AlarmReceiver.APPOINTMENT_MESSAGE);
                appNotificationId=(int)passedData.getInt("uniqueAppId");


             }
             else if(type.equals("PRES"))
             {
                medication_type=(String)passedData.getString("type");
                medication_message=(String)passedData.get(AlarmReceiver.PRESCRIPTION_RENEWAL_MESSAGE);
                presNotificationId=(int)passedData.getInt("presUniqueId");
             }
             else if(type.equals("EME"))
             {
                medication_type=(String)passedData.getString("type");
                medication_message=(String)passedData.get(AlarmReceiver.EMERGENCY_MEDICATION_MESSAGE);
                medId=(int)passedData.getInt("id");


             }
             else
             {
                medication_type=(String)passedData.getString("type");
                medication_message=(String)passedData.get(AlarmReceiver.MEICATION_TITRATION_MESSAGE);

             }
         }



      }


   }


   @Override
   public  void onPause()
   {
      super.onPause();
   }
   @Override
   protected void onResume()
   {
      super.onResume();

   }
   @Override
   protected void onDestroy()
   {


      // TODO Auto-generated method stub
      if (broadcastReceiversRegistered)
      {
         unregisterReceiver(sendBroadcastReceiver);
         unregisterReceiver(deliveryBroadcastReceiver);
      }


      super.onDestroy();
   }





   public void startRecording(View view)
   {
      try {
         DBAdapter adapter = new DBAdapter(this);

         if (adapter.getLastLoginUser() != null)
         {
            Intent intent = new Intent(this, VideoRecorderView.class);
            startActivity(intent);
         }
         else
         {
            Toast.makeText(this, getString(R.string.no_user_associated), Toast.LENGTH_SHORT).show();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public void showSMSConfirmationDialog(View view)
   {
      try {
         dialog = new Dialog(this);
         Display display = getWindowManager().getDefaultDisplay();
         Point size = new Point();
         display.getSize(size);
         int width = size.x;
         DBAdapter adapter = new DBAdapter(getBaseContext());

         SMS sms = adapter.getSMS();

         dialog.setContentView(R.layout.sms_confirmation_dialog);
         dialog.setTitle(R.string.sms_confirmation_title);
         if (sms != null)
         {
            TextView message = (TextView)dialog.findViewById(R.id.message_body);
            message.setText(sms.getMessage());
            TextView recipent = (TextView)dialog.findViewById(R.id.recipent_number);
            recipent.setText(sms.getPhone());

            Button confirmButton = (Button)dialog.findViewById(R.id.confirm);
            confirmButton.getLayoutParams().width = (width / 2) - 10;
            confirmButton.setOnClickListener(this);
            Button cancelButton = (Button)dialog.findViewById(R.id.cancel);
            cancelButton.getLayoutParams().width = (width / 2) - 10;
            cancelButton.setOnClickListener(this);
            dialog.show();
         }
         else
         {
            Toast.makeText(getApplicationContext(), R.string.no_sms_defined, Toast.LENGTH_LONG).show();
         }
      } catch (Resources.NotFoundException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void onClick(View view)
   {
      try {
         Intent intent;
         switch (view.getId())
         {
         case R.id.login:
            intent = new Intent(this, LoginActivity.class);
            if(!TextUtils.isEmpty(medTypeFlag)){

                  intent.putExtra(MED_TYPE, "Med" + medReminderId);
                  intent.putExtra(MED_TYPE_FLAG, medTypeFlag);
                  intent.putExtra(MED_NAME, title);
                  intent.putExtra(MED_REMINDER_DATE, date);
                  intent.putExtra(MED_REMINDER_TIME, time);
                  intent.putExtra(USER_NAME, LoginActivity.userName);
                  intent.putExtra(MED_ID_Notify, medId);
                  intent.putExtra(NOTIFICATION_ID, medReminderId);
                  intent.putExtra(MED_REMINDER_ID, medReminderId);
                  intent.putExtra("type", medication_type);
                  intent.putExtra("Message", medication_message);
                  startActivity(intent);
               }
               else {
               startActivity(intent);
            }
            if(!TextUtils.isEmpty(medication_type)) {
               if (medication_type.equals("PRES")) {
                  intent.putExtra("type", medication_type);
                  intent.putExtra("presUniqueId",presNotificationId);
                  intent.putExtra("presMessage", medication_message);

               } else if (medication_type.equals("App")) {
                  intent.putExtra("type", medication_type);
                  intent.putExtra("appMessage", medication_message);
                  intent.putExtra("appUniqueId",appNotificationId);


               } else if (medication_type.equals("EME")) {
                  intent.putExtra("type", medication_type);
                  intent.putExtra("emeMessage", medication_message);
                  intent.putExtra("emeReminderId",medId);


               } else {
                  intent.putExtra("type", medication_type);
                  intent.putExtra("titMessage", medication_message);


               }
               startActivity(intent);
            }
            else
            {
            startActivity(intent);
               }

            break;
         case R.id.signup_button:
            intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            break;
         case R.id.record_video:
            startRecording(view);
            break;
         case R.id.send_sms:
            showSMSConfirmationDialog(view);
            break;
         case R.id.confirm:
            sendSMS();
            break;
         case R.id.cancel:
            dialog.dismiss();
            break;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void sendSMS()
   {

      try {
         TextView view = (TextView)dialog.findViewById(R.id.recipent_number);
         String phoneNo = view.getText() != null && view.getText().length() > 0 ? view.getText().toString() : "";
         view = (TextView)dialog.findViewById(R.id.message_body);
         String message = view.getText() != null && view.getText().length() > 0 ? view.getText().toString() : "";
         dialog.dismiss();
         if (phoneNo.length() > 0 && message.length() > 0)
         {
            registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED));
            registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));
            broadcastReceiversRegistered = true;
            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNo, null, message, sentPI, deliveredPI);
         }
         else
         {
            Toast.makeText(getApplicationContext(), R.string.no_sms_defined, Toast.LENGTH_LONG).show();
         }
      } catch (Resources.NotFoundException e) {
         e.printStackTrace();
      }
   }
}
