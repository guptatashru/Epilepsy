package com.hp.epilepsy.widget;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.SeizuresListAdapter;
import com.hp.epilepsy.widget.model.SeizureDetails;

import java.util.Collections;
import java.util.List;

public class SeizuresList extends Fragment implements IStepScreen{

	private List<SeizureDetails> seizures;

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		seizures=((List<SeizureDetails>) getArguments().getSerializable("seizureDetails"));
		View rootView = inflater.inflate(R.layout.activity_seizure_list, container,
				false);
		if(seizures.size()>0)
		{
			((TextView)rootView.findViewById(R.id.asl_day_of_week)).setText(seizures.get(1).getDayOfWeek());
			((TextView)rootView.findViewById(R.id.asl_day_month)).setText(seizures.get(1).getDayOfMonth());
		}

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		Collections.sort(seizures);
		ListView listView=(ListView)getView().findViewById(R.id.asl_seizure_list);
		SeizuresListAdapter adapter = new SeizuresListAdapter(getActivity(),seizures);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				SeizureDetails seizureDetails =seizures.get(position);
				Bundle args = new Bundle();
				args.putSerializable("seizureDetails", seizureDetails);
				Fragment fragment = new SeizureDetailsFragment();
				if (fragment != null) {
					fragment.setArguments(args);
					FragmentManager frgManager = getFragmentManager();
					frgManager.beginTransaction().replace(R.id.content_frame, fragment,getActivity().getString(R.string.seizure_detail_frag_tag)).addToBackStack(getString(R.string.seizure_detail_frag_tag))
					.commit();

				}}});

	}
}







