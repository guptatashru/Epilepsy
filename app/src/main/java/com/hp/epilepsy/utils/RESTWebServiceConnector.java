package com.hp.epilepsy.utils;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.hp.epilepsy.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class RESTWebServiceConnector extends AsyncTask<String, String, Integer> {
	final int FAILURE = 0;
	final int SUCCESS = 1;
	ProgressDialog progressDialog;
	String response;
	IRESTWebServiceCaller caller;
	ArrayList<NameValuePair> params;
	boolean usingCustomJson;
	JSONObject myJsonOb;

	public RESTWebServiceConnector(IRESTWebServiceCaller caller) {
		this.caller = caller;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(caller.getContext(),
				ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Retrieving data from server...");
		progressDialog.show();
	}

	@Override
	protected void onPostExecute(Integer result) {
		if (progressDialog.isShowing()) {
			progressDialog.hide();
		}
		if (result == FAILURE) {
			Toast.makeText(caller.getContext(), caller.getContext().getString(R.string.error_connect), Toast.LENGTH_LONG)
					.show();
		} else {
			caller.onResponseReceived(response);
		}
	}

	@Override
	protected Integer doInBackground(String... params) {
		String url = params[0];

		HttpPost httpPost = new HttpPost(url);

		HttpClient httpClient = new DefaultHttpClient();
		if (usingCustomJson) {
			try {
				String jsonString = myJsonOb.toString();
				// ArrayList<NameValuePair> mParams= new
				// ArrayList<NameValuePair>();
				// mParams.add(new
				// BasicNameValuePair("transitBoxData",jsonString));
				// httpPost.setEntity((HttpEntity) new
				// UrlEncodedFormEntity(mParams, HTTP.UTF_8));
				StringEntity mysEnt = new StringEntity(jsonString, HTTP.UTF_8);
				mysEnt.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				httpPost.setEntity(mysEnt);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (this.params != null) {
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(
						getParams()));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {

		}
		HttpResponse httpResponse;

		try {
			httpResponse = httpClient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				InputStream inputStream = entity.getContent();
				response = convertStreamToString(inputStream);
				inputStream.close();
				return SUCCESS;
			}
		} catch (Exception e) {
			return FAILURE;
		}
		return FAILURE;
	}

	public String convertStreamToString(InputStream inputStream) {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream, "iso-8859-1"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		try {
			while (bufferedReader != null
					&& (line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			inputStream.close();
		} catch (IOException e) {
			Log.e("Exception", "Error", e);
		}
		return stringBuilder.toString();
	}

	public ArrayList<NameValuePair> getParams() {
		return params;
	}

}
