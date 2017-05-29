package com.hp.epilepsy.widget.model;

/**
 * Created by mahmoumo on 1/13/2016.
 */
public class MedicationReminders {
    private String MedReminderDate;
    private int ID;
	private String UserName;
    private String MedicationName;
    private int MedicationID;
    private int medicationHandlingStatus=0;
    private String medicationHandlingReason;
    private int reminderFlag=0;


    public String getMedicationHandlingReason() {
        return medicationHandlingReason;
    }

    public void setMedicationHandlingReason(String medicationHandlingReason) {
        this.medicationHandlingReason = medicationHandlingReason;
    }

    public int getMedicationHandlingStatus() {
        return medicationHandlingStatus;
    }

    public void setMedicationHandlingStatus(int medicationHandlingStatus) {
        this.medicationHandlingStatus = medicationHandlingStatus;
    }
    public int getReminderFlag()
    {
        return reminderFlag;
    }
    public void setReminderFlag(int reminderFlag)
    {
        this.reminderFlag=reminderFlag;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getMedicationID() {
        return MedicationID;
    }

    public void setMedicationID(int medicationID) {
        MedicationID = medicationID;
    }



    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getMedicationName(){return  MedicationName;}
    public void setMedicationName(String MedicationName) {
        this.MedicationName = MedicationName;
    }


    public String getMedReminderDate() {
        return MedReminderDate;
    }

    public void setMedReminderDate(String medReminderDate) {
        MedReminderDate = medReminderDate;
    }
}
