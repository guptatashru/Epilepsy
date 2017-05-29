package com.hp.epilepsy.widget.model;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.MultipleSelectSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public class InfoMultiSpinner extends Spinner implements
OnMultiChoiceClickListener, OnCancelListener {

	private List<SpinnerItemCheck> mItems;
	private MultipleSelectSpinnerAdapter adapter;
	private String defaultText;
	private ArrayAdapter<String> holdAdapter;
	
	public InfoMultiSpinner(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public InfoMultiSpinner(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
	}

	public InfoMultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	
	public void setSpinnerItems(List<ISpinnerItem> displayedItems,String text) {
		
		defaultText=text;
		if (mItems == null)
		{
			mItems = new ArrayList<SpinnerItemCheck>();			
			for (ISpinnerItem iSpinnerItem : displayedItems) {
				SpinnerItemCheck mSpinnerItemCheck=new SpinnerItemCheck();
			mSpinnerItemCheck.setItem(iSpinnerItem);
			mSpinnerItemCheck.setChecked(false);
			mItems.add(mSpinnerItemCheck);
			}
		}
		
		holdAdapter = new ArrayAdapter<String>(getContext(),
				R.layout.my_simple_spinner_item, new String[] { defaultText });
		setAdapter(holdAdapter);
		
		adapter = new MultipleSelectSpinnerAdapter(this.getContext(),mItems);
	}
	
//	private List<ISpinnerItem> getSpinnerItems()
//	{
//		List<ISpinnerItem> mList=new ArrayList<ISpinnerItem>();
//		for (SpinnerItemCheck lItem : mItems) {
//			mList.add(lItem.getItem());
//		}
//		return mList;
//	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) 
	{
		if (which == 0) {
			AlertDialog dia = (AlertDialog) dialog;
			ListView listView = dia.getListView();
			for (int i = 0; i < mItems.size(); i++) {
				mItems.get(which).setChecked(isChecked);
				listView.setItemChecked(i, isChecked);
			}
		} else {
			mItems.get(which).setChecked(isChecked);
		}

	}

	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean performClick() {
		try {
			
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

		builder.setAdapter(adapter, null);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setAdapter(holdAdapter);
					}
				});
		builder.setCancelable(true);
		builder.setOnCancelListener(this);
		Window window = builder.show().getWindow();
		window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		AlertDialog alertDialog=builder.create();
		
		ListView listView= alertDialog.getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		} catch (Exception e) {
			e.toString();
		}
		return true;
	}

	@Override
	public void onCancel(DialogInterface dialog) {

		setAdapter(holdAdapter);
		
	}
	
	public List<?> getSelectedItems()
	{
		List<ISpinnerItem> mSpinnerItems=new ArrayList<ISpinnerItem>();
		for (SpinnerItemCheck i : mItems) {
			if(i.isChecked())
			{

			mSpinnerItems.add(i.getItem());

			}
		}

		return mSpinnerItems;
		
	}
	
	public String getCommaSeperatedValues()
	{
		StringBuffer buffer = new StringBuffer();
		String prefix="";
		for (SpinnerItemCheck i : mItems) {
			
			if(i.isChecked())
			{
				buffer.append(prefix);
				prefix=", ";
				buffer.append(i.getItem().getName());
			}
			
		}

		buffer.insert(0, '\'');	
		buffer.append('\'');
		return buffer.toString();
	}

}


