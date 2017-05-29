package com.hp.epilepsy.widget.model;

import java.util.Date;

/**
 * Created by mahmoumo on 2/9/2016.
 */
public class AppointmentReminder {

    private int ID;
    private int AppointmentID;
    private String UserName;
    private String GPName;
    private int reminderFlag;
    private String ReminderAlert;
    private Date AppointmentReminderDateTime;
    private String reminderDate;

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public int getReminderFlag() {
        return reminderFlag;
    }

    public void setReminderFlag(int reminderFlag) {
        this.reminderFlag = reminderFlag;
    }

    public String getReminderAlert() {
        return ReminderAlert;
    }

    public String getGPName() {
        return GPName;
    }

    public void setGPName(String GPName) {
        this.GPName = GPName;
    }

    public void setReminderAlert(String reminderAlert) {
        ReminderAlert = reminderAlert;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getAppointmentID() {
        return AppointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        AppointmentID = appointmentID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public Date getAppointmentReminderDateTime() {
        return AppointmentReminderDateTime;
    }

    public void setAppointmentReminderDateTime(Date appointmentReminderDateTime) {
        AppointmentReminderDateTime = appointmentReminderDateTime;
    }



}
