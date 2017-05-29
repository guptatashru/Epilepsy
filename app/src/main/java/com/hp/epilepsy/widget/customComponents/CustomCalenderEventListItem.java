package com.hp.epilepsy.widget.customComponents;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.widget.MedicationDairy;
import com.hp.epilepsy.widget.SeizureDetailsFragment;
import com.hp.epilepsy.widget.adapter.CalenderEventsListCustomAdapter;
import com.hp.epilepsy.widget.model.Appointment;
import com.hp.epilepsy.widget.model.EmergencyMedicationEntity;
import com.hp.epilepsy.widget.model.MissedMedication;
import com.hp.epilepsy.widget.model.NewMedication;
import com.hp.epilepsy.widget.model.SeizureDetails;

import java.text.SimpleDateFormat;

/**
 * Created by elmalah on 4/13/2016.
 */
public class CustomCalenderEventListItem extends LinearLayout {

    Context context;
    Object itemEntity;
    int type;

    TextView time_text,title_text;
    public CustomCalenderEventListItem(final Context context, final Object itemEntity, final int type) {
        super(context);
        try {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.addView(inflater.inflate(R.layout.calender_event_list_sub_item, new LinearLayout(context), false));
            this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.FILL_PARENT));

            this.context=context;
            this.itemEntity=itemEntity;
            this.type=type;


            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (type){
                        case CalenderEventsListCustomAdapter.SIEZURE_TYPE_SELECTED:

                            Bundle args = new Bundle();
                            args.putSerializable("seizureDetails", ((SeizureDetails)itemEntity));
                            Fragment fragment = new SeizureDetailsFragment();
                            if (fragment != null) {
                                fragment.setArguments(args);
                                FragmentManager frgManager = ((Activity) context)
                                        .getFragmentManager();
                                frgManager.beginTransaction().replace(R.id.calender_list_event_fragment_container, fragment, ((Activity) context).getString(R.string.seizure_detail_frag_tag)).addToBackStack(((Activity) context).getString(R.string.seizure_detail_frag_tag))
                                        .commit();
                            }

                            //  Toast.makeText(context, ((SeizureDetails)itemEntity).getSeizureType().getName(), Toast.LENGTH_LONG).show();
                            break;
                        case CalenderEventsListCustomAdapter.MISSED_MEDICATIONS_TYPE_SELECTED:

                            navigateToSpecific(CalenderEventsListCustomAdapter.MISSED_MEDICATIONS_TYPE_SELECTED,((MissedMedication)itemEntity).getMedicationId());
                            // Toast.makeText(context, ((MissedMedication)itemEntity).getMedicationName(), Toast.LENGTH_LONG).show();
                            break;
                        case CalenderEventsListCustomAdapter.MEDICATION_TITERATIONS_TYPE_SELECTED:
                            System.out.println("CalenderEventsListCustomAdapter"+((NewMedication)itemEntity).getId());

                            navigateToSpecific(CalenderEventsListCustomAdapter.MEDICATION_TITERATIONS_TYPE_SELECTED,((NewMedication)itemEntity).getId());
                            // Toast.makeText(context, ((NewMedication)itemEntity).getTitrationStartDate(), Toast.LENGTH_LONG).show();
                            break;
                        case CalenderEventsListCustomAdapter.EMERGENCY_MEDICATIONS_TYPE_SELECTED:

                            navigateToSpecific(CalenderEventsListCustomAdapter.EMERGENCY_MEDICATIONS_TYPE_SELECTED,((EmergencyMedicationEntity)itemEntity).getId());
                            // Toast.makeText(context, ((EmergencyMedicationEntity)itemEntity).getEmergencyMedicationName()+"", Toast.LENGTH_LONG).show();
                            break;
                        case CalenderEventsListCustomAdapter.PRESCRIPTION_RENEWALS_TYPE_SELECTED:

                            navigateToSpecific(CalenderEventsListCustomAdapter.PRESCRIPTION_RENEWALS_TYPE_SELECTED,((NewMedication)itemEntity).getId());
                            //  Toast.makeText(context,"PRESCRIPTION_RENEWALS_TYPE_SELECTED", Toast.LENGTH_LONG).show();
                            break;
                        case CalenderEventsListCustomAdapter.APPOINTMENTS_TYPE_SELECTED:

                            navigateToSpecific(CalenderEventsListCustomAdapter.APPOINTMENTS_TYPE_SELECTED,((Appointment)itemEntity).getID());
                            // Toast.makeText(context, ((Appointment)itemEntity).getAppointmentDate(), Toast.LENGTH_LONG).show();
                            break;
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void initiateViews(){

        try {
            time_text=(TextView)findViewById(R.id.time_text);
            title_text=(TextView)findViewById(R.id.title_text);

            switch (type){
                case CalenderEventsListCustomAdapter.SIEZURE_TYPE_SELECTED:

                    time_text.setText(((SeizureDetails)itemEntity).getTime());
                    title_text.setText(((SeizureDetails)itemEntity).getSeizureType().getName());
                    break;
                case CalenderEventsListCustomAdapter.MISSED_MEDICATIONS_TYPE_SELECTED:
                  //  DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                  //  SimpleDateFormat formatter2 = new SimpleDateFormat(DateTimeFormats.isoDateFormat2);
                   // String x = timeFormat.format(formatter2.parse(((MissedMedication)itemEntity).getMedicationReminderTime()));
                    time_text.setText(new SimpleDateFormat("h:mm a").format(DateTimeFormats.convertStringToDateTimeWithDeafultFormat(((MissedMedication)itemEntity).getMedicationReminderTime())));
                 //   time_text.setText(x);
                    title_text.setText(((MissedMedication)itemEntity).getMedicationName());
                    break;
                case CalenderEventsListCustomAdapter.MEDICATION_TITERATIONS_TYPE_SELECTED:

                   time_text.setText(DateTimeFormats.reverseDateFormat(((NewMedication)itemEntity).getTitrationStartDate()));
                    title_text.setText(((NewMedication)itemEntity).getMedication_name());
                    break;
                case CalenderEventsListCustomAdapter.EMERGENCY_MEDICATIONS_TYPE_SELECTED:

                    time_text.setText(DateTimeFormats.reverseDateFormat(((EmergencyMedicationEntity)itemEntity).getExpiryDate()));
                    //time_text.setText(((EmergencyMedicationEntity)itemEntity).getExpiryDate());
                    title_text.setText(((EmergencyMedicationEntity)itemEntity).getEmergencyMedicationName());
                    break;
                case CalenderEventsListCustomAdapter.PRESCRIPTION_RENEWALS_TYPE_SELECTED:

                    time_text.setText(DateTimeFormats.reverseDateFormat(((NewMedication)itemEntity).getPrescriptionRenewalDate()));
                   // time_text.setText(((NewMedication)itemEntity).getPrescriptionRenewalDate());
                    title_text.setText(((NewMedication)itemEntity).getMedication_name());
                    break;
                case CalenderEventsListCustomAdapter.APPOINTMENTS_TYPE_SELECTED:

                    time_text.setText(((Appointment)itemEntity).getAppointmentTime());
                    title_text.setText(((Appointment)itemEntity).getGpName());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void navigateToSpecific(int type , int selected_id){
        try {
            Bundle args = new Bundle();
            String tagString = null;
            Fragment frag = null;
            FragmentTransaction ft;
            switch (type){
                case CalenderEventsListCustomAdapter.SIEZURE_TYPE_SELECTED:

                    break;
                case CalenderEventsListCustomAdapter.MISSED_MEDICATIONS_TYPE_SELECTED:
                    frag = new com.hp.epilepsy.widget.NewMedication();
                    tagString = context.getString(R.string.NEW_MEDICATION_FRAGMENT);
                    args.putSerializable(MedicationDairy.MEDICATION_ID, selected_id);
                    args.putInt("where", 1);
                    break;
                case CalenderEventsListCustomAdapter.MEDICATION_TITERATIONS_TYPE_SELECTED:
                    frag = new com.hp.epilepsy.widget.NewMedication();
                    tagString = context.getString(R.string.NEW_MEDICATION_FRAGMENT);
                    System.out.println("selected id1"+selected_id);
                    args.putSerializable(MedicationDairy.MEDICATION_ID, selected_id);
                    args.putInt("where", 1);
                    break;
                case CalenderEventsListCustomAdapter.EMERGENCY_MEDICATIONS_TYPE_SELECTED:

                    frag = new com.hp.epilepsy.widget.MyEmergencyMedicationsFragment();
                    tagString = context.getString(R.string.MY_EMERGENCY_MEDICATION);
                    args.putSerializable("MedicationID", selected_id);
                    args.putInt("where", 1);
                    break;
                case CalenderEventsListCustomAdapter.PRESCRIPTION_RENEWALS_TYPE_SELECTED:

                    frag = new com.hp.epilepsy.widget.NewMedication();
                    tagString = context.getString(R.string.NEW_MEDICATION_FRAGMENT);
                    System.out.println("selected id"+selected_id);
                    args.putSerializable(MedicationDairy.MEDICATION_ID, selected_id);
                    args.putInt("where", 1);

                    // Toast.makeText(context,"PRESCRIPTION_RENEWALS_TYPE_SELECTED", Toast.LENGTH_LONG).show();
                    break;
                case CalenderEventsListCustomAdapter.APPOINTMENTS_TYPE_SELECTED:

                    args = new Bundle();
                    frag = new com.hp.epilepsy.widget.NewAppointment();
                    tagString = context.getString(R.string.NEW_APPOINTMENT_FRAGMENT);
                    args.putSerializable("AppointmentID",selected_id);
                    args.putInt("where", 1);

                    // Toast.makeText(context, ((Appointment)itemEntity).getAppointmentDate(), Toast.LENGTH_LONG).show();
                    break;
            }

            frag.setArguments(args);
            ft = ((Activity)context).getFragmentManager().beginTransaction();
            ft.replace(R.id.calender_list_event_fragment_container, frag, tagString);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
