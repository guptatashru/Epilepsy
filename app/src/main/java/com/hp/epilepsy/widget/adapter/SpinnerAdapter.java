package com.hp.epilepsy.widget.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.model.ISpinnerItem;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {

	List<ISpinnerItem> mSpinnerItems;
	Context mContext;
	
	public SpinnerAdapter(List<ISpinnerItem> items,Context context) {
		mSpinnerItems=items;
		mContext=context;
	}
	
	@Override
	public int getCount() {
		return mSpinnerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mSpinnerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView=null;

		try {
			LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.spinner_info_item, parent, false);
			((TextView)rowView.findViewById(R.id.spinner_name)).setText(mSpinnerItems.get(position).getName());
			ImageView mImageView=(ImageView)rowView.findViewById(R.id.info_icon);

			if(mSpinnerItems.get(position).getDescription()==null||mSpinnerItems.get(position).getDescription().isEmpty())
            {
                mImageView.setVisibility(ImageView.INVISIBLE);
                mImageView.setClickable(false);
            }


			mImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

            AlertDialog.Builder dialogBuilder= new AlertDialog.Builder(mContext);

            dialogBuilder.setTitle(mSpinnerItems.get(position).getName());
            dialogBuilder.setMessage(mSpinnerItems.get(position).getDescription());

            dialogBuilder.setCancelable(true);

            AlertDialog dialog=dialogBuilder.create();
            dialog.show();
                }
            });
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rowView;
	}

}
