package com.hp.epilepsy.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ServiceUtil {
	public static boolean isNetworkConnected(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
