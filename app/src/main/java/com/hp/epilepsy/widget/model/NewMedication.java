package com.hp.epilepsy.widget.model;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

/**
 * Created by mahmoumo on 11/1/2015.
 */
public class NewMedication implements Serializable{

    private String medication_name;
    private int id;
    private String Unit;
    private String medication_description;
    private int dosage;
    private String start_date;

    public String getPrescriptionRenewalDate() {
        return prescriptionRenewalDate;
    }

    public void setPrescriptionRenewalDate(String prescriptionRenewalDate) {
        this.prescriptionRenewalDate = prescriptionRenewalDate;
    }

    private String prescriptionRenewalDate;

    public String getMedStartDate() {
        return medStartDate;
    }

    public void setMedStartDate(String medStartDate) {
        this.medStartDate = medStartDate;
    }

    private String medStartDate;

    public String getTitrationStartDate() {
        return titrationStartDate;
    }

    public void setTitrationStartDate(String titrationStartDate) {
        this.titrationStartDate = titrationStartDate;
    }

    private String titrationStartDate;
    private int titrationNumWeeks;
    private boolean titrationOn;

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    private boolean delete;
    private ArrayList<String> medicationImages;
    private ArrayList<MedicationReminders> MedicationReminders;

    public NewMedication(){
        setMedicationImages(new ArrayList<String>());
        setMedicationReminders(new ArrayList<MedicationReminders>());
    }

    public ArrayList<String> getMedicationImages() {
        return medicationImages;
    }

    public void setMedicationImages(ArrayList<String> medicationImages) {
        this.medicationImages = medicationImages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedication_name() {
        return medication_name;
    }

    public void setMedication_name(String medication_namee) {
        this.medication_name = medication_namee;
    }

    public String getMedication_description() {
        return medication_description;
    }

    public void setMedication_description(String medication_description) {
        this.medication_description = medication_description;
    }

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }



    public boolean isTitrationOn() {
        return titrationOn;
    }

    public void setTitrationOn(boolean titrationOn) {
        this.titrationOn = titrationOn;
    }


    public int getTitrationNumWeeks() {
        return titrationNumWeeks;
    }

    public void setTitrationNumWeeks(int titrationNumWeeks) {
        this.titrationNumWeeks = titrationNumWeeks;
    }

    public ArrayList<MedicationReminders> getMedicationReminders() {
        return MedicationReminders;
    }

    public void setMedicationReminders(ArrayList<MedicationReminders> medicationReminders) {
        MedicationReminders = medicationReminders;
    }
}
