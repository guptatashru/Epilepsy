package com.hp.epilepsy.widget.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by elmalah on 3/16/2016.
 */
public class EmergencyMedicationEntity {

    int id;
    String emergencyMedicationName;
    String Unit;
    String expiryDate;
    int dosage;
    int reminderDayeBefore;

    public int getReminderFlag() {
        return reminderFlag;
    }

    public void setReminderFlag(int reminderFlag) {
        this.reminderFlag = reminderFlag;
    }

    boolean hasReminder;
    private boolean deleted;
    private int emergencyMedicationID;
    private int reminderFlag;


MedicationReminders medicationReminder=new MedicationReminders();

    public MedicationReminders getMedicationReminder() {
        return medicationReminder;
    }
   public int getemergencyMedicationID(){return emergencyMedicationID;}
    public void setMedicationReminder(MedicationReminders medicationReminder) {
        this.medicationReminder = medicationReminder;
    }

    private ArrayList<String> medicationImages=new ArrayList<>();

    private ArrayList<Date> MedicationReminders=new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public ArrayList<String> getMedicationImages() {
        return medicationImages;
    }

    public void setMedicationImages(ArrayList<String> medicationImages) {
        this.medicationImages = medicationImages;
    }

    public ArrayList<Date> getMedicationReminders() {
        return MedicationReminders;
    }

    public void setMedicationReminders(ArrayList<Date> medicationReminders) {
        MedicationReminders = medicationReminders;
    }

    public String getEmergencyMedicationName() {
        return emergencyMedicationName;
    }

    public void setEmergencyMedicationName(String emergencyMedicationName) {
        this.emergencyMedicationName = emergencyMedicationName;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public int getReminderDayeBefore() {
        return reminderDayeBefore;
    }

    public void setReminderDayeBefore(int reminderDayeBefore) {
        this.reminderDayeBefore = reminderDayeBefore;
    }

    public boolean isHasReminder() {
        return hasReminder;
    }

    public void setHasReminder(boolean hasReminder) {
        this.hasReminder = hasReminder;
    }

    public void setEmergencyMedicationID(int emergencyMedicationID) {
        this.emergencyMedicationID = emergencyMedicationID;
    }
}
