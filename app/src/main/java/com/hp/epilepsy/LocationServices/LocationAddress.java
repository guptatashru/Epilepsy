package com.hp.epilepsy.LocationServices;

/**
 * Created by mahmoumo on 2/2/2016.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.hp.epilepsy.utils.ServiceUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAddress {
    private static final String TAG = "LocationAddress";
    private static Context ctx;

    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        ctx = context;
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    if (ServiceUtil.isNetworkConnected(context)) {
                        //TODO check network here

                        List<Address> addressList = geocoder.getFromLocation(
                                latitude, longitude, 1);
                        if (addressList != null && addressList.size() > 0) {
                            Address address = addressList.get(0);
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                if (address.getAddressLine(i) != null) {
                                    sb.append(address.getAddressLine(i)).append(" ");
                                }
                            }
                            if (address.getLocality() != null) {
                                sb.append(address.getLocality()).append(" ");
                            }
                            if (address.getPostalCode() != null) {
                                sb.append(address.getPostalCode()).append(" ");
                            }
                            if (address.getCountryName() != null) {
                                sb.append(address.getCountryName());
                            }
                            result = sb.toString();
                        }
                    } else {

                    }
                } catch (IOException e) {
                   // Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        //result = "Address: " + result;
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = null;
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }


    public static void getAddressFromLocation(final String locationAddress,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {

                    List<Address> addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address addresss = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        sb.append(addresss.getLatitude()).append(" ");
                        sb.append(addresss.getLongitude());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                   // Log.e(TAG, "Unable to connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                     /*   result = "Address: " + locationAddress +
                                "\n\nLatitude and Longitude :\n" + result;*/
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address: " + locationAddress +
                                "\n Unable to get Latitude and Longitude for this address location.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }


}