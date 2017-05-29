package com.hp.epilepsy.widget;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.widget.adapter.AppointmentsListViewCustomAdapter;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.model.Appointment;
import com.hp.epilepsy.widget.model.NewContactEntity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAppointments extends Fragment {
    ListView Appointments_Lstview;
    AppointmentsListViewCustomAdapter adapter;
    DBAdapter dbAdapter;
    ArrayList<NewContactEntity> contactsList;
    Button add_btn;
    DBAdapter adapterr;
    String[] GPName = null;
    String[] AppointmentDate = null;
    String[] AppointmentTime = null;
    int[] AppointmentID = null;
    int index = 0;

    public MyAppointments() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        try {
            rootView = null;
            rootView = inflater.inflate(R.layout.fragment_list_of_appointments, container, false);
            Appointments_Lstview = (ListView) rootView.findViewById(R.id.appointments_Listview);
            dbAdapter = new DBAdapter(this.getActivity());
            adapterr = new DBAdapter(this.getActivity());
            adapterr.displayAllUsers();

            GetappointmentsOrderedbyDate();
            adapter = new AppointmentsListViewCustomAdapter(this.getActivity(), GPName, AppointmentDate,AppointmentTime, AppointmentID);
            Appointments_Lstview.setAdapter(adapter);

            add_btn = (Button) rootView.findViewById(R.id.Add_appointment_btn);
           // sortByDate = (Button) rootView.findViewById(R.id.sortbyDate);
            //sortByName = (Button) rootView.findViewById(R.id.sortbyName);
            add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    contactsList = dbAdapter.getContactsList();
                    if (contactsList != null && contactsList.size() > 0) {
                        Bundle args = new Bundle();
                        Fragment frag = new NewAppointment();
                        String tagString = getString(R.string.NEW_APPOINTMENT_FRAGMENT);
                        args.putSerializable("AppointmentID",-1);
                        args.putInt("where",0);
                        frag.setArguments(args);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame,frag,tagString);
                        String backStateName = frag.getClass().getName();
                  /*  ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);*/
                        ft.addToBackStack(backStateName);
                        ft.commit();
                    }else
                    {
                       //TODO here
                        showDialogue();
                    }



            }
        });

/*
            sortByDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sortByDate.setBackgroundColor(getResources().getColor(R.color.yellow));
                    sortByDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);


                    sortByName.setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.arrow_up_float,0);
                    sortByName.setBackgroundColor(getResources().getColor(R.color.grayy));

                    GetappointmentsOrderedbyDate();
                    adapter = new AppointmentsListViewCustomAdapter(getActivity(), GPName, AppointmentDate,AppointmentTime, AppointmentID);
                    Appointments_Lstview.setAdapter(adapter);
                }
            });

            sortByName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sortByName.setBackgroundColor(getResources().getColor(R.color.yellow));
                    sortByName.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);


                    sortByDate.setBackgroundColor(getResources().getColor(R.color.grayy));
                    sortByDate.setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.arrow_up_float,0);

                    GetappointmentsorderedbyName();
                    adapter = new AppointmentsListViewCustomAdapter(getActivity(), GPName, AppointmentDate,AppointmentTime, AppointmentID);
                    Appointments_Lstview.setAdapter(adapter);
                }
            });
*/

            Appointments_Lstview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    //navigate to NewAppointment Page with AppointmentD
                    // to get all data related with this Appointment by it's ID
                    Bundle args = new Bundle();
                    Fragment frag = new NewAppointment();
                    String tagString = getString(R.string.NEW_APPOINTMENT_FRAGMENT);
                    args.putSerializable("AppointmentID", AppointmentID[position]);
                    args.putInt("where", 0);
                    frag.setArguments(args);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, frag, tagString);
                    /*ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);*/
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }


    void showDialogue() {

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("No Contacts Available To Add Appointment")
                .setTitle("Note");

        builder.setPositiveButton("Create new contact", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                // User clicked OK button
                Fragment fragment;
                Bundle args = new Bundle();
                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;
                fragment = new NewContactFragment();
                args.putSerializable("ContactID", -1);
                args.putSerializable("AppointmentContacts", 3);
                fragment.setArguments(args);
                fragmentManager = getActivity().getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.commit();



               /* Bundle args = new Bundle();
                Fragment frag = new NewContactFragment();
                String tagString = getString(R.string.CONTACTS_LIST);
                //the below -1 value mean the user need to add new Medication not update exist one
                args.putSerializable("ContactID", -1);
                frag.setArguments(args);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, frag, tagString);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
*/
            }
        });

// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public void GetappointmentsOrderedbyDate() {
        ArrayList<Appointment> appointmentOBJ = new ArrayList<Appointment>();
        try {
            appointmentOBJ = adapterr.getExistingAppointments();

            GPName = null;
            AppointmentDate = null;
            AppointmentTime=null;
            AppointmentID = null;
            index = 0;
            //Initialize array's sizes
            for (Appointment appointment : appointmentOBJ) {
                index++;
            }

            GPName = new String[index];
            AppointmentDate = new String[index];
            AppointmentTime = new String[index];
            AppointmentID = new int[index];

            for (int i = 0; i < appointmentOBJ.size(); i++) {
                Appointment appointment = appointmentOBJ.get(i);
                AppointmentID[i] = appointment.getID();
                GPName[i] = appointment.getGpName();
                AppointmentDate[i] = DateTimeFormats.reverseDateFormat(appointment.getAppointmentDate().toString());
                AppointmentTime[i] = appointment.getAppointmentTime().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetappointmentsorderedbyName() {
        ArrayList<Appointment> appointmentOBJ = new ArrayList<Appointment>();
        try {
            appointmentOBJ = adapterr.getAppointmentsbyName();

            GPName = null;
            AppointmentDate = null;
            AppointmentTime = null;
            AppointmentID = null;
            index = 0;

            //Initialize array's sizes
            for (Appointment appointment : appointmentOBJ) {
                index++;
            }

            GPName = new String[index];
            AppointmentDate = new String[index];
            AppointmentTime = new String[index];
            AppointmentID = new int[index];

            for (int i = 0; i < appointmentOBJ.size(); i++) {
                Appointment appointment = appointmentOBJ.get(i);
                AppointmentID[i] = appointment.getID();
                GPName[i] = appointment.getGpName();
                AppointmentDate[i] = appointment.getAppointmentDate().toString();
                AppointmentTime[i] = appointment.getAppointmentTime().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
