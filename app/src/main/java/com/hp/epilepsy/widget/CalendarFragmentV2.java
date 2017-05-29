package com.hp.epilepsy.widget;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.CalendarDayEvent;
import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.utils.FileUtils;
import com.hp.epilepsy.widget.adapter.CalenderEventsListAdapter;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.model.Appointment;
import com.hp.epilepsy.widget.model.EmergencyMedicationEntity;
import com.hp.epilepsy.widget.model.Event;
import com.hp.epilepsy.widget.model.MedicationReminders;
import com.hp.epilepsy.widget.model.MissedMedication;
import com.hp.epilepsy.widget.model.NewMedication;
import com.hp.epilepsy.widget.model.PrescriptionReminders;
import com.hp.epilepsy.widget.model.SeizureDetails;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//import com.alamkanak.weekview.WeekView;

public class CalendarFragmentV2 extends Fragment implements IStepScreen {
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private Map<Date, List<Event>> upcomingEvents = new HashMap<>();
    Button showPreviousMonthBut;
    Button MonthView;
    Button WeekView;
    Button showNextMonthBut;
    TextView currentmonth;
    CompactCalendarView compactCalendarView;
    List<Event> mutableEvents;
    ListView bookingsListView;
    ArrayAdapter adapter;
    Date SelectedDate = new Date();
    ImageButton calenderListEvents;
    SimpleDateFormat dateFormatter;
    CalenderEventsListAdapter calenderEventsListAdapter;


    public CalendarFragmentV2() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_calendarv, container, false);
        InitializeViews(rootView);

        setUpOnClickListeners();
        try {
            AddMissedMedicationRemindersToCalendar(compactCalendarView);
            AddPrescriptionRenewalToCalendar(compactCalendarView);
            AddAppointmentsToCalendar(compactCalendarView);
           // AddMedicationRemindersToCalendar(compactCalendarView);
            AddSeizuresToCalendar(compactCalendarView);
            AddEmergencyMedicationsToCalendar(compactCalendarView);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rootView;
    }


    public void InitializeViews(View rootView) {

        try {
            mutableEvents = new ArrayList<>();
            bookingsListView = (ListView) rootView.findViewById(R.id.bookings_listview);
            showPreviousMonthBut = (Button) rootView.findViewById(R.id.prev_button);
            MonthView = (Button) rootView.findViewById(R.id.Month_View);
            WeekView = (Button) rootView.findViewById(R.id.Week_View);
            showNextMonthBut = (Button) rootView.findViewById(R.id.next_button);
            currentmonth = (TextView) rootView.findViewById(R.id.currentMonthh);
            compactCalendarView = (CompactCalendarView) rootView.findViewById(R.id.compactcalendar_view);
            compactCalendarView.drawSmallIndicatorForEvents(true);
            compactCalendarView.setCurrentDayBackgroundColor(Color.YELLOW);
            compactCalendarView.setShouldShowMondayAsFirstDay(false);
            compactCalendarView.setCurrentSelectedDayBackgroundColor(getResources().getColor(R.color.blue_border));
            compactCalendarView.invalidate();
            currentmonth.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
            upcomingEvents.clear();


            calenderListEvents = (ImageButton) rootView.findViewById(R.id.calenderListEvents);
            calenderListEvents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), CalenderEventsListActivity.class));
                }
            });

            bookingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Event item = (Event) mutableEvents.get(position);
                    if (item.getType() == FileUtils.SIEZURE_TYPE) {
                        String time = item.getEventTime();
                        //load seizure details from the database with it's data
                        GetSeizureDetails(SelectedDate, time);
                    }
                }
            });
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }


    public void setUpOnClickListeners() {
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                SelectedDate = dateClicked;
                mutableEvents = upcomingEvents.get(dateClicked);
                if (mutableEvents != null) {
                    calenderEventsListAdapter = new CalenderEventsListAdapter(getActivity(), mutableEvents);
                    bookingsListView.setAdapter(calenderEventsListAdapter);
                    calenderEventsListAdapter.notifyDataSetChanged();
                } else {
                    mutableEvents = new ArrayList<Event>();
                    Event e = new Event();
                    e.setType(5);
                    mutableEvents.add(e);
                    calenderEventsListAdapter = new CalenderEventsListAdapter(getActivity(), mutableEvents);
                    bookingsListView.setAdapter(calenderEventsListAdapter);
                    calenderEventsListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                currentmonth.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
            }
        });

        MonthView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.setVisibility(View.VISIBLE);
                bookingsListView.setVisibility(View.VISIBLE);
            }
        });
        WeekView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.setVisibility(View.GONE);
                bookingsListView.setVisibility(View.GONE);
            }
        });
        showPreviousMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.showPreviousMonth();
                currentmonth.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
            }
        });
        showNextMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.showNextMonth();
                currentmonth.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
            }
        });
    }


    private Date setToMidnight(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date setToMidnight(Calendar cal) {

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void GetSeizureDetails(Date date, String Time) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            DBAdapter adapter = new DBAdapter(this.getActivity());
            List<SeizureDetails> seizures = adapter.getSeizuresPerDateandTime(calendar.getTime(), Time);
            Bundle args = new Bundle();
            if (seizures.size() == 1) {
                args.putSerializable("seizureDetails", seizures.get(0));
                Fragment fragment = new SeizureDetailsFragment();
                if (fragment != null) {
                    fragment.setArguments(args);
                    FragmentManager frgManager = ((HomeView) this.getActivity())
                            .getFragmentManager();
                    frgManager.beginTransaction().replace(R.id.content_frame, fragment, this.getActivity().getString(R.string.seizure_detail_frag_tag)).addToBackStack(this.getActivity().getString(R.string.seizure_detail_frag_tag))
                            .commit();
                }

            } else if (seizures.size() != 0) {
                args.putSerializable("seizureDetails", (Serializable) seizures);
                args.putString("SeizureListDate", seizures.get(1).getDate());
                Fragment fragment = new SeizuresList();
                if (fragment != null) {
                    fragment.setArguments(args);
                    FragmentManager frgManager = ((HomeView) this.getActivity())
                            .getFragmentManager();
                    frgManager.beginTransaction().replace(R.id.content_frame, fragment, this.getActivity().getString(R.string.seizure_list_frag_tag)).addToBackStack(this.getActivity().getString(R.string.seizure_list_frag_tag))
                            .commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void AddAppointmentsToCalendar(CompactCalendarView compactCalendarView) throws ParseException {
        try {
            List<Date> date = new ArrayList<Date>();
            DBAdapter adapter = new DBAdapter(this.getActivity());
            ArrayList<Appointment> appoints;
            appoints = adapter.getAppointments();
            for (int i = 0; i < appoints.size(); i++) {
                Appointment appointmentt = appoints.get(i);
                Calendar appointmentTime = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormats.isoDateFormat2, Locale.ENGLISH);
                appointmentTime.setTime(sdf.parse(appointmentt.getAppointmentDate() + " " + appointmentt.getAppointmentTime()));
                Date newDatee = setToMidnight(appointmentTime);
                compactCalendarView.addEvent(new CalendarDayEvent(newDatee.getTime(), Color.GREEN), false);
                List<Event> eventListss = new ArrayList<>();
                Event bookk = new Event();
                bookk.setTitle(appointmentt.getGpName());
                bookk.setEventDate(newDatee);
                bookk.setEventTime(appointmentt.getAppointmentTime());
                bookk.setIsReminder(false);
                bookk.setType(FileUtils.APPOINTMENT_TYPE);
                eventListss.add(bookk);
                List<Event> xx = upcomingEvents.get(newDatee);
                if (xx != null) {
                    xx.add(bookk);
                } else {
                    xx = new ArrayList<Event>();
                    xx.add(bookk);
                }
                upcomingEvents.put(newDatee, xx);
            }


            //this loop to sort dates ASC for adding them in ordering maner in the calendar
         /*  ArrayList<Appointment> appointments;
            appointments = adapter.getAppointmentsReminders();

           for (int i = 0; i < appointments.size(); i++) {
                date.clear();
                Appointment appointment = appointments.get(i);

                for (int j = 0; j < appointment.getAppointmentReminders().size(); j++) {
                    date.add(appointment.getAppointmentReminders().get(j));
                }
                Collections.sort(date);
                System.out.println("the size of r is"+appointment.getAppointmentReminders().size());

                //get appointment reminders from appointment object and add them to calendar as events
                for (int k = 0; k < appointment.getAppointmentReminders().size(); k++) {
                    Date newDate = setToMidnight(date.get(k));
                    System.out.println("the new date is"+newDate +" "+newDate.getTime());

                    compactCalendarView.addEvent(new CalendarDayEvent(newDate.getTime(), Color.rgb(0, 255, 0)), false);
                    List<Event> eventLists = new ArrayList<>();


                    Event book = new Event();
                    book.setTitle(new SimpleDateFormat("h:mm a", Locale.getDefault()).format(date.get(k)) + "                           reminder: " + appointment.getGpName());
                    book.setEventDate(newDate);
                    book.setType(FileUtils.APPOINTMENT_TYPE);
                    book.setIsReminder(true);
                    eventLists.add(book);
                    List<Event> x = upcomingEvents.get(newDate);
                    if (x != null) {
                        x.add(book);
                    } else {
                        x = new ArrayList<Event>();
                        x.add(book);
                    }
                    upcomingEvents.put(newDate, x);
                }
            }*/
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    /**
     * -----------------------------------------------------------------------------
     * Class Name: AddPrescriptionRenewalToCalendar
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: Add Prescription Renewal To Calendar
     * -----------------------------------------------------------------------------
     */

    public void AddPrescriptionRenewalToCalendar(CompactCalendarView compactCalendarView) throws ParseException {

        try {
            List<Date> date = new ArrayList<Date>();
            DBAdapter adapter = new DBAdapter(this.getActivity());
            ArrayList<PrescriptionReminders> REMINDERS;
            REMINDERS = adapter.getAllPrescriptionReminders();

            //this loop to add appointment date itself to calendar
            for (int i = 0; i < REMINDERS.size(); i++) {

                PrescriptionReminders appointmentt = REMINDERS.get(i);
                Calendar appointmentTime = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormats.isoDateFormat, Locale.ENGLISH);
                appointmentTime.setTime(sdf.parse(appointmentt.getRenewalDate()));
                Date newDatee = setToMidnight(appointmentTime);
                int numOfWeeks = 12;
                if (appointmentt.getRenewalDate() != null && numOfWeeks > 0) {
                    for (int j = 1; j <= numOfWeeks + 1; j++) {
                        compactCalendarView.addEvent(new CalendarDayEvent(appointmentTime.getTimeInMillis(), Color.rgb(128, 0, 128)), false);
                        appointmentTime.add(Calendar.DATE, 28);

                    }
                }


                List<Event> eventListss = new ArrayList<>();
                Event bookk = new Event();
                bookk.setTitle(appointmentt.getMedicationName());
                bookk.setEventDate(newDatee);
                bookk.setEventTime("9:00 AM");
                bookk.setIsReminder(false);
                bookk.setType(FileUtils.PRESCRIPTION_RENEWAL_TYPE);
                eventListss.add(bookk);
                List<Event> xx = upcomingEvents.get(newDatee);
                if (xx != null) {
                    xx.add(bookk);
                } else {
                    xx = new ArrayList<Event>();
                    xx.add(bookk);
                }
                upcomingEvents.put(newDatee, xx);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


   /* public void AddMedicationRemindersToCalendar(CompactCalendarView compactCalendarView) throws ParseException {
        try {
            DBAdapter adapter = new DBAdapter(this.getActivity());
            ArrayList<NewMedication> medications;
            medications = adapter.getMedicationsReminders();
            for (int i = 0; i < medications.size(); i++) {

                NewMedication newMedication = medications.get(i);
                // add medications to CalenderView
                addToCalenderMedication(newMedication);

                //Add reminders to list
                if (newMedication.getMedicationReminders().size() > 0) {
                    addRemindersMedicationToCalender(newMedication.getMedicationReminders(), newMedication.getMedication_name());
                }

                Calendar titrationtime = Calendar.getInstance();
                if (newMedication.isTitrationOn()) {
                    titrationtime.setTime(DateTimeFormats.convertStringToDate(newMedication.getTitrationStartDate()));
                    Date newDatee = setToMidnight(titrationtime);

                    List<Event> eventListss = new ArrayList<>();

                    Event bookk = new Event();
                    bookk.setTitle(newMedication.getMedication_name() + " week " + 1);
                    bookk.setEventDate(titrationtime.getTime());
                    bookk.setType(FileUtils.MEDICATION_TYPE);
                    bookk.setIsReminder(false);
                    eventListss.add(bookk);

                    List<Event> xx = upcomingEvents.get(newDatee);
                    if (xx != null) {
                        xx.add(bookk);
                    } else {
                        xx = new ArrayList<>();
                        xx.add(bookk);
                    }
                    upcomingEvents.put(newDatee, xx);

                    for (int n = 1; n < (newMedication.getTitrationNumWeeks()); n++) {
                        int week = n;
                        titrationtime.add(Calendar.DATE, 7);
                        Date newDate = setToMidnight(titrationtime);

                        List<Event> eventLists = new ArrayList<>();

                        Event book = new Event();
                        book.setTitle(newMedication.getMedication_name() + " Week " + (week + 1));
                        book.setEventDate(titrationtime.getTime());
                        book.setType(FileUtils.MEDICATION_TYPE);
                        book.setIsReminder(false);
                        eventLists.add(book);

                        List<Event> x = upcomingEvents.get(newDate);
                        if (x != null) {
                            x.add(book);
                        } else {
                            x = new ArrayList<>();
                            x.add(book);
                        }
                        upcomingEvents.put(newDate, x);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * -----------------------------------------------------------------------------
     * Class Name: AddMissedMedicationRemindersToCalendar
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: Add Missed Medication Reminders To Calendar
     * -----------------------------------------------------------------------------
     */


    public void AddMissedMedicationRemindersToCalendar(CompactCalendarView compactCalendarView) throws ParseException {
        try {
            DBAdapter adapter = new DBAdapter(this.getActivity());
            ArrayList<MissedMedication> medications;
            medications = adapter.GetMissedMedications();
            for (int i = 0; i < medications.size(); i++) {
                MissedMedication newMedication = medications.get(i);
                Date newDate = setToMidnight(DateTimeFormats.convertStringToDate(newMedication.getMedicationReminderDate()));
                compactCalendarView.addEvent(new CalendarDayEvent(newDate.getTime(), getResources().getColor(R.color.orange_switch)), false);
                // add medications to CalenderView
                List<Event> eventListss = new ArrayList<>();
                Event bookk = new Event();
                bookk.setTitle(newMedication.getMedicationName());
                bookk.setEventDate(DateTimeFormats.convertStringToDateTimeFormat(newMedication.getMedicationReminderDate()));
                bookk.setEventTime(new SimpleDateFormat("h:mm a").format(DateTimeFormats.convertStringToDateTimeWithDeafultFormat(newMedication.getMedicationReminderTime())));
                bookk.setType(FileUtils.MEDICATION_TYPE);
                bookk.setMedicationHandlStatus(2);
                bookk.setIsReminder(true);
                eventListss.add(bookk);
                List<Event> xx = upcomingEvents.get(newDate);
                if (xx != null) {
                    xx.add(bookk);
                } else {
                    xx = new ArrayList<>();
                    xx.add(bookk);
                }
                upcomingEvents.put(newDate, xx);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void addToCalenderMedication(NewMedication newMedication) {
        try {
            Calendar startTime = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormats.isoDateFormat, Locale.ENGLISH);
            try {
                startTime.setTime(sdf.parse(newMedication.getStart_date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date newDate = setToMidnight(startTime);
            compactCalendarView.addEvent(new CalendarDayEvent(newDate.getTime(), getResources().getColor(R.color.orange_switch)), false);
            List<Event> eventLists = new ArrayList<>();

            Event book = new Event();
            book.setTitle(newMedication.getMedication_name());
            try {
                book.setEventDate(new SimpleDateFormat(DateTimeFormats.isoDateFormat).parse(newMedication.getStart_date()));
            } catch (ParseException e) {

                e.printStackTrace();
            }
            book.setType(FileUtils.MEDICATION_TYPE);
            book.setIsReminder(false);
            eventLists.add(book);
            List<Event> x = upcomingEvents.get(newDate);
            if (x != null) {
                x.add(book);
            } else {
                x = new ArrayList<>();
                x.add(book);
            }
            upcomingEvents.put(newDate, x);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    void addRemindersMedicationToCalender(ArrayList<MedicationReminders> reminders, String medicationName) {

        try {
            for (int k = 0; k < reminders.size(); k++) {

                Calendar startTime = Calendar.getInstance();
                Calendar EndTime = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());

                Date newDate = null;
                try {
                    newDate = setToMidnight(sdf.parse(reminders.get(k).getMedReminderDate()));
                    startTime.setTime(sdf.parse(reminders.get(k).getMedReminderDate()));
                    EndTime.setTime(sdf.parse(reminders.get(k).getMedReminderDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                compactCalendarView.addEvent(new CalendarDayEvent(newDate.getTime(), getResources().getColor(R.color.orange_switch)), false);
                List<Event> eventLists = new ArrayList<>();

                Event book = new Event();
                book.setTitle(medicationName);

                try {
                    book.setEventDate(sdf.parse(reminders.get(k).getMedReminderDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                book.setType(FileUtils.MEDICATION_TYPE);
                book.setIsReminder(true);
              //  book.setMedicationHandlStatus(reminders.get(k).getMedicationHandlingStatus());
                eventLists.add(book);
                List<Event> x = upcomingEvents.get(newDate);
                if (x != null) {
                    x.add(book);
                } else {
                    x = new ArrayList<>();
                    x.add(book);
                }
                upcomingEvents.put(newDate, x);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void AddSeizuresToCalendar(CompactCalendarView compactCalendarView) {


        try {
            DBAdapter adapter = new DBAdapter(this.getActivity());
            List<SeizureDetails> seizures = adapter.getSeizures();
            List<Date> date = new ArrayList<Date>();
            //Merge date and time to on date object from sqlite database


            for (int i = 0; i < seizures.size(); i++) {
                if (seizures.size() >= 1) {
                    Calendar startTime = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormats.isoDateFormat2, Locale.ENGLISH);
                    try {
                        startTime.setTime(sdf.parse(seizures.get(i).getDate() + " " + seizures.get(i).getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Date newDate = setToMidnight(startTime);

                    compactCalendarView.addEvent(new CalendarDayEvent(newDate.getTime(), Color.RED), false);
                    List<Event> eventLists = new ArrayList<>();

                    Event book = new Event();
                    book.setTitle(seizures.get(i).getSeizureType().getName());
                    book.setEventDate(new SimpleDateFormat(DateTimeFormats.isoDateFormat, Locale.getDefault()).parse(seizures.get(i).getDate()));
                    //Wed Apr 05 00:00:00 GMT+05:30 2017
                    book.setEventTime(seizures.get(i).getTime());
                    book.setType(FileUtils.SIEZURE_TYPE);
                    book.setIsReminder(false);
                    eventLists.add(book);
                    //Wed Apr 05 00:00:00 GMT+05:30 2017
                    List<Event> x = upcomingEvents.get(newDate);
                    if (x != null) {
                        x.add(book);
                    } else {
                        x = new ArrayList<>();
                        x.add(book);
                    }
                    upcomingEvents.put(newDate, x);
                }
            }
        } catch (Exception ex) {
            // Toast.makeText(this.getActivity(), ex.toString(), Toast.LENGTH_LONG).show();
        }
    }



    /**
     * -----------------------------------------------------------------------------
     * Class Name: AddEmergencyMedicationsToCalendar
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: Add Emergency Medication to Calender
     * -----------------------------------------------------------------------------
     */
    public void AddEmergencyMedicationsToCalendar(CompactCalendarView compactCalendarView) throws ParseException {

        try {
            DBAdapter adapter = new DBAdapter(this.getActivity());
            ArrayList<EmergencyMedicationEntity> emergencyMedicationsList;
            emergencyMedicationsList = adapter.getEmergencyMedications();
            for (int i = 0; i < emergencyMedicationsList.size(); i++) {
                EmergencyMedicationEntity emergencyMedicationEntity = emergencyMedicationsList.get(i);
                MedicationReminders medicationReminders = adapter.getEmergencyMedicationReminders(emergencyMedicationEntity.getId());
                Calendar emergencyMedicationTime = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormats.isoDateFormat, Locale.ENGLISH);
                emergencyMedicationTime.setTime(sdf.parse(emergencyMedicationEntity.getExpiryDate()));
                Date newDatee = setToMidnight(emergencyMedicationTime);
                compactCalendarView.addEvent(new CalendarDayEvent(newDatee.getTime(), getResources().getColor(R.color.bg_blue)), false);
                List<Event> eventListss = new ArrayList<>();
                Event bookk = new Event();
                bookk.setTitle("Your emergency medication" + " " + emergencyMedicationEntity.getEmergencyMedicationName() + " " + "needs to be renewed");
                bookk.setEventDate(newDatee);
                bookk.setEventTime("9:00 AM");
                bookk.setType(FileUtils.EMERGENCY_MEDICATION_TYPE);
                bookk.setIsReminder(false);
                eventListss.add(bookk);
                List<Event> xx = upcomingEvents.get(newDatee);
                if (xx != null) {
                    xx.add(bookk);
                } else {
                    xx = new ArrayList<>();
                    xx.add(bookk);
                }
                upcomingEvents.put(newDatee, xx);


                /********************************************************************************************/
          /*  if(medicationReminders.getMedReminderDate()!=null) {
                Date reminderDate = sdf.parse(medicationReminders.getMedReminderDate());

                if (reminderDate != null) {

                    Date newDate = setToMidnight(reminderDate);

                    compactCalendarView.addEvent(new CalendarDayEvent(newDate.getTime(), getResources().getColor(R.color.bg_blue)), false);
                    List<Event> reminderEventLists = new ArrayList<>();

                    Event reminderEvent = new Event();
                    reminderEvent.setTitle(emergencyMedicationEntity.getEmergencyMedicationName() + "");
                    reminderEvent.setEventDate(newDate);

                    reminderEvent.setMedicationHandleReasons(medicationReminders.getMedicationHandlingReason());
                    reminderEvent.setMedicationHandlStatus(medicationReminders.getMedicationHandlingStatus());
                    reminderEvent.setType(FileUtils.EMERGENCY_MEDICATION_TYPE);
                    reminderEvent.setIsReminder(true);
                    reminderEventLists.add(reminderEvent);
                    List<Event> x = upcomingEvents.get(newDate);
                    if (x != null) {
                        x.add(reminderEvent);
                    } else {
                        x = new ArrayList<>();
                        x.add(reminderEvent);
                    }
                    upcomingEvents.put(newDate, x);

                }
            }*/

                /*******************************************************************************************/
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }


    }


}

