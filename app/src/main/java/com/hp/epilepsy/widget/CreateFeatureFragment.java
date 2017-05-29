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
public class CreateFeatureFragment extends Fragment implements OnClickListener {


	private void setupWidgets(View view) {
		TextView title = (TextView) view.findViewById(R.id.cf_title);
			title.setFocusableInTouchMode(true);
			title.requestFocus();
		view.findViewById(R.id.cf_save).setOnClickListener(this);
		view.findViewById(R.id.cf_delete).setOnClickListener(this);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(
				R.layout.activity_create_feature, container, false);
		setupWidgets(rootView);
		return rootView;
	}

	
	

	public void handleSave(View view) {

		try {
			DBAdapter adapter = new DBAdapter(getActivity());

			EditText text = (EditText) getView().findViewById(R.id.cf_feature_name);
			String featureName = text.getText()+"";
			text = (EditText) getView().findViewById(R.id.cf_feature_desc);
			String featureDesc = text.getText()+"";
			boolean success = false;
			try {
                success = adapter.createFeature(featureName, featureDesc);
            } catch (Exception e) {
                Toast.makeText(getActivity(), getString(R.string.operation_fail), Toast.LENGTH_LONG).show();
            }

			Toast.makeText(getActivity(), success?getString(R.string.operation_done):getString(R.string.operation_fail), Toast.LENGTH_LONG).show();

			handleDelete(view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleDelete(View view) {
		getActivity().onBackPressed();
	}



	

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.cf_save:
			handleSave(view);
			break;
		case R.id.cf_delete:
			handleDelete(view);
			break;
		}

	}

}
