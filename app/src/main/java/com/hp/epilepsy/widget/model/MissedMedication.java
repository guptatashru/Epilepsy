package com.hp.epilepsy.widget.model;

/**
 * Created by mahmoumo on 4/7/2016.
 */
public class MissedMedication {

    private int medicationId;
    private int UnderScID;
    private String medicationName;
    private String medicationReminderDate;
    private String medicationReminderTime;

    public String getMissedReason() {
        return MissedReason;
    }

    public void setMissedReason(String missedReason) {
        MissedReason = missedReason;
    }

    private String MissedReason;

    public String getMedStartDate() {
        return medStartDate;
    }

    public void setMedStartDate(String medStartDate) {
        this.medStartDate = medStartDate;
    }

    private String medStartDate;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public int getMedicationId() {
        return medicationId;
    }

    public void setUnderScID(int underScID) {
        UnderScID = underScID;
    }

    public int getUnderScID() {
        return UnderScID;
    }

    public void setMedicationId(int medicationId) {
        this.medicationId = medicationId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getMedicationReminderDate() {
        return medicationReminderDate;
    }

    public void setMedicationReminderDate(String medicationReminderDate) {
        this.medicationReminderDate = medicationReminderDate;
    }


    public static String getCSVHeader() {
        String commaDelimiter = ",";
        String NewLine="\n";
        StringBuilder builder = new StringBuilder();
        builder.append("Missed Medications");
        builder.append(NewLine);
        builder.append("Medication Name");
        builder.append(commaDelimiter);
        builder.append("Start Date");
        return builder.toString();
    }


    public String toCSV() {
        //If Changing the structure of this also change the structure of the
        String commaDelimiter=",";
        StringBuilder builder=new StringBuilder();
        builder.append(medicationName.replace(",", " "));
        builder.append(commaDelimiter);
        builder.append(medStartDate.replace(",", " "));
        builder.append(commaDelimiter);
        return builder.toString();
    }


    public String getMedicationReminderTime() {
        return medicationReminderTime;
    }

    public void setMedicationReminderTime(String medicationReminderTime) {
        this.medicationReminderTime = medicationReminderTime;
    }
}
