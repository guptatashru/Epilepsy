package com.hp.epilepsy.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.AlarmHelper;
import com.hp.epilepsy.utils.AlarmReceiver;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.model.Appointment;
import com.hp.epilepsy.widget.model.AppointmentReminder;
import com.hp.epilepsy.widget.model.NewContactEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@SuppressLint("SimpleDateFormat")
public class NewAppointment extends Fragment implements OnClickListener, IStepScreen {

    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat dateFormatterr;

    static final int APPOINTMENT_REMINDERS_REQUEST = 1;
    static final int APPOINTMENT_LOCATION_REQUEST = 2;

    DBAdapter adapter;
    Spinner GPName;
    EditText AppointmentNotes;
    EditText AppointmentLocationmanually;
    EditText date;
    EditText time;
    Button getLocation;
    Button Reminder;
    Button sendToEmail;
    ImageButton Notes;
    LinearLayout save;
    LinearLayout delete;
    private int AppointmentID;
    private boolean Update = false;
    Appointment AppointmentObj;
    ArrayList<String> ReminderTime = new ArrayList<String>();
    ArrayList<NewContactEntity> contactsList;
    com.hp.epilepsy.widget.model.Location loc;
    int where;
    String pattern = "dd-MM-yyyy hh:mm a";
    String UserNme;
    ArrayAdapter<String> Spinneradapter;

    public NewAppointment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = null;
        rootView = inflater.inflate(R.layout.fragment_appointments, container, false);
        adapter = new DBAdapter(this.getActivity());
        InitializeViews(rootView);
        AppointmentReminderActivity.IsAppointmentUpdated = false;
        AppointmentID = (int) getArguments().getSerializable("AppointmentID");
        where = (int) getArguments().getInt("where");
        UserNme = getArguments().getString("userName");

        GetAppointmentDataFromDB(AppointmentID);
        AppointmentObj = new Appointment();
        fillDropDownLists(rootView);
        onBackeKeyPressed(rootView);
        return rootView;
    }

    public void InitializeViews(View view) {

        loc = new com.hp.epilepsy.widget.model.Location();
        GPName = (Spinner) view.findViewById(R.id.GPName_txt);
        AppointmentNotes = (EditText) view.findViewById(R.id.appointments_notes);
        AppointmentLocationmanually = (EditText) view.findViewById(R.id.location_manual_txt);
        date = (EditText) view.findViewById(R.id.date);
        time = (EditText) view.findViewById(R.id.time);
        getLocation = (Button) view.findViewById(R.id.btnLocation);
        Notes = (ImageButton) view.findViewById(R.id.btnNote);
        Reminder = (Button) view.findViewById(R.id.btnReminder);
        sendToEmail = (Button) view.findViewById(R.id.btnSendToEmail);
        save = (LinearLayout) view.findViewById(R.id.rec_appointment_save);
        delete = (LinearLayout) view.findViewById(R.id.rec_appointment_delete);
        initializeGPNameSpinner(view);
    }

    void initializeGPNameSpinner(View View) {
        contactsList = adapter.getContactsList();

        if (contactsList != null && contactsList.size() > 0) {
            Spinneradapter = new ArrayAdapter<>(this.getActivity(), R.layout.question_spinner_item, getStringContactsNames(contactsList));
            GPName.setAdapter(Spinneradapter);
        } else {
            //showDialogue();

        }
    }

    List<String> getStringContactsNames(ArrayList<NewContactEntity> contactsList) {
        List<String> contactsNames = new ArrayList<>();
        contactsNames.add(" contacts .......");
        for (int i = 0; i < contactsList.size(); i++)
            contactsNames.add(" "+contactsList.get(i).getContactName());
        return contactsNames;
    }

    private void setUpOnClickListeners(View view) {
        if (date != null)
            date.setOnClickListener(this);
        if (time != null)
            time.setOnClickListener(this);
        if (getLocation != null)
            getLocation.setOnClickListener(this);
        if (Reminder != null)
            Reminder.setOnClickListener(this);
        if (sendToEmail != null)
            sendToEmail.setOnClickListener(this);
        if (Notes != null)
            Notes.setOnClickListener(this);
        if (save != null)
            save.setOnClickListener(this);
        if (delete != null)
            delete.setOnClickListener(this);
    }


    public void onBackeKeyPressed(View v) {
        //back to Appointment list when key back pressed
        try {
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {


                        Fragment fragment;
                        FragmentManager fragmentManager;
                        FragmentTransaction fragmentTransaction;
                        switch (where) {
                            case 0:
                                fragment = new MyAppointments();
                                fragmentManager = getActivity().getFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.content_frame, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                break;
                            case 1:
                                fragment = new CalenderEventsListFragment();
                                fragmentManager = getActivity().getFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.calender_list_event_fragment_container, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                              //  Toast.makeText(getActivity(), where + " == this is where", Toast.LENGTH_LONG).show();
                                break;
                            case 3:
                                fragment = new CalendarFragmentV2();
                                fragmentManager = getActivity().getFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.content_frame, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                break;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String validateInputs(String GpName, String date, String time, String notes, String location) {
        if (GpName.length() == 0) {
            return "GP Name can't be empty";
        } else if (date.length() == 0) {
            return "Appointment date can't be empty";
        } else if (time.length() == 0) {
            return "Appointment time can't be empty";
        }
        return "";
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == APPOINTMENT_REMINDERS_REQUEST) {
                AppointmentSelectedReminders(data);
            }
            if (requestCode == APPOINTMENT_LOCATION_REQUEST) {
                convertSelectedLocationToAddress(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void AppointmentSelectedReminders(Intent intent) {

        if (intent != null) {
            Bundle passedData = intent.getExtras();
            ReminderTime = new ArrayList<String>();
            ReminderTime = (ArrayList<String>) passedData.get(AppointmentReminderActivity.INTENT_APPINTMENT_REMNDERD);
            for(int i=0;i<ReminderTime.size();i++)
            {
            }
        }
    }


    public void convertSelectedLocationToAddress(Intent intent) {
        try {
            if (intent != null) {
                Bundle passedData = intent.getExtras();
                loc = (com.hp.epilepsy.widget.model.Location) passedData.getSerializable(MapsActivity.SER_LOCATION_KEY);
                //GetUserAddress(loc.getLatitude(), loc.getLongitude());
            }
        } catch (Exception ex) {
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public Context getContext() {
        return this.getActivity();
    }

    private void fillDropDownLists(View rootView) {
        //TODO Auto-generated method stub
        DBAdapter dbAdapter = new DBAdapter(this.getContext());
        dbAdapter.open();
        setUpOnClickListeners(rootView);
        dbAdapter.close();

    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: SaveAppointment
     * Created By:Shruti and Nikunj
     * Purpose :save new appointments
     * -----------------------------------------------------------------------------
     */

    public void SaveAppointment() {
        if (GPName.getSelectedItem() != null && GPName.getSelectedItem().toString().length() > 0) {

            String errorMessage = validateInputs(GPName.getSelectedItem().toString(), date.getText().toString(), time.getText().toString(), AppointmentNotes.getText().toString(), AppointmentLocationmanually.getText().toString());
            if (errorMessage.length() == 0) {
                try {
                    AppointmentObj.setGpName(GPName.getSelectedItem().toString());
                    AppointmentObj.setAppointmentNotes(AppointmentNotes.getText().toString());
                    AppointmentObj.setAppointmentDate(DateTimeFormats.reverseDateFormatBack(date.getText().toString()));
                    AppointmentObj.setAppointmentTime(time.getText().toString());
                    AppointmentObj.setAppointmentDateTime(date.getText().toString() + " " + time.getText().toString());
                    AppointmentObj.setUserAddress(AppointmentLocationmanually.getText().toString());
                    AppointmentObj.setDelete(false);
                    AppointmentObj.setLatitude(loc.getLatitude());
                    AppointmentObj.setLongitude(loc.getLongitude());
                    long appointment_id = adapter.SaveAppointment(AppointmentObj);

                    //Date AppointmentDate = convertToDateTime(DateTimeFormats.reverseDateFormatBack(AppointmentObj.getAppointmentDateTime()));
                    SimpleDateFormat indianFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                    Date newAppointmentDate = indianFormat.parse(AppointmentObj.getAppointmentDateTime());


                    //this should be for loop to save appointment reminders
                    if (ReminderTime.size() != 0) {

                        SaveAppointmentReminders(newAppointmentDate, (int) appointment_id);

                    }
                    AlarmHelper alarmHelper = new AlarmHelper(getContext());
                    alarmHelper.createAppointmentAlarms((int) appointment_id, AppointmentObj.getGpName());


                    Fragment fragment = new MyAppointments();
                    FragmentManager fragmentManager = getActivity().getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.toString(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "No Contacts", Toast.LENGTH_LONG).show();

            Fragment fragment;
            FragmentManager fragmentManager;
            FragmentTransaction fragmentTransaction;
            fragment = new MyAppointments();
            fragmentManager = getActivity().getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
        }
    }




    public void UpdateAppointment() {
        String errorMessage = validateInputs(GPName.getSelectedItem().toString(), date.getText().toString(), time.getText().toString(), AppointmentNotes.getText().toString(), AppointmentLocationmanually.getText().toString());
        if (errorMessage.length() == 0) {
            try {


                AppointmentObj.setID(AppointmentID);
                AppointmentObj.setGpName(GPName.getSelectedItem().toString());
                AppointmentObj.setAppointmentNotes(AppointmentNotes.getText().toString());
                AppointmentObj.setAppointmentDate(DateTimeFormats.reverseDateFormatBack(date.getText().toString()));
                AppointmentObj.setAppointmentTime(time.getText().toString());
                AppointmentObj.setAppointmentDateTime(date.getText().toString() + " " + time.getText().toString());
                AppointmentObj.setUserAddress(AppointmentLocationmanually.getText().toString());
                AppointmentObj.setDelete(false);
                AppointmentObj.setLatitude(loc.getLatitude());
                AppointmentObj.setLongitude(loc.getLongitude());

                AlarmHelper alarmHelper = new AlarmHelper(getContext());
                long id = adapter.UpdateAppointment(AppointmentObj);
                Date AppointmentDate1 = convertToDateTime((AppointmentObj.getAppointmentDateTime()));

                if (AppointmentReminderActivity.IsAppointmentUpdated) {
                    ///update Medication prescription data by removing the existing data and insert the updated data///////////
                    if (ReminderTime.size() != 0) {
                        alarmHelper.cancelAppointmentAlarms(AppointmentObj.getID(), AppointmentObj.getGpName());
                        adapter.removeAppointmentReminders(AppointmentID);
                        SaveAppointmentReminders(AppointmentDate1, AppointmentID);
                        alarmHelper.createAppointmentAlarms(AppointmentObj.getID(), AppointmentObj.getGpName());
                    }
                }

                AppointmentReminderActivity.IsAppointmentUpdated = false;
                Toast.makeText(getContext(), " The appointment updated successfully", Toast.LENGTH_LONG).show();


                Fragment fragment;
                String tagString;
                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;
                switch (where) {
                    case 0:
                        fragment = new MyAppointments();
                        fragmentManager = getActivity().getFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 1:
                        fragment = new CalenderEventsListFragment();
                        fragmentManager = getActivity().getFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.calender_list_event_fragment_container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                       // Toast.makeText(getActivity(), where + " == this is where", Toast.LENGTH_LONG).show();
                        break;
                }

            } catch (Exception ex) {
                Toast.makeText(getContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }


    public void SaveAppointmentReminders(Date ReminderDate, int AppointmentId) {
        try {
            DBAdapter adapter = new DBAdapter(this.getActivity());
            for (int i = 0; i < ReminderTime.size(); i++) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(ReminderDate);
                if (ReminderTime.get(i).contains("OneHour")) {
                    cal.add(Calendar.HOUR, -1);
                    Date date = cal.getTime();


                    adapter.SaveAppointmentReminders(AppointmentId, date.toString(), "OneHour", dateFormatter.format(date), timeFormatter.format(date));
                }
                if (ReminderTime.get(i).contains("OneDay")) {
                    cal.add(Calendar.DATE, -1);
                    Date date = cal.getTime();
                    adapter.SaveAppointmentReminders(AppointmentId, date.toString(), "OneDay", dateFormatter.format(date), timeFormatter.format(date));
                }
                if (ReminderTime.get(i).contains("OneWeek")) {
                    cal.add(Calendar.DATE, -7);
                    Date date = cal.getTime();
                    adapter.SaveAppointmentReminders(AppointmentId, date.toString(), "OneWeek", dateFormatter.format(date), timeFormatter.format(date));
                }
                if (ReminderTime.get(i).contains("TwoWeeks")) {
                    cal.add(Calendar.DATE, -14);
                    Date date = cal.getTime();
                    adapter.SaveAppointmentReminders(AppointmentId, date.toString(), "TwoWeeks", dateFormatter.format(date), timeFormatter.format(date));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void UpdateAppointmentToDeleted() {
        try {
            DBAdapter adapter = new DBAdapter(this.getActivity());
            Appointment appointment = new Appointment();
            appointment.setID(AppointmentID);
            appointment.setDelete(true);
            adapter.UpdateAppointmentToDeleted(appointment);
            AlarmHelper alarmHelper=new AlarmHelper(getContext());
            alarmHelper.cancelAppointmentAlarms(AppointmentID,GPName.getSelectedItem().toString());
            //return to Appointments list after update...
            Fragment fragment = new MyAppointments();
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void NotificationDialog(String Title,String Message ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Message)
                .setCancelable(false)
                .setTitle(Title);

        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //get appointment data from database by it's ID and fill the view fields by this data
    public void GetAppointmentDataFromDB(int AppointmentId) {
        try {
            if (AppointmentId != -1) {
                Update = true;
                if (where==3){
                    LoginActivity.userName=UserNme;

                    String intentType = getArguments().getString("Type");
                    if(intentType.equals("App"))
                    {
                        String AppointmentMessage=getArguments().getString(AlarmReceiver.APPOINTMENT_MESSAGE);
                        NotificationDialog("Appointment Alert",AppointmentMessage);

                    }
                }

                Appointment appointmentObj = adapter.getAppointmentById(AppointmentId);
                loc.setLatitude(appointmentObj.getLatitude());
                loc.setLongitude(appointmentObj.getLongitude());
                fillFields(appointmentObj.getGpName(), appointmentObj.getAppointmentDate(), appointmentObj.getAppointmentTime(), appointmentObj.getUserAddress(), appointmentObj.getAppointmentNotes());
                //get Appointment reminders
                ArrayList<AppointmentReminder> reminders = adapter.getAppointmentRemindersByIDDuplicate(AppointmentId);
                //get reminder alert{OneHoure|OneWeek|...etc|} to pass it to Appointment reminder activity
                if (reminders.size() != 0) {
                    if (reminders != null) {
                        ReminderTime.clear();

                        for (int j = 0; j < reminders.size(); j++) {
                            ReminderTime.add(reminders.get(j).getReminderAlert());
                        }
                    }
                }



            }else
            {
                dateFormatterr = new SimpleDateFormat(DateTimeFormats.isoDateFormatReverse, Locale.US);
                Calendar c = Calendar.getInstance();
                String todayDate = dateFormatterr.format(c.getTime());
                date.setText(todayDate);

                dateFormatterr = new SimpleDateFormat(DateTimeFormats.isoTimeFormat, Locale.US);
                Calendar cc = Calendar.getInstance();
                String todatime = dateFormatterr.format(cc.getTime());
                time.setText(todatime);
            }
        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    //fill the fields by appointment data
    public void fillFields(String GpName, String Date, String Time, String address, String appointmentNotes) {
        GPName.setSelection(Spinneradapter.getPosition(GpName));
        AppointmentNotes.setText(appointmentNotes);
        AppointmentLocationmanually.setText(address);
        date.setText(DateTimeFormats.reverseDateFormat(Date));
        time.setText(Time);
    }



    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.date:
                showDatePicker(view);
                break;
            case R.id.btnLocation:
                Intent intentt = new Intent(getActivity(), MapsActivity.class);
                intentt.putExtra(MapsActivity.LOCATION_LATITUDE, loc.getLatitude());
                intentt.putExtra(MapsActivity.LOCATION_LONITUDE, loc.getLongitude());
                startActivityForResult(intentt, APPOINTMENT_LOCATION_REQUEST);
                break;
            case R.id.btnReminder:
                Intent intent = new Intent(this.getContext(), AppointmentReminderActivity.class);
                intent.putExtra(AppointmentReminderActivity.APPOINTMENT_REMINDERS_ALERTS, ReminderTime);
                startActivityForResult(intent, APPOINTMENT_REMINDERS_REQUEST);
                break;
            case R.id.btnNote:
                AppointmentNoteAlert();
                break;
            case R.id.btnSendToEmail:
                SendAppointmentViaEmail(loc.getLatitude(), loc.getLongitude());
                break;
            case R.id.time:
                showTimePicker(view);
                break;
            case R.id.rec_appointment_save:
                if (Update) {
                    DBAdapter adapter= new DBAdapter(getActivity());

                    adapter.getUpdateReminderFlag("App", AppointmentID,"0");
                    UpdateAppointment();
                } else {
                    SaveAppointment();
                }
                break;
            case R.id.rec_appointment_delete:
                if (Update) {
                    UpdateAppointmentToDeleted();
                } else {
                    Fragment fragment = new MyAppointments();
                    FragmentManager fragmentManager = getActivity().getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;
        }
    }


    private void SendAppointmentViaEmail(double lat, double lang) {
        String errorMessage = validateInputs(GPName.getSelectedItem().toString(), date.getText().toString(), time.getText().toString(), AppointmentNotes.getText().toString(), AppointmentLocationmanually.getText().toString());
        if (errorMessage.length() == 0) {
            try {
                String bodyFormat = null;
                if (lat != 0.0 && lang != 0.0) {
                    bodyFormat = "Hi" + "\n\n" + "Appointment scheduled on " + date.getText().toString() + " at " + time.getText().toString() + " please find the Address link: " + "\n" + "http://maps.google.com/?q=" + lat + "," + lang+"\n"+"Notes: "+AppointmentNotes.getText().toString();
                } else {
                    bodyFormat = "Hi" + "\n\n" + "Appointment scheduled on " + date.getText().toString() + " at " + time.getText().toString() + " please find the Address "+"\n"+ AppointmentLocationmanually.getText().toString()+"\n"+"Notes: "+AppointmentNotes.getText().toString();
                }
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Appointment Scheduled with " + GPName.getSelectedItem().toString());
                emailIntent.putExtra(Intent.EXTRA_TEXT, bodyFormat);
                this.getActivity().startActivity(Intent.createChooser(emailIntent, "Send Email"));
            } catch (Exception ex) {
                Toast.makeText(this.getActivity(), ex.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }


    public void AppointmentNoteAlert() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getActivity());
            alertDialog.setTitle("Appointment Notes");
            alertDialog.setMessage("This section is to allow the user to enter any questions or thoughts here that you want to discus with your medical professional at the appointment.");

            alertDialog.setNegativeButton("Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
        } catch (Exception ex) {
            Toast.makeText(this.getContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }
    }


    public Date convertToDateTime(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }


    public void onResume() {
        super.onResume();
    }

    public void showDatePicker(View view) {
        try {
            int day, month, year;

            String text = ((EditText) view).getText().toString();
            String text2 = DateTimeFormats.reverseDateFormat(text);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            Date date;
            try {
                date = text2 != null && text2.length() != 0 ? dateFormat.parse(text2)
                        : new Date();
            } catch (ParseException e) {
                date = new Date();
            }
            c.setTime(date);
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            DateRangePicker datePicker = DateRangePicker.newInstance(day, month,
                    year);
            datePicker.show(getFragmentManager(), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void showTimePicker(View view) {
        try {
            int hour, minute;

            String text = ((EditText) view).getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//            SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm a");
            Calendar c = Calendar.getInstance();
            Date date;
            try {
                date = text != null && text.length() != 0 ? dateFormat.parse(text)
                        : new Date();
            } catch (ParseException e) {
                date = new Date();
            }
            c.setTime(date);
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

            TimeRangePicker timePicker = TimeRangePicker.newInstance(hour, minute);
            timePicker.show(getFragmentManager(), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///Date and time and location classes
    public static class DateRangePicker extends DialogFragment implements
            OnDateSetListener {

        private static DateRangePicker newInstance(int day, int month, int year) {
            DateRangePicker dateOfBirthPicker = new DateRangePicker();
            Bundle args = new Bundle();
            args.putInt("day", day);
            args.putInt("month", month);
            args.putInt("year", year);
            dateOfBirthPicker.setArguments(args);
            return dateOfBirthPicker;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int day = getArguments().getInt("day");
            int month = getArguments().getInt("month");
            int year = getArguments().getInt("year");
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this,
                    year, month, day);
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {

            EditText editText = (EditText) getActivity()
                    .findViewById(R.id.date);
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            Date date = new Date(c.getTimeInMillis());
            String formattedDate = dateFormat.format(date);
            editText.setText(formattedDate);
        }
    }

    public static class TimeRangePicker extends DialogFragment implements
            OnTimeSetListener {

        private static TimeRangePicker newInstance(int hour, int minute) {
            TimeRangePicker timeRangePicker = new TimeRangePicker();
            Bundle args = new Bundle();
            args.putInt("hour", hour);
            args.putInt("minute", minute);
            timeRangePicker.setArguments(args);
            return timeRangePicker;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hour = getArguments().getInt("hour");
            int minute = getArguments().getInt("minute");
            TimePickerDialog dialog = new TimePickerDialog(getActivity(), this,
                    hour, minute, false);
            return dialog;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            EditText editText = (EditText) getActivity()
                    .findViewById(R.id.time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm a");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            Date date = new Date(c.getTimeInMillis());
            String formattedDate = dateFormat2.format(date);
            editText.setText(formattedDate);
        }


    }

    void showDialogue() {

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("No Contacts Available To Add Appointment")
                .setTitle("Note");

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Fragment fragment;
                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;
                fragment = new MyAppointments();
                fragmentManager = getActivity().getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.commit();
            }
        });

// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public Date convertToIsoDateformat(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeFormats.deafultDateFormat);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }
}
