package com.hp.epilepsy.widget.model;

import java.util.Date;

/**
 * Created by elmalah on 4/7/2016.
 */
public class Event {
    String title;
    Date eventDate;
    int type;
    int medicationHandlStatus;
    String medicationHandleReasons;
    boolean isReminder;
    String eventTime;

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public boolean isReminder() {
        return isReminder;
    }

    public void setIsReminder(boolean isReminder) {
        this.isReminder = isReminder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMedicationHandlStatus() {
        return medicationHandlStatus;
    }

    public void setMedicationHandlStatus(int medicationHandlStatus) {
        this.medicationHandlStatus = medicationHandlStatus;
    }

    public String getMedicationHandleReasons() {
        return medicationHandleReasons;
    }

    public void setMedicationHandleReasons(String medicationHandleReasons) {
        this.medicationHandleReasons = medicationHandleReasons;
    }
}
