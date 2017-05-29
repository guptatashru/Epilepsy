package com.hp.epilepsy.widget.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.FileUtils;
import com.hp.epilepsy.widget.model.Event;

import java.text.SimpleDateFormat;
import java.util.List;


public class CalenderEventsListAdapter extends BaseAdapter {
    public Activity context;
    public LayoutInflater inflater;
    List<Event> Events;

    public CalenderEventsListAdapter(Activity context, List<Event> mutableEvents) {
        super();
        this.context = context;
        this.Events = mutableEvents;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return Events.size();
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
                convertView = inflater.inflate(R.layout.calender_event_list_item, null);
                holder.reminder_handling_icon = (ImageView) convertView.findViewById(R.id.reminder_handling_icon);
                holder.reminder_date = (TextView) convertView.findViewById(R.id.reminder_date);
                holder.reminder_handling_status = (TextView) convertView.findViewById(R.id.reminder_handling_status);
                holder.reasons_text = (TextView) convertView.findViewById(R.id.reasons_text);
                holder.reasonsLayout = (LinearLayout) convertView.findViewById(R.id.reasons_layout);
                holder.typeColorLinear = (ImageView) convertView.findViewById(R.id.typeColorLinear);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();
            switch (Events.get(position).getType()) {
                case FileUtils.EMERGENCY_MEDICATION_TYPE:
                    handleEmergencyMedication(Events.get(position), holder);
                    break;
                case FileUtils.MEDICATION_TYPE:
                    handleMedication(Events.get(position), holder);
                    break;
                case FileUtils.SIEZURE_TYPE:
                    handleSiezure(Events.get(position), holder);
                    break;
                case FileUtils.APPOINTMENT_TYPE:
                    handleAppointment(Events.get(position), holder);
                    break;
                case FileUtils.PRESCRIPTION_RENEWAL_TYPE:
                    handleMedicationPrescriptionRenewal(Events.get(position), holder);
                    break;
                case 5:
                    handleNoEvents(holder);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    void handleNoEvents(ViewHolder holder) {

        try {
            holder.reasonsLayout.setVisibility(View.GONE);
            holder.reminder_handling_icon.setVisibility(View.INVISIBLE);
            holder.reminder_date.setText("No Events Exist : ");
            holder.reminder_handling_status.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void handleEmergencyMedication(Event event, ViewHolder holder) {

        try {
            holder.typeColorLinear.setBackgroundResource(R.color.bg_blue);
            if (event.isReminder()) {
                if (event.getMedicationHandlStatus() == 2 || event.getMedicationHandlStatus() == 3)
                    holder.reasonsLayout.setVisibility(View.VISIBLE);
                else
                    holder.reasonsLayout.setVisibility(View.GONE);
                holder.reminder_date.setText("Reminder : " + event.getTitle());
                holder.reminder_handling_status.setText(getHandlingStatus(event.getMedicationHandlStatus()));
                holder.reasons_text.setText(event.getMedicationHandleReasons());
            } else {
                holder.reasonsLayout.setVisibility(View.GONE);
                holder.reminder_handling_icon.setVisibility(View.INVISIBLE);
                holder.reminder_date.setText("E.M : " + event.getTitle());
                holder.reminder_handling_status.setText(event.getEventTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void handleMedicationPrescriptionRenewal(Event event, ViewHolder holder) {

        try {
            holder.typeColorLinear.setBackgroundResource(R.color.purple);
            holder.reasonsLayout.setVisibility(View.GONE);
            holder.reminder_handling_icon.setVisibility(View.INVISIBLE);
            holder.reminder_date.setText("Prescription Renewal: " + event.getTitle());
            holder.reminder_handling_status.setText(event.getEventTime());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: handleMedication
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: handle medication for calendar view
     * -----------------------------------------------------------------------------
     */
    void handleMedication(Event event, ViewHolder holder) {

        try {
            holder.typeColorLinear.setBackgroundResource(R.color.orange_switch);

            if (event.isReminder()) {
                if (event.getMedicationHandlStatus() == 2 || event.getMedicationHandlStatus() == 3) {
                    holder.reasonsLayout.setVisibility(View.GONE);
                    holder.reminder_handling_icon.setVisibility(View.GONE);
                    holder.reminder_date.setText("Missed Medication : " + event.getTitle() + "\n\nTime          : " + event.getEventTime());
                    holder.reminder_handling_status.setVisibility(View.GONE);
                    holder.reasons_text.setText(event.getMedicationHandleReasons());
                } /*else {
                    holder.reasonsLayout.setVisibility(View.GONE);
                    holder.reminder_date.setText("Reminder : " + event.getTitle() + "\n\nTime          : " + new SimpleDateFormat("h:mm a").format(event.getEventDate()));
//                holder.reminder_handling_status.setText(getHandlingStatus(event.getMedicationHandlStatus()));
                    holder.reminder_handling_status.setText(new SimpleDateFormat("h:mm a").format(event.getEventDate()));
                    holder.reasons_text.setText(event.getMedicationHandleReasons());
                }*/
            } /*else {
                holder.reasonsLayout.setVisibility(View.GONE);
                holder.reminder_handling_icon.setVisibility(View.GONE);
                holder.reminder_date.setText("Med: " + event.getTitle());
                if (event.getEventDate() != null)
                    holder.reminder_handling_status.setText(new SimpleDateFormat(DateTimeFormats.isoDateFormatReverse).format(event.getEventDate()) + "");
                else
                    holder.reminder_handling_status.setText("00:00");
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void handleSiezure(Event event, ViewHolder holder) {

        try {
            holder.typeColorLinear.setBackgroundColor(Color.RED);

            if (event.isReminder()) {
                if (event.getMedicationHandlStatus() == 2 || event.getMedicationHandlStatus() == 3)
                    holder.reasonsLayout.setVisibility(View.VISIBLE);
                else
                    holder.reasonsLayout.setVisibility(View.GONE);

                holder.reminder_date.setText("Reminder : " + new SimpleDateFormat("h:mm a").format(event.getEventTime()) + "");
                holder.reminder_handling_status.setText(getHandlingStatus(event.getMedicationHandlStatus()));
                holder.reasons_text.setText(event.getMedicationHandleReasons());
            } else {
                holder.reasonsLayout.setVisibility(View.GONE);
                holder.reminder_handling_icon.setVisibility(View.GONE);
                holder.reminder_date.setText("Siezure: " + event.getTitle());
                holder.reminder_handling_status.setText(event.getEventTime() + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void handleAppointment(Event event, ViewHolder holder) {

        try {
            holder.typeColorLinear.setBackgroundResource(R.color.green);

            if (event.isReminder()) {
                if (event.getMedicationHandlStatus() == 2 || event.getMedicationHandlStatus() == 3)
                    holder.reasonsLayout.setVisibility(View.VISIBLE);
                else
                    holder.reasonsLayout.setVisibility(View.GONE);

                holder.reminder_date.setText("Reminder : " + new SimpleDateFormat("h:mm a").format(event.getEventDate()) + "");
                holder.reminder_handling_status.setText(getHandlingStatus(event.getMedicationHandlStatus()));
                holder.reasons_text.setText(event.getMedicationHandleReasons());
            } else {

                holder.reasonsLayout.setVisibility(View.GONE);
                holder.reminder_handling_icon.setVisibility(View.GONE);
                holder.reminder_date.setText("Appointment : " + event.getTitle());
                holder.reminder_handling_status.setText(event.getEventTime().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    String getHandlingStatus(int status) {
        String statusName = "";

        switch (status) {
            case 0:
                statusName = "Upcoming";
                break;
            case 1:
                statusName = "taken";
                break;
            case 2:
                statusName = "missed";
                break;
            case 3:
                statusName = "Taken not Registered";
                break;
        }
        return statusName;
    }

    public static class ViewHolder {
        TextView reminder_date;
        TextView reminder_handling_status;
        TextView reasons_text;
        LinearLayout reasonsLayout;
        ImageView typeColorLinear;
        ImageView reminder_handling_icon;

    }
}
