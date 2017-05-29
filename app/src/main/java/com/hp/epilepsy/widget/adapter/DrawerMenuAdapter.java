package com.hp.epilepsy.widget.adapter;

/**
 * @author Said Gamal
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.model.DrawerMenuItem;

import java.util.List;

/**
 * The Class DrawerMenuAdapter.
 */
public class DrawerMenuAdapter extends ArrayAdapter<DrawerMenuItem> {

	/** The context. */
	Context context;
	
	/** The drawer item list. */
	List<DrawerMenuItem> drawerItemList;
	
	/** The layout res id. */
	int layoutResID;

	/**
	 * Instantiates a new drawer menu adapter.
	 *
	 * @param context the context
	 * @param layoutResourceID the layout resource id
	 * @param listItems the list items
	 */
	public DrawerMenuAdapter(Context context, int layoutResourceID,
			List<DrawerMenuItem> listItems) {
		super(context, layoutResourceID, listItems);
		this.context = context;
		this.drawerItemList = listItems;
		this.layoutResID = layoutResourceID;

	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;
		try {
			DrawerItemHolder drawerHolder;
			view = convertView;

			if (view == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                drawerHolder = new DrawerItemHolder();

                view = inflater.inflate(layoutResID, parent, false);
                drawerHolder.ItemName = (TextView) view
                        .findViewById(R.id.drawer_itemName);
                view.setTag(drawerHolder);

            } else {
                drawerHolder = (DrawerItemHolder) view.getTag();

            }

			DrawerMenuItem dItem = this.drawerItemList
                    .get(position);
			drawerHolder.ItemName.setText(dItem.getItemName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return view;
	}

	/**
	 * The Class DrawerItemHolder.
	 */
	private static class DrawerItemHolder {
		
		/** The Item name. */
		TextView ItemName;
	}
}