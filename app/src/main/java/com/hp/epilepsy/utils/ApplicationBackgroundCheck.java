package com.hp.epilepsy.utils;

import android.app.ActivityManager;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * -----------------------------------------------------------------------------
 * Class Name: ApplicationBackgroundCheck
 * Created By:Nikunj & Shruti
 * Purpose:To handle the application background/foregorund
 * -----------------------------------------------------------------------------
 */
public class ApplicationBackgroundCheck {
    public static int setIncrement(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int i = prefs.getInt("backgroundFlag", 0);
        i += 1;
        prefs.edit().putInt("backgroundFlag", i).commit();
        return i;
    }
    public static int getDecrement(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int j = prefs.getInt("backgroundFlag", 0);
        j -= 1;
        prefs.edit().putInt("backgroundFlag",j).commit();
        return j;

    }

    public static int getCurrentValue(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt("backgroundFlag", 0);
    }
    public static boolean isMyServiceRunning(Class<?> serviceClass,Context cntx) {
            ActivityManager manager = (ActivityManager) cntx.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }

    public static void showNotificationDialog(String Title, String Message, String Type,int flag,Context cntx,FragmentManager fragmentManager) {
        DialogFragment newFragment = NotificationAlertDialog.newInstance(Title, Message, Type,flag);
        newFragment.setCancelable(false);
        newFragment.show(fragmentManager, "dialog");
    }

}