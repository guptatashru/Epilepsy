package com.hp.epilepsy.widget;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.DBAdapter;

@SuppressLint("SimpleDateFormat")
public class CreateSMSFragment extends Fragment implements OnClickListener {


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(
				R.layout.activity_create_sms, container, false);
		setupWidgets(rootView);
		return rootView;
	}
	

	private void setupWidgets(View view) {
		TextView title = (TextView) view.findViewById(R.id.crsm_title);
			title.setFocusableInTouchMode(true);
			title.requestFocus();
		view.findViewById(R.id.crsm_save).setOnClickListener(this);
		//view.findViewById(R.id.crsm_delete).setOnClickListener(this);
	}



	public void handleSave(View view) {
		
		DBAdapter adapter = new DBAdapter(getActivity());
		
		EditText text = (EditText) getView().findViewById(R.id.crsm_phone);
		String phone = text.getText()+"";
		text = (EditText) getView().findViewById(R.id.crsm_message);
		String message = text.getText()+"";
		boolean success = false;
		try {
			success = adapter.createSMS(phone, message);
        } catch (Exception e) {
           Toast.makeText(getActivity(), getString(R.string.operation_fail), Toast.LENGTH_LONG).show();
       }
       Toast.makeText(getActivity(), success?getString(R.string.operation_done):getString(R.string.operation_fail), Toast.LENGTH_LONG).show();
		
		handleDelete(view);
	}

	public void handleDelete(View view) {
		getActivity().onBackPressed();
		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.crsm_save:
			handleSave(view);
			break;
		/*case R.id.crsm_delete:
			handleDelete(view);
			break;*/
		}

	}

}
