package com.hp.epilepsy.widget.model;

/**
 * Created by mahmoumo on 4/12/2016.
 */
public class PrescriptionReminders {

    private int Id;
    private int MedicationId;
    private String prescriptionReminderDate;

    private String Alertbefore;
    private String renewalDate;
    private String Username;
    private String MedicationName;
    private int reminderFlag;

    public int getReminderFlag() {
        return reminderFlag;
    }

    public void setReminderFlag(int reminderFlag) {
        this.reminderFlag = reminderFlag;
    }

    public String getMedicationName() {
        return MedicationName;
    }

    public void setMedicationName(String medicationName) {
        MedicationName = medicationName;
    }




    public String getAlertbefore() {
        return Alertbefore;
    }

    public void setAlertbefore(String alertbefore) {
        Alertbefore = alertbefore;
    }

    public String getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(String renewalDate) {
        this.renewalDate = renewalDate;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }


    public int getMedicationId() {
        return MedicationId;
    }

    public void setMedicationId(int medicationId) {
        MedicationId = medicationId;
    }

    public String getPrescriptionReminderDate() {
        return prescriptionReminderDate;
    }

    public void setPrescriptionReminderDate(String prescriptionReminderDate) {
        this.prescriptionReminderDate = prescriptionReminderDate;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }


}
