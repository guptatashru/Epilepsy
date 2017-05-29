package com.hp.epilepsy.widget.adapter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.utils.IRESTWebServiceCaller;
import com.hp.epilepsy.utils.ServiceUtil;
import com.hp.epilepsy.widget.LoginActivity;
import com.hp.epilepsy.widget.model.Appointment;
import com.hp.epilepsy.widget.model.AppointmentReminder;
import com.hp.epilepsy.widget.model.EmergencyMedicationEntity;
import com.hp.epilepsy.widget.model.MedicationReminders;
import com.hp.epilepsy.widget.model.MissedMedication;
import com.hp.epilepsy.widget.model.NewContactEntity;
import com.hp.epilepsy.widget.model.NewMedication;
import com.hp.epilepsy.widget.model.PrescriptionReminders;
import com.hp.epilepsy.widget.model.RecordedVideo;
import com.hp.epilepsy.widget.model.SMS;
import com.hp.epilepsy.widget.model.SeizureDetails;
import com.hp.epilepsy.widget.model.SeizureDuration;
import com.hp.epilepsy.widget.model.SeizureFeature;
import com.hp.epilepsy.widget.model.SeizurePostFeature;
import com.hp.epilepsy.widget.model.SeizurePreFeature;
import com.hp.epilepsy.widget.model.SeizureTrigger;
import com.hp.epilepsy.widget.model.SeizureType;
import com.hp.epilepsy.widget.model.TakenMedication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/**
 * -----------------------------------------------------------------------------
 * Class Name: DBAdapter
 * Created By:mahmoud
 * Modified By:Nikunj & Shruti
 * Purpose: database operations
 * -----------------------------------------------------------------------------
 */

public class DBAdapter {
    public static final String DATABASE_NAME = "db.Epilepsy";
    public static final int DATABASE_VERSION = 4;
    private static final String ISOShortDatePattern = "yyyy-MM-dd";
    private static final String DateTimePattern = "MMMM d, yyyy hh:mm a";
    Map<String, HashMap<String, List>> items = new TreeMap<String, HashMap<String, List>>(Collections.reverseOrder());
    public static final String SIEZURE_TYPE = "Seizures";
    public static final String MISSED_MEDICATIONS_TYPE = "MissedMedication";
    public static final String MEDICATION_TITERATIONS_TYPE = "MedicationTitration";
    public static final String APPOINTMENTS_TYPE = "Appointments";
    public static final String EMERGENCY_MEDICATIONS_TYPE = "EmergencyMedications";
    public static final String PRESCRIPTION_RENEWALS_TYPE = "Prescription_renewals";

    private static final String CREATE_TABLE_CONTACTS =
            "CREATE TABLE contacts (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "contactName varchar(255),"
                    + "relationship varchar(255),"
                    + "userName varchar(255), "
                    + "e_mail varchar(255),"
                    + "deletedFlag boolean, "
                    + "contactNumber varchar(255) "
                    + ");";

    private static final String CREATE_TABLE_EMERGENCY_MEDICATION_REMINDER =
            "CREATE TABLE EmergencyReminder (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "emergencyMedication_id INTEGER, "
                    + "userName varchar(255), "
                    + "DateTime varchar(255), "
                    + "medicationHandlingReason varchar(255), "
                    + "medicationHandlingStatus INTEGER, "
                    + "reminderFlag INTEGER DEFAULT (0), "
                    + "reminderDaysBefor INTEGER,"
                    + "FOREIGN KEY(emergencyMedication_id) REFERENCES emergencyMedication(_id)"
                    + ");";

    private static final String CREATE_TABLE_EMERGENCY_MEDICATION =
            "CREATE TABLE emergencyMedication (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "medicationName varchar(255),"
                    + "dosage INTEGER,"
                    + "reminderDaysBefor INTEGER,"
                    + "deletedFlag boolean, "
                    + "userName varchar(255), "
                    + "unit varchar(255), "
                    + "reminderFlag INTEGER DEFAULT (0), "

                    + "DateTime varchar(255) "
                    + ");";

    private static final String CREATE_TABLE_EMERGENCY_MEDICATION_IMAGES =
            "CREATE TABLE emergency_medications_images (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "emergency_medication_id INTEGER, "
                    + "Image_link varchar(255), "
                    + "userName varchar(255),"
                    + "FOREIGN KEY(emergency_medication_id) REFERENCES emergencyMedication(_id)"
                    + ");";

    private static final String CREATE_TABLE_APPOINTMENT_REMINDER =
            "CREATE TABLE AppointmentReminder (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "appointment_id INTEGER, "
                    + "userName varchar(255), "
                    + "ReminderAlert varchar(255), "
                    + "DateTime varchar(255), "
                    + "Date varchar(255), "
                    + "Time varchar(255), "
                    + "reminder_App_Flag INTEGER DEFAULT (0) , "
                    + "FOREIGN KEY(appointment_id) REFERENCES Appointment(_id)"
                    + ");";

    private static final String CREATE_TABLE_APPOINTMENT =
            "CREATE TABLE Appointment (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "GpName varchar(255),"
                    + "UserAddress varchar(255),"
                    + "userName varchar(255), "
                    + "deletedFlag boolean, "
                    + "latitude DOUBLE, "
                    + "longitude DOUBLE, "
                    + "Date varchar(255), "
                    + "Time varchar(255), "
                    + "DateTime varchar(255), "
                    + "reminderFlag INTEGER DEFAULT (0) , "
                    + "AppointmentNotes varchar(255)"
                    + ");";

    private static final String CREATE_TABLE_MEDICATION =
            "CREATE TABLE new_medication (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "medication_name varchar(255), "
                    + "userName varchar(255), "
                    + "medication_dosag INTEGER, "
                    + "medication_unit varchar(255), "
                    + "medication_desc varchar(255), "
                    + "start_data varchar(255), "
                    + "deleteMed boolean, "
                    + "PrescriptionrenewalDate varchar(255),"
                    + "medStartDate varchar(255), "
                    + "titrationOn boolean, "
                    + "titrationStartdate varchar(255), "
                    + "titrationNumWeeks int"
                    + ");";

    private static final String CREATE_TABLE_MEDICATION_IMAGES =
            "CREATE TABLE medications_images (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "medication_id INTEGER, "
                    + "Image_link varchar(255), "
                    + "userName varchar(255),"
                    + "FOREIGN KEY(medication_id) REFERENCES new_medication(_id)"
                    + ");";

    private static final String CREATE_TABLE_MEDICATION_REMINDERS =
            "CREATE TABLE MedicationReminders (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "medication_id INTEGER, "
                    + "RminderDateTime varchar(255), "
                    + "MedicationAlarmDateTime varchar(255), "
                    + "medicationHandlingReason varchar(255), "
                    + "medicationHandlingStatus INTEGER, "
                    + "userName varchar(255), "
                    + "reminderFlag INTEGER DEFAULT (0), "
                    + "FOREIGN KEY(medication_id) REFERENCES new_medication(_id)"

                    + ");";

    private static final String CREATE_TABLE_MEDICATION_ALARM_REMINDERS =
            "CREATE TABLE MedicationAlarmReminders (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "medication_id INTEGER, "
                    + "Reminder_id INTEGER, "
                    + "MedicationAlarmDateTime varchar(255), "
                    + "medicationHandlingReason varchar(255), "
                    + "medicationHandlingStatus INTEGER, "
                    + "userName varchar(255),"
                    + "ReminderDateTime varchar(255),"
                    + "FOREIGN KEY(medication_id) REFERENCES new_medication(_id),"
                    + "FOREIGN KEY(Reminder_id) REFERENCES MedicationReminders(_id)"

                    + ");";

    private static final String CREATE_TABLE_PRESCRIPTION =
            "CREATE TABLE Prescription (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "medication_id INTEGER,"
                    + "prescriptionReminderDate varchar(255),"
                    + "Alertbefore varchar(255),"
                    + "userName varchar(255), "
                    + "reminderFlag INTEGER DEFAULT (0), "
                    + "FOREIGN KEY(medication_id) REFERENCES new_medication(_id)"
                    + ");";


    private static String DB_PATH = "";
    private static String TAG = "DataBaseHelper"; // Tag just for the LogCat
    final Context context;
    SQLiteDatabase db;
    DatabaseHelper dbhelper;

    public DBAdapter(Context c) {
        context = c;

        dbhelper = new DatabaseHelper(context);
        try {
            dbhelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DBAdapter open() {
        if(dbhelper!=null)
        db = dbhelper.getWritableDatabase();
        else {
            dbhelper = new DatabaseHelper(context);
            db = dbhelper.getWritableDatabase();
        }
        return this;
    }


    private void insertItem(Object object, String type, String date) {

        try {
            HashMap<String, List> subItems = items.get(date);
            if (subItems == null) {
                subItems = new HashMap<>();
            }
            List displayedObjects = subItems.get(type);
            if (displayedObjects == null) {
                displayedObjects = new ArrayList();
            }
            displayedObjects.add(object);
            subItems.put(type, displayedObjects);
            items.put(date, subItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: getAppointmentRemindersByIDOnReboot
     * Created By: Nikunj & Shruti
     * Created Date: 09-02-2017
     * Purpose: Set appointment at booting time
     * -----------------------------------------------------------------------------
     */
    public ArrayList<AppointmentReminder> getAppointmentRemindersByIDOnReboot() {
        ArrayList<AppointmentReminder> reminder = null;
        try {
            reminder = new ArrayList<>();
            AppointmentReminder reminderOBJ = new AppointmentReminder();
            String sqlQuary = "select Appointment._id,Appointment.DateTime,Appointment.GpName from Appointment where Appointment.reminderFlag == " + 0 + ";";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                do {

                    reminderOBJ.setID(cursor.getInt(0));
                    reminderOBJ.setGPName(cursor.getString(1));
                    reminderOBJ.setAppointmentReminderDateTime(convertAppointmentDateTimeToDateTime(cursor.getString(2)));
                    reminder.add(reminderOBJ);
                    reminderOBJ = new AppointmentReminder();

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reminder;
    }


    /**
     * -----------------------------------------------------------------------------
     * Class Name: getAppointmentRemindersByIDOnReboot
     * Created By: Nikunj & Shruti
     * Created Date: 09-02-2017
     * Modified By:
     * Modified Date:
     * Purpose: Set appointment at booting time
     * -----------------------------------------------------------------------------
     */
    public ArrayList<PrescriptionReminders> getPrescriptionRemindersOnReboot() {
        ArrayList<PrescriptionReminders> reminder = null;
        try {
            reminder = new ArrayList<PrescriptionReminders>();
            PrescriptionReminders presc = new PrescriptionReminders();
            //String sqlQuary = "select new_medication._id,new_medication.medication_name,Prescription._id,Prescription.prescriptionReminderDate,Prescription.Alertbefore from new_medication join Prescription on new_medication._id=Prescription.medication_id where new_medication.username= '" + LoginActivity.userName + "' and new_medication._id=" + medication_Id + ";";
            String sqlQuary = "select new_medication._id,new_medication.medication_name,Prescription._id,Prescription.prescriptionReminderDate,Prescription.Alertbefore from new_medication join Prescription on new_medication._id=Prescription.medication_id where Prescription.reminderFlag == " + 0 + ";";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    presc.setMedicationId(Integer.valueOf(cursor.getString(0)));
                    presc.setMedicationName(cursor.getString(1));
                    presc.setId(cursor.getInt(2));
                    presc.setPrescriptionReminderDate(cursor.getString(3));
                    presc.setAlertbefore(cursor.getString(4));
                    reminder.add(presc);
                    presc = new PrescriptionReminders();
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reminder;
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: getEmergencyMedicationsOnReboot
     * Created By: Nikunj & Shruti
     * Created Date: 09-02-2017
     * Modified By:
     * Modified Date:
     * Purpose: Retrieve stored emergencyMedications
     * -----------------------------------------------------------------------------
     */

    public ArrayList<EmergencyMedicationEntity> getEmergencyMedicationsOnReboot() {
        String sqlQuary = "select emergencyMedication._id,emergency_medications_images.emergency_medication_id,emergencyMedication.medicationName,emergencyMedication.DateTime,emergencyMedication.reminderDaysBefor,emergencyMedication.unit, emergency_medications_images.Image_link from emergencyMedication join emergency_medications_images on emergencyMedication._id=emergency_medications_images.emergency_medication_id where emergencyMedication.deletedFlag == " + 0 + " and emergencyMedication.reminderFlag = " + 0 + ";";
        open();
        Cursor cursor = db.rawQuery(sqlQuary, null);
        EmergencyMedicationEntity emMedicationss = null;
        int EmmedicationId = -1;
        ArrayList<EmergencyMedicationEntity> Medications = new ArrayList<EmergencyMedicationEntity>();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                if (EmmedicationId == -1 || EmmedicationId != cursor.getInt(0)) {
                    if (emMedicationss != null) {
                        Medications.add(emMedicationss);
                    }
                    emMedicationss = new EmergencyMedicationEntity();
                    emMedicationss.setId(cursor.getInt(0));
                    emMedicationss.setEmergencyMedicationName(cursor.getString(2));
                    emMedicationss.setExpiryDate(cursor.getString(3));
                    emMedicationss.setReminderDayeBefore(cursor.getInt(4));
                    emMedicationss.setUnit(cursor.getString(5));
                    emMedicationss.setEmergencyMedicationID(cursor.getInt(1));
                    EmmedicationId = cursor.getInt(0);
                }
                emMedicationss.getMedicationImages().add(cursor.getString(6));

                emMedicationss.setEmergencyMedicationID(cursor.getInt(1));
            } while (cursor.moveToNext());

            Medications.add(emMedicationss);
            cursor.close();
            close();
        }
        return Medications;
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: getSeizuresBetweenDates
     * Created By: mahmoud
     * Modified By:Nikunj and Shruti
     * Purpose: get seizures betwwn specified two dates
     * -----------------------------------------------------------------------------
     */
    public ArrayList<SeizureDetails> getSeizuresBetweenDates(String startDate, String endDate) {
        try {
            open();
            SeizureDetails seizureDetails;
            String x_start = null;
            ArrayList<SeizureDetails> seizurelist = new ArrayList<SeizureDetails>();
            Date date_start = DateTimeFormats.convertReportsStringToDate(startDate);
            Date date_end = DateTimeFormats.convertReportsStringToDate(endDate);

            String sqlQuery = "select id, stypeId, durationId, sdate, stime from seizure_details where userName = '"
                    + LoginActivity.userName + "';";

            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    seizureDetails = new SeizureDetails();
                    seizureDetails.setId(cursor.getInt(0));
                    seizureDetails.setSeizureType(getSeizureType(cursor.getString(1)));
                    seizureDetails.setSeizureDuration(getSeizureDuration(cursor.getString(2)));
                    seizureDetails.setSqlDateTime(cursor.getString(3));
                    seizureDetails.setTime(cursor.getString(4));

                    try {

                        if (cursor.getString(3) != null) {
                            Date x = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(3));
                            x_start = new SimpleDateFormat(DateTimeFormats.isoDateFormatReverse).format(x);

                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (x_start != null) {

                        Date date_seizure = DateTimeFormats.convertReportsStringToDate(x_start);
                        if (date_seizure.after(date_start) && (date_seizure.before(date_end) || date_seizure.equals(date_end))) {

                            seizurelist.add(seizureDetails);
                        }
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
            close();

            return seizurelist;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //this method to get seizures betwwn specified two dates
    public ArrayList<SeizureDetails> getOccurenceSeizuresBetweenDates(String startDate, String endDate, int stypeId) {
        ArrayList<SeizureDetails> seizurelist = null;
        try {
            open();
            SeizureDetails seizureDetails;
            seizurelist = new ArrayList<SeizureDetails>();

            String sqlQuery = "select id, stypeId, durationId, sdate, stime from seizure_details where sdate between '"
                    + startDate + "' and '" + endDate + "' and userName = '"
                    + LoginActivity.userName + "' and stypeId = '"
                    + stypeId + "';";

            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //create new object from SeizureDetails class
                    seizureDetails = new SeizureDetails();
                    seizureDetails.setId(cursor.getInt(0));
                    seizureDetails.setSeizureType(getSeizureType(cursor.getString(1)));
                    seizureDetails.setSeizureDuration(getSeizureDuration(cursor.getString(2)));
                    seizureDetails.setSqlDateTime(cursor.getString(3));
                    seizureDetails.setTime(cursor.getString(4));
                    seizurelist.add(seizureDetails);
                } while (cursor.moveToNext());
                cursor.close();
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seizurelist;
    }


    public ArrayList<TakenMedication> getTakenMedicationsTest(String MedicationName) {
        ArrayList<TakenMedication> TakenMedicationList = new ArrayList<TakenMedication>();
        TakenMedication takenMed;
        open();
        String sqlQuery = "select new_medication._id,new_medication.medication_name,new_medication.medStartDate, MedicationReminders.RminderDateTime  from new_medication join MedicationReminders on new_medication._id=MedicationReminders.medication_id where MedicationReminders.medicationHandlingStatus=1 and new_medication.username= '" + LoginActivity.userName + "' and new_medication.medication_name ='" + MedicationName + "';";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                takenMed = new TakenMedication();
                takenMed.setMedicationId(cursor.getInt(0));
                takenMed.setMedicationName(cursor.getString(1));
                takenMed.setMedStartDate(cursor.getString(2));
                takenMed.setMedicationReminderDate(cursor.getString(3));
                TakenMedicationList.add(takenMed);

            } while (cursor.moveToNext());
            cursor.close();
        }
        close();
        return TakenMedicationList;
    }


    //this method to get the missed medication between two dates
    public ArrayList<TakenMedication> getTakenMedications(String MedicationName) {
        ArrayList<TakenMedication> TakenMedicationList = new ArrayList<TakenMedication>();
        TakenMedication takenMed;
        open();
        String sqlQuery = "select new_medication._id,new_medication.medication_name,new_medication.medStartDate, MedicationAlarmReminders.MedicationAlarmDateTime  from new_medication join MedicationAlarmReminders on new_medication._id=MedicationAlarmReminders.medication_id where MedicationAlarmReminders.medicationHandlingStatus=1 and new_medication.username= '" + LoginActivity.userName + "' and new_medication.medication_name ='" + MedicationName + "';";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                takenMed = new TakenMedication();
                takenMed.setMedicationId(cursor.getInt(0));
                takenMed.setMedicationName(cursor.getString(1));
                takenMed.setMedStartDate(cursor.getString(2));
                takenMed.setMedicationReminderDate(cursor.getString(3));
                TakenMedicationList.add(takenMed);

            } while (cursor.moveToNext());
            cursor.close();
        }
        close();
        return TakenMedicationList;
    }


    //this method to get the missed medication between two dates
    public ArrayList<NewMedication> getMedicationsBetweenDates(String startDate, String endDate) {
        ArrayList<NewMedication> MedicationList = new ArrayList<>();
        try {
            NewMedication Medication;
            open();
            String sqlQuery = "select new_medication._id,new_medication.medication_name,new_medication.medStartDate from new_medication where new_medication.username= '" + LoginActivity.userName + "' and new_medication.medStartDate between'" + startDate + "' and '" + endDate + "';";

            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Medication = new NewMedication();
                    Medication.setId(cursor.getInt(0));
                    Medication.setMedication_name(cursor.getString(1));
                    Medication.setMedStartDate(cursor.getString(2));
                    MedicationList.add(Medication);
                } while (cursor.moveToNext());
                cursor.close();
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MedicationList;
    }

    //this method to get the missed medication between two dates
    public ArrayList<MissedMedication> getMissedMedicationsDateRange(String startDate, String endDate) {
        ArrayList<MissedMedication> MedicationList = new ArrayList<>();
        try {
            MissedMedication Medication;
            open();
            String sqlQuery = "select new_medication._id,new_medication.medication_name,new_medication.medStartDate, MedicationAlarmReminders.MedicationAlarmDateTime from new_medication join MedicationAlarmReminders where new_medication._id=MedicationAlarmReminders.medication_id and MedicationAlarmReminders.medicationHandlingStatus==2 and new_medication.username= '" + LoginActivity.userName + "' and new_medication.medStartDate between'" + startDate + "' and '" + endDate + "';";
            //String sqlQuery = "select new_medication._id,new_medication.medication_name,new_medication.medStartDate, MedicationReminders.medicationHandlingReason from new_medication join MedicationReminders where new_medication._id=MedicationReminders.medication_id and MedicationReminders.medicationHandlingStatus=2 and new_medication.username= '" + LoginActivity.userName + "' and new_medication.medStartDate between'" + startDate + "' and '" + endDate + "';";
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Medication = new MissedMedication();
                    Medication.setMedicationId(cursor.getInt(0));
                    Medication.setMedicationName(cursor.getString(1));
                    Medication.setMedStartDate(cursor.getString(2));
                    MedicationList.add(Medication);

                } while (cursor.moveToNext());
                cursor.close();
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MedicationList;
    }


    //this method to get the missed medication between two dates
    public ArrayList<NewMedication> getSpecificMissedMedicationsBetweenDates(String startDate, String endDate, String medicationName) {
        ArrayList<NewMedication> MedicationList = new ArrayList<>();
        try {
            NewMedication Medication;
            open();
//            String sqlQuery = "select new_medication._id,new_medication.medication_name,new_medication.medStartDate, MedicationReminders.RminderDateTime  from new_medication join MedicationReminders on new_medication._id=MedicationReminders.medication_id where MedicationReminders.medicationHandlingStatus=2 and new_medication.username= '" + LoginActivity.userName + "' and new_medication.medication_name ='" + medicationName + "' and new_medication.medStartDate between'" + startDate + "' and '" + endDate + "';";
            String sqlQuery = "select new_medication._id,new_medication.medication_name,new_medication.medStartDate, MedicationAlarmReminders.MedicationAlarmDateTime  from new_medication join MedicationAlarmReminders on new_medication._id=MedicationAlarmReminders.medication_id where MedicationAlarmReminders.medicationHandlingStatus=2 and new_medication.username= '" + LoginActivity.userName + "' and new_medication.medication_name ='" + medicationName + "' and new_medication.medStartDate between'" + startDate + "' and '" + endDate + "';";

            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.moveToFirst()) {

                do {
                    Medication = new NewMedication();
                    Medication.setId(cursor.getInt(0));
                    Medication.setMedication_name(cursor.getString(1));
                    Medication.setMedStartDate(cursor.getString(2));
                    Medication.setMedication_description(cursor.getString(3));
                    MedicationList.add(Medication);

                } while (cursor.moveToNext());
                cursor.close();
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MedicationList;
    }


    public ArrayList<MissedMedication> getTakenMedicationsBetweenDates(String startDate, String endDate) {
        ArrayList<MissedMedication> missedMedicationList = new ArrayList<MissedMedication>();
        MissedMedication missedMed;
        open();
        String sqlQuery = "select new_medication._id,new_medication.medication_name,new_medication.medStartDate, MedicationReminders.RminderDateTime,MedicationReminders.medicationHandlingReason from new_medication join MedicationReminders on new_medication._id=MedicationReminders.medication_id where MedicationReminders.medicationHandlingStatus=1 and new_medication.username= '" + LoginActivity.userName + "' and new_medication.medStartDate between'" + startDate + "' and '" + endDate + "';";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                missedMed = new MissedMedication();
                missedMed.setMedicationId(cursor.getInt(0));
                missedMed.setMedicationName(cursor.getString(1));
                missedMed.setMedStartDate(cursor.getString(2));
                missedMed.setMedicationReminderDate(cursor.getString(3));
                missedMed.setMissedReason(cursor.getString(4));
                //add missed medication object to the mised medication list
                missedMedicationList.add(missedMed);

            } while (cursor.moveToNext());
            cursor.close();
        }
        close();
        return missedMedicationList;
    }


    public Map<String, HashMap<String, List>> getCalendarListData() {
        try {
            open();
            SeizureDetails seizureDetails;
            String sqlQuery = "select id,stypeId, durationId, sdate, stime,emergency_medication_given,ambulance_called from seizure_details where userName = '" + LoginActivity.userName + "' ORDER BY date(sdate) ASC";
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //create new object from SeizureDetails class
                    seizureDetails = new SeizureDetails();
                    seizureDetails.setId(cursor.getInt(0));
                    seizureDetails.setSeizureType(getSeizureType(cursor.getString(1)));
                    seizureDetails.setSeizureDuration(getSeizureDuration(cursor.getString(2)));
                    seizureDetails.setSqlDateTime(cursor.getString(3));
                    seizureDetails.setTime(cursor.getString(4));
                    seizureDetails.setSeizureTriggers(getSeizureTriggersBySeizureId(cursor.getInt(0)));
                    seizureDetails.setSqlEmegencyMedicationGiven(cursor.getInt(5));
                    seizureDetails.setSqlAmbulanceCalled(cursor.getInt(6));
                    seizureDetails.setSeizurePreFeatures(getSeizurePreFeaturesBySeizureId(cursor.getInt(0)));
                    seizureDetails.setSeizureFeatures(getSeizureFeaturesBySeizureId(cursor.getInt(0)));
                    seizureDetails.setSeizurePostFeatures(getSeizurePostFeaturesBySeizureId(cursor.getInt(0)));

                    //Add seizure item to HashMap
                    insertItem(seizureDetails, SIEZURE_TYPE, cursor.getString(3));

                } while (cursor.moveToNext());
                cursor.close();
            }
            close();


            //************************  Add MissedMedication and Medication titration to HashMap object *********************************//
            MissedMedication missedMed;
            NewMedication MedTitration;
            open();
            sqlQuery = "select new_medication._id,new_medication.medication_name,new_medication.titrationOn,new_medication.titrationStartdate,new_medication.titrationNumWeeks,new_medication.medStartDate, MedicationAlarmReminders.MedicationAlarmDateTime,MedicationAlarmReminders.ReminderDateTime  from new_medication join MedicationAlarmReminders on new_medication._id=MedicationAlarmReminders.medication_id where MedicationAlarmReminders.medicationHandlingStatus==2";
            cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    missedMed = new MissedMedication();
                    missedMed.setMedicationId(cursor.getInt(0));
                    missedMed.setMedicationName(cursor.getString(1));
                    missedMed.setMedicationReminderDate(cursor.getString(6));
                    missedMed.setMedicationReminderTime(cursor.getString(7));
                    missedMed.setType("Medication");
                    //add missedMedication object to HashMap
                    insertItem(missedMed, MISSED_MEDICATIONS_TYPE, DateTimeFormats.GetDateFromDateTimeFormat(cursor.getString(6)));
                } while (cursor.moveToNext());
                cursor.close();
            }
            close();


            //////////////////////////////////add emergency medication to HashMap object/////////////////////
            sqlQuery = "select emergencyMedication._id,emergencyMedication.medicationName,emergencyMedication.DateTime from emergencyMedication where emergencyMedication.userName= '" + LoginActivity.userName + "';";
            open();
            cursor = db.rawQuery(sqlQuery, null);
            EmergencyMedicationEntity emergencyMedicationEntity = null;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    emergencyMedicationEntity = new EmergencyMedicationEntity();
                    emergencyMedicationEntity.setId(cursor.getInt(0));
                    emergencyMedicationEntity.setEmergencyMedicationName(cursor.getString(1));
                    emergencyMedicationEntity.setExpiryDate(cursor.getString(2));
                    insertItem(emergencyMedicationEntity, EMERGENCY_MEDICATIONS_TYPE, cursor.getString(2));

                } while (cursor.moveToNext());
                cursor.close();
            }
            close();

            ////////////////////////////Add Medication titration to HashMap Object//////////////////////////////////////////////////
            open();
            sqlQuery = "select new_medication._id,new_medication.medication_name,new_medication.titrationOn,new_medication.titrationStartdate,new_medication.titrationNumWeeks from new_medication where  new_medication.username= '" + LoginActivity.userName + "';";
            cursor = db.rawQuery(sqlQuery, null);
            DateTimeFormats format = new DateTimeFormats();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    MedTitration = new NewMedication();
                    //check if this medication contain titration or not
                    boolean titrationOn = cursor.getInt(2) > 0;
                    if (titrationOn) {
                        MedTitration.setId(cursor.getInt(0));
                        MedTitration.setMedication_name(cursor.getString(1) + " week 1");
                        MedTitration.setTitrationStartDate(cursor.getString(3));

                        int n = cursor.getInt(4);
                        insertItem(MedTitration, "MedicationTitration", cursor.getString(3));
                        Date date = format.convertStringToDate(cursor.getString(3));
                        Calendar titrationTime = Calendar.getInstance();
                        titrationTime.setTime(date);
                        for (int i = 1; i < n; i++) {
                            MedTitration = new NewMedication();
                            titrationTime.add(Calendar.DATE, 7);
                            MedTitration.setId(cursor.getInt(0));
                            MedTitration.setMedication_name(cursor.getString(1) + " week " + (i + 1));
                            MedTitration.setTitrationStartDate(new SimpleDateFormat("yyyy-MM-dd").format(titrationTime.getTime()));
                            insertItem(MedTitration, MEDICATION_TITERATIONS_TYPE, new SimpleDateFormat("yyyy-MM-dd").format(titrationTime.getTime()));
                        }
                    }

                } while (cursor.moveToNext());
                cursor.close();
            }
            close();


            ////////////////////////////Add Prescription renewal to HashMap Object//////////////////////////////////////////////////
            open();
            NewMedication MedPrescription;
            sqlQuery = "select new_medication._id,new_medication.medication_name,new_medication.PrescriptionrenewalDate from new_medication where new_medication.username= '" + LoginActivity.userName + "';";
            cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    MedPrescription = new NewMedication();
                    if (cursor.getString(2).length() != 0) {
                        MedPrescription.setId(cursor.getInt(0));
                        MedPrescription.setMedication_name(cursor.getString(1));
                        MedPrescription.setPrescriptionRenewalDate(cursor.getString(2));
                        insertItem(MedPrescription, PRESCRIPTION_RENEWALS_TYPE, cursor.getString(2));
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
            close();


            //////////////////////////////////Add Appointment to HashMap object////////////////////////////////
            open();
            Appointment appointment;
            sqlQuery = "select Appointment._id,Appointment.GpName,Appointment.Date,Appointment.Time,Appointment.DateTime from Appointment where Appointment.userName= '" + LoginActivity.userName + "' ORDER BY datetime(DateTime) ASC";
            cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    appointment = new Appointment();
                    appointment.setID(cursor.getInt(0));
                    appointment.setGpName(cursor.getString(1));
                    appointment.setAppointmentDate(cursor.getString(2));
                    appointment.setAppointmentTime(cursor.getString(3));

                    //add appointments  to HashMap object
                    insertItem(appointment, APPOINTMENTS_TYPE, cursor.getString(2));
                } while (cursor.moveToNext());
                cursor.close();
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }


//treeMap=items;
        return items;
    }


  /*  public ArrayList<MissedMedication> GetMissedMedications() {
        MissedMedication missedMed;
        ArrayList<MissedMedication> reminder = new ArrayList<MissedMedication>();
        open();
        String sqlQuery = "select new_medication._id,new_medication.medication_name,new_medication.titrationOn,new_medication.titrationStartdate,new_medication.titrationNumWeeks,new_medication.medStartDate, MedicationAlarmReminders.MedicationAlarmDateTime,MedicationAlarmReminders._id from new_medication join MedicationAlarmReminders on new_medication._id=MedicationAlarmReminders.medication_id where MedicationAlarmReminders.medicationHandlingStatus==2 and new_medication.username= '" + LoginActivity.userName + "'";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                missedMed = new MissedMedication();
                missedMed.setMedicationId(cursor.getInt(0));
                missedMed.setMedicationName(cursor.getString(1));
                missedMed.setMedicationReminderDate(cursor.getString(6));
                missedMed.setUnderScID(cursor.getInt(7));
                missedMed.setType("Medication");
                //add missedMedication object to HashMap
                // insertItem(missedMed, MISSED_MEDICATIONS_TYPE, cursor.getString(6));
                reminder.add(missedMed);
            } while (cursor.moveToNext());
            cursor.close();
        }
        close();
        return reminder;
    };*/

    /**
     * -----------------------------------------------------------------------------
     * Class Name: GetMissedMedications
     * Created By: Nikunj and Shruti
     * Purpose: get missed medications
     * -----------------------------------------------------------------------------
     */

    public ArrayList<MissedMedication> GetMissedMedications() {
        MissedMedication missedMed;
        ArrayList<MissedMedication> reminder = new ArrayList<MissedMedication>();
        open();
        String sqlQuery = "select new_medication._id,new_medication.medication_name,new_medication.titrationOn,new_medication.titrationStartdate,new_medication.titrationNumWeeks,new_medication.medStartDate, MedicationAlarmReminders.MedicationAlarmDateTime,MedicationAlarmReminders.ReminderDateTime from new_medication join MedicationAlarmReminders on new_medication._id=MedicationAlarmReminders.medication_id where MedicationAlarmReminders.medicationHandlingStatus==2 and new_medication.username= '" + LoginActivity.userName + "'";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                missedMed = new MissedMedication();
                missedMed.setMedicationId(cursor.getInt(0));
                missedMed.setMedicationName(cursor.getString(1));
                missedMed.setMedicationReminderDate(cursor.getString(6));
                missedMed.setMedicationReminderTime(cursor.getString(7));
                missedMed.setType("Medication");
                reminder.add(missedMed);
            } while (cursor.moveToNext());
            cursor.close();
        }
        close();
        return reminder;
    }



    public  String getDateofMissedMedication(int medication_id,int _id)
    {
        String sDateofMissedMedication="";

        open();
        String sqlQuery="select MedicationReminders.RminderDateTime from MedicationReminders,MedicationAlarmReminders where MedicationReminders.medication_id='"+ medication_id +"'  and  MedicationReminders.medicationHandlingStatus = '2' and MedicationReminders._id='"+_id +"' and MedicationReminders._id == MedicationAlarmReminders._id";

        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                sDateofMissedMedication = cursor.getString(0);
            } while (cursor.moveToNext());
            cursor.close();
        }
        close();
        return sDateofMissedMedication;


    }

    public void close() {
        dbhelper.close();
    }

    public List<SeizureType> getSeizureTypes() {

        List<SeizureType> seizureTypes = null;
        try {
            String sqlQuery = "select id, type, desc, userDefined from seizure_type where userName= '" + LoginActivity.userName + "' or userDefined=0;";
            seizureTypes = new ArrayList<>();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    SeizureType mSeizureType = new SeizureType();
                    mSeizureType.setId(cursor.getInt(0));
                    mSeizureType.setName(cursor.getString(1));
                    mSeizureType.setDescription(cursor.getString(2));
                    mSeizureType.setUserDefined(cursor.getInt(3));
                    seizureTypes.add(mSeizureType);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seizureTypes;
    }

    public List<SeizureDuration> getSeizureDurations() {

        List<SeizureDuration> seizureDurations = null;
        try {
            String sqlQuery = "select id, duration, desc from duration;";
            seizureDurations = new ArrayList<SeizureDuration>();

            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    SeizureDuration mDuration = new SeizureDuration();
                    mDuration.setId(cursor.getInt(0));
                    mDuration.setName(cursor.getString(1));
                    mDuration.setDescription(cursor.getString(2));
                    seizureDurations.add(mDuration);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seizureDurations;
    }

    public List<SeizureTrigger> getSeizureTriggers() {
        List<SeizureTrigger> seizureTriggers = null;
        try {
            String sqlQuery = "select id, trigger, desc, userDefined from trigger where userName= '" + LoginActivity.userName + "' or userDefined=0;";
            seizureTriggers = new ArrayList<SeizureTrigger>();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    SeizureTrigger mTrigger = new SeizureTrigger();
                    mTrigger.setId(cursor.getInt(0));
                    mTrigger.setName(cursor.getString(1));
                    mTrigger.setDescription(cursor.getString(2));
                    mTrigger.setUserDefined(cursor.getInt(3));
                    seizureTriggers.add(mTrigger);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seizureTriggers;
    }

    public SeizureTrigger getTriggerFromId(long triggerId) {
        SeizureTrigger mTrigger = null;
        try {
            String sqlQuery = "select id, trigger, desc, userDefined from trigger where id=" + triggerId;
            mTrigger = new SeizureTrigger();
            open();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                mTrigger.setId(cursor.getInt(0));
                mTrigger.setName(cursor.getString(1));
                mTrigger.setDescription(cursor.getString(2));
                mTrigger.setUserDefined(cursor.getInt(3));
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mTrigger;
    }


    /**
     * -----------------------------------------------------------------------------
     * Class Name: getSeizureTriggersBySeizureId
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: get Seizure Triggers By SeizureId
     * -----------------------------------------------------------------------------
     */
    public List<SeizureTrigger> getSeizureTriggersBySeizureId(long seizureId) {
        List<SeizureTrigger> seizureTriggers = null;
        try {
            String sqlQuery = "select id, seizureDetailId, triggerId from seizure_detail_trigger where seizureDetailId="
                    + seizureId;
            seizureTriggers = new ArrayList<SeizureTrigger>();
            open();
            Cursor mCursor = db.rawQuery(sqlQuery, null);
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                do {
                    seizureTriggers.add(getTriggerFromId(mCursor.getLong(2)));
                }
                while (mCursor.moveToNext());
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seizureTriggers;
    }

    public List<SeizurePreFeature> getSeizurePreFeatures() {
        List<SeizurePreFeature> seizurePreFeatures = null;
        try {
            String sqlQuery = "select id, pre_feature, desc, userDefined from pre_feature where userName= '" + LoginActivity.userName + "' or userDefined=0;";
            seizurePreFeatures = new ArrayList<SeizurePreFeature>();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    SeizurePreFeature mPreFeature = new SeizurePreFeature();
                    mPreFeature.setId(cursor.getInt(0));
                    mPreFeature.setName(cursor.getString(1));
                    mPreFeature.setDescription(cursor.getString(2));
                    mPreFeature.setUserDefined(cursor.getInt(3));
                    seizurePreFeatures.add(mPreFeature);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seizurePreFeatures;
    }

    private List<SeizurePreFeature> getSeizurePreFeaturesBySeizureId(long seizureId) {
        List<SeizurePreFeature> seizurePreFeature = null;
        try {
            String sqlQuery = "select id, seizureDetailId, preFeatureId from seizure_detail_pre_feature where seizureDetailId="
                    + seizureId;

            seizurePreFeature = new ArrayList<SeizurePreFeature>();
            open();
            Cursor mCursor = db.rawQuery(sqlQuery, null);
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                do {
                    seizurePreFeature.add(getPreFeatureFromId(mCursor.getLong(2)));
                }
                while (mCursor.moveToNext());
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seizurePreFeature;
    }

    private SeizurePreFeature getPreFeatureFromId(long preFeatureId) {
        SeizurePreFeature mPreFeature = null;
        try {
            String sqlQuery = "select id, pre_feature, desc, userDefined from pre_feature where id=" + preFeatureId;
            mPreFeature = new SeizurePreFeature();
            open();
            Cursor cursor = db.rawQuery(sqlQuery, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                mPreFeature.setId(cursor.getInt(0));
                mPreFeature.setName(cursor.getString(1));
                mPreFeature.setDescription(cursor.getString(2));
                mPreFeature.setUserDefined(cursor.getInt(3));
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mPreFeature;
    }

    public List<SeizureFeature> getSeizureFeatures() {
        List<SeizureFeature> seizureFeatures = null;
        try {
            String sqlQuery = "select id, feature, desc, userDefined from feature where userName= '" + LoginActivity.userName + "' or userDefined=0;";
            seizureFeatures = new ArrayList<SeizureFeature>();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    SeizureFeature mFeature = new SeizureFeature();
                    mFeature.setId(cursor.getInt(0));
                    mFeature.setName(cursor.getString(1));
                    mFeature.setDescription(cursor.getString(2));
                    mFeature.setUserDefined(cursor.getInt(3));
                    seizureFeatures.add(mFeature);

                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seizureFeatures;
    }

    private List<SeizureFeature> getSeizureFeaturesBySeizureId(long seizureId) {
        List<SeizureFeature> seizureFeature = null;
        try {
            String sqlQuery = "select id, seizure_detail_id, feature_id from seizure_detail_feature where seizure_detail_id="
                    + seizureId;

            seizureFeature = new ArrayList<SeizureFeature>();
            open();
            Cursor mCursor = db.rawQuery(sqlQuery, null);
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                do {
                    seizureFeature.add(getFeatureFromId(mCursor.getLong(2)));
                }
                while (mCursor.moveToNext());
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seizureFeature;
    }

    private SeizureFeature getFeatureFromId(long preFeatureId) {
        SeizureFeature mFeature = null;
        try {
            String sqlQuery = "select id, feature, desc, userDefined from feature where id=" + preFeatureId;
            mFeature = new SeizureFeature();
            open();
            Cursor cursor = db.rawQuery(sqlQuery, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                mFeature.setId(cursor.getInt(0));
                mFeature.setName(cursor.getString(1));
                mFeature.setDescription(cursor.getString(2));
                mFeature.setUserDefined(cursor.getInt(3));
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mFeature;
    }

    public List<SeizurePostFeature> getSeizurePostFeatures() {
        List<SeizurePostFeature> seizureTriggers = null;
        try {
            String sqlQuery = "select id, post_feature, desc, userDefined  from post_feature where userName= '" + LoginActivity.userName + "' or userDefined=0;";
            seizureTriggers = new ArrayList<SeizurePostFeature>();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {

                    SeizurePostFeature mPostFeature = new SeizurePostFeature();
                    mPostFeature.setId(cursor.getInt(0));
                    mPostFeature.setName(cursor.getString(1));
                    mPostFeature.setDescription(cursor.getString(2));
                    mPostFeature.setUserDefined(cursor.getInt(3));
                    seizureTriggers.add(mPostFeature);

                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seizureTriggers;
    }

    private List<SeizurePostFeature> getSeizurePostFeaturesBySeizureId(long seizureId) {
        List<SeizurePostFeature> seizurePostFeatures = null;
        try {
            String sqlQuery = "select id, seizureDetailId, postFeatureId from seizure_detail_post_feature where seizureDetailId="
                    + seizureId;

            seizurePostFeatures = new ArrayList<SeizurePostFeature>();
            open();
            Cursor mCursor = db.rawQuery(sqlQuery, null);
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                do {
                    seizurePostFeatures.add(getPostFeatureFromId(mCursor.getLong(2)));
                }
                while (mCursor.moveToNext());
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seizurePostFeatures;
    }

    private SeizurePostFeature getPostFeatureFromId(long preFeatureId) {
        SeizurePostFeature mPostFeature = null;
        try {
            String sqlQuery = "select id, post_feature, desc, userDefined from post_feature where id=" + preFeatureId;
            mPostFeature = new SeizurePostFeature();
            open();
            Cursor cursor = db.rawQuery(sqlQuery, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                mPostFeature.setId(cursor.getInt(0));
                mPostFeature.setName(cursor.getString(1));
                mPostFeature.setDescription(cursor.getString(2));
                mPostFeature.setUserDefined(cursor.getInt(3));
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mPostFeature;
    }

    public SMS getSMS() {

        try {
            String sqlQuery = "select phone,message from sms;";

            open();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                String phone = cursor.getString(0);
                String message = cursor.getString(1);
                close();
                return new SMS(phone, message);
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean removeSeizure(long ID) {
        int a = 0;
        try {
            open();
            a = db.delete("seizure_details", "id=" + ID, null);
        } catch (Exception e) {
            e.toString();
        }
        close();
        return a > 0;

    }



    /**
     * -----------------------------------------------------------------------------
     * Class Name: getSeizureDetailsInDateRange
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose:get Seizure Details InDateRange
     * -----------------------------------------------------------------------------
     */
    @SuppressLint("SimpleDateFormat")
    public List<SeizureDetails> getSeizureDetailsInDateRange(Date fromDate, Date toDate) {
        List<SeizureDetails> seizures = null;
     /*   try {*/
        SimpleDateFormat dateFormat = new SimpleDateFormat(ISOShortDatePattern);

        String sqlQuery = "select id,stypeId, durationId,video_link, sdesc, sdate, stime,"
                + "emergency_medication_given,ambulance_called from seizure_details where sdate between '"
                + dateFormat.format(fromDate) + "' and '" + dateFormat.format(toDate) + "' and userName = '"
                + LoginActivity.userName + "';";

        seizures = new ArrayList<SeizureDetails>();
        SeizureDetails seizureDetails;
        open();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                seizureDetails = new SeizureDetails();
                seizureDetails.setId(cursor.getInt(0));
                seizureDetails.setSeizureType(getSeizureType(cursor.getString(1)));
                seizureDetails.setSeizureDuration(getSeizureDuration(cursor.getString(2)));

                // Get the recorded video
                RecordedVideo recordedVideo = new RecordedVideo();
                recordedVideo.setPath(cursor.getString(3));
                seizureDetails.setRecordedVideo(recordedVideo);

                seizureDetails.setDesc(cursor.getString(4));
                seizureDetails.setSqlDateTime(cursor.getString(5));
                seizureDetails.setTime(cursor.getString(6));

                seizureDetails.setSqlEmegencyMedicationGiven(cursor.getInt(7));
                seizureDetails.setSqlAmbulanceCalled(cursor.getInt(8));
                seizureDetails.setSeizureTriggers(getSeizureTriggersBySeizureId(seizureDetails.getId()));
                seizureDetails.setSeizurePreFeatures(getSeizurePreFeaturesBySeizureId(seizureDetails.getId()));
                seizureDetails.setSeizureFeatures(getSeizureFeaturesBySeizureId(seizureDetails.getId()));
                seizureDetails.setSeizurePostFeatures(getSeizurePostFeaturesBySeizureId(seizureDetails.getId()));
                seizures.add(seizureDetails);
            }
            while (cursor.moveToNext());

        }
        close();
        return seizures;
       /* } catch (Exception e) {
            e.printStackTrace();
        }
       return  null;*/
    }

    @SuppressLint("SimpleDateFormat")
    public HashMap<String, List<String>> getSeizuresPerMonth(int month, int year) {
        HashMap<String, List<String>> items = null;
        try {
            items = new HashMap<String, List<String>>();
            List<String> seizuresWithVideo = new ArrayList<String>();
            List<String> seizuresWithoutVideo = new ArrayList<String>();

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

            SimpleDateFormat dateFormat = new SimpleDateFormat(ISOShortDatePattern);
            String firstDateString = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

            String lastDateString = dateFormat.format(calendar.getTime());
            String sqlQuery = "select sdate,video_link,count(*) from seizure_details where sdate BETWEEN '"
                    + firstDateString + "' AND '" + lastDateString + "' and userName ='" + LoginActivity.userName
                    + "' group by sdate order by sdate;";

            open();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String video_link = cursor.getString(1);
                    if (video_link != null && video_link.length() != 0)
                        seizuresWithVideo.add(cursor.getString(0));
                    else
                        seizuresWithoutVideo.add(cursor.getString(0));
                }
                while (cursor.moveToNext());
            }

            items.put("withVideo", seizuresWithVideo);
            items.put("withoutVideo", seizuresWithoutVideo);
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    @SuppressLint("SimpleDateFormat")
    public List<SeizureDetails> getSeizuresPerDate(Date date) {
        List<SeizureDetails> seizures = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(ISOShortDatePattern);

            String sqlQuery = "select id,stypeId, durationId,video_link, sdesc, sdate, stime, "
                    + "emergency_medication_given,ambulance_called from seizure_details where date(sdate) = date('"
                    + dateFormat.format(date) + "') and userName = '" + LoginActivity.userName + "';";

            seizures = new ArrayList<SeizureDetails>();
            SeizureDetails seizureDetails;
            open();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    seizureDetails = new SeizureDetails();
                    seizureDetails.setId(cursor.getInt(0));
                    seizureDetails.setSeizureType(getSeizureType(cursor.getString(1)));
                    seizureDetails.setSeizureDuration(getSeizureDuration(cursor.getString(2)));

                    // Get the recorded video
                    RecordedVideo recordedVideo = new RecordedVideo();
                    recordedVideo.setPath(cursor.getString(3));
                    seizureDetails.setRecordedVideo(recordedVideo);

                    seizureDetails.setDesc(cursor.getString(4));
                    seizureDetails.setSqlDateTime(cursor.getString(5));
                    seizureDetails.setTime(cursor.getString(6));

                    seizureDetails.setSqlEmegencyMedicationGiven(cursor.getInt(7));
                    seizureDetails.setSqlAmbulanceCalled(cursor.getInt(8));

                    seizureDetails.setSeizureTriggers(getSeizureTriggersBySeizureId(seizureDetails.getId()));
                    seizureDetails.setSeizurePreFeatures(getSeizurePreFeaturesBySeizureId(seizureDetails.getId()));
                    seizureDetails.setSeizureFeatures(getSeizureFeaturesBySeizureId(seizureDetails.getId()));

                    seizureDetails.setSeizurePostFeatures(getSeizurePostFeaturesBySeizureId(seizureDetails.getId()));

                    seizures.add(seizureDetails);
                }
                while (cursor.moveToNext());
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seizures;
    }


    public List<SeizureDetails> getSeizuresPerDateandTime(Date date, String Time) {
        List<SeizureDetails> seizures = null;
      /*  try {*/
        SimpleDateFormat dateFormat = new SimpleDateFormat(ISOShortDatePattern);

        String sqlQuery = "select id,stypeId, durationId,video_link, sdesc, sdate, stime, "
                + "emergency_medication_given,ambulance_called from seizure_details where date(sdate) = date('"
                + dateFormat.format(date) + "') and userName = '" + LoginActivity.userName + "' and stime = '" + Time + "'";

        seizures = new ArrayList<SeizureDetails>();
        SeizureDetails seizureDetails;
        open();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                seizureDetails = new SeizureDetails();
                seizureDetails.setId(cursor.getInt(0));
                seizureDetails.setSeizureType(getSeizureType(cursor.getString(1)));
                seizureDetails.setSeizureDuration(getSeizureDuration(cursor.getString(2)));

                // Get the recorded video
                RecordedVideo recordedVideo = new RecordedVideo();
                recordedVideo.setPath(cursor.getString(3));
                seizureDetails.setRecordedVideo(recordedVideo);

                seizureDetails.setDesc(cursor.getString(4));
                seizureDetails.setSqlDateTime(cursor.getString(5));
                seizureDetails.setTime(cursor.getString(6));

                seizureDetails.setSqlEmegencyMedicationGiven(cursor.getInt(7));
                seizureDetails.setSqlAmbulanceCalled(cursor.getInt(8));

                seizureDetails.setSeizureTriggers(getSeizureTriggersBySeizureId(seizureDetails.getId()));
                seizureDetails.setSeizurePreFeatures(getSeizurePreFeaturesBySeizureId(seizureDetails.getId()));
                seizureDetails.setSeizureFeatures(getSeizureFeaturesBySeizureId(seizureDetails.getId()));

                seizureDetails.setSeizurePostFeatures(getSeizurePostFeaturesBySeizureId(seizureDetails.getId()));

                seizures.add(seizureDetails);
            }
            while (cursor.moveToNext());

        }
        close();
        return seizures;
       /* } catch (Exception e) {
            e.printStackTrace();
        }
    */
    }


    public List<SeizureDetails> getSeizures() {

        List<SeizureDetails> seizures = null;
        try {
            String sqlQuery = "select id,stypeId, durationId,video_link, sdesc, sdate, stime, "
                    + "emergency_medication_given,ambulance_called from seizure_details where userName = '" + LoginActivity.userName + "';";

            seizures = new ArrayList<SeizureDetails>();
            SeizureDetails seizureDetails;
            open();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    seizureDetails = new SeizureDetails();
                    seizureDetails.setId(cursor.getInt(0));
                    seizureDetails.setSeizureType(getSeizureType(cursor.getString(1)));
                    seizureDetails.setSeizureDuration(getSeizureDuration(cursor.getString(2)));

                    // Get the recorded video
                    RecordedVideo recordedVideo = new RecordedVideo();
                    recordedVideo.setPath(cursor.getString(3));
                    seizureDetails.setRecordedVideo(recordedVideo);

                    seizureDetails.setDesc(cursor.getString(4));
                    seizureDetails.setSqlDateTime(cursor.getString(5));
                    seizureDetails.setTime(cursor.getString(6));

                    seizureDetails.setSqlEmegencyMedicationGiven(cursor.getInt(7));
                    seizureDetails.setSqlAmbulanceCalled(cursor.getInt(8));

                    seizureDetails.setSeizureTriggers(getSeizureTriggersBySeizureId(seizureDetails.getId()));
                    seizureDetails.setSeizurePreFeatures(getSeizurePreFeaturesBySeizureId(seizureDetails.getId()));
                    seizureDetails.setSeizureFeatures(getSeizureFeaturesBySeizureId(seizureDetails.getId()));

                    seizureDetails.setSeizurePostFeatures(getSeizurePostFeaturesBySeizureId(seizureDetails.getId()));

                    seizures.add(seizureDetails);
                }
                while (cursor.moveToNext());
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seizures;
    }


    private SeizureDuration getSeizureDuration(String durationID) {
        SeizureDuration mDuration = null;
        try {
            mDuration = new SeizureDuration();
            String sqlQuery = "select id, duration, desc from duration where id=" + durationID;
            open();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                mDuration.setId(cursor.getLong(0));
                mDuration.setName(cursor.getString(1));
                mDuration.setDescription(cursor.getString(2));
            } else {
                close();
                return null;
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDuration;

    }

    private SeizureType getSeizureType(String typeId) {
        SeizureType mSeizureType = null;
        try {
            mSeizureType = new SeizureType();
            String sqlQuery = "select id,type,desc,userDefined from seizure_type where id=" + typeId;
            open();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                mSeizureType.setId(cursor.getLong(0));
                mSeizureType.setName(cursor.getString(1));
                mSeizureType.setDescription(cursor.getString(2));
                mSeizureType.setUserDefined(cursor.getInt(3));

            } else {
                close();
                return null;
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mSeizureType;
    }

    public boolean createSeizureType(String type, String desc) throws Exception {
        if (type.length() == 0)
            throw new Exception("Null Values");
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", type);
        contentValues.put("desc", desc);
        contentValues.put("userDefined", 1);
        contentValues.put("userName", LoginActivity.userName);
        long rowId = db.insert("seizure_type", null, contentValues);
        close();
        return rowId != -1;
    }

    public boolean createTrigger(String trigger, String desc) throws Exception {
        if (trigger.length() == 0)
            throw new Exception("Null Values");
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("trigger", trigger);
        contentValues.put("desc", desc);
        contentValues.put("userDefined", 1);
        contentValues.put("userName", LoginActivity.userName);
        long rowId = db.insert("trigger", null, contentValues);
        close();
        return rowId != -1;
    }

    public boolean createFeature(String feature, String desc) throws Exception {
        if (feature.length() == 0)
            throw new Exception("Null Values");
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("feature", feature);
        contentValues.put("desc", desc);
        contentValues.put("userDefined", 1);
        contentValues.put("userName", LoginActivity.userName);
        long rowId = db.insert("feature", null, contentValues);
        close();
        return rowId != -1;
    }

    public boolean createPreFeature(String preFeature, String desc) throws Exception {
        if (preFeature.length() == 0)
            throw new Exception("Null Values");
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pre_feature", preFeature);
        contentValues.put("desc", desc);
        contentValues.put("userDefined", 1);
        contentValues.put("userName", LoginActivity.userName);
        long rowId = db.insert("pre_feature", null, contentValues);
        close();
        return rowId != -1;
    }

    public boolean createPostFeature(String postFeature, String desc) throws Exception {
        if (postFeature.length() == 0)
            throw new Exception("Null Values");
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("post_feature", postFeature);
        contentValues.put("desc", desc);
        contentValues.put("userDefined", 1);
        contentValues.put("userName", LoginActivity.userName);
        long rowId = db.insert("post_feature", null, contentValues);
        close();
        return rowId != -1;
    }

    public boolean createSMS(String phone, String message) throws Exception {
        if (phone.length() == 0 || message.length() == 0)
            throw new Exception("Null Values");
        String sqlQuery = "select id from sms;";
        open();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", phone);
        contentValues.put("message", message);
        contentValues.put("userName", LoginActivity.userName);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(0);

            long rowId = db.update("sms", contentValues, "id = ?", new String[]{id});
            close();
            return rowId == 1;
        } else {
            long rowId = db.insert("sms", null, contentValues);
            close();
            return rowId != -1;
        }

    }

    public boolean saveSeizureDetails(SeizureDetails seizureDetails) throws Exception {

        boolean transactionSuccessful = false;
        try {
            ContentValues contentValues = new ContentValues();

            // Check that both the
            if (seizureDetails.getSeizureType().getId() < 1 || seizureDetails.getSeizureDuration().getId() < 1) {
                throw new Exception("Information Not Valid");
            }
            contentValues.put("stypeId", seizureDetails.getSeizureType().getId());
            contentValues.put("durationId", seizureDetails.getSeizureDuration().getId());
            contentValues.put("video_link", seizureDetails.getRecordedVideo() != null ? seizureDetails.getRecordedVideo()
                    .getPath() : "");
            contentValues.put("sdesc", seizureDetails.getDesc());
            contentValues.put("sdate", seizureDetails.getSQLDateTime());
            contentValues.put("stime", seizureDetails.getTime());
            contentValues.put("emergency_medication_given", seizureDetails.getSqlEmegencyMedicationGiven());
            contentValues.put("ambulance_called", seizureDetails.getSqlAmbulanceCalled());
            contentValues.put("userName", LoginActivity.userName);

            transactionSuccessful = true;
            open();
            db.beginTransaction();
            long rowId = 0;
            if (seizureDetails.getId() != 0) {

                contentValues.put("id", seizureDetails.getId());
                String query = "select * from seizure_details where id = " + seizureDetails.getId();

                Cursor cursor = db.rawQuery(query, null);
                if (cursor.getCount() > 0) {
                    db.update("seizure_details", contentValues, "id = ?", new String[]{seizureDetails.getId() + ""});
                    rowId = seizureDetails.getId();
                }
            } else {
                rowId = db.insert("seizure_details", null, contentValues);
            }

            if (rowId > 0) {
                seizureDetails.setId(rowId);
                boolean triggersInserted = createSeizureDetailTrigger(seizureDetails);
                boolean preFeaturesInserted = createSeizureDetailPreFeature(seizureDetails);
                boolean featuresInserted = createSeizureDetailFeature(seizureDetails);
                boolean postFeaturesInserted = createSeizurePostFeature(seizureDetails);

                if (triggersInserted && preFeaturesInserted && featuresInserted && postFeaturesInserted) {
                    transactionSuccessful = true;
                    db.setTransactionSuccessful();
                } else {
                    transactionSuccessful = false;

                }
            } else {
                transactionSuccessful = false;
            }
            // If the Transaction was commited it will be saved to the DB otherwise
            // it will be rolledback
            db.endTransaction();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactionSuccessful;
    }

    private boolean createSeizureDetailTrigger(SeizureDetails seizureDetails) {
        boolean allEntered = true;
        for (SeizureTrigger seizureTrigger : seizureDetails.getSeizureTriggers()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("seizureDetailId", seizureDetails.getId());
            contentValues.put("triggerId", seizureTrigger.getId());

            long rowId = db.insert("seizure_detail_trigger", null, contentValues);
            if (rowId == -1) {
                allEntered = false;
            }
        }
        return allEntered;
    }

    private boolean createSeizureDetailPreFeature(SeizureDetails seizureDetails) {
        boolean allEntered = true;
        for (SeizurePreFeature seizurePreFeature : seizureDetails.getSeizurePreFeatures()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("seizureDetailId", seizureDetails.getId());
            contentValues.put("preFeatureId", seizurePreFeature.getId());

            long rowId = db.insert("seizure_detail_pre_feature", null, contentValues);
            if (rowId == -1) {
                allEntered = false;
            }
        }
        return allEntered;
    }

    private boolean createSeizureDetailFeature(SeizureDetails seizureDetails) {
        boolean allEntered = true;
        for (SeizureFeature seizureFeature : seizureDetails.getSeizureFeatures()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("seizure_detail_id", seizureDetails.getId());
            contentValues.put("feature_id", seizureFeature.getId());

            long rowId = db.insert("seizure_detail_feature", null, contentValues);
            if (rowId == -1) {
                allEntered = false;
            }
        }
        return allEntered;
    }


    public boolean checkuserAlter(String quest, String answer) {
        boolean done = false;
        try {
            open();
            //  long rowId =-1;
            done = true;
            ContentValues contentValues = new ContentValues();
            contentValues.put("question2", quest);
            contentValues.put("answer2", answer);

            // long rowId= db.insert("user",contentValues,"userName = ?",LoginActivity.userName.toString());
            long rowId = db.update("user", contentValues, "userName =?", new String[]{LoginActivity.userName});

            if (rowId == -1) {
                done = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return done;
    }


    private boolean createSeizurePostFeature(SeizureDetails seizureDetails) {
        boolean allEntered = true;
        try {
            for (SeizurePostFeature seizurePostFeature : seizureDetails.getSeizurePostFeatures()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("seizureDetailId", seizureDetails.getId());
                contentValues.put("postFeatureId", seizurePostFeature.getId());

                long rowId = db.insert("seizure_detail_post_feature", null, contentValues);
                if (rowId == -1) {
                    allEntered = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allEntered;
    }


    public boolean isFirstLogin(String userName) {
        String sqlQuery = "select id from user where userName = '" + userName + "' and firstLogin = 0;";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        boolean isFirst = (cursor.getCount() > 0);
        return isFirst;
    }

    public boolean updateFirstLogin(String userName) {
        long rowId = -1;
        ContentValues contentValues = new ContentValues();
        contentValues.put("firstLogin", 1);
        open();
        rowId = db.update("user", contentValues, "userName = ?", new String[]{userName});
        close();
        return rowId != -1;
    }

    public boolean saveuserData(String userName, String password) {
        String sqlQuery = "update user set lastLogin = 0";
        open();
        //TODO cursor redundant
        Cursor cursor = db.rawQuery(sqlQuery, null);
        cursor.moveToFirst();
        cursor.close();
        long rowId = -1;
        sqlQuery = "select id from user where userName='" + userName + "' and password = '" + password + "';";
        cursor = db.rawQuery(sqlQuery, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("userName", userName);
        contentValues.put("password", password);
        contentValues.put("lastLogin", 1);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(0);
            rowId = db.update("user", contentValues, "id = ?", new String[]{id});
        } else {
            rowId = db.insert("user", null, contentValues);
        }
        return rowId != -1;
    }

    public void handleLogin(String userName, String password, IRESTWebServiceCaller caller) {
        String sqlQuery = "select id from user where userName = '" + userName + "' and password = '" + password + "';";
        open();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        int count = cursor.getCount();
        close();
        if (count > 0) {
            caller.onResponseReceived("{status:\"true\"}");
        } else {
            Toast.makeText(context, context.getString(R.string.invalid_username_password), Toast.LENGTH_SHORT).show();
        }
    }

    public String getLastLoginUser() {
        String user = null;
        try {
            String sqlQuery = "select userName from user where lastLogin = 1;";
            user = null;
            open();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                user = cursor.getString(0);
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }


    public long SaveMedication(NewMedication medication) {
        try {
            String sqlQuery = "select _id from new_medication where userName = '" + LoginActivity.userName + "' and medication_name = '" + medication.getMedication_name() + "';";
            open();
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                close();
                return 0;
            } else {

                ContentValues contentValues = new ContentValues();
                contentValues.put("medication_name ", medication.getMedication_name());
                contentValues.put("username ", LoginActivity.userName);
                contentValues.put("medication_dosag ", medication.getDosage());
                contentValues.put("medication_unit ", medication.getUnit());
                contentValues.put("medication_desc ", medication.getMedication_description());
                contentValues.put("start_data ", medication.getStart_date());

                if (medication.getPrescriptionRenewalDate() == null) {
                    contentValues.put("PrescriptionrenewalDate ", "");
                } else {
                    contentValues.put("PrescriptionrenewalDate ", medication.getPrescriptionRenewalDate());
                }
                if (medication.getMedStartDate() == null) {
                    contentValues.put("medStartDate ", "");
                } else {
                    contentValues.put("medStartDate ", medication.getMedStartDate().toString());
                }
                if (medication.getTitrationStartDate() == null) {
                    contentValues.put("titrationStartdate ", "");
                } else {
                    contentValues.put("titrationStartdate ", medication.getTitrationStartDate().toString());
                }
                contentValues.put("titrationNumWeeks", medication.getTitrationNumWeeks());
                contentValues.put("titrationOn", medication.isTitrationOn());
                contentValues.put("deleteMed", medication.isDelete());
                //contentValues.put("titrationOn",medication.get);

                long rowId = db.insert("new_medication", null, contentValues);
                close();
                return rowId;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    public long SaveAppointment(Appointment appointmentObj) {
        long rowId = 0;
        try {
            open();
            ContentValues values = new ContentValues();
            values.put("GpName", appointmentObj.getGpName());
            values.put("userName", LoginActivity.userName);
            values.put("AppointmentNotes", appointmentObj.getAppointmentNotes());
            values.put("UserAddress", appointmentObj.getUserAddress());
            values.put("latitude", appointmentObj.getLatitude());
            values.put("longitude", appointmentObj.getLongitude());
            values.put("Date", appointmentObj.getAppointmentDate());
            values.put("Time", appointmentObj.getAppointmentTime());
            values.put("deletedFlag", appointmentObj.isDelete());
            values.put("DateTime", appointmentObj.getAppointmentDateTime());
            rowId = db.insert("Appointment", null, values);
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }


    public long SavePrescriptionReminders(PrescriptionReminders reminder) {
        long rowId = 0;
        try {
            open();
            ContentValues values = new ContentValues();
            values.put("medication_id", reminder.getMedicationId());
            values.put("prescriptionReminderDate", reminder.getPrescriptionReminderDate());
            values.put("Alertbefore", reminder.getAlertbefore());
            values.put("userName", LoginActivity.userName);
            rowId = db.insert("Prescription", null, values);
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public long SaveAppointmentReminders(int AppointmentID, String ReminderDateTime, String RemindBefore, String reminderDate, String reminderTime) {
        long rowId = 0;
        try {
            open();
            ContentValues values = new ContentValues();
            values.put("appointment_id", AppointmentID);
            values.put("DateTime", ReminderDateTime);
            values.put("userName", LoginActivity.userName);
            values.put("ReminderAlert", RemindBefore);
            values.put("Date", reminderDate);
            values.put("Time", reminderTime);
            rowId = db.insert("AppointmentReminder", null, values);
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }


    public long UpdateAppointment(Appointment appointmentObj) {
        long rowId = 0;
        try {
            open();
            ContentValues values = new ContentValues();
            values.put("GpName", appointmentObj.getGpName());
            values.put("userName", LoginActivity.userName);
            values.put("AppointmentNotes", appointmentObj.getAppointmentNotes());
            values.put("UserAddress", appointmentObj.getUserAddress());
            values.put("latitude", appointmentObj.getLatitude());
            values.put("longitude", appointmentObj.getLongitude());
            values.put("Date", appointmentObj.getAppointmentDate());
            values.put("Time", appointmentObj.getAppointmentTime());
            values.put("deletedFlag", appointmentObj.isDelete());
            values.put("DateTime", appointmentObj.getAppointmentDateTime());
            rowId = db.update("Appointment", values, "_id = ?", new String[]{String.valueOf(appointmentObj.getID())});

            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public long SaveNewContact(NewContactEntity newContactEntity) {
        long rowId = 0;
        try {
            open();
            ContentValues values = new ContentValues();
            values.put("contactName", newContactEntity.getContactName());
            values.put("userName", LoginActivity.userName);
            values.put("deletedFlag", newContactEntity.isDeleted());
            values.put("relationship", newContactEntity.getRelationship());
            values.put("e_mail", newContactEntity.getE_mail());
            values.put("contactNumber", newContactEntity.getContactNumber());
            rowId = db.insert("contacts", null, values);
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    /*******************************************************************/
    public long SaveEmergencyMedication(EmergencyMedicationEntity emergencyMedicationEntity) {
        long rowId = 0;
        try {
            open();
            ContentValues values = new ContentValues();
            values.put("medicationName", emergencyMedicationEntity.getEmergencyMedicationName());
            values.put("userName", LoginActivity.userName);
            values.put("deletedFlag", emergencyMedicationEntity.isDeleted());
            values.put("dosage", emergencyMedicationEntity.getDosage());
            values.put("unit", emergencyMedicationEntity.getUnit());
            values.put("DateTime", emergencyMedicationEntity.getExpiryDate());
            values.put("reminderDaysBefor", emergencyMedicationEntity.getReminderDayeBefore());
            rowId = db.insert("emergencyMedication", null, values);
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public long SaveEmergenctMedicationReminder(int emergencyMedicationID, String ReminderDateTime, int reminderDayesBefore) {
        long rowId = 0;
        try {
            open();
            ContentValues values = new ContentValues();
            values.put("emergencyMedication_id", emergencyMedicationID);
            values.put("userName", LoginActivity.userName);
            values.put("DateTime", ReminderDateTime);
            values.put("reminderDaysBefor", reminderDayesBefore);
            rowId = db.insert("EmergencyReminder", null, values);
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public long UpdateEmergencyMedication(EmergencyMedicationEntity emergencyMedicationEntity) {
        long rowid = 0;
        try {
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("medicationName", emergencyMedicationEntity.getEmergencyMedicationName());
            contentValues.put("userName", LoginActivity.userName);
            contentValues.put("deletedFlag", emergencyMedicationEntity.isDeleted());
            contentValues.put("dosage", emergencyMedicationEntity.getDosage());
            contentValues.put("unit", emergencyMedicationEntity.getUnit());
            contentValues.put("DateTime", emergencyMedicationEntity.getExpiryDate());

            rowid = db.update("emergencyMedication", contentValues, "_id = ?", new String[]{String.valueOf(emergencyMedicationEntity.getId())});
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowid;
    }

    public long UpdateContact(NewContactEntity newContactEntity) {
        long rowid = 0;
        try {
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("contactName", newContactEntity.getContactName());
            contentValues.put("userName", LoginActivity.userName);
            contentValues.put("deletedFlag", newContactEntity.isDeleted());
            contentValues.put("relationship", newContactEntity.getRelationship());
            contentValues.put("e_mail", newContactEntity.getE_mail());
            contentValues.put("contactNumber", newContactEntity.getContactNumber());

            rowid = db.update("contacts", contentValues, "_id = ?", new String[]{String.valueOf(newContactEntity.getId())});
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowid;
    }

    public long UpdateMedication(NewMedication medication) {
        long rowid = 0;
        try {
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("medication_name ", medication.getMedication_name());
            contentValues.put("username ", LoginActivity.userName);
            contentValues.put("medication_dosag ", medication.getDosage());
            contentValues.put("medication_unit ", medication.getUnit());
            contentValues.put("medication_desc ", medication.getMedication_description());
            contentValues.put("start_data ", medication.getStart_date());
            if (medication.getPrescriptionRenewalDate() == null) {
                contentValues.put("PrescriptionrenewalDate ", "");
            } else {
                contentValues.put("PrescriptionrenewalDate ", medication.getPrescriptionRenewalDate());
            }

            if (medication.getMedStartDate() == null) {
                contentValues.put("medStartDate ", "");
            } else {
                contentValues.put("medStartDate ", medication.getMedStartDate().toString());
            }
            contentValues.put("titrationStartdate", medication.getTitrationStartDate().toString());
            contentValues.put("titrationNumWeeks", medication.getTitrationNumWeeks());
            contentValues.put("titrationOn", medication.isTitrationOn());
            contentValues.put("deleteMed", medication.isDelete());
            rowid = db.update("new_medication", contentValues, "_id = ?", new String[]{String.valueOf(medication.getId())});
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowid;
    }

    public long UpdateMedicationToDeleted(NewMedication medication) {
        long rowid = 0;
        try {
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("deleteMed", medication.isDelete());
            rowid = db.update("new_medication", contentValues, "_id = ?", new String[]{String.valueOf(medication.getId())});
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowid;
    }

    public long UpdateAppointmentToDeleted(Appointment appointmentObj) {
        long rowid = 0;
        try {
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("deletedFlag", appointmentObj.isDelete());
            rowid = db.update("Appointment", contentValues, "_id = ?", new String[]{String.valueOf(appointmentObj.getID())});
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowid;
    }

    public long UpdateEmergencyMedicationToDeleted(EmergencyMedicationEntity medication) {
        long rowid = 0;
        try {
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("deletedFlag", medication.isDeleted());
            rowid = db.update("emergencyMedication", contentValues, "_id = ?", new String[]{String.valueOf(medication.getId())});
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowid;
    }


    /**
     * -----------------------------------------------------------------------------
     * Class Name: AddMedicationAlarmReminder
     * Created By:Nikunj & Shruti
     * -----------------------------------------------------------------------------
     */
    public long AddMedicationAlarmReminder(int medicationId, int reminderId, String MedicationAlarmDateTime, int medicationHandlingStatus, String medicationHandlingReason,String ReminderDateTime) {
        long rowid = 0;
        try {
            open();
            ContentValues value = new ContentValues();
            value.put("medication_id ", medicationId);
            value.put("Reminder_id ", reminderId);
            value.put("medicationHandlingStatus ", medicationHandlingStatus);
            value.put("MedicationAlarmDateTime ", MedicationAlarmDateTime);
            value.put("userName ", LoginActivity.userName);
            value.put("ReminderDateTime",ReminderDateTime);

            if (medicationHandlingReason.length() != 0) {
                value.put("medicationHandlingReason ", medicationHandlingReason);
            }
            rowid = db.insert("MedicationAlarmReminders", null, value);
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowid;
    }



    public long UpdateContactToDeleted(NewContactEntity contact) {
        long rowid = 0;
        try {
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("deletedFlag", contact.isDeleted());
            rowid = db.update("contacts", contentValues, "_id = ?", new String[]{String.valueOf(contact.getId())});
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowid;
    }


    public ArrayList<EmergencyMedicationEntity> getEmergencyMedications() {
        String sqlQuary = "select emergencyMedication._id,emergencyMedication.medicationName,emergencyMedication.DateTime,emergencyMedication.reminderDaysBefor,emergencyMedication.unit, emergency_medications_images.Image_link from emergencyMedication join emergency_medications_images on emergencyMedication._id=emergency_medications_images.emergency_medication_id where emergencyMedication.userName= '" + LoginActivity.userName + "' and emergencyMedication.deletedFlag == " + 0 + ";";
        open();
        Cursor cursor = db.rawQuery(sqlQuary, null);
        EmergencyMedicationEntity emMedicationss = null;
        int EmmedicationId = -1;
        ArrayList<EmergencyMedicationEntity> Medications = new ArrayList<EmergencyMedicationEntity>();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                if (EmmedicationId == -1 || EmmedicationId != cursor.getInt(0)) {
                    if (emMedicationss != null) {
                        Medications.add(emMedicationss);
                    }
                    emMedicationss = new EmergencyMedicationEntity();
                    emMedicationss.setId(cursor.getInt(0));
                    emMedicationss.setEmergencyMedicationName(cursor.getString(1));
                    emMedicationss.setExpiryDate(cursor.getString(2));
                    emMedicationss.setReminderDayeBefore(cursor.getInt(3));
                    emMedicationss.setUnit(cursor.getString(4));
                    EmmedicationId = cursor.getInt(0);
                }
                emMedicationss.getMedicationImages().add(cursor.getString(5));
            } while (cursor.moveToNext());

            Medications.add(emMedicationss);
            cursor.close();
            close();
        }
        return Medications;
    }


    public ArrayList<NewMedication> getMedicationss() {
        String sqlQuary = "select new_medication._id,new_medication.medication_name,new_medication.start_data,new_medication.titrationOn,new_medication.titrationStartdate,new_medication.titrationNumWeeks,new_medication.medStartDate, medications_images.Image_link from new_medication join medications_images on new_medication._id=medications_images.medication_id where new_medication.deleteMed == " + 0 + ";";
        open();
        Cursor cursor = db.rawQuery(sqlQuary, null);
        NewMedication newMedication = null;
        ArrayList<String> images = new ArrayList<String>();
        int medicationId = -1;
        ArrayList<NewMedication> medications = new ArrayList<NewMedication>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                if (medicationId == -1 || medicationId != cursor.getInt(0)) {
                    if (newMedication != null) {
                        medications.add(newMedication);
                    }
                    newMedication = new NewMedication();
                    newMedication.setId(cursor.getInt(0));
                    newMedication.setMedication_name(cursor.getString(1));
                    newMedication.setStart_date(cursor.getString(2));
                    newMedication.setTitrationOn(cursor.getInt(3) != 0);
                    newMedication.setTitrationStartDate(cursor.getString(4));
                    newMedication.setTitrationNumWeeks(cursor.getInt(5));
                    newMedication.setMedStartDate(cursor.getString(6));
                    medicationId = cursor.getInt(0);
                }
                newMedication.getMedicationImages().add(cursor.getString(7));
            } while (cursor.moveToNext());
            medications.add(newMedication);
            cursor.close();
            close();
        }
        return medications;
    }


    public ArrayList<NewMedication> getMedications() {
        ArrayList<NewMedication> medications = null;
        String sqlQuary = "select new_medication._id,new_medication.medication_name,new_medication.start_data,new_medication.titrationOn,new_medication.titrationStartdate,new_medication.titrationNumWeeks,new_medication.medStartDate, medications_images.Image_link from new_medication join medications_images on new_medication._id=medications_images.medication_id where new_medication.username= '" + LoginActivity.userName + "' and new_medication.deleteMed == " + 0 + ";";
        open();
        Cursor cursor = db.rawQuery(sqlQuary, null);
        medications = new ArrayList<NewMedication>();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                NewMedication newMedication = new NewMedication();
                newMedication.setId(cursor.getInt(0));
                newMedication.setMedication_name(cursor.getString(1));
                newMedication.setStart_date(cursor.getString(2));
                newMedication.setTitrationStartDate(cursor.getString(4));
                newMedication.setTitrationNumWeeks(cursor.getInt(5));
                newMedication.setMedStartDate(cursor.getString(6));
                newMedication.setMedicationReminders(getMedicationRemindersByMedicationId(cursor.getInt(0)));
                newMedication.getMedicationImages().add(cursor.getString(7));

                medications.add(newMedication);
            } while (cursor.moveToNext());

            cursor.close();
            close();

        }
        return medications;
    }


    public ArrayList<MedicationReminders> getMedicationRemindersOnReboot() {

        ArrayList<MedicationReminders> medicationReminders = null;
        try {

            //String sqlQuary = "select MedicationReminders._id,MedicationReminders.medication_id,MedicationReminders.RminderDateTime,MedicationReminders.medicationHandlingReason,MedicationReminders.medicationHandlingStatus from MedicationReminders  where MedicationReminders.username= '" + ""+LoginActivity.userName +"" + "';";
            String sqlQuary = "select MedicationReminders._id,MedicationReminders.medication_id,MedicationReminders.RminderDateTime,MedicationReminders.MedicationAlarmDateTime,MedicationReminders.medicationHandlingReason,MedicationReminders.medicationHandlingStatus,MedicationReminders.userName,new_Medication.medication_name from MedicationReminders join new_Medication where MedicationReminders.medication_id == new_Medication._id and  MedicationReminders.reminderFlag == " + 0 + ";";

            open();

            Cursor cursor = db.rawQuery(sqlQuary, null);

            int cnt = cursor.getCount();

            medicationReminders = new ArrayList<>();

            if (cursor.moveToFirst()) {
                do {

                    MedicationReminders medicationReminder = new MedicationReminders();
                    medicationReminder.setID(cursor.getInt(0));
                    medicationReminder.setMedicationID(cursor.getInt(1));
                    medicationReminder.setMedReminderDate(cursor.getString(2));
                    medicationReminder.setMedicationHandlingReason(cursor.getString(4));
                    medicationReminder.setMedicationHandlingStatus(cursor.getInt(5));
                    medicationReminder.setUserName(cursor.getString(6));
                    medicationReminder.setMedicationName(cursor.getString(7));
                    medicationReminders.add(medicationReminder);
                } while (cursor.moveToNext());
            }
            cursor.close();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return medicationReminders;
    }


    public ArrayList<MedicationReminders> getMedicationRemindersByMedicationId(int medicationId) {
        ArrayList<MedicationReminders> medicationReminders = null;
        try {
            String sqlQuary = "select MedicationReminders._id,MedicationReminders.medication_id,MedicationReminders.RminderDateTime,MedicationReminders.medicationHandlingReason,MedicationReminders.medicationHandlingStatus from MedicationReminders  where MedicationReminders.username= '" + LoginActivity.userName + "' and MedicationReminders.medication_id == " + medicationId + ";";
            Log.e("DBAdapter", " Query" + sqlQuary);
            open();


            Cursor cursor = db.rawQuery(sqlQuary, null);

            int cnt = cursor.getCount();
            Log.e("DBAdapter", " Count" + cnt);

            medicationReminders = new ArrayList<>();

            if (cursor.moveToFirst()) {
                do {

                    MedicationReminders medicationReminder = new MedicationReminders();
                    medicationReminder.setID(cursor.getInt(0));
                    medicationReminder.setMedicationID(cursor.getInt(1));
                    medicationReminder.setMedReminderDate(cursor.getString(2));

                    medicationReminder.setMedicationHandlingReason(cursor.getString(3));
                    medicationReminder.setMedicationHandlingStatus(cursor.getInt(4));
//                    medicationReminder.setReminderFlag(cursor.getInt(5));

                    medicationReminders.add(medicationReminder);
                } while (cursor.moveToNext());
            }
            cursor.close();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return medicationReminders;
    }


    public ArrayList<NewContactEntity> getContactsList() {
        ArrayList<NewContactEntity> contactsList = null;
        try {
            String sqlQuary = "select contacts._id,contacts.contactName,contacts.relationship,contacts.e_mail,contacts.contactNumber from contacts where contacts.userName= '" + LoginActivity.userName + "' and contacts.deletedFlag == " + 0 + ";";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            contactsList = new ArrayList<NewContactEntity>();

            if (cursor.moveToFirst()) {
                do {

                    NewContactEntity newContactEntity = new NewContactEntity();
                    newContactEntity.setId(cursor.getInt(0));
                    newContactEntity.setContactName(cursor.getString(1));
                    newContactEntity.setRelationship(cursor.getString(2));
                    newContactEntity.setE_mail(cursor.getString(3));
                    newContactEntity.setContactNumber(cursor.getString(4));
                    contactsList.add(newContactEntity);
                } while (cursor.moveToNext());

                cursor.close();
                close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactsList;
    }

    /**************************************************************************************************/


    //get appointments ordered by datetime from the database
    public ArrayList<Appointment> getAppointments() {
        ArrayList<Appointment> appointments = null;
        try {
            String sqlQuary = "select Appointment._id,Appointment.GpName,Appointment.Date,Appointment.Time,Appointment.DateTime from Appointment where Appointment.userName= '" + LoginActivity.userName + "' ORDER BY datetime(DateTime) ASC";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            appointments = new ArrayList<Appointment>();

            if (cursor.moveToFirst()) {

                do {
                    //create new object from Appointment Model class to hold the new data
                    Appointment newappointment = new Appointment();

                    newappointment.setID(cursor.getInt(0));
                    newappointment.setGpName(cursor.getString(1));
                    newappointment.setAppointmentDate(cursor.getString(2));
                    newappointment.setAppointmentTime(cursor.getString(3));
                    newappointment.setAppointmentDateTime(cursor.getString(4));

                    appointments.add(newappointment);


                } while (cursor.moveToNext());
                cursor.close();
                close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    }


    public ArrayList<Appointment> getExistingAppointments() {
        ArrayList<Appointment> appointments = null;
        try {
            String sqlQuary = "select Appointment._id,Appointment.GpName,Appointment.Date,Appointment.Time,Appointment.DateTime from Appointment where  Appointment.deletedFlag == " + 0 + " and  Appointment.userName= '" + LoginActivity.userName + "' ORDER BY datetime(DateTime) ASC";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            appointments = new ArrayList<Appointment>();

            if (cursor.moveToFirst()) {

                do {

                    ///create new object from Appointment Model class to hold the new data
                    Appointment newappointment = new Appointment();

                    newappointment.setID(cursor.getInt(0));
                    newappointment.setGpName(cursor.getString(1));
                    newappointment.setAppointmentDate(cursor.getString(2));
                    newappointment.setAppointmentTime(cursor.getString(3));
                    newappointment.setAppointmentDateTime(cursor.getString(4));

                    ///add appointment object to the list of appointments
                    appointments.add(newappointment);

                } while (cursor.moveToNext());
                cursor.close();
                close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    }


    //get appointments ordered by datetime from the database
    public ArrayList<Appointment> getAppointmentsbyName() {
        ArrayList<Appointment> appointments = null;
        try {
            String sqlQuary = "select Appointment._id,Appointment.GpName,Appointment.Date,Appointment.Time from Appointment where Appointment.userName= '" + LoginActivity.userName + "' ORDER BY GpName ASC";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            appointments = new ArrayList<Appointment>();
            if (cursor.moveToFirst()) {

                do {
                    ///create new object from Appointment Model class to hold the new data
                    Appointment newappointment = new Appointment();

                    newappointment.setID(cursor.getInt(0));
                    newappointment.setGpName(cursor.getString(1));
                    newappointment.setAppointmentDate(cursor.getString(2));
                    newappointment.setAppointmentTime(cursor.getString(3));

                    ///add appointment object to the list of appointments
                    appointments.add(newappointment);

                } while (cursor.moveToNext());
                cursor.close();
                close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    }


    public ArrayList<Appointment> getAppointmentsReminders() {
        ArrayList<Appointment> appointments = null;
      /*  try {*/
        String sqlQuary = "select Appointment._id,Appointment.GpName,Appointment.UserAddress,Appointment.Date,Appointment.Time,Appointment.AppointmentNotes,AppointmentReminder.ReminderAlert,AppointmentReminder.DateTime from Appointment join AppointmentReminder on Appointment._id=AppointmentReminder.appointment_id where Appointment.userName= '" + LoginActivity.userName + "';";
        open();
        Cursor cursor = db.rawQuery(sqlQuary, null);
        appointments = new ArrayList<Appointment>();
        if (cursor.moveToFirst()) {

            do {

                Appointment appointment = new Appointment();
                appointment.setID(cursor.getInt(0));
                appointment.setGpName(cursor.getString(1));
                appointment.setUserAddress(cursor.getString(2));
                appointment.setAppointmentDate(cursor.getString(3));
                appointment.setAppointmentTime(cursor.getString(4));
                appointment.setAppointmentNotes(cursor.getString(5));


                // this should be for loop to get All reminders for Appointment
                if (cursor.getString(7) != null) {
                    appointment.getAppointmentReminders().add(convertToDateformat2(cursor.getString(7)));
                }

                appointments.add(appointment);
            } while (cursor.moveToNext());

            cursor.close();
            close();

        }
        return appointments;
      /*  } catch (Exception e) {
            e.printStackTrace();
        }*/

    }


    public ArrayList<NewMedication> getMedicationsReminders() {

        ArrayList<NewMedication> medications = null;
        try {
            String sqlQuary = "select new_medication._id,new_medication.medication_name,new_medication.start_data,new_medication.titrationOn,new_medication.titrationStartdate,new_medication.titrationNumWeeks,new_medication.medStartDate, MedicationReminders.RminderDateTime from new_medication join MedicationReminders on new_medication._id=MedicationReminders.medication_id where new_medication.username= '" + LoginActivity.userName + "';";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            NewMedication newMedication = null;
            int medicationId = -1;
            medications = new ArrayList<NewMedication>();
            if (cursor.moveToFirst()) {
                do {
                    if (medicationId == -1 || medicationId != cursor.getInt(0)) {
                        if (newMedication != null) {
                            medications.add(newMedication);
                        }
                        newMedication = new NewMedication();
                        newMedication.setId(cursor.getInt(0));
                        newMedication.setMedication_name(cursor.getString(1));
                        newMedication.setStart_date(cursor.getString(2));
                        newMedication.setTitrationOn(cursor.getInt(3) != 0);
                        newMedication.setTitrationStartDate(cursor.getString(4));
                        newMedication.setTitrationNumWeeks(cursor.getInt(5));

                        medicationId = cursor.getInt(0);
                        newMedication.setMedicationReminders(getMedicationRemindersByMedicationId(medicationId));
                    }
                } while (cursor.moveToNext());
                medications.add(newMedication);
                cursor.close();
                close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return medications;
    }


    public Date convertToDateTime(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimePattern);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }

    public Date convertAppointmentDateTimeToDateTime(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeFormats.deafultDateFormat);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }


    public Date convertToDateformat2(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }


    public Appointment getAppointmentById(int AppointmentId) {
        Appointment appointmentOBJ = null;
        try {
            appointmentOBJ = new Appointment();
            String sqlQuary = "select Appointment.GpName,Appointment.Date,Appointment.Time,Appointment.UserAddress,Appointment.latitude,Appointment.longitude,Appointment.AppointmentNotes from Appointment where Appointment._id=" + AppointmentId + ";";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    appointmentOBJ.setGpName(cursor.getString(0));
                    appointmentOBJ.setAppointmentDate(cursor.getString(1));
                    appointmentOBJ.setAppointmentTime(cursor.getString(2));
                    appointmentOBJ.setUserAddress(cursor.getString(3));
                    appointmentOBJ.setLatitude(cursor.getDouble(4));
                    appointmentOBJ.setLongitude(cursor.getDouble(5));
                    appointmentOBJ.setAppointmentNotes(cursor.getString(6));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointmentOBJ;
    }


    public ArrayList<MedicationReminders> getMedicationReminders(int MedicationID) {
        ArrayList<MedicationReminders> reminder = null;
        try {
            reminder = new ArrayList<MedicationReminders>();
            MedicationReminders reminderOBJ = new MedicationReminders();
            String sqlQuary = "select MedicationReminders._id,MedicationReminders.RminderDateTime,MedicationReminders.medication_id,MedicationReminders.userName from MedicationReminders  where MedicationReminders.userName= '" + LoginActivity.userName + "' and MedicationReminders.medication_id = " + MedicationID + ";";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    reminderOBJ.setID(cursor.getInt(0));
                    reminderOBJ.setMedReminderDate(cursor.getString(1));
                    reminderOBJ.setMedicationID(cursor.getInt(2));
                    reminderOBJ.setUserName(cursor.getString(3));
                    reminder.add(reminderOBJ);
                    //create new object from the MedicationReminders class
                    reminderOBJ = new MedicationReminders();
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reminder;
    }


    public MedicationReminders getMedicationRemindersById(int MedicationReminderId) {
        System.out.println("ii");
        MedicationReminders reminderOBJ = new MedicationReminders();
        try {


            String sqlQuary = "select MedicationReminders._id,MedicationReminders.RminderDateTime,MedicationReminders.medication_id from MedicationReminders  where  MedicationReminders._id = " + MedicationReminderId + ";";
            open();

            Cursor cursor = db.rawQuery(sqlQuary, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    reminderOBJ.setID(cursor.getInt(0));
                    reminderOBJ.setMedReminderDate(cursor.getString(1));
                    reminderOBJ.setMedicationID(cursor.getInt(2));
                   // reminderOBJ.setUserName(cursor.getString(3));

                    //create new object from the MedicationReminders class

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reminderOBJ;
    }


    public MedicationReminders getMedicationRemindersByDateTime(String ReminderDateTime) {
        MedicationReminders reminderOBJ = new MedicationReminders();
        try {


            String sqlQuary = "select MedicationReminders._id,MedicationReminders.RminderDateTime,MedicationReminders.medication_id,MedicationReminders.userName from MedicationReminders  where MedicationReminders.userName= '" + LoginActivity.userName + "' and MedicationReminders.RminderDateTime = '" + ReminderDateTime + "';";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    reminderOBJ.setID(cursor.getInt(0));
                    reminderOBJ.setMedReminderDate(cursor.getString(1));
                    reminderOBJ.setMedicationID(cursor.getInt(2));
                    reminderOBJ.setUserName(cursor.getString(3));

                    //create new object from the MedicationReminders class

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reminderOBJ;
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: getPrescriptionReminders
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: get Prescription Reminders
     * -----------------------------------------------------------------------------
     */

    public ArrayList<PrescriptionReminders> getPrescriptionReminders(int medication_Id) {
        ArrayList<PrescriptionReminders> reminder = null;
        try {
            reminder = new ArrayList<PrescriptionReminders>();
            PrescriptionReminders presc = new PrescriptionReminders();
            String sqlQuary = "select new_medication._id,new_medication.medication_name,Prescription._id,Prescription.prescriptionReminderDate,Prescription.Alertbefore from new_medication join Prescription on new_medication._id=Prescription.medication_id where new_medication._id=" + medication_Id + ";";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    presc.setMedicationId(cursor.getInt(0));
                    presc.setMedicationName(cursor.getString(1));
                    presc.setId(cursor.getInt(2));
                    presc.setPrescriptionReminderDate(cursor.getString(3));
                    presc.setAlertbefore(cursor.getString(4));
                    reminder.add(presc);
                    presc = new PrescriptionReminders();
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reminder;
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: getPrescriptionReminders_WithNoRepeat
     * Created By:Nikunj & Shruti
     * Purpose: get Prescription Reminders_WithNoRepeat
     * -----------------------------------------------------------------------------
     */
    public ArrayList<PrescriptionReminders> getPrescriptionReminders_WithNoRepeat(int medication_Id) {
        ArrayList<PrescriptionReminders> reminder = null;
        try {
            reminder = new ArrayList<PrescriptionReminders>();
            PrescriptionReminders presc = new PrescriptionReminders();
            String sqlQuary = "select new_medication._id,Prescription.reminderFlag,new_medication.medication_name,Prescription._id,Prescription.prescriptionReminderDate,Prescription.Alertbefore from new_medication join Prescription on new_medication._id=Prescription.medication_id where Prescription._id =" + medication_Id + " AND Prescription.reminderFlag == " + 0 + ";";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    presc.setMedicationId(cursor.getInt(0));
                    presc.setMedicationName(cursor.getString(2));
                    presc.setId(cursor.getInt(3));
                    presc.setPrescriptionReminderDate(cursor.getString(4));
                    presc.setAlertbefore(cursor.getString(5));
                    reminder.add(presc);
                    presc = new PrescriptionReminders();
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reminder;
    }







    public ArrayList<PrescriptionReminders> getPrescriptionRemindersBoot(int medication_Id) {
        ArrayList<PrescriptionReminders> reminder = null;
        try {
            reminder = new ArrayList<PrescriptionReminders>();
            PrescriptionReminders presc = new PrescriptionReminders();
            String sqlQuary = "select new_medication._id,new_medication.medication_name,Prescription._id,Prescription.prescriptionReminderDate,Prescription.Alertbefore from new_medication join Prescription on new_medication._id=Prescription.medication_id where new_medication._id=" + medication_Id + ";";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    presc.setMedicationName(cursor.getString(1));
                    presc.setId(cursor.getInt(2));
                    presc.setPrescriptionReminderDate(cursor.getString(3));
                    presc.setAlertbefore(cursor.getString(4));
                    reminder.add(presc);
                    presc = new PrescriptionReminders();
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reminder;
    }


    public ArrayList<PrescriptionReminders> getAllPrescriptionReminders() {
        ArrayList<PrescriptionReminders> reminder = null;
        try {
            reminder = new ArrayList<PrescriptionReminders>();
            PrescriptionReminders presc = new PrescriptionReminders();
            String sqlQuary = "select new_medication._id,new_medication.medication_name, new_medication.PrescriptionrenewalDate from new_medication where new_medication.username= '" + LoginActivity.userName + "';";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    presc.setMedicationId(cursor.getInt(0));
                    presc.setMedicationName(cursor.getString(1));
                    presc.setRenewalDate(cursor.getString(2));
                    reminder.add(presc);
                    presc = new PrescriptionReminders();
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reminder;
    }


    public MedicationReminders getEmergencyMedicationReminders(int emergency_MedicationID) {
        MedicationReminders reminderOBJ = new MedicationReminders();
        String sqlQuary = "select EmergencyReminder._id,EmergencyReminder.DateTime,EmergencyReminder.emergencyMedication_id,EmergencyReminder.userName from EmergencyReminder  where EmergencyReminder.userName= '" + LoginActivity.userName + "' and EmergencyReminder.emergencyMedication_id = " + emergency_MedicationID + ";";
        open();
        Cursor cursor = db.rawQuery(sqlQuary, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {

                reminderOBJ.setID(cursor.getInt(0));
                reminderOBJ.setMedReminderDate(cursor.getString(1));
                reminderOBJ.setMedicationID(cursor.getInt(2));
                reminderOBJ.setUserName(cursor.getString(3));

            } while (cursor.moveToNext());
        }

        return reminderOBJ;
    }


    /**
     * -----------------------------------------------------------------------------
     * Class Name: getEmergencyMedicationRemindersReboot
     * Created By: Nikunj & Shruti
     * Created Date: 10-02-2016
     * Modified By:
     * Modified Date:
     * Purpose: Reminders Emergency
     * -----------------------------------------------------------------------------
     */

    public MedicationReminders getEmergencyMedicationRemindersReboot(int emergency_MedicationID) {
        MedicationReminders reminderOBJ = new MedicationReminders();
        String sqlQuary = "select EmergencyReminder._id,EmergencyReminder.DateTime,EmergencyReminder.emergencyMedication_id,EmergencyReminder.userName from EmergencyReminder;";
        open();
        Cursor cursor = db.rawQuery(sqlQuary, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {

                reminderOBJ.setID(cursor.getInt(0));
                reminderOBJ.setMedReminderDate(cursor.getString(1));
                reminderOBJ.setMedicationID(cursor.getInt(2));
                reminderOBJ.setUserName(cursor.getString(3));

            } while (cursor.moveToNext());
        }

        return reminderOBJ;
    }


    /**
     * -----------------------------------------------------------------------------
     * Class Name: getAppointmentRemindersByIDDuplicate
     * Created By:Nikunj & Shruti
     * Purpose: duplicate of getAppointmentRemindersByID function with table appointment
     * -----------------------------------------------------------------------------
     */
    public ArrayList<AppointmentReminder> getAppointmentRemindersByIDDuplicate(int AppointmentId) {
        ArrayList<AppointmentReminder> reminder = null;
        try {
            reminder = new ArrayList<>();
            AppointmentReminder reminderOBJ = new AppointmentReminder();
            String sqlQuary = "select Appointment._id,Appointment.GpName,AppointmentReminder ._id,AppointmentReminder.DateTime ,AppointmentReminder.ReminderAlert,Appointment.Date from Appointment join AppointmentReminder on Appointment._id=AppointmentReminder.appointment_id where Appointment._id=" + AppointmentId + ";";
         //   String sqlQuary = "select Appointment._id,Appointment.DateTime,Appointment.GpName from Appointment  where Appointment._id= " + AppointmentId + ";";
            open();

            Cursor cursor = db.rawQuery(sqlQuary, null);
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                do {
                    reminderOBJ.setID(cursor.getInt(0));
                    reminderOBJ.setGPName(cursor.getString(1));
                    reminderOBJ.setAppointmentID(Integer.parseInt(cursor.getString(2)));
                    reminderOBJ.setAppointmentReminderDateTime(convertToDateformat2(cursor.getString(3)));
                    reminderOBJ.setReminderAlert(cursor.getString(4));
                    reminderOBJ.setReminderDate(cursor.getString(5));

                    reminder.add(reminderOBJ);

                    reminderOBJ = new AppointmentReminder();

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reminder;
    }



    public ArrayList<AppointmentReminder> getAppointmentReminders_WithNoRepeat(int AppointmentId) {

        ArrayList<AppointmentReminder> reminder = null;
        try {
            reminder = new ArrayList<>();
            AppointmentReminder reminderOBJ = new AppointmentReminder();
            String sqlQuary = "select Appointment._id,Appointment.GpName,AppointmentReminder ._id,AppointmentReminder.DateTime ,AppointmentReminder.ReminderAlert  from Appointment join AppointmentReminder on Appointment._id=AppointmentReminder.appointment_id where AppointmentReminder._id=" + AppointmentId + " AND AppointmentReminder.reminder_App_Flag == " + 0 + ";";
            open();

            Cursor cursor = db.rawQuery(sqlQuary, null);
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                do {
                    reminderOBJ.setID(cursor.getInt(0));
                    reminderOBJ.setGPName(cursor.getString(1));
                    reminderOBJ.setAppointmentID(Integer.parseInt(cursor.getString(2)));
                    reminderOBJ.setAppointmentReminderDateTime(convertToDateformat2(cursor.getString(3)));
                    reminderOBJ.setReminderAlert(cursor.getString(4));
                    reminder.add(reminderOBJ);
                    reminderOBJ = new AppointmentReminder();
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reminder;
    }






















    public ArrayList<AppointmentReminder> getAppointmentRemindersByID(int AppointmentId) {

       System.out.println("in get appo");
        ArrayList<AppointmentReminder> reminder = null;
        try {
            reminder = new ArrayList<>();
            AppointmentReminder reminderOBJ = new AppointmentReminder();
            String sqlQuary = "select AppointmentReminder._id,AppointmentReminder.DateTime,AppointmentReminder.ReminderAlert,Appointment.GpName from AppointmentReminder,Appointment;";
            open();
            Log.e("DBAdapter", " Query" + sqlQuary);
            Cursor cursor = db.rawQuery(sqlQuary, null);

           // System.out.println("grets count is" + cursor.getCount());
            if (cursor.getCount() > 0) {
              //  System.out.println("gret count is" + cursor.getCount());
                cursor.moveToFirst();
                do {
                    System.out.println("in dowhile");

                    reminderOBJ.setID(cursor.getInt(0));
                    reminderOBJ.setAppointmentReminderDateTime(convertAppointmentDateTimeToDateTime(cursor.getString(1)));
                    reminderOBJ.setReminderAlert(cursor.getString(2));
                    reminderOBJ.setGPName(cursor.getString(3));
                    reminderOBJ.setReminderFlag(cursor.getInt(4));
                    reminder.add(reminderOBJ);
                    System.out.println("the reminde is" + reminder);
                    reminderOBJ = new AppointmentReminder();

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reminder;
    }


    public void getUpdateReminderFlag(String type, int medId, String sFlag) {

        String sqlQuery = "";

        if (type.equals("App"))
            sqlQuery = "UPDATE  Appointment SET  reminderFlag = " + sFlag + " WHERE  _id  = " + medId + ";";
        else if (type.equals("EME"))
            sqlQuery = "UPDATE  emergencyMedication SET  reminderFlag = " + sFlag + " WHERE  _id  = " + medId + ";";
        else if (type.equals("Med"))
            sqlQuery = "UPDATE  MedicationReminders    SET  reminderFlag = " + sFlag + " WHERE  medication_id   = " + medId + ";";
        else if (type.equals("PRES")) {
            System.out.println("i am in pres");
            sqlQuery = "UPDATE  Prescription     SET  reminderFlag = " + sFlag + " WHERE  medication_id   = " + medId + ";";
        }
        open();
        db.execSQL(sqlQuery);

    }

    public void getUpdatePresFlag(String type, int medId, String sFlag) {
        System.out.println("updated the reminder flag1"+type + " "+medId);
        String sqlQuery = "";

        if (type.equals("App"))
            sqlQuery = "UPDATE  AppointmentReminder SET  reminder_App_Flag = " + sFlag + " WHERE  _id  = " + medId + ";";
        else if (type.equals("EME"))
            sqlQuery = "UPDATE  emergencyMedication SET  reminderFlag = " + sFlag + " WHERE  _id  = " + medId + ";";
        else if (type.equals("Med"))
            sqlQuery = "UPDATE  MedicationReminders    SET  reminderFlag = " + sFlag + " WHERE  medication_id   = " + medId + ";";
        else if (type.equals("PRES")) {
            System.out.println("i am in pres");
            sqlQuery = "UPDATE  Prescription     SET  reminderFlag = " + sFlag + " WHERE  _id   = " + medId + ";";
        }
        open();
        db.execSQL(sqlQuery);

    }



    public ArrayList<NewMedication> getMedicationbyID(int MedicationID) {
        ArrayList<NewMedication> medications = null;
        try {
            String sqlQuary = "select new_medication._id,new_medication.medication_name,new_medication.start_data,new_medication.medication_desc,new_medication.medication_unit,new_medication.medication_dosag,new_medication.medStartDate,new_medication.titrationOn,new_medication.titrationStartdate,new_medication.titrationNumWeeks,new_medication.PrescriptionrenewalDate, medications_images.Image_link from new_medication join medications_images on new_medication._id=medications_images.medication_id where new_medication.username= '" + LoginActivity.userName + "' and new_medication._id = " + MedicationID + ";";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            NewMedication newMedication = null;
            int medicationId = -1;
            medications = new ArrayList<NewMedication>();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    if (medicationId == -1 || medicationId != cursor.getInt(0)) {
                        if (newMedication != null) {
                            medications.add(newMedication);
                        }
                        newMedication = new NewMedication();
                        newMedication.setId(cursor.getInt(0));
                        newMedication.setMedication_name(cursor.getString(1));
                        newMedication.setStart_date(cursor.getString(2));
                        newMedication.setMedication_description(cursor.getString(3));
                        newMedication.setUnit(cursor.getString(4));
                        newMedication.setDosage(cursor.getInt(5));
                        newMedication.setMedStartDate(cursor.getString(6));
                        newMedication.setTitrationOn(cursor.getInt(7) != 0);
                        newMedication.setTitrationStartDate(cursor.getString(8));
                        newMedication.setTitrationNumWeeks(cursor.getInt(9));
                        newMedication.setPrescriptionRenewalDate(cursor.getString(10));

                        medicationId = cursor.getInt(0);
                    }
                    newMedication.getMedicationImages().add(cursor.getString(11));
                } while (cursor.moveToNext());
                medications.add(newMedication);
                cursor.close();
                close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return medications;
    }


    public NewMedication getSpecificMedicationbyID(int MedicationID) {

        NewMedication newMedication = null;
        try {
            String sqlQuary = "select new_medication._id,new_medication.medication_name,new_medication.start_data,new_medication.medication_desc,new_medication.medication_unit,new_medication.medication_dosag,new_medication.medStartDate,new_medication.titrationOn,new_medication.titrationStartdate,new_medication.titrationNumWeeks,new_medication.PrescriptionrenewalDate from new_medication where new_medication._id = " + MedicationID + ";";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                newMedication = new NewMedication();
                newMedication.setId(cursor.getInt(0));
                newMedication.setMedication_name(cursor.getString(1));
                newMedication.setStart_date(cursor.getString(2));
                newMedication.setMedication_description(cursor.getString(3));
                newMedication.setUnit(cursor.getString(4));
                newMedication.setDosage(cursor.getInt(5));
                newMedication.setMedStartDate(cursor.getString(6));
                newMedication.setTitrationOn(cursor.getInt(7) != 0);
                newMedication.setTitrationStartDate(cursor.getString(8));
                newMedication.setTitrationNumWeeks(cursor.getInt(9));
                newMedication.setPrescriptionRenewalDate(cursor.getString(10));

            }

            cursor.close();
            close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return newMedication;
    }


    /*************************************************************************************************************/

    /**
     * -----------------------------------------------------------------------------
     * Class Name: Alarm Receiver
     * Created By: Nikunj & Shruti
     * Created Date: 09-02-2016
     * Modified By:
     * Modified Date:
     * Purpose: Reminders Arraylist
     * -----------------------------------------------------------------------------
     */

    public EmergencyMedicationEntity getEmergencyMedicationbyIDReboot(int MedicationID) {
        EmergencyMedicationEntity newMedication = null;
        try {
            String sqlQuary = "select emergencyMedication._id,emergencyMedication.medicationName,emergencyMedication.DateTime,emergencyMedication.unit,emergencyMedication.dosage, emergency_medications_images.Image_link from emergencyMedication join emergency_medications_images on emergencyMedication._id=emergency_medications_images.emergency_medication_id;";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            newMedication = null;
            int medicationId = -1;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    if (medicationId == -1 || medicationId != cursor.getInt(0)) {

                        newMedication = new EmergencyMedicationEntity();
                        newMedication.setId(cursor.getInt(0));
                        newMedication.setEmergencyMedicationName(cursor.getString(1));
                        newMedication.setExpiryDate(cursor.getString(2));
                        newMedication.setUnit(cursor.getString(3));
                        newMedication.setDosage(cursor.getInt(4));

                        medicationId = cursor.getInt(0);
                    }
                    newMedication.getMedicationImages().add(cursor.getString(5));
                } while (cursor.moveToNext());
                cursor.close();
                close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newMedication;
    }


    public EmergencyMedicationEntity getEmergencyMedicationbyID(int MedicationID) {
        EmergencyMedicationEntity newMedication = null;
        try {
            String sqlQuary = "select emergencyMedication._id,emergencyMedication.medicationName,emergencyMedication.DateTime,emergencyMedication.unit,emergencyMedication.dosage, emergency_medications_images.Image_link from emergencyMedication join emergency_medications_images on emergencyMedication._id=emergency_medications_images.emergency_medication_id where emergencyMedication.userName= '" + LoginActivity.userName + "' and emergencyMedication._id = " + MedicationID + ";";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            newMedication = null;
            int medicationId = -1;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    if (medicationId == -1 || medicationId != cursor.getInt(0)) {

                        newMedication = new EmergencyMedicationEntity();
                        newMedication.setId(cursor.getInt(0));
                        newMedication.setEmergencyMedicationName(cursor.getString(1));
                        newMedication.setExpiryDate(cursor.getString(2));
                        newMedication.setUnit(cursor.getString(3));
                        newMedication.setDosage(cursor.getInt(4));

                        medicationId = cursor.getInt(0);
                    }
                    newMedication.getMedicationImages().add(cursor.getString(5));
                } while (cursor.moveToNext());
                cursor.close();
                close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newMedication;
    }

    /*************************************************************************************************************/
    public NewContactEntity getContactbyID(int ContactID) {
        NewContactEntity newContact = null;
        try {
            String sqlQuary = "select contacts._id,contacts.contactName,contacts.e_mail,contacts.relationship,contacts.contactNumber from contacts   where contacts.userName= '" + LoginActivity.userName + "' and contacts._id = " + ContactID + ";";
            open();
            Cursor cursor = db.rawQuery(sqlQuary, null);
            newContact = null;
            ArrayList<String> images = new ArrayList<String>();
            int contactId = -1;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    if (contactId == -1 || contactId != cursor.getInt(0)) {
                        newContact = new NewContactEntity();
                        newContact.setId(cursor.getInt(0));
                        newContact.setContactName(cursor.getString(1));
                        newContact.setE_mail(cursor.getString(2));
                        newContact.setRelationship(cursor.getString(3));
                        newContact.setContactNumber(cursor.getString(4));


                        contactId = cursor.getInt(0);
                    }
                } while (cursor.moveToNext());
                cursor.close();
                close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newContact;
    }


    public long SaveMedicationImages(int medication_id, String image_link) {
        long rowId = 0;
        try {
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("medication_id", medication_id);
            contentValues.put("userName", LoginActivity.userName);
            contentValues.put("Image_link", image_link);
            rowId = db.insert("medications_images", null, contentValues);
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public long SaveEmergencyMedicationImages(int emergency_medication_id, String image_link) {
        long rowId = 0;
        try {
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("emergency_medication_id", emergency_medication_id);
            contentValues.put("userName", LoginActivity.userName);
            contentValues.put("Image_link", image_link);
            rowId = db.insert("emergency_medications_images", null, contentValues);
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public long SaveMedicationReminders(int medication_id, String ReminderDate) {
        long rowId = 0;
        try {
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("medication_id", medication_id);

            contentValues.put("RminderDateTime", ReminderDate);
            contentValues.put("userName", LoginActivity.userName);
            rowId = db.insert("MedicationReminders", null, contentValues);
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public boolean removeMedImages(int medication_id) {
        int a = 0;
        try {
            open();
            a = db.delete("medications_images", "medication_id=" + medication_id, null);
        } catch (Exception e) {
            e.toString();
        }
        close();
        return a > 0;
    }

    public boolean removeMedication(int medication_id) {
        int a = 0;
        try {
            open();
            a = db.delete("new_medication", "_id=" + medication_id, null);
        } catch (Exception e) {
            e.toString();
        }
        close();
        return a > 0;
    }


    public boolean removeMedPrescriptions(int medication_id) {
        int a = 0;
        try {
            open();
            a = db.delete("Prescription", "medication_id=" + medication_id, null);
        } catch (Exception e) {
            e.toString();
        }
        close();
        return a > 0;
    }

    public boolean removeEmergencyMedImages(int medication_id) {
        int a = 0;
        try {
            open();
            a = db.delete("emergency_medications_images", "emergency_medication_id=" + medication_id, null);
        } catch (Exception e) {
            e.toString();
        }
        close();
        return a > 0;
    }


    public boolean removeMedicationReminders(int medication_id) {
        int a = 0;
        try {
            open();
            a = db.delete("MedicationReminders", "medication_id=" + medication_id, null);
        } catch (Exception e) {
            e.toString();
        }
        close();
        return a > 0;
    }

    public boolean removeEmergencyMedicationReminders(int medication_id) {
        int a = 0;
        try {
            open();
            a = db.delete("EmergencyReminder", "emergencyMedication_id=" + medication_id, null);
        } catch (Exception e) {
            e.toString();
        }
        close();
        return a > 0;
    }

    public boolean removeAppointmentReminders(int appointment_id) {
        int a = 0;
        try {
            open();
            a = db.delete("AppointmentReminder", "appointment_id=" + appointment_id, null);
        } catch (Exception e) {
            e.toString();
        }
        close();
        return a > 0;
    }


    //(userName, password, question, answer, name, email, phone,question2_body,answer2,question3_body,answer3);
    public boolean handleRegister(String userName, String password, String question, String answer, String name,
                                  String email, String phone, String question2, String answer2, String question3, String answer3) {


        String sqlQuery = "select id from user where userName = '" + userName + "' and password = '" + password + "';";
        open();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.getCount() > 0) {
            close();
            return false;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("password", password);
            contentValues.put("username", userName);
            contentValues.put("question", question);
            contentValues.put("answer", answer);
            contentValues.put("question2", question2);
            contentValues.put("answer2", answer2);
            contentValues.put("question3", question3);
            contentValues.put("answer3", answer3);
            contentValues.put("name", name);
            contentValues.put("email", email);
            contentValues.put("phone", phone);

            long rowId = db.insert("user", null, contentValues);

            close();
            return rowId != -1;
        }
    }


    public void handleForgetPasswordOffline(String userName, String email, IRESTWebServiceCaller caller) {
        String sqlQuery = "select question,answer,question2,answer2,question3,answer3 from user where userName = '" + userName + "' and email = '" + email
                + "';";
        open();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            caller.onResponseReceived("{status:\"true\",question:\"" + cursor.getString(0) + "\",answer:\""
                    + cursor.getString(1) + "\",question2:\"" + cursor.getString(2) + "\",answer2:\"" + cursor.getString(3) + "\",question3:\"" + cursor.getString(4) + "\",answer3:\"" + cursor.getString(5) + "\"}");
            close();
        } else {
            Toast.makeText(context, context.getString(R.string.username_email_validation_error), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void handleChangePassword(String userName, String email, String password, IRESTWebServiceCaller caller) {
        ContentValues values = new ContentValues();
        values.put("password", password);
        open();
        if (!ServiceUtil.isNetworkConnected(context)) {
            values.put("isOfflineUpdate", 1);

            db.update("user", values, "userName = ? and email  = ?", new String[]{userName, email});
            caller.onResponseReceived("{status:\"true\"}");
            close();
        }

    }

    public List<String> displayAllUsers() {
        open();
        List<String> users = new ArrayList<String>();
        Cursor cursor = null;
        try {
            String sqlQuery = "select userName from user;";
            cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    users.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } finally {
            //  cursor.close();
            close();
        }
        return users;
    }

    public String isOfflinePasswordUpdated() {
        String sqlQuery = "select email from user where userName = '" + LoginActivity.userName
                + "' and isOfflineUpdate = 1;";
        String result = "";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getString(0);

            }
        } finally {
            cursor.close();
            close();
        }
        return result;
    }

    public String GetEmailbyUsername(String UserName) {
        open();
        String sqlQuery = "select email from user where userName = '" + UserName + "';";
        String result = "";
        try {

            Cursor cursor = null;
            cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getString(0);

            }
            close();
        } catch (Exception ex) {
        }
        return result;
    }

    public String GetPasswordbyUsername(String UserName) {
        open();
        String sqlQuery = "select password from user where userName = '" + UserName + "';";
        String result = "";
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getString(0);
            }
            close();
        } catch (Exception ex) {
        }
        return result;
    }


    public void resetOfflineUpdateFlag() {
        try {
            ContentValues values = new ContentValues();
            values.put("isOfflineUpdate", 0);
            open();
            db.update("user", values, "userName = ?", new String[]{LoginActivity.userName});
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private Context con;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            con = context;
            if (android.os.Build.VERSION.SDK_INT >= 4.2) {
                DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
            } else {
                DB_PATH = context.getString(R.string._data_data_) + context.getPackageName() + "/databases/";
            }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            try {
                //these tables should execute in the nest release with Database version 4.0
                db.execSQL(CREATE_TABLE_MEDICATION_ALARM_REMINDERS);
                db.execSQL(CREATE_TABLE_EMERGENCY_MEDICATION);
                db.execSQL(CREATE_TABLE_EMERGENCY_MEDICATION_REMINDER);
                db.execSQL(CREATE_TABLE_APPOINTMENT);
                db.execSQL(CREATE_TABLE_APPOINTMENT_REMINDER);
                db.execSQL(CREATE_TABLE_MEDICATION);
                db.execSQL(CREATE_TABLE_MEDICATION_REMINDERS);
                db.execSQL(CREATE_TABLE_MEDICATION_IMAGES);
                db.execSQL(CREATE_TABLE_EMERGENCY_MEDICATION_IMAGES);
                db.execSQL(CREATE_TABLE_CONTACTS);
                db.execSQL(CREATE_TABLE_PRESCRIPTION);
                db.execSQL("INSERT INTO Prescription (_id) VALUES  ('1240000');");
                db.execSQL("Delete from Prescription  where _id ='1240000'");
                db.execSQL("INSERT INTO EmergencyReminder (_id) VALUES  ('2240000');");
                db.execSQL("Delete from EmergencyReminder  where _id ='2240000'");
                db.execSQL("INSERT INTO AppointmentReminder (_id) VALUES  ('3240000');");
                db.execSQL("Delete from AppointmentReminder  where _id ='3240000'");
                //This script executed on version 3.0 of the database
                db.execSQL("ALTER TABLE user ADD COLUMN question2 TEXT;");
                db.execSQL("ALTER TABLE user ADD COLUMN answer2 TEXT;");
                db.execSQL("ALTER TABLE user ADD COLUMN question3 TEXT;");
                db.execSQL("ALTER TABLE user ADD COLUMN answer3 TEXT;");

            } catch (Exception ex) {
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            onCreate(db);
            if (newVersion > oldVersion) {
                try {
                    //these tables should execute in the next release with Database version 4.0 .
                    db.execSQL(CREATE_TABLE_MEDICATION_ALARM_REMINDERS);
                    db.execSQL(CREATE_TABLE_APPOINTMENT);
                    db.execSQL(CREATE_TABLE_APPOINTMENT_REMINDER);
                    db.execSQL(CREATE_TABLE_MEDICATION);
                    db.execSQL(CREATE_TABLE_MEDICATION_REMINDERS);
                    db.execSQL(CREATE_TABLE_MEDICATION_IMAGES);
                    db.execSQL(CREATE_TABLE_EMERGENCY_MEDICATION_IMAGES);
                    db.execSQL(CREATE_TABLE_CONTACTS);
                    db.execSQL(CREATE_TABLE_PRESCRIPTION);
                    //This script executed on version 3.0 of the database
                    db.execSQL("ALTER TABLE user ADD COLUMN question2 TEXT;");
                    db.execSQL("ALTER TABLE user ADD COLUMN answer2 TEXT;");
                    db.execSQL("ALTER TABLE user ADD COLUMN question3 TEXT;");
                    db.execSQL("ALTER TABLE user ADD COLUMN answer3 TEXT;");
                } catch (Exception ex) {
                }
            }
        }

        private void executeSQLScript(SQLiteDatabase db, BufferedReader reader)
                throws IOException {
            String line;
            StringBuilder statement = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                statement.append(line);
                statement.append("\n");
                if (line.endsWith(";")) {
                    db.execSQL(statement.toString());
                    statement = new StringBuilder();
                }
            }
        }

        public void createDataBase() throws IOException {
            // If database not exists copy it from the assets
            boolean mDataBaseExist = checkDataBase();
            if (!mDataBaseExist) {
                try {
                    // Copy the database from assests
                    copyDataBase();
                    this.getReadableDatabase();
                    this.close();
                    Log.e(TAG, "createDatabase database created");
                } catch (IOException mIOException) {
                    throw new Error("ErrorCopyingDataBase");
                }
            }
        }

        // Check that the database exists here: /data/data/your
        // package/databases/Da Name
        private boolean checkDataBase() {
            File dbFile = new File(DB_PATH + DATABASE_NAME);
            return dbFile.exists();
        }

        // Copy the database from assets
        private void copyDataBase() throws IOException {
            InputStream mInput = con.getAssets().open(DATABASE_NAME);
            String outFileName = DB_PATH + DATABASE_NAME;
            OutputStream mOutput = new FileOutputStream(outFileName);
            byte[] mBuffer = new byte[1024];
            int mLength;
            while ((mLength = mInput.read(mBuffer)) > 0) {
                mOutput.write(mBuffer, 0, mLength);
            }
            mOutput.flush();
            mOutput.close();
            mInput.close();
        }

    }

}
