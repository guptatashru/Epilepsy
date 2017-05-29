package com.hp.epilepsy.widget.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mahmoumo on 2/8/2016.
 */
public class Appointment {

    private int ID;
    private String GpName;
    private String UserName;
    private String AppointmentNotes;
    private double latitude;
    private double longitude;

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    private boolean delete;
    private ArrayList<Date> AppointmentReminders;

    public ArrayList<Date> getAppointmentReminders() {
        return AppointmentReminders;
    }

    public void setAppointmentReminders(ArrayList<Date> appointmentReminders) {
        AppointmentReminders = appointmentReminders;
    }



    public Appointment()
    {
        setAppointmentReminders(new ArrayList<Date>());
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



    public String getUserAddress() {
        return UserAddress;
    }

    public void setUserAddress(String userAddress) {
        UserAddress = userAddress;
    }

    private String UserAddress;


    public String getAppointmentDateTime() {
        return AppointmentDateTime;
    }

    public void setAppointmentDateTime(String appointmentDateTime) {
        AppointmentDateTime = appointmentDateTime;
    }

    private String AppointmentDateTime;
    private String AppointmentDate;

    public String getAppointmentTime() {
        return AppointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        AppointmentTime = appointmentTime;
    }

    public String getAppointmentDate() {
        return AppointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        AppointmentDate = appointmentDate;
    }

    private String AppointmentTime;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getGpName() {
        return GpName;
    }

    public void setGpName(String gpName) {
        GpName = gpName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getAppointmentNotes() {
        return AppointmentNotes;
    }

    public void setAppointmentNotes(String appointmentNotes) {
        AppointmentNotes = appointmentNotes;
    }
}
