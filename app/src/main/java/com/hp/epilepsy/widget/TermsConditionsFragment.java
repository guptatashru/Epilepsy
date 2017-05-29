package com.hp.epilepsy.widget;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hp.epilepsy.R;

public class TermsConditionsFragment extends Fragment implements IStepScreen {



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.terms_conditions, container,
				false);
		return rootView;

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	
		TextView textView = (TextView) getView().findViewById(R.id.content);
		textView.setText(Html.fromHtml(getActivity().getString(
				R.string.terms_conditions)));

		CheckBox accept = (CheckBox) getActivity().findViewById(R.id.accept);
		accept.setVisibility(View.GONE);
	}

	
}
