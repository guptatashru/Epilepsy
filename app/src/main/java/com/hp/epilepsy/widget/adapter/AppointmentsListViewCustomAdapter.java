package com.hp.epilepsy.widget.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hp.epilepsy.R;

/**
 * Created by Mohsen Mahmoud on 24/2/2016.
 */
public class AppointmentsListViewCustomAdapter extends BaseAdapter {
    public Activity context;
    public LayoutInflater inflater;
    String[] GPNames;
    String[] AppointmentDate;
    String[] AppointmentTime;
    int[] AppointmentID;

    public AppointmentsListViewCustomAdapter(Activity context, String[] GpNames, String[] AppointmetDates, String[] AppointmetTimes, int[] AppointmentID) {
        super();
        this.context = context;
        this.GPNames = GpNames;
        this.AppointmentDate = AppointmetDates;
        this.AppointmentTime=AppointmetTimes;
        this.AppointmentID = AppointmentID;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return GPNames.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
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
            convertView = inflater.inflate(R.layout.appointment_row_layout, null);
            holder.Gp_name = (TextView) convertView.findViewById(R.id.Gp_name);
            holder.appointment_date = (TextView) convertView.findViewById(R.id.appointment_date);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
            holder.Gp_name.setText(GPNames[position]);
            holder.appointment_date.setText("Appointment Date: " + AppointmentDate[position]+" "+AppointmentTime[position]);
        } catch (Exception ex) {
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView Gp_name;
        TextView appointment_date;
    }
}
