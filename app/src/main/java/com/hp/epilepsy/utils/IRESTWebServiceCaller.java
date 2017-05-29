package com.hp.epilepsy.utils;


import android.content.Context;
 

public interface IRESTWebServiceCaller 
{
	void onResponseReceived(String jsonServiceResponse);

	Context getContext();
}
