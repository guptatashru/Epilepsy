package com.hp.epilepsy.widget;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.DBAdapter;

@SuppressLint("SimpleDateFormat")
public class CreateSeizureTypeFragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.activity_create_seizure_type, container, false);
		
		setupWidgets(rootView);
		return rootView;
	}

	private void setupWidgets(View view) {
		try {
			TextView title = (TextView) view.findViewById(R.id.cst_title);
			title.setFocusableInTouchMode(true);
			title.requestFocus();
			view.findViewById(R.id.cst_save).setOnClickListener(this);
			view.findViewById(R.id.cst_delete).setOnClickListener(this);

			EditText tv = (EditText) view.findViewById(R.id.cst_seizure_desc);
			tv.setImeOptions(EditorInfo.IME_ACTION_DONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	

	public void handleSave() {

		try {
			DBAdapter adapter = new DBAdapter(getActivity());

			EditText text = (EditText) getView().findViewById(R.id.cst_seizure_name);
			String seizureName = text.getText()+"";
			text = (EditText) getView().findViewById(R.id.cst_seizure_desc);
			String seizureDesc = text.getText()+"";
			boolean success = false;
			try {
                success = adapter.createSeizureType(seizureName, seizureDesc);
            } catch (Exception e) {
               Toast.makeText(getActivity(), getString(R.string.operation_fail), Toast.LENGTH_LONG).show();
           }
			Toast.makeText(getActivity(), success?getString(R.string.operation_done):getString(R.string.operation_fail), Toast.LENGTH_LONG).show();

			handleDelete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleDelete( ) {
		getActivity().onBackPressed();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.cst_save:
			handleSave();
			break;
		case R.id.cst_delete:
			handleDelete();
			break;
		}

	}

}
