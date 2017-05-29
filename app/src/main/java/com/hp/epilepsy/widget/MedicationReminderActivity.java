/*package com.hp.epilepsy.widget;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MedicationReminderActivity extends Activity {
    public static String INTENT_MED_START_DATE = "INTENT_MED_START_DATE";
    public static String INTENT_MED_START_DATE_TEXT = "INTENT_MED_START_DATE_TEXT";
    public static String INTENT_MED_REMINDER_TIMES = "INTENT_MED_REMINDER_TIMES";

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static boolean IsReminderUpdated = false;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    Calendar startDateCal = Calendar.getInstance();
    List<Calendar> reminders = new ArrayList<Calendar>();

    DatePicker dpMedStartDate;
    EditText  dpMedStartDatetxt;
    int medicationId;

    NumberPicker numDoses;

    List<TimePicker> tpReminders;

    TimePicker tpReminder1;
    TimePicker tpReminder2;
    TimePicker tpReminder3;
    TimePicker tpReminder4;
    TimePicker tpReminder5;
    TimePicker tpReminder6;
    TimePicker tpReminder7;
    TimePicker tpReminder8;

    Button btnOk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_reminder);


        initViews();
    }

    private void initViews() {


        try {
            IsReminderUpdated = false;
            Bundle passedData = this.getIntent().getExtras();

            btnOk = (Button) this.findViewById(R.id.btnOk);
            dpMedStartDatetxt = (EditText) this.findViewById(R.id.dpMedStartDateText);


            //click event for the dpMedStartDatetxt edittext
            dpMedStartDatetxt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(MedicationReminderActivity.this, date, SelctedDate
                            .get(Calendar.YEAR), SelctedDate.get(Calendar.MONTH),
                            SelctedDate.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            btnOk.setOnClickListener(btnOkOnClickListener);

            dpMedStartDate = (DatePicker) this.findViewById(R.id.dpMedStartDate);

            numDoses = (NumberPicker) this.findViewById(R.id.numDoses);

            numDoses.setOnValueChangedListener(numDosesValueChangeListener);

            tpReminder1 = (TimePicker) this.findViewById(R.id.tpReminder1);

            tpReminder2 = (TimePicker) this.findViewById(R.id.tpReminder2);

            tpReminder3 = (TimePicker) this.findViewById(R.id.tpReminder3);

            tpReminder4 = (TimePicker) this.findViewById(R.id.tpReminder4);

            tpReminder5 = (TimePicker) this.findViewById(R.id.tpReminder5);

            tpReminder6 = (TimePicker) this.findViewById(R.id.tpReminder6);

            tpReminder7 = (TimePicker) this.findViewById(R.id.tpReminder7);

            tpReminder8 = (TimePicker) this.findViewById(R.id.tpReminder8);


            tpReminders = new ArrayList<TimePicker>();
            tpReminders.add(tpReminder1);
            tpReminders.add(tpReminder2);
            tpReminders.add(tpReminder3);
            tpReminders.add(tpReminder4);
            tpReminders.add(tpReminder5);
            tpReminders.add(tpReminder6);
            tpReminders.add(tpReminder7);
            tpReminders.add(tpReminder8);

            //Holding medication Start Date...
            startDateCal = Calendar.getInstance();

            //Holding reminders if any...
            List<Calendar> reminderCals = new ArrayList<Calendar>();

            if (passedData != null) {
                if (passedData.containsKey(INTENT_MED_START_DATE)) {
                    if (passedData.get(INTENT_MED_START_DATE) != null) {
                        startDateCal = (Calendar) passedData.get(INTENT_MED_START_DATE);
                        System.out.println("the start date cal is"+startDateCal);

                        //set the reminder date to the edittex if exist
                        //  dpMedStartDate.updateDate(startDateCal.get(Calendar.YEAR), startDateCal.get(Calendar.MONTH), startDateCal.get(Calendar.DAY_OF_MONTH));
                        // dpMedStartDatetxt.setText(dpMedStartDate.getYear() + "-" +dpMedStartDate.getMonth() + "-" +dpMedStartDate.getDayOfMonth());
                    }
                    if (passedData.get(INTENT_MED_START_DATE_TEXT) != null) {
                        String startDate = (String) passedData.get(INTENT_MED_START_DATE_TEXT);
                        System.out.println("the startd"+startDate);

                        dpMedStartDatetxt.setText(DateTimeFormats.reverseDateFormat(startDate));
                        System.out.println("the dep is"+dpMedStartDatetxt.getText().toString());             //todays date
                    }
                }

            }

            if (passedData != null) {
                if (passedData.containsKey(INTENT_MED_REMINDER_TIMES)) {
                    if (passedData.get(INTENT_MED_REMINDER_TIMES) != null) {
                        System.out.println("yes ia m");
                        Object[] reminderTimesObjList = (Object[]) passedData.get(INTENT_MED_REMINDER_TIMES);
                        System.out.println("the len is"+reminderTimesObjList.length);

                        for (int b = 0; b < reminderTimesObjList.length; b++) {
                            System.out.println("reminder list"+reminderTimesObjList[b]);
                            reminderCals.add((Calendar) reminderTimesObjList[b]);

                        }
                    }
                }
            } else {
                Calendar calReminder = Calendar.getInstance();

                //increment the month of calender object by 1 because it stating with index 0 so the month 2 will be 1
                calReminder.add(Calendar.MONTH, 1);

                dpMedStartDatetxt.setText(DateTimeFormats.reverseDateFormat(calReminder.get(Calendar.YEAR)+"-"+ calReminder.get(Calendar.MONTH) +"-"+ calReminder.get(Calendar.DAY_OF_MONTH)));

                int hour = 10;
                for (int k = 0; k < 1; k++) {
                    calReminder.set(Calendar.HOUR, Calendar.HOUR_OF_DAY);
                    calReminder.set(Calendar.MINUTE, Calendar.MINUTE);
                    hour++;

                    // reminderCals.add(calReminder);
                }
            }

            //check if the reminderCals is equal 0 this means there is not reminders putted to this medication
            // so we are gonna to set the value of reminder to the current time
            if(reminderCals.size()==0)
            {
                System.out.println("i am 0");

                Calendar calReminder = Calendar.getInstance();

                tpReminders.get(0).setCurrentHour(calReminder.get(Calendar.HOUR_OF_DAY));
                tpReminders.get(0).setCurrentMinute(calReminder.get(Calendar.MINUTE));
                System.out.println("the updated time1 is"+tpReminders.get(0).getCurrentHour()+":"+tpReminders.get(0).getCurrentMinute());


            }else {




                settings = getSharedPreferences("MY_PREFS_NAME", 0);
                editor = settings.edit();
                for (int j = 0; j < reminderCals.size(); j++) {


                    tpReminders.get(j).setCurrentHour(settings.getInt("hour", 0));

                    tpReminders.get(j).setCurrentMinute(settings.getInt("minute", 0));
                    System.out.println("the updated time is" + tpReminders.get(j).getCurrentHour() + ":" + tpReminders.get(j).getCurrentMinute());
                }


            }


            //Setting number of doses...
            numDoses.setMinValue(0);
            numDoses.setMaxValue(8);
            if(reminderCals.size()==0)
            {
                numDoses.setValue(1);
                System.out.println("i am 1");
            }else {
                numDoses.setValue(reminderCals.size());
                System.out.println("i am many");
            }
            updateVisibleReminders(numDoses.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    Calendar SelctedDate = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {


            // TODO Auto-generated method stub
            SelctedDate.set(Calendar.YEAR, year);
            SelctedDate.set(Calendar.MONTH, monthOfYear);
            SelctedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };


    //get the date from DatePickerDialog and set it to the edittext
    private void updateLabel() {
        String myFormat = DateTimeFormats.isoDateFormat; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dpMedStartDatetxt.setText(DateTimeFormats.reverseDateFormat(sdf.format(SelctedDate.getTime())));
    }



    NumberPicker.OnValueChangeListener numDosesValueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {

            updateVisibleReminders(newVal);
        }
    };

    private void updateVisibleReminders(int numDoses) {
        for (int i = 0; i < numDoses; i++) {
            ((TimePicker) tpReminders.get(i)).setVisibility(View.VISIBLE);
        }


        for (int i = numDoses; i < tpReminders.size(); i++) {
            ((TimePicker) tpReminders.get(i)).setVisibility(View.GONE);
        }
    }


    View.OnClickListener btnOkOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                startDateCal.set(dpMedStartDate.getYear(), dpMedStartDate.getMonth(), dpMedStartDate.getDayOfMonth(), 0, 0, 0);
                startDateCal=SelctedDate;
                System.out.println("the start date cal is"+startDateCal);

                IsReminderUpdated=true;

                TimePicker tpReminder;
                Calendar calReminder;
                for (int w = 0; w < 8; w++) {
                    tpReminder = tpReminders.get(w);

                    if (tpReminder.getVisibility() == View.VISIBLE) {
                        calReminder = Calendar.getInstance();
                        System.out.println("the final time0 is"+tpReminder.getCurrentHour()+":"+tpReminder.getCurrentMinute());
                        calReminder.set(startDateCal.get(Calendar.YEAR), startDateCal.get(Calendar.MONTH), startDateCal.get(Calendar.DAY_OF_MONTH), tpReminder.getCurrentHour(), tpReminder.getCurrentMinute());
                        System.out.println("the called"+calReminder.getTime().getHours()+" "+ calReminder.getTime().getMinutes());

                        // settings = getSharedPreferences("MY_PREFS_NAME", 0);
                        //  editor = settings.edit();
                        editor.putInt("hour",tpReminder.getCurrentHour());
                        editor.putInt("minute", tpReminder.getCurrentMinute());

                        editor.commit();
                        reminders.add(calReminder);
                    }
                }



                Intent intent = getIntent();
                intent.putExtra(INTENT_MED_START_DATE, startDateCal);
                intent.putExtra(INTENT_MED_REMINDER_TIMES, reminders.toArray());
                setResult(RESULT_OK, intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };*/

package com.hp.epilepsy.widget;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;



/**
 * -----------------------------------------------------------------------------
 * Class Name: MedicationReminderActivity
 * Created By:Mahmoud
 * Modified By:Shruti and Nikunj
 * -----------------------------------------------------------------------------
 */
public class MedicationReminderActivity extends Activity {
    public static String INTENT_MED_START_DATE = "INTENT_MED_START_DATE";
    public static String INTENT_MED_START_DATE_TEXT = "INTENT_MED_START_DATE_TEXT";
    public static String INTENT_MED_REMINDER_TIMES = "INTENT_MED_REMINDER_TIMES";
    public static boolean IsReminderUpdated = false;
    Calendar startDateCal = Calendar.getInstance();
    List<Calendar> reminders = new ArrayList<Calendar>();
    DatePicker dpMedStartDate;
    EditText  dpMedStartDatetxt;
    NumberPicker numDoses;
    List<TimePicker> tpReminders;
    TimePicker tpReminder1;
    TimePicker tpReminder2;
    TimePicker tpReminder3;
    TimePicker tpReminder4;
    TimePicker tpReminder5;
    TimePicker tpReminder6;
    TimePicker tpReminder7;
    TimePicker tpReminder8;

    Button btnOk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_reminder);


        initViews();
    }
    @Override
    public  void onPause()
    {
        super.onPause();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private void initViews() {
        try {
            IsReminderUpdated = false;
            Bundle passedData = this.getIntent().getExtras();

            btnOk = (Button) this.findViewById(R.id.btnOk);
            dpMedStartDatetxt = (EditText) this.findViewById(R.id.dpMedStartDateText);


            //click event for the dpMedStartDatetxt edittext
            dpMedStartDatetxt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(MedicationReminderActivity.this, date, SelctedDate
                            .get(Calendar.YEAR), SelctedDate.get(Calendar.MONTH),
                            SelctedDate.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            btnOk.setOnClickListener(btnOkOnClickListener);

            dpMedStartDate = (DatePicker) this.findViewById(R.id.dpMedStartDate);

            numDoses = (NumberPicker) this.findViewById(R.id.numDoses);

            numDoses.setOnValueChangedListener(numDosesValueChangeListener);

            tpReminder1 = (TimePicker) this.findViewById(R.id.tpReminder1);

            tpReminder2 = (TimePicker) this.findViewById(R.id.tpReminder2);

            tpReminder3 = (TimePicker) this.findViewById(R.id.tpReminder3);

            tpReminder4 = (TimePicker) this.findViewById(R.id.tpReminder4);

            tpReminder5 = (TimePicker) this.findViewById(R.id.tpReminder5);

            tpReminder6 = (TimePicker) this.findViewById(R.id.tpReminder6);

            tpReminder7 = (TimePicker) this.findViewById(R.id.tpReminder7);

            tpReminder8 = (TimePicker) this.findViewById(R.id.tpReminder8);


            tpReminders = new ArrayList<TimePicker>();
            tpReminders.add(tpReminder1);
            tpReminders.add(tpReminder2);
            tpReminders.add(tpReminder3);
            tpReminders.add(tpReminder4);
            tpReminders.add(tpReminder5);
            tpReminders.add(tpReminder6);
            tpReminders.add(tpReminder7);
            tpReminders.add(tpReminder8);

            //Holding medication Start Date...
            startDateCal = Calendar.getInstance();

            //Holding reminders if any...
            List<Calendar> reminderCals = new ArrayList<Calendar>();

            if (passedData != null) {
                if (passedData.containsKey(INTENT_MED_START_DATE)) {
                    if (passedData.get(INTENT_MED_START_DATE) != null) {
                        startDateCal = (Calendar) passedData.get(INTENT_MED_START_DATE);
                    }
                    if (passedData.get(INTENT_MED_START_DATE_TEXT) != null) {
                        String startDate = (String) passedData.get(INTENT_MED_START_DATE_TEXT);
                        dpMedStartDatetxt.setText(DateTimeFormats.reverseDateFormat(startDate));
                    }
                }
            }

            if (passedData != null) {
                if (passedData.containsKey(INTENT_MED_REMINDER_TIMES)) {
                    if (passedData.get(INTENT_MED_REMINDER_TIMES) != null) {
                        Object[] reminderTimesObjList = (Object[]) passedData.get(INTENT_MED_REMINDER_TIMES);
                        for (int b = 0; b < reminderTimesObjList.length; b++) {
                            reminderCals.add((Calendar) reminderTimesObjList[b]);
                        }
                    }
                }
            } else {
                Calendar calReminder = Calendar.getInstance();

                //increment the month of calender object by 1 because it stating with index 0 so the month 2 will be 1
                calReminder.add(Calendar.MONTH, 1);

                dpMedStartDatetxt.setText(DateTimeFormats.reverseDateFormat(calReminder.get(Calendar.YEAR)+"-"+ calReminder.get(Calendar.MONTH) +"-"+ calReminder.get(Calendar.DAY_OF_MONTH)));

                int hour = 10;
                for (int k = 0; k < 1; k++) {
                    calReminder.set(Calendar.HOUR, Calendar.HOUR_OF_DAY);
                    calReminder.set(Calendar.MINUTE, Calendar.MINUTE);
                    hour++;

                }
            }

            //check if the reminderCals is equal 0 this means there is not reminders putted to this medication
            // so we are gonna to set the value of reminder to the current time
            if(reminderCals.size()==0)
            {
                Calendar calReminder = Calendar.getInstance();
            }else {

                //get the values of reminderCals reminders  to tpReminders that will be shown based on setValue(reminderCals.size())
                for (int j = 0; j < reminderCals.size(); j++) {
                    tpReminders.get(j).setCurrentHour(reminderCals.get(j).get(Calendar.HOUR_OF_DAY));
                    tpReminders.get(j).setCurrentMinute(reminderCals.get(j).get(Calendar.MINUTE));
                }
            }

            //Setting number of doses...
            numDoses.setMinValue(0);
            numDoses.setMaxValue(8);
            if(reminderCals.size()==0)
            {
                numDoses.setValue(1);
            }else {
                numDoses.setValue(reminderCals.size());
            }
            updateVisibleReminders(numDoses.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    Calendar SelctedDate = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            SelctedDate.set(Calendar.YEAR, year);
            SelctedDate.set(Calendar.MONTH, monthOfYear);
            SelctedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };


    //get the date from DatePickerDialog and set it to the edittext
    private void updateLabel() {
        String myFormat = DateTimeFormats.isoDateFormat; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dpMedStartDatetxt.setText(DateTimeFormats.reverseDateFormat(sdf.format(SelctedDate.getTime())));
    }



    NumberPicker.OnValueChangeListener numDosesValueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
            updateVisibleReminders(newVal);
        }
    };

    private void updateVisibleReminders(int numDoses) {
        for (int i = 0; i < numDoses; i++) {
            ((TimePicker) tpReminders.get(i)).setVisibility(View.VISIBLE);
        }


        for (int i = numDoses; i < tpReminders.size(); i++) {
            ((TimePicker) tpReminders.get(i)).setVisibility(View.GONE);
        }
    }


    View.OnClickListener btnOkOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                startDateCal.set(dpMedStartDate.getYear(), dpMedStartDate.getMonth(), dpMedStartDate.getDayOfMonth(), 0, 0, 0);
                startDateCal=SelctedDate;

                IsReminderUpdated=true;

                TimePicker tpReminder;
                Calendar calReminder;
                for (int w = 0; w < 8; w++) {
                    tpReminder = tpReminders.get(w);
                    if (tpReminder.getVisibility() == View.VISIBLE) {
                        calReminder = Calendar.getInstance();
                        calReminder.set(startDateCal.get(Calendar.YEAR), startDateCal.get(Calendar.MONTH), startDateCal.get(Calendar.DAY_OF_MONTH), tpReminder.getCurrentHour(), tpReminder.getCurrentMinute());
                        reminders.add(calReminder);
                    }
                }


                Intent intent = getIntent();
                intent.putExtra(INTENT_MED_START_DATE, startDateCal);
                intent.putExtra(INTENT_MED_REMINDER_TIMES, reminders.toArray());
                setResult(RESULT_OK, intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}








