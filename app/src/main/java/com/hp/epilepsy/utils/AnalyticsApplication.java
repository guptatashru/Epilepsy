package com.hp.epilepsy.utils;

/**
 * Created by mahmoumo on 11/15/2015.
 */

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;

/**
 * This is a subclass of {@link Application} used to provide shared objects for this app, such as
 * the {@link Tracker}.
 */

public class AnalyticsApplication extends Application {
    public static final String TAG = AnalyticsApplication.class
            .getSimpleName();

    private static AnalyticsApplication mInstance;

    public static synchronized AnalyticsApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        System.out.println("on create service");
        mInstance = this;
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }




    public synchronized Tracker getGoogleAnalyticsTracker() {
        try {
            AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
            return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

}

