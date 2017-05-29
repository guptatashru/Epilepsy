package com.hp.epilepsy.widget.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.widget.customComponents.CustomCalenderEventListItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by elmalah on 12/2/2015.
 */
public class CalenderEventsListCustomAdapter extends BaseAdapter {
    Activity context;
    LayoutInflater inflater;
    Map<String, HashMap<String, List>> datacollection;
    String[] mKeys;

    public static final int SIEZURE_TYPE_SELECTED = 1;
    public static final int MISSED_MEDICATIONS_TYPE_SELECTED = 2;
    public static final int MEDICATION_TITERATIONS_TYPE_SELECTED = 3;
    public static final int EMERGENCY_MEDICATIONS_TYPE_SELECTED = 4;
    public static final int APPOINTMENTS_TYPE_SELECTED = 5;
    public static final int PRESCRIPTION_RENEWALS_TYPE_SELECTED = 6;

    public CalenderEventsListCustomAdapter(Activity context, Map<String, HashMap<String, List>> datacollection) {
        super();
        this.context = context;
        this.datacollection = datacollection;
        mKeys = datacollection.keySet().toArray(new String[datacollection.size()]);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return datacollection.size();
    }

    @Override
    public Object getItem(int position) {
        return datacollection.get(mKeys[position]);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.calender_event_list_group, null);
                holder.dateText = (TextView) convertView.findViewById(R.id.date_title);
                holder.siezure_container = (LinearLayout) convertView.findViewById(R.id.siezure_container);
                holder.missed_medications_container = (LinearLayout) convertView.findViewById(R.id.missed_medications_container);
                holder.medication_titrations_container = (LinearLayout) convertView.findViewById(R.id.medication_titerations_container);
                holder.prescription_renewals_container = (LinearLayout) convertView.findViewById(R.id.prescription_renewels_container);
                holder.emergency_medications_container = (LinearLayout) convertView.findViewById(R.id.emergency_medications_container);
                holder.appointments_container = (LinearLayout) convertView.findViewById(R.id.appointments_container);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.siezure_container.removeAllViews();
            holder.missed_medications_container.removeAllViews();
            holder.medication_titrations_container.removeAllViews();
            holder.prescription_renewals_container.removeAllViews();
            holder.emergency_medications_container.removeAllViews();
            holder.appointments_container.removeAllViews();

            SimpleDateFormat format = new SimpleDateFormat(DateTimeFormats.isoDateFormat);
            Date now = null;
            try {
                now = format.parse(mKeys[position]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DateFormat sdf = new SimpleDateFormat("EEE dd MMM yyyy");
            Log.e("Test HashMap", now + "  == " + getItem(position).toString());
            holder.dateText.setText(sdf.format(now) + "");


            if(position==2)
            {
                Log.e("Test HashMap", now + "  == " + position);
            }



             if (datacollection.get(mKeys[position]).get(DBAdapter.MISSED_MEDICATIONS_TYPE) != null &&
                    datacollection.get(mKeys[position]).get(DBAdapter.MISSED_MEDICATIONS_TYPE).size() > 0) {
                drawSelectedTypeListElements(holder, MISSED_MEDICATIONS_TYPE_SELECTED, datacollection.get(mKeys[position]).get(DBAdapter.MISSED_MEDICATIONS_TYPE));
            }
             if (datacollection.get(mKeys[position]).get(DBAdapter.SIEZURE_TYPE) != null &&
                    datacollection.get(mKeys[position]).get(DBAdapter.SIEZURE_TYPE).size() > 0) {

                drawSelectedTypeListElements(holder, SIEZURE_TYPE_SELECTED, datacollection.get(mKeys[position]).get(DBAdapter.SIEZURE_TYPE));

            }
            if (datacollection.get(mKeys[position]).get(DBAdapter.MEDICATION_TITERATIONS_TYPE) != null &&
                    datacollection.get(mKeys[position]).get(DBAdapter.MEDICATION_TITERATIONS_TYPE).size() > 0) {
                drawSelectedTypeListElements(holder, MEDICATION_TITERATIONS_TYPE_SELECTED, datacollection.get(mKeys[position]).get(DBAdapter.MEDICATION_TITERATIONS_TYPE));

            }
            if (datacollection.get(mKeys[position]).get(DBAdapter.APPOINTMENTS_TYPE) != null &&
                    datacollection.get(mKeys[position]).get(DBAdapter.APPOINTMENTS_TYPE).size() > 0) {
                drawSelectedTypeListElements(holder, APPOINTMENTS_TYPE_SELECTED, datacollection.get(mKeys[position]).get(DBAdapter.APPOINTMENTS_TYPE));

            }
            if (datacollection.get(mKeys[position]).get(DBAdapter.EMERGENCY_MEDICATIONS_TYPE) != null &&
                    datacollection.get(mKeys[position]).get(DBAdapter.EMERGENCY_MEDICATIONS_TYPE).size() > 0) {
                drawSelectedTypeListElements(holder, EMERGENCY_MEDICATIONS_TYPE_SELECTED, datacollection.get(mKeys[position]).get(DBAdapter.EMERGENCY_MEDICATIONS_TYPE));

            }
            if (datacollection.get(mKeys[position]).get(DBAdapter.PRESCRIPTION_RENEWALS_TYPE) != null &&
                    datacollection.get(mKeys[position]).get(DBAdapter.PRESCRIPTION_RENEWALS_TYPE).size() > 0) {
                drawSelectedTypeListElements(holder, PRESCRIPTION_RENEWALS_TYPE_SELECTED, datacollection.get(mKeys[position]).get(DBAdapter.PRESCRIPTION_RENEWALS_TYPE));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public static class ViewHolder {

        TextView dateText;
        LinearLayout siezure_container, missed_medications_container,
                medication_titrations_container, prescription_renewals_container,
                emergency_medications_container, appointments_container;

    }

    void drawSelectedTypeListElements(ViewHolder holder, int type, List<Object> list) {

        try {
            switch (type) {
                case SIEZURE_TYPE_SELECTED:
                    holder.siezure_container.setVisibility(View.VISIBLE);
                    handleSiezureListItems(holder.siezure_container, list, type);
                    break;
                case MISSED_MEDICATIONS_TYPE_SELECTED:
                    holder.missed_medications_container.setVisibility(View.VISIBLE);
                    handleSiezureListItems(holder.missed_medications_container, list, type);
                    break;
                case MEDICATION_TITERATIONS_TYPE_SELECTED:
                    holder.medication_titrations_container.setVisibility(View.VISIBLE);

                    handleSiezureListItems(holder.medication_titrations_container, list, type);
                    break;
                case APPOINTMENTS_TYPE_SELECTED:
                    holder.appointments_container.setVisibility(View.VISIBLE);
                    handleSiezureListItems(holder.appointments_container, list, type);
                    break;
                case EMERGENCY_MEDICATIONS_TYPE_SELECTED:
                    holder.emergency_medications_container.setVisibility(View.VISIBLE);
                    handleSiezureListItems(holder.emergency_medications_container, list, type);
                    break;
                case PRESCRIPTION_RENEWALS_TYPE_SELECTED:
                    holder.prescription_renewals_container.setVisibility(View.VISIBLE);
                    handleSiezureListItems(holder.prescription_renewals_container, list, type);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void handleSiezureListItems(View container, List<Object> list, int type) {
        try {
            for (int i = 0; i < list.size(); i++) {
                CustomCalenderEventListItem customCalenderEventListItem = new CustomCalenderEventListItem(container.getContext(), list.get(i), type);
                customCalenderEventListItem.initiateViews();
                ((LinearLayout) container).addView(customCalenderEventListItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isEnabled(int position) {

        return false;
    }

}

