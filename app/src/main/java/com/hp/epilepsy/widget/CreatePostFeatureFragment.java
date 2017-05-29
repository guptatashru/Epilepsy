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
public class CreatePostFeatureFragment extends Fragment implements OnClickListener {

	public CreatePostFeatureFragment() {

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.activity_create_post_feature, container, false);
		setupWidgets(rootView);
		return rootView;
	}
	
	private void setupWidgets(View view) {
		TextView title = (TextView) view.findViewById(R.id.cpof_title);
			title.setFocusableInTouchMode(true);
			title.requestFocus();
		view.findViewById(R.id.cpof_save).setOnClickListener(this);
		view.findViewById(R.id.cpof_delete).setOnClickListener(this);
	}


	public void handleSave(View view) {

		try {
			DBAdapter adapter = new DBAdapter(getActivity());

			EditText text = (EditText) getView().findViewById(R.id.cpof_post_feature_name);
			String postFeatureName = text.getText()+"";
			text = (EditText) getView().findViewById(R.id.cpof_post_feature_desc);
			String postFeatureDesc = text.getText()+"";
			boolean success = false;
			try {
                success = adapter.createPostFeature(postFeatureName, postFeatureDesc);
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
		case R.id.cpof_save:
			handleSave(view);
			break;
		case R.id.cpof_delete:
			handleDelete(view);
			break;
		}

	}

}
