package com.hp.epilepsy.widget.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.model.SpinnerItemCheck;

import java.util.List;

public class MultipleSelectSpinnerAdapter extends BaseAdapter {


	private List<SpinnerItemCheck> mItems;
	private Context mContext;

	public MultipleSelectSpinnerAdapter(Context context, List<SpinnerItemCheck> items)
	{
		mItems=items;
		mContext=context;
	}


	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
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
			rowView = inflater.inflate(R.layout.multi_spinner_item, parent, false);
			((TextView)rowView.findViewById(R.id.multi_spinner_name)).setText(mItems.get(position).getItem().getName());
			ImageView mImageView=(ImageView)rowView.findViewById(R.id.multi_info_icon);


			if(mItems.get(position).getItem().getDescription()==null||mItems.get(position).getItem().getDescription().isEmpty())
            {
                mImageView.setVisibility(ImageView.INVISIBLE);
                mImageView.setClickable(false);

            }else {
                mImageView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                AlertDialog.Builder dialogBuilder= new AlertDialog.Builder(mContext);

                dialogBuilder.setTitle(mItems.get(position).getItem().getName());
                dialogBuilder.setMessage(mItems.get(position).getItem().getDescription());

                dialogBuilder.setCancelable(true);

                AlertDialog dialog=dialogBuilder.create();
                dialog.show();
                    }
                });
            }

			CheckBox mCheckBox=(CheckBox) rowView.findViewById(R.id.multi_checkbox);
			mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mItems.get(position).setChecked(isChecked);
                }
            });
			mCheckBox.setChecked(mItems.get(position).isChecked());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowView;
		
	}

}
