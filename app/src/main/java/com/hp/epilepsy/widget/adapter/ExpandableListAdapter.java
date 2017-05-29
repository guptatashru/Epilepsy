package com.hp.epilepsy.widget.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.CreateFeatureFragment;
import com.hp.epilepsy.widget.CreatePostFeatureFragment;
import com.hp.epilepsy.widget.CreatePreFeatureFragment;
import com.hp.epilepsy.widget.CustomPopupWindow;
import com.hp.epilepsy.widget.HomeView;
import com.hp.epilepsy.widget.MedicationDairy;
import com.hp.epilepsy.widget.MyEmergencyMedicationsFragment;

import java.util.HashMap;
import java.util.List;

/**
 * Created by elmalah on 3/2/2016.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    PopupWindow popupWindow;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData, PopupWindow popupWindow) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.popupWindow = popupWindow;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        try {
            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.right_hand_sub_menu_list_item, null);
            }
            /************************** Handle Select option from subMenu *********************************/
            final TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            txtListChild.setText(childText);
            txtListChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = null;
                    Bundle args = new Bundle();
                    String tagString = "";
                    if (childText.equalsIgnoreCase(_context.getString(R.string.create_pre_feature))) {
                        fragment = new CreatePreFeatureFragment();
                        tagString = _context.getString(R.string.create_pre_feature_main_frag_tag);
                        popupWindow.dismiss();
                    } else if (childText.equalsIgnoreCase(_context.getString(R.string.create_During_Feature))) {
                        fragment = new CreateFeatureFragment();
                        tagString = _context.getString(R.string.create_feature_frag_tag);
                        popupWindow.dismiss();
                    } else if (childText.equalsIgnoreCase(_context.getString(R.string.create_post_feature))) {
                        fragment = new CreatePostFeatureFragment();
                        tagString = _context.getString(R.string.create_post_feature_tag);
                        popupWindow.dismiss();
                    } else if (childText.equalsIgnoreCase(_context.getString(R.string.create_Daily))) {
                        fragment = new MedicationDairy();
                        tagString = _context.getString(R.string.record_siezure_detail_frag_tag);
                        popupWindow.dismiss();
                    } else if (childText.equalsIgnoreCase(_context.getString(R.string.create_Emergency))) {
                        fragment = new MyEmergencyMedicationsFragment();
                        tagString = _context.getString(R.string.MY_EMERGENCY_MEDICATION);
                        args.putSerializable("MedicationID", -1);
                        popupWindow.dismiss();
                    } else {
                    }

                    if (fragment != null) {
                        fragment.setArguments(args);
                        FragmentManager frgManager = ((HomeView) _context).getFragmentManager();

                        frgManager.beginTransaction().add(R.id.content_frame, fragment, tagString).addToBackStack(tagString).commit();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
/************************************************************************************************/
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        try {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.right_hand_list_group, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}