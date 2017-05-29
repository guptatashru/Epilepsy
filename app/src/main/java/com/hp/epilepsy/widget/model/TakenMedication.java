package com.hp.epilepsy.widget.model;

import java.io.Serializable;

/**
 * Created by mahmoumo on 5/15/2016.
 */
public class TakenMedication implements Serializable{

    private int medicationId;

    public int getMedicationStatus() {
        return MedicationStatus;
    }

    public void setMedicationStatus(int medicationStatus) {
        MedicationStatus = medicationStatus;
    }

    private int MedicationStatus;
    private String medicationName;
    private String medicationReminderDate;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    private  int month;

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

}
