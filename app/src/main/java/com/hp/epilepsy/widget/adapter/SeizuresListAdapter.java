package com.hp.epilepsy.widget.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.model.SeizureDetails;

import java.util.List;

public class SeizuresListAdapter extends BaseAdapter  {

	private List<SeizureDetails> mSeizureList;
	private Context mContext;

	public SeizuresListAdapter(Context context, List<SeizureDetails> list) {
		mSeizureList=list;
		mContext=context;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		try {
			LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(
                    R.layout.list_row_item, parent, false);
			TextView seizureView = (TextView) view.findViewById(R.id.list_ad_seizure_name);
			TextView durationView = (TextView) view.findViewById(R.id.list_ad_duration);
			TextView timeView = (TextView) view.findViewById(R.id.list_ad_seizure_time);

			SeizureDetails seizureDetails = mSeizureList.get(position);
			seizureView.setText(seizureDetails.getSeizureType().getName());
			durationView.setText("Duration: "+seizureDetails.getSeizureDuration().getName());
			timeView.setText(seizureDetails.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	@Override
	public int getCount() {
		return mSeizureList.size();
	}

	@Override
	public Object getItem(int position) {
		return mSeizureList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	

	

}
