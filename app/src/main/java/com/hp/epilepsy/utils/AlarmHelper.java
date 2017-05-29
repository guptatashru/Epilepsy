package com.hp.epilepsy.utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.hp.epilepsy.widget.LoginActivity;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.model.AppointmentReminder;
import com.hp.epilepsy.widget.model.MedicationReminders;
import com.hp.epilepsy.widget.model.NewMedication;
import com.hp.epilepsy.widget.model.PrescriptionReminders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmHelper {
    private Context context;
    private AlarmManager alarmManager;
    //Medication Const attributes
    public static String MED_ID = "MED_ID";
    public static String IS_EME_MEDICATION = "IS_EME_MEDICATION";
    public static String MED_NAME = "MED_NAME";
    public static String EMERGENCY_EXPIRTY_DATE = "EMERGENCY_EXPIRTY_DATE";
    public static String MED_REMINDER_ID = "MED_REMINDER_ID";
    public static String MED_REMINDER_STARTING_TIME = "MED_STARTING_TIME";
    public static String MED_TITRATION_TIME = "MED_TITRATION_TIME";
    public static String MED_TITRATION_WEEK_NUM = "MED_TITRATION_WEEK_NUM";
    public static  String USER_NAME="USER_NAME";
    public boolean IsEmergencyMedication = false;


    //Appointment Const attributes
    public static String APP_ID = "APP_ID";
    public static String GP_NAME = "GP_NAME";
    public static String APP_REMINDER_ID = "APP_REMINDER_ID";
    public static String APP_REMINDER_STARTING_TIME = "APP_REMINDER_STARTING_TIME";
    public static String APP_REMINDER__TIME="APP_REMINDER__TIME";


    //Emergency Medication const attributes
    public static String EME_ID = "EME_ID";
    public static String MEE_MEDICATION_NAME = "MEE_MEDICATION_NAME";
    public static String EMERGENCY_REMINDER_ID = "EMERGENCY_REMINDER_ID";
    public static String EMERGENCY_REMINDER_STARTING_TIME = "EMERGENCY_REMINDER_STARTING_TIME";
    public static String EMERGENCY_REMINDER_EXPIRY_TIME = "EMERGENCY_REMINDER_EXPIRY_TIME";



    //Prescription const attributes
    public static String PRESC_MEDICATION__ID = "PRESC_MEDICATION__ID";
    public static String PRESC_MEDICATION_NAME = "PRESC_MEDICATION_NAME";
    public static String PRESCR_REMINDER_ID = "PRESCR_REMINDER_ID";
    public static String PRESCRIPTION_REMINDER_STARTING_TIME = "PRESCRIPTION_REMINDER_STARTING_TIME";
    public static String PRESCRIPTION_REMINDER_ALERT_BEFORE = "PRESCRIPTION_REMINDER_ALERT_BEFORE";


    public static final String NOTIFICATION_SERVICE = "notification";
    public static int MED_TITRATION_ID_FACTOR = 1000000;


    public AlarmHelper(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: createMedicationTitration
     * Created By: mahmoud
     * Modified By:Shruti & Nikunj
     * Purpose: create med titration
     * -----------------------------------------------------------------------------
     */
    public void createMedicationTitration(int medicationId, String medicationName, Date startDate, int numOfWeeks) {
        try {
            Calendar calendar = Calendar.getInstance();
            if (startDate != null && numOfWeeks > 0) {
                for (int i = 1; i <=numOfWeeks; i++) {
                    DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                    calendar.add(Calendar.DATE, 7);
                    String d=(formatter.format(calendar.getTime()));
                    Date date=formatter.parse(d);
                    PendingIntent alarmIntent = initTitrationPendingIntent(medicationId, medicationName, date, i);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


   public void cancelMedicationTitration(int medicationId) {
        try {
            DBAdapter db = new DBAdapter(context);
            NewMedication medication = db.getMedicationbyID(medicationId).get(0);
            DateTimeFormats format = new DateTimeFormats();
            String medicationName = medication.getMedication_name();
            Date startDate = format.convertStringToDate(medication.getTitrationStartDate());
            int numOfWeeks = medication.getTitrationNumWeeks();
            if (startDate != null && numOfWeeks > 0) {
                for (int i = 1; i <= numOfWeeks; i++) {
                    startDate.setTime(startDate.getTime() + (i * AlarmManager.INTERVAL_DAY * 7));
                    PendingIntent alarmIntent = initTitrationPendingIntent(medicationId, medicationName, startDate, i);
                    alarmManager.cancel(alarmIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: createEmergencyMedicationAlarm
     * Created By: mahmoud
     * Modified By:Shruti & Nikunj
     * Purpose: create Emergency Medication Alarm
     * -----------------------------------------------------------------------------
     */
    public void createEmergencyMedicationAlarm(int medicationId, String mediactioName, String expirtyDate) {
        try {
            DBAdapter db = new DBAdapter(context);
            MedicationReminders medReminder = db.getEmergencyMedicationReminders(medicationId);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatter2 = new SimpleDateFormat(DateTimeFormats.deafultDateFormat);
            String x = formatter2.format(formatter.parse(medReminder.getMedReminderDate()));
            createEmergencyMedicationAlarm(medicationId, mediactioName, medReminder.getID(), DateTimeFormats.convertToIsoDateformat(x), expirtyDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelEmergencyMedicationAlarms(int medicationId, String mediactioName, String expirtyDate) {
        try {
            DBAdapter db = new DBAdapter(context);
            MedicationReminders medReminder = db.getEmergencyMedicationReminders(medicationId);
            cancelEmergencyMedicationAlarm(medicationId, mediactioName, medReminder.getID(), DateTimeFormats.convertToIsoDateformat(medReminder.getMedReminderDate()), expirtyDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: createAppointmentAlarms
     * Created By:Shruti & Nikunj
     * Purpose: create Appointment Alarms
     * -----------------------------------------------------------------------------
     */
    public void createAppointmentAlarms(int appointmentId, String GPName) {
        try {
            DBAdapter db = new DBAdapter(context);
            List<AppointmentReminder> appReminders = db.getAppointmentRemindersByIDDuplicate(appointmentId);
            AppointmentReminder reminder;
            for (int i = 0; i < appReminders.size(); i++) {
                reminder = appReminders.get(i);
                createAppointmentAlarm(appointmentId, GPName, reminder.getAppointmentID(), reminder.getAppointmentReminderDateTime(),reminder.getReminderDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * -----------------------------------------------------------------------------
     * Class Name: cancelAppointmentAlarms
     * Created By:Nikunj &Shruti
     * Purpose: cancel Appointment Alarms
     * -----------------------------------------------------------------------------
     */
    public void cancelAppointmentAlarms(int appointmentId, String GPName) {
        try {
            DBAdapter db = new DBAdapter(context);
            List<AppointmentReminder> appReminders = db.getAppointmentRemindersByIDDuplicate(appointmentId);
            AppointmentReminder reminder;
            for (int i = 0; i < appReminders.size(); i++) {
                reminder = appReminders.get(i);
                cancelAppointmentAlarm(appointmentId, GPName, reminder.getAppointmentID(), reminder.getAppointmentReminderDateTime(),reminder.getReminderDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * -----------------------------------------------------------------------------
     * Class Name: cancelAppointmentAlarms
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: cancel Appointment Alarms
     * -----------------------------------------------------------------------------
     */
    public void createMedicationAlarms(int medicationId, String mediactioName, String ExpirtyDate) {
        try {
            DBAdapter db = new DBAdapter(context);
            List<MedicationReminders> medReminders = db.getMedicationReminders(medicationId);
            MedicationReminders reminder;
            for (int i = 0; i < medReminders.size(); i++) {
                reminder = medReminders.get(i);
                createMedicationAlarm(medicationId, mediactioName, reminder.getID(), DateTimeFormats.convertToIsoDateformat(reminder.getMedReminderDate()), ExpirtyDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: createMedPrescriptionAlarm
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: create Med Prescription Alarm
     * -----------------------------------------------------------------------------
     */
    public void createMedPrescriptionAlarm(int medicationId, String medicationName) {
        try {
            DBAdapter db = new DBAdapter(context);
            ArrayList<PrescriptionReminders> presReminder = db.getPrescriptionReminders(medicationId);
            PrescriptionReminders reminder;
            for (int i = 0; i < presReminder.size(); i++) {
                reminder = presReminder.get(i);
                createPrescriptionAlarm(medicationId, medicationName, reminder.getId(), DateTimeFormats.convertStringToDate(reminder.getPrescriptionReminderDate()), reminder.getAlertbefore());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: cancelMedPrescriptionAlarm
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: cancel Med Prescription Alarm
     * -----------------------------------------------------------------------------
     */

    public void cancelMedPrescriptionAlarm(int medicationId, String medicationName) {
        try {
            DBAdapter db = new DBAdapter(context);
            ArrayList<PrescriptionReminders> presReminder = db.getPrescriptionReminders(medicationId);
            PrescriptionReminders reminder;
            for (int i = 0; i < presReminder.size(); i++) {
                reminder = presReminder.get(i);
                cancelPrescriptionAlarm(medicationId, medicationName, reminder.getId(), DateTimeFormats.convertStringToDate(reminder.getPrescriptionReminderDate()), reminder.getAlertbefore());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * -----------------------------------------------------------------------------
     * Class Name: cancelMedicationAlarms
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: cancel Medication Alarms
     * -----------------------------------------------------------------------------
     */

    public void cancelMedicationAlarms(int medicationId, String mediactioName, String expirtyDate) {
        try {
            DBAdapter db = new DBAdapter(context);
            List<MedicationReminders> medReminders = db.getMedicationReminders(medicationId);
            MedicationReminders reminder;
            for (int i = 0; i < medReminders.size(); i++) {
                reminder = medReminders.get(i);
                cancelMedicationAlarm(medicationId, mediactioName, reminder.getID(), DateTimeFormats.convertToIsoDateformat(reminder.getMedReminderDate()), expirtyDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void createMedicationAlarm(int medicationId, String medicationName, int medicationReminderId, Date startingTime, String expirtyDate) {
        PendingIntent alarmIntentNotification = initAlarmPendingIntentNotification(medicationId, medicationName, medicationReminderId, startingTime, expirtyDate);
      //  alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startingTime.getTime(), AlarmManager.INTERVAL_DAY, alarmIntentNotification);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startingTime.getTime(),AlarmManager.INTERVAL_DAY, alarmIntentNotification);

    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: createPrescriptionAlarm
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: create Prescription Alarm
     * -----------------------------------------------------------------------------
     */
    public void createPrescriptionAlarm(int medicationId, String medicationName, int prescriptionReminder, Date startingTime, String alertbefore) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startingTime);
        cal.set(Calendar.HOUR, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startingTimeedited = cal.getTime();
        PendingIntent alarmIntent = initMedicationPrescriptionReminderAlarm(medicationId, medicationName, prescriptionReminder , startingTimeedited, alertbefore);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startingTimeedited.getTime(),AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: cancelPrescriptionAlarm
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: cancel Prescription Alarm
     * -----------------------------------------------------------------------------
     */

    public void cancelPrescriptionAlarm(int medicationId, String medicationName, int prescriptionReminder, Date startingTime,String alertbefore) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startingTime);
        cal.set(Calendar.HOUR, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startingTimeedited = cal.getTime();
        PendingIntent alarmIntent = initMedicationPrescriptionReminderAlarm(medicationId, medicationName, prescriptionReminder, startingTimeedited,alertbefore);
        alarmManager.cancel(alarmIntent);
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: createEmergencyMedicationAlarm
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: create Emergency Medication Alarm
     * -----------------------------------------------------------------------------
     */
    public void createEmergencyMedicationAlarm(int medicationId, String medicationName, int medicationReminderId, Date startingTime, String expirtyDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startingTime);
        cal.set(Calendar.HOUR, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startingTimeedited = cal.getTime();
        PendingIntent alarmIntent = initEmergencyMedicationReminderAlarm(medicationId, medicationName, medicationReminderId, startingTimeedited,expirtyDate);
        alarmManager.set(AlarmManager.RTC_WAKEUP,startingTimeedited.getTime(),alarmIntent);


    }


    public void cancelMedicationAlarm(int medicationId, String medicationName, int medicationReminderId, Date startingTime, String experityDate) {
        PendingIntent alarmIntentnotifications = initAlarmPendingIntentNotification(medicationId, medicationName, medicationReminderId, startingTime, experityDate);
        alarmManager.cancel(alarmIntentnotifications);
    }


    public void cancelEmergencyMedicationAlarm(int medicationId, String medicationName, int medicationReminderId, Date startingTime, String experityDate) {
        PendingIntent alarmIntent = initEmergencyMedicationReminderAlarm(medicationId, medicationName, medicationReminderId, startingTime,experityDate);
        alarmManager.cancel(alarmIntent);
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: createAppointmentAlarm
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: create Appointment Alarm
     * -----------------------------------------------------------------------------
     */

    public void createAppointmentAlarm(int appointmentId, String GPName, int appointmentReminderId, Date startingTime,String reminderTime) {
        if(!((Calendar.getInstance().getTimeInMillis()-startingTime.getTime())>0))
        {
            PendingIntent alarmIntent = initAppointmentReminderAlarm(appointmentId, GPName, appointmentReminderId, startingTime,reminderTime);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startingTime.getTime(), AlarmManager.INTERVAL_DAY, alarmIntent);
        }


    }


    public void cancelAppointmentAlarm(int appointmentId, String GPName, int appointmentReminderId, Date startingTime,String reminderTime) {
        PendingIntent alarmIntent = initAppointmentReminderAlarm(appointmentId, GPName, appointmentReminderId, startingTime,reminderTime);
        alarmManager.cancel(alarmIntent);

    }

    public PendingIntent initAlarmPendingIntentNotification(int medicationId, String medicationName, int medicationReminderId, Date startingTime, String ExpirtyDate) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(MED_ID, medicationId);
            intent.putExtra(MED_NAME, medicationName);
            intent.putExtra(EMERGENCY_EXPIRTY_DATE, ExpirtyDate);
            intent.putExtra(USER_NAME, LoginActivity.userName);
            intent.putExtra(MED_REMINDER_ID, medicationReminderId);
            intent.putExtra(IS_EME_MEDICATION, IsEmergencyMedication);
            intent.putExtra(MED_REMINDER_STARTING_TIME, startingTime);
            return PendingIntent.getBroadcast(context, medicationReminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public PendingIntent initAppointmentReminderAlarm(int appointmentId, String GPName, int appointmentReminderId, Date startingTime,String reminderTime) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(APP_ID, appointmentId);
            intent.putExtra(GP_NAME, GPName);
            intent.putExtra(USER_NAME, LoginActivity.userName);
            intent.putExtra(APP_REMINDER_ID, appointmentReminderId);
            intent.putExtra(APP_REMINDER_STARTING_TIME, startingTime);
            intent.putExtra(APP_REMINDER__TIME,reminderTime);

            return PendingIntent.getBroadcast(context, appointmentReminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public PendingIntent initEmergencyMedicationReminderAlarm(int emMedicationId, String MedicationName, int EmergemcyReminderId, Date startingTime,String expiryDate) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(EME_ID, emMedicationId);
            intent.putExtra(MEE_MEDICATION_NAME, MedicationName);
            intent.putExtra(USER_NAME, LoginActivity.userName);
            intent.putExtra(EMERGENCY_REMINDER_ID, EmergemcyReminderId);
            intent.putExtra(EMERGENCY_REMINDER_STARTING_TIME, startingTime);
            intent.putExtra(EMERGENCY_REMINDER_EXPIRY_TIME,expiryDate);
            return PendingIntent.getBroadcast(context, EmergemcyReminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * -----------------------------------------------------------------------------
     * Class Name: initMedicationPrescriptionReminderAlarm
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: pending intent for Prescription
     * -----------------------------------------------------------------------------
     */
    public PendingIntent initMedicationPrescriptionReminderAlarm(int MedicationId, String MedicationName, int PrescriptionReminderId, Date startingTime,String alertbefore) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(PRESC_MEDICATION__ID, MedicationId);
            intent.putExtra(PRESC_MEDICATION_NAME, MedicationName);
            intent.putExtra(PRESCR_REMINDER_ID, PrescriptionReminderId);
            intent.putExtra(USER_NAME, LoginActivity.userName);
            intent.putExtra(PRESCRIPTION_REMINDER_STARTING_TIME, startingTime);
            intent.putExtra(PRESCRIPTION_REMINDER_ALERT_BEFORE, alertbefore);
            return PendingIntent.getBroadcast(context, PrescriptionReminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public PendingIntent initTitrationPendingIntent(int medicationId, String medicationName, Date notificationTime, int titrationWeek) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(MED_ID, medicationId);
            intent.putExtra(MED_NAME, medicationName);
            intent.putExtra(USER_NAME, LoginActivity.userName);
            intent.putExtra(MED_TITRATION_TIME, notificationTime);
            intent.putExtra(MED_TITRATION_WEEK_NUM, titrationWeek);
            return PendingIntent.getBroadcast(context, medicationId + MED_TITRATION_ID_FACTOR + titrationWeek, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

}
