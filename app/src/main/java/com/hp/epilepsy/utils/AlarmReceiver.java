package com.hp.epilepsy.utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.HomeView;
import com.hp.epilepsy.widget.LoginActivity;
import com.hp.epilepsy.widget.MedicationAlarmsActivity;
import com.hp.epilepsy.widget.RecordVideoActivity;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.model.AppointmentReminder;
import com.hp.epilepsy.widget.model.EmergencyMedicationEntity;
import com.hp.epilepsy.widget.model.MedicationReminders;
import com.hp.epilepsy.widget.model.PrescriptionReminders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by mahmoumo
 * Modified by : Nikunj & Shruti
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String NOTIFICATION_SERVICE = "notification";
    public static String MED_TYPE = "MED_TYPE";
    public static String MED_TYPE_FLAG = "MED_TYPE_FLAG";
    public static String MED_ID_Notification = "MED_ID_Notification";
    public static String MED_NAME = "MED_NAME";
    public static String MED_REMINDER_DATE = "MED_REMINDER_DATE";
    public static String MED_REMINDER_TIME = "MED_REMINDER_TIME";
    public static String USER_NAME = "USER_NAME";
    public static String MED_REMINDER_ID = "MED_REMINDER_ID";
    public static String PRESCRIPTION_RENEWAL_MESSAGE = "PRESCRIPTION_RENEWAL_MESSAGE";
    public static String MEICATION_TITRATION_MESSAGE = "MEICATION_TITRATION_MESSAGE";
    public static String APPOINTMENT_MESSAGE = "APPOINTMENT_MESSAGE";
    public static String EMERGENCY_MEDICATION_MESSAGE = "EMERGENCY_MEDICATION_MESSAGE";
    public static String NOTIFICATION_ID = "NOTIFICATION_ID";
    Context ctx;
    private AlarmManager alarmManager;


    @Override
    public void onReceive(Context context, Intent intent) {

        DBAdapter ad = new DBAdapter(context);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            ArrayList<MedicationReminders> medicationRemindersOnReboot = ad.getMedicationRemindersOnReboot();
            ArrayList<AppointmentReminder> appointmentReminders = ad.getAppointmentRemindersByIDOnReboot();
            ArrayList<PrescriptionReminders> prescriptionReminderses = ad.getPrescriptionRemindersOnReboot();
            ArrayList<EmergencyMedicationEntity> alEmergencyMedi = ad.getEmergencyMedicationsOnReboot();

            /* MEDICATION ALARM SET AREA */
            if (medicationRemindersOnReboot != null && medicationRemindersOnReboot.size() > 0)
                for (int iInc = 0; iInc < medicationRemindersOnReboot.size(); iInc++) {
                    AlarmHelper alarmHelper = new AlarmHelper(context);
                    Date startingDate = DateTimeFormats.convertToIsoDateformat(medicationRemindersOnReboot.get(iInc).getMedReminderDate());
                    PendingIntent alarmIntentNotification = alarmHelper.initAlarmPendingIntentNotification(medicationRemindersOnReboot.get(iInc).getMedicationID(), medicationRemindersOnReboot.get(iInc).getMedicationName(), medicationRemindersOnReboot.get(iInc).getID(), startingDate, null);
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startingDate.getTime(), AlarmManager.INTERVAL_DAY, alarmIntentNotification);
                }
 /*
            /*APPOINTMENT REMINDER*/

            if (appointmentReminders != null && appointmentReminders.size() > 0)
                for (int i = 0; i < appointmentReminders.size(); i++) {
                    AlarmHelper alarmHelper = new AlarmHelper(context);
                    PendingIntent alarmIntent = alarmHelper.initAppointmentReminderAlarm(appointmentReminders.get(i).getAppointmentID(), appointmentReminders.get(i).getGPName(), appointmentReminders.get(i).getAppointmentID(), appointmentReminders.get(i).getAppointmentReminderDateTime(),appointmentReminders.get(i).getReminderDate());
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, appointmentReminders.get(i).getAppointmentReminderDateTime().getTime(), AlarmManager.INTERVAL_DAY, alarmIntent);
                }

            /*PRESCRIPTION ALARM*/
            if (prescriptionReminderses != null && prescriptionReminderses.size() > 0) {
                DBAdapter db = new DBAdapter(context);
                AlarmHelper alarmHelper = new AlarmHelper(context);
                for (int i = 0; i < prescriptionReminderses.size(); i++) {

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(DateTimeFormats.convertStringToDate(prescriptionReminderses.get(i).getPrescriptionReminderDate()));
                    cal.set(Calendar.HOUR, 9);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    Date startingTimeedited = cal.getTime();
                    PendingIntent alarmIntent = alarmHelper.initMedicationPrescriptionReminderAlarm(prescriptionReminderses.get(i).getMedicationId(), prescriptionReminderses.get(i).getMedicationName(), prescriptionReminderses.get(i).getId(), DateTimeFormats.convertStringToDate(prescriptionReminderses.get(i).getPrescriptionReminderDate()), prescriptionReminderses.get(i).getAlertbefore());
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startingTimeedited.getTime(), AlarmManager.INTERVAL_DAY, alarmIntent);
                }

            }


            /*EMERGENCY MEDICATION ALARM*/

            if (alEmergencyMedi != null && alEmergencyMedi.size() > 0) {

                for (int iPrescInc = 0; iPrescInc < alEmergencyMedi.size(); iPrescInc++) {
                    EmergencyMedicationEntity emergencyMedicationEntity = ad.getEmergencyMedicationbyIDReboot(alEmergencyMedi.get(iPrescInc).getemergencyMedicationID());
                    AlarmHelper alarmHelper = new AlarmHelper(context);
                    alarmHelper.createEmergencyMedicationAlarm(alEmergencyMedi.get(iPrescInc).getemergencyMedicationID(), alEmergencyMedi.get(iPrescInc).getEmergencyMedicationName(), emergencyMedicationEntity.getExpiryDate());
                    try {
                        DBAdapter db = new DBAdapter(context);
                        MedicationReminders medReminder = db.getEmergencyMedicationRemindersReboot(alEmergencyMedi.get(iPrescInc).getemergencyMedicationID());
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat formatter2 = new SimpleDateFormat(DateTimeFormats.deafultDateFormat);
                        String x = formatter2.format(formatter.parse(medReminder.getMedReminderDate()));
                        Date startingTime = DateTimeFormats.convertToIsoDateformat(x);
                        PendingIntent alarmIntentNotification = alarmHelper.initEmergencyMedicationReminderAlarm(alEmergencyMedi.get(iPrescInc).getemergencyMedicationID(), alEmergencyMedi.get(iPrescInc).getEmergencyMedicationName(), alEmergencyMedi.get(iPrescInc).getId(), startingTime,emergencyMedicationEntity.getExpiryDate());
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startingTime.getTime(), AlarmManager.INTERVAL_DAY, alarmIntentNotification);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


        }

        try {
            this.ctx = context;
            Bundle data = intent.getExtras();
            if (data != null) {

                if (data.containsKey(AlarmHelper.APP_ID) && data.containsKey(AlarmHelper.APP_REMINDER_ID)) {
                    int AppointmentId = (int) data.get(AlarmHelper.APP_ID);
                    int AppointmentReminderId = (int) data.get(AlarmHelper.APP_REMINDER_ID);
                    String GPName = (String) data.get(AlarmHelper.GP_NAME);
                    String appReminderDate=(String)data.get(AlarmHelper.APP_REMINDER__TIME);
                    String UserNAme = (String) data.get(AlarmHelper.USER_NAME);
                    LoginActivity.userName = UserNAme;
                    Date DateTime = (Date) data.get(AlarmHelper.APP_REMINDER_STARTING_TIME);
                    System.out.println("date time is"+DateTime);
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                    LoginActivity.userName = UserNAme;
                    String date = dateFormat.format(DateTime);
                    String time = timeFormat.format(DateTime);
                    issueAlarmNotification(ctx, "App", null, "Appointment reminder", AppointmentId, "Appointment with " + GPName + " on " + DateTimeFormats.reverseDateFormat(appReminderDate)+ " " + time, AppointmentReminderId);

                } else if (data.containsKey(AlarmHelper.EME_ID) && data.containsKey(AlarmHelper.EMERGENCY_REMINDER_ID)) {

                    int emergencyMedicationID = (int) data.get(AlarmHelper.EME_ID);
                    int emergencyMedicationReminderId = (int) data.get(AlarmHelper.EMERGENCY_REMINDER_ID);
                    String UserNAme = (String) data.get(AlarmHelper.USER_NAME);
                    String emergencyMedicationNane = (String) data.get(AlarmHelper.MEE_MEDICATION_NAME);
                    String expiryDate=(String)data.get(AlarmHelper.EMERGENCY_REMINDER_EXPIRY_TIME);
                    Date DateTime = (Date) data.get(AlarmHelper.EMERGENCY_REMINDER_STARTING_TIME);
                    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                    DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                    LoginActivity.userName = UserNAme;
                    String date = dateFormat.format(DateTime);
                    String time = timeFormat.format(DateTime);
                    LoginActivity.userName = UserNAme;
                    issueAlarmNotification(ctx, "EME", expiryDate, "Emergency medication", emergencyMedicationID, emergencyMedicationNane, emergencyMedicationReminderId);

                } else if (data.containsKey(AlarmHelper.PRESC_MEDICATION__ID) && data.containsKey(AlarmHelper.PRESCR_REMINDER_ID)) {
                    int medicationID = (int) data.get(AlarmHelper.PRESC_MEDICATION__ID);
                    int prescriptionReminderId = (int) data.get(AlarmHelper.PRESCR_REMINDER_ID);
                    String UserNAme = (String) data.get(AlarmHelper.USER_NAME);
                    LoginActivity.userName = UserNAme;
                    String prescriptionMedicationName = (String) data.get(AlarmHelper.PRESC_MEDICATION_NAME);
                    String alertbefore = (String) data.get(AlarmHelper.PRESCRIPTION_REMINDER_ALERT_BEFORE);
                    Date DateTime = (Date) data.get(AlarmHelper.PRESCRIPTION_REMINDER_STARTING_TIME);
                    Date d = new Date();
                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(DateTime);
                    cal2.setTime(d);
                    if (alertbefore.equals("OneDay")) {
                        issueAlarmNotification(ctx, "PRES", "Prescription renewal reminder", medicationID, "You have to renew your medication " + prescriptionMedicationName + " tomorrow", prescriptionReminderId, null, prescriptionReminderId);
                    } else if (alertbefore.equals("ThreeDays")) {
                        issueAlarmNotification(ctx, "PRES", "Prescription renewal reminder", medicationID, "You have to renew your medication " + prescriptionMedicationName + " after 3 days", prescriptionReminderId, null, prescriptionReminderId);
                    } else if (alertbefore.equals("OneWeek")) {
                        issueAlarmNotification(ctx, "PRES", "Prescription renewal reminder", medicationID, "You have to renew your medication " + prescriptionMedicationName + " after 1 week", prescriptionReminderId, null, prescriptionReminderId);
                    }

                } else if (data.containsKey(AlarmHelper.MED_TITRATION_TIME) && data.containsKey(AlarmHelper.MED_TITRATION_WEEK_NUM)) {
                    int medId = (int) data.get(AlarmHelper.MED_ID);
                    String medName = (String) data.get(AlarmHelper.MED_NAME);
                    String UserNAme = (String) data.get(AlarmHelper.USER_NAME);
                    Date datetime = (Date) data.get(AlarmHelper.MED_TITRATION_TIME);
                    LoginActivity.userName = UserNAme;
                    int weekNum = (int) data.get(AlarmHelper.MED_TITRATION_WEEK_NUM);
                    issueAlarmNotification(ctx, "MED_TIT", null, medName, 0, "Your dosage of Drug " + medName + " is changing today, it's week " + weekNum, medId + AlarmHelper.MED_TITRATION_ID_FACTOR + weekNum);

                } else if (data.containsKey(AlarmHelper.MED_REMINDER_ID)) {
                    int medId = (int) data.get(AlarmHelper.MED_ID);
                    int medReminderId = (int) data.get(AlarmHelper.MED_REMINDER_ID);
                    String UserNAme = (String) data.get(AlarmHelper.USER_NAME);
                    String EmergencyExpiryDate = (String) data.get(AlarmHelper.EMERGENCY_EXPIRTY_DATE);
                    String medName = (String) data.get(AlarmHelper.MED_NAME);
                    Date datetime = (Date) data.get(AlarmHelper.MED_REMINDER_STARTING_TIME);
                    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                    DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                    LoginActivity.userName = UserNAme;
                    String date = dateFormat.format(datetime);
                    String time = timeFormat.format(datetime);
                    issueAlarmNotification(ctx, "Med", date, medName, medId, time, medReminderId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void issueAlarmNotification(Context ctx, String type, String title, int Id, String message, int notificationId, String MedicationName, int uniquePresID) {
        try {
            DBAdapter ad = new DBAdapter(ctx);//PRES
            int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
            Intent notificationIntent = null;
            if(ApplicationBackgroundCheck.getCurrentValue(ctx)>0)
            {
                notificationIntent = new Intent(ctx, HomeView.class);
            }
            else {

                notificationIntent = new Intent(ctx, RecordVideoActivity.class);
            }
            ArrayList<PrescriptionReminders> alPresctiptionReminder = ad.getPrescriptionReminders_WithNoRepeat(uniquePresID);

            if(alPresctiptionReminder.size()>0){
                        notificationIntent.putExtra(PRESCRIPTION_RENEWAL_MESSAGE, message);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        notificationIntent.putExtra("type", type);
                        notificationIntent.putExtra("userName", LoginActivity.userName);
                        notificationIntent.putExtra("id", notificationId);
                        notificationIntent.putExtra("Ali", type);
                        notificationIntent.putExtra("medPresId", Id);
                        notificationIntent.putExtra("presUniqueId", uniquePresID);
                        notificationIntent.putExtra("title",title);
                        getFireNotification(ctx, title, message, notificationIntent, type, Id, uniquePresID);

                    }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void issueAlarmNotification(Context ctx, String type, String Date, String title, int MedId, String Message, int medReminderId) {
        try {

            DBAdapter ad = new DBAdapter(ctx);
            boolean isMedication = false;
            int NotificationId = new Random().nextInt((10000 - 5) + 1) + 1;
            Intent notificationIntent;
            if (type.equals("Med")) {
                if(ApplicationBackgroundCheck.getCurrentValue(ctx)>0)
                {
                    notificationIntent = new Intent(ctx, MedicationAlarmsActivity.class);
                    notificationIntent.putExtra("flag", "Abc");
                }
                else {

                    notificationIntent = new Intent(ctx, RecordVideoActivity.class);
                    notificationIntent.putExtra("flag", "Abc1");
                }
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                notificationIntent.putExtra(MED_TYPE, type + medReminderId);
                notificationIntent.putExtra(MED_TYPE_FLAG, type);
                notificationIntent.putExtra(MED_NAME, title);
                notificationIntent.putExtra(MED_REMINDER_DATE, Date);
                notificationIntent.putExtra(MED_REMINDER_TIME, Message);
                notificationIntent.putExtra(USER_NAME, LoginActivity.userName);
                notificationIntent.putExtra(MED_ID_Notification, MedId);
                notificationIntent.putExtra(NOTIFICATION_ID, medReminderId);
                notificationIntent.putExtra(MED_REMINDER_ID, medReminderId);
                isMedication = true;
                getFireNotification(ctx, title, Message, notificationIntent, type, MedId, medReminderId);

            } else {
                if(ApplicationBackgroundCheck.getCurrentValue(ctx)>0)
                {
                    notificationIntent = new Intent(ctx, HomeView.class);

                }
                else {
                    notificationIntent = new Intent(ctx, RecordVideoActivity.class);
                }

                if (type.equals("MED_TIT")) {
                    notificationIntent.putExtra(MEICATION_TITRATION_MESSAGE, Message);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    notificationIntent.putExtra("type", type);
                    notificationIntent.putExtra("userName", LoginActivity.userName);
                    notificationIntent.putExtra("id", MedId);
                    notificationIntent.putExtra("Ali", type);
                    notificationIntent.putExtra("title",title);
                    getFireNotification(ctx, title, Message, notificationIntent, type, MedId, medReminderId);
                }

                if (type.equals("App")) {
                    ArrayList<AppointmentReminder> alAppointmentReminder = ad.getAppointmentReminders_WithNoRepeat(medReminderId);
                    if (alAppointmentReminder.size() > 0) {
                        notificationIntent.putExtra(APPOINTMENT_MESSAGE, Message);
                        notificationIntent.putExtra("uniqueAppId", medReminderId);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        notificationIntent.putExtra("type", type);
                        notificationIntent.putExtra("userName", LoginActivity.userName);
                        notificationIntent.putExtra("id", MedId);
                        notificationIntent.putExtra("Ali", type);
                        notificationIntent.putExtra("title",title);
                        getFireNotification(ctx, title, Message, notificationIntent, type, MedId, medReminderId);
                    }
                }
                if (type.equals("EME")) {
                    notificationIntent.putExtra(EMERGENCY_MEDICATION_MESSAGE, "Emergency Medication " + Message + " " +"is about to expire on"+" "+":"+" "+ DateTimeFormats.reverseDateFormat(Date));
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    notificationIntent.putExtra("type", type);
                    notificationIntent.putExtra("userName", LoginActivity.userName);
                    notificationIntent.putExtra("id", MedId);
                    notificationIntent.putExtra("Ali", type);
                    notificationIntent.putExtra("title",title);
                    getFireNotification(ctx, title, Message, notificationIntent, type, MedId, medReminderId);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getFireNotification(Context Cntx, String title, String Message, Intent notificationIntent, String type, int MedId, int medReminderId) {
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, medReminderId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(Message)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(pendingIntent);
       NotificationManager mNotifyMgr =
                (NotificationManager) ctx.getSystemService(ctx.NOTIFICATION_SERVICE);
        if (type.equals("MED_TIT")) {
            mNotifyMgr.notify(MedId, mBuilder.build());
        } else {

            mNotifyMgr.notify(medReminderId, mBuilder.build());
        }

    }

}