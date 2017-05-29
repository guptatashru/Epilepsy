package com.hp.epilepsy.widget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.DBAdapter;

public class TermsConditionsActivity extends Activity implements
		OnCheckedChangeListener {

	public TermsConditionsActivity() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_conditions);

		TextView textView = (TextView) findViewById(R.id.content);
		textView.setText(Html.fromHtml(getString(R.string.terms_conditions)));
		CheckBox accept = (CheckBox) findViewById(R.id.accept);
		accept.setOnCheckedChangeListener(this);
	}
	@Override
	public  void onPause()
	{
		super.onPause();
		/*ApplicationBackgroundCheck.getDecrement(getApplicationContext());
		System.out.println("getCurrentValue17"+ApplicationBackgroundCheck.getCurrentValue(getApplicationContext()));*/
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		/*ApplicationBackgroundCheck.setIncrement(getApplicationContext());
		System.out.println("getCurrentValue18"+ApplicationBackgroundCheck.getCurrentValue(getApplicationContext()));*/
	}


	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		try {
			if (isChecked) {
                DBAdapter adapter = new DBAdapter(this);

                adapter.updateFirstLogin(LoginActivity.userName);

                Intent mainView = new Intent(this, HomeView.class);
                startActivity(mainView);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
