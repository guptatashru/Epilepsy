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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiSpinner extends Spinner implements
		OnMultiChoiceClickListener, OnCancelListener {

	private List<String> items;
	private boolean[] selected;
	private MultiSpinnerListener listener;

	private String defaultText;

	public MultiSpinner(Context context) {
		super(context);
	}

	public MultiSpinner(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
	}

	public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {

		if (which == 0) {
			AlertDialog dia = (AlertDialog) dialog;
			ListView listView = dia.getListView();
			for (int i = 0; i < selected.length; i++) {
				selected[i] = isChecked;
				listView.setItemChecked(i, isChecked);
			}
		} else {
			selected[which] = isChecked;
		}

	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// refresh text on spinner
		ArrayList<String> itemsSelected = new ArrayList<String>();
		for (int i = 1; i < items.size(); i++) {
			if (selected[i] == true) {
				itemsSelected.add(items.get(i));

			}
		}

		String spinnerText = defaultText;
		if (itemsSelected.size() == 1)
			spinnerText = itemsSelected.get(0).toString();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
				R.layout.my_simple_spinner_item, new String[] { spinnerText });
		setAdapter(adapter);
		listener.onItemsSelected(
				Arrays.copyOfRange(selected, 1, selected.length), getId());

	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean performClick() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

		builder.setMultiChoiceItems(
				items.toArray(new CharSequence[items.size()]), selected, this);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.cancel();
					}
				});
		builder.setCancelable(false);
		builder.setOnCancelListener(this);
		Window window = builder.show().getWindow();
		window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		return true;
	}

	public void setItems(List<String> displayedItems, String allText,
			MultiSpinnerListener listener) {
		if (items == null)
			items = new ArrayList<String>();
		this.items.add(getContext().getString(android.R.string.selectAll));
		this.items.addAll(displayedItems);
		this.listener = listener;
		this.defaultText = allText;
		// all selected by default
		selected = new boolean[items.size()];
		for (int i = 0; i < selected.length; i++)
			selected[i] = false;

		// all text on the spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
				R.layout.my_simple_spinner_item, new String[] { allText });
		setAdapter(adapter);

	}

	public void setItems(List<String> displayedItems, boolean[] selectedItems,
			String allText, MultiSpinnerListener listener) {
		if (items == null){
			items = new ArrayList<String>();
		this.items.add(getContext().getString(android.R.string.selectAll));
		this.items.addAll(displayedItems);
		}
		this.listener = listener;
		this.defaultText = allText;
		// all selected by default
		selected = new boolean[items.size()];
		selected[0] = true;
		for (int i = 0; i < selectedItems.length; i++) {
			selected[i+1] = selectedItems[i];
			if (selectedItems[i] == false)
				selected[0] = false;
		}

		// all text on the spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
				R.layout.my_simple_spinner_item, new String[] { allText });
		setAdapter(adapter);

	}

	public interface MultiSpinnerListener {
		void onItemsSelected(boolean[] selected, int viewId);
	}
}
