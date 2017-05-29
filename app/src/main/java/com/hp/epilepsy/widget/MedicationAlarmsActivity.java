package com.hp.epilepsy.widget;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.AlarmHelper;
import com.hp.epilepsy.utils.AlarmReceiver;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.model.MedicationReminders;
import com.hp.epilepsy.widget.model.NewMedication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;




/**
 * -----------------------------------------------------------------------------
 * Class Name: MedicationAlarmsActivity
 * Created By:mahmoud
 * Modified By:Nikunj & Shruti
 * Purpose:to handle daily med notfications
 * -----------------------------------------------------------------------------
 */


public class MedicationAlarmsActivity extends Activity {
    TextView txtDate;
    TextView txtDateTime;
    TextView txtTime;
    TextView txtMedDesc;
    EditText missedReason;
    RadioButton tookMedication;
    RadioButton missedMedicatio;
    RadioButton faildToRecordMedication;
    public boolean IsEmergencyMed;
    Button btnDismiss;
    Button btnSave;
    int medId;
    String medTypeFlag;
    int medReminderId;
    int NotificationID;
    String EmergencyExpiryDate;
    int medicationStatus = 0;
    Date Reminderdatetime;
    SimpleDateFormat dateFormatter;
    LinearLayout temp;
    DBAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_alarms);
        temp = (LinearLayout) findViewById(R.id.templayout);
        try {

            Bundle passedData = this.getIntent().getExtras();
            if (passedData != null) {
                if (passedData.containsKey(AlarmHelper.MED_TITRATION_TIME) && passedData.containsKey(AlarmHelper.MED_TITRATION_WEEK_NUM)) {
                    int medId = (int) passedData.get(AlarmHelper.MED_ID);
                    String medName = (String) passedData.get(AlarmHelper.MED_NAME);
                    Date datetime = (Date) passedData.get(AlarmHelper.MED_TITRATION_TIME);
                    int weekNum = (int) passedData.get(AlarmHelper.MED_TITRATION_WEEK_NUM);
                    String UserNAme = (String) passedData.get(AlarmHelper.USER_NAME);
                    LoginActivity.userName = UserNAme;
                    finish();

                } else if (passedData.containsKey(AlarmReceiver.MED_TYPE)&& passedData.getString("flag").equals("Abc")) {

                    //Issue clicking on the notification dublicates the apperance MedicationAlarmsActivity  with the same reminder because it created twice
                    initAlarmViews();
                    DBAdapter adapter = new DBAdapter(getApplicationContext());
                    medId = (int) passedData.get(AlarmReceiver.MED_ID_Notification);
                    medTypeFlag = (String) passedData.get(AlarmReceiver.MED_TYPE_FLAG);
                    String title = (String) passedData.get(AlarmReceiver.MED_NAME);
                    String UserNAme = (String) passedData.get(AlarmReceiver.USER_NAME);
                    LoginActivity.userName = UserNAme;
                    String MedicatioReminderId = ((String) passedData.get(AlarmReceiver.MED_TYPE)).substring(3);
                    //get medicationReminder object by Medication Reminder Id
                    MedicationReminders reminderOBJ = adapter.getMedicationRemindersById(Integer.parseInt(MedicatioReminderId));
                    String MedicationReminderDate = reminderOBJ.getMedReminderDate();
                    Date datetime = DateTimeFormats.convertStringToDateTimeWithDeafultFormat(MedicationReminderDate);   //error
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                    String date = dateFormat.format(datetime);
                    String time = timeFormat.format(datetime);
                    //Get Medication object  by MEdication Id
                    NewMedication newMedication = adapter.getSpecificMedicationbyID(reminderOBJ.getMedicationID());
                    // medReminderId = (int) passedData.get(AlarmReceiver.MED_REMINDER_ID);
                    NotificationID = (int) passedData.get(AlarmReceiver.NOTIFICATION_ID);
                    txtDate.setText(date);
                    txtTime.setText(time);
                    txtDateTime.setText(MedicationReminderDate);
                    txtMedDesc.setText(title);



                } else if(passedData.getString("flag").equals("Abc1")) {
                    initAlarmViews();
                    DBAdapter adapter = new DBAdapter(getApplicationContext());
                    medId = (int) passedData.get(LoginActivity.MED_ID_Notify);
                    medTypeFlag = (String) passedData.get(LoginActivity.MED_TYPE_FLAG);
                    String title = (String) passedData.get(LoginActivity.MED_NAME);
                    String UserNAme = (String) passedData.get(LoginActivity.USER_NAME);
                    LoginActivity.userName = UserNAme;
                    String MedicatioReminderId = ((String) passedData.get(LoginActivity.MED_TYPE)).substring(3);
                    //get medicationReminder object by Medication Reminder Id
                    MedicationReminders reminderOBJ = adapter.getMedicationRemindersById(Integer.parseInt(MedicatioReminderId));
                    String MedicationReminderDate = reminderOBJ.getMedReminderDate();
                    Date datetime = DateTimeFormats.convertStringToDateTimeWithDeafultFormat(MedicationReminderDate);   //error
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                    String date = dateFormat.format(datetime);
                    String time = timeFormat.format(datetime);
                    //Get Medication object  by MEdication Id
                    NewMedication newMedication = adapter.getSpecificMedicationbyID(reminderOBJ.getMedicationID());
                    // medReminderId = (int) passedData.get(AlarmReceiver.MED_REMINDER_ID);
                    NotificationID = (int) passedData.get(LoginActivity.NOTIFICATION_ID);

                    txtDate.setText(date);
                    txtTime.setText(time);
                    txtDateTime.setText(MedicationReminderDate);
                    txtMedDesc.setText(title);

                }
            }
        }
        catch (Exception e)
        {
        }
    }




    @Override
    public  void onPause()
    {
        super.onPause();
    }

    private void initAlarmViews() {
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDateTime = (TextView) findViewById(R.id.txtDateTime);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtMedDesc = (TextView) findViewById(R.id.txtMedDesc);
        missedReason = (EditText) findViewById(R.id.missedReason);
        tookMedication = (RadioButton) findViewById(R.id.tookMedication);
        missedMedicatio = (RadioButton) findViewById(R.id.missedMedication);
        faildToRecordMedication = (RadioButton) findViewById(R.id.faildToRecord);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDismiss = (Button) findViewById(R.id.btnDismiss);
        btnSave.setOnClickListener(btnSaveOnClickListener);
        btnDismiss.setOnClickListener(btnDismissOnClickListener);
    }


    public void issueAlarmNotification(String title, String message, int medicationId, String Username, int MedicationReinderId) {

        try {
            int random = new Random().nextInt((10000 - 5) + 1) + 1;

            Intent notificationIntent = new Intent(this, MedicationAlarmsActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            LoginActivity.userName = Username;
            notificationIntent.putExtra(AlarmReceiver.MED_TYPE, "Med" + medReminderId);
            notificationIntent.putExtra(AlarmReceiver.MED_NAME, title);
            notificationIntent.putExtra(AlarmReceiver.MED_REMINDER_TIME, medicationId);
            notificationIntent.putExtra(AlarmReceiver.USER_NAME, LoginActivity.userName);
            notificationIntent.putExtra(AlarmReceiver.MED_ID_Notification, medicationId);
            notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, MedicationReinderId);
            notificationIntent.putExtra(AlarmReceiver.MED_REMINDER_ID, MedicationReinderId);
            notificationIntent.putExtra("userName", Username);
            notificationIntent.putExtra("id", medicationId);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, MedicationReinderId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                            .setLights(Color.YELLOW, 100, 100)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);
            NotificationManager mNotifyMgr = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            checkScreenWakeUp();
            mNotifyMgr.notify(MedicationReinderId, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void checkScreenWakeUp() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();

        Log.e("screen on...........", "" + isScreenOn);

        if (isScreenOn == false) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");

            wl_cpu.acquire(10000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
          }

    // 2.0 and above
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // Before 2.0
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    View.OnClickListener btnSaveOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (tookMedication.isChecked() || missedMedicatio.isChecked()) {

                DBAdapter adapter = new DBAdapter(getApplicationContext());
                if (tookMedication.isChecked()) {
                    medicationStatus = 1;
                } else if (missedMedicatio.isChecked()) {

                    medicationStatus = 2;
                } else if (faildToRecordMedication.isChecked()) {
                    medicationStatus = 3;
                }
                if ((medicationStatus == 3 && missedReason.getText().length() == 0)) {
                    Toast.makeText(MedicationAlarmsActivity.this, "Please enter the reason for missing medication", Toast.LENGTH_SHORT).show();
                } else {

                    MedicationReminders reminderOBJ = adapter.getMedicationRemindersByDateTime(txtDateTime.getText().toString());
                    int MedicationReminderId = reminderOBJ.getID();
                    int MedicationId = reminderOBJ.getMedicationID();
                    String MedicationUniqueTime=reminderOBJ.getMedReminderDate();

                    dateFormatter = new SimpleDateFormat(DateTimeFormats.isoDateFormat2, Locale.US);
                    Calendar c = Calendar.getInstance();
                    String todayDate = dateFormatter.format(c.getTime());


                    adapter.AddMedicationAlarmReminder(MedicationId, MedicationReminderId, todayDate, medicationStatus, missedReason.getText().toString(),MedicationUniqueTime);
                    NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    nMgr.cancel(MedicationReminderId);
                    adapter.getUpdateReminderFlag(medTypeFlag, medId,"1");

                }
            } else {
                Toast.makeText(getApplicationContext(), "Please check the medication as missed or taken", Toast.LENGTH_SHORT).show();
            }
            Intent i=new Intent(getApplicationContext(),HomeView.class);
            startActivity(i);

           /* Fragment fragment = new MedicationDairy();
            String tagString = getString(R.string.record_siezure_detail_frag_tag);


            if (fragment != null) {

                FragmentManager frgManager = getFragmentManager();
                frgManager.beginTransaction().replace(R.id.content_frame, fragment, tagString).addToBackStack(null)
                        .commit();
            }
            temp.setVisibility(View.GONE);*/


        }
    };


    View.OnClickListener btnDismissOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DBAdapter adapter = new DBAdapter(getApplicationContext());
            MedicationReminders reminderOBJ = adapter.getMedicationRemindersByDateTime(txtDateTime.getText().toString());
            int MedicationReminderId = reminderOBJ.getID();
            int MedicationId = reminderOBJ.getMedicationID();
            String MedicationReminderTime=reminderOBJ.getMedReminderDate();
            medicationStatus = 1;
            adapter.AddMedicationAlarmReminder(MedicationId, MedicationReminderId, new Date().toString(), medicationStatus, missedReason.getText().toString(),MedicationReminderTime);
            NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(MedicationReminderId);

            Intent intent = new Intent(MedicationAlarmsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
