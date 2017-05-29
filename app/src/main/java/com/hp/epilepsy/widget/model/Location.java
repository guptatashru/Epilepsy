package com.hp.epilepsy.widget.model;

import java.io.Serializable;

/**
 * Created by mahmoumo on 3/15/2016.
 */
public class Location implements Serializable {
    double latitude=0.0;
    double longitude=0.0;
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


}

