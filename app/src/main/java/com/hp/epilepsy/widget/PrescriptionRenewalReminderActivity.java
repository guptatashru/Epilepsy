package com.hp.epilepsy.widget;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PrescriptionRenewalReminderActivity extends Activity implements CheckBox.OnCheckedChangeListener {

    public static String INTENT_PRESCRIPTION_REMNDERD = "INTENT_PRESCRIPTION_REMNDERD";
    static final String PRESCRIPTION_REMINDERS_ALERTS = "PRESCRIPTION_REMINDERS_ALERTS";
    static final String RENEWAL_DATE = "RENEWAL_DATE";
    ArrayList<String> ReminderAlerts;
    public static boolean IsPrescriptionUpdated = false;

    CheckBox OneDay;
    CheckBox ThreeDays;
    CheckBox OneWeek;
    CheckBox AllOfThem;
    EditText renewalDate;

    ArrayList<String> RemindeBefore = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptionrenewal_reminder);

        initializeView();
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


    public void initializeView() {
        try {
            Bundle passedData = this.getIntent().getExtras();
            ReminderAlerts = new ArrayList<String>();

            renewalDate = (EditText) findViewById(R.id.renewalDate);
            OneDay = (CheckBox) findViewById(R.id.one_day);
            ThreeDays = (CheckBox) findViewById(R.id.three_day);
            OneWeek = (CheckBox) findViewById(R.id.one_week);
            AllOfThem = (CheckBox) findViewById(R.id.All_Of_Them);

            OneDay.setOnCheckedChangeListener(this);
            ThreeDays.setOnCheckedChangeListener(this);
            OneWeek.setOnCheckedChangeListener(this);
            AllOfThem.setOnCheckedChangeListener(this);

            if (passedData != null) {
                if (passedData.containsKey(PRESCRIPTION_REMINDERS_ALERTS)) {
                    ReminderAlerts = (ArrayList<String>) passedData.get(PRESCRIPTION_REMINDERS_ALERTS);
                    if (ReminderAlerts != null) {
                        for (int i = 0; i < ReminderAlerts.size(); i++) {
                            String Alert = ReminderAlerts.get(i);

                            if (Alert.equals("OneDay")) {

                                OneDay.setChecked(true);
                            }
                            if (Alert.equals("ThreeDays")) {

                                ThreeDays.setChecked(true);
                            }
                            if (Alert.equals("OneWeek")) {

                                OneWeek.setChecked(true);
                            }
                        }
                    }
                }
                if (passedData.containsKey(RENEWAL_DATE)) {
                    String RenrwalDate = (String) passedData.get(RENEWAL_DATE);

                    //reverse the date to the needed format
                    renewalDate.setText(DateTimeFormats.reverseDateFormat(RenrwalDate));
                }
            } else {
                renewalDate.setText(new SimpleDateFormat("dd-MM-yyyy").format((new Date())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void Done_btnClickListener(View view) {
        try {
            switch (view.getId()) {
                case R.id.renewalDate:
                    showDatePicker(view);
                    break;
                case R.id.done_btn:
                    if (renewalDate.getText().toString().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please select renewal date", Toast.LENGTH_LONG).show();
                    } else {

                        if (OneDay.isChecked()) {
                            RemindeBefore.add("OneDay");
                        }
                        if (ThreeDays.isChecked()) {
                            RemindeBefore.add("ThreeDays");

                        }
                        if (OneWeek.isChecked()) {
                            RemindeBefore.add("OneWeek");
                        }


                       // IsPrescriptionUpdated=true;
                        //pass reminders to Appointments Activity
                        Intent intent = getIntent();
                        intent.putExtra(INTENT_PRESCRIPTION_REMNDERD, RemindeBefore);
                        intent.putExtra(RENEWAL_DATE,DateTimeFormats.reverseDateFormatBack(renewalDate.getText().toString()));
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    }
                case R.id.done_button:

                    IsPrescriptionUpdated=true;
                    if (renewalDate.getText().toString().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please select renewal date", Toast.LENGTH_LONG).show();
                    } else {

                        if (OneDay.isChecked()) {

                            RemindeBefore.add("OneDay");
                        }
                        if (ThreeDays.isChecked()) {

                            RemindeBefore.add("ThreeDays");

                        }
                        if (OneWeek.isChecked()) {

                            RemindeBefore.add("OneWeek");
                        }

                        for(int i=0;i<RemindeBefore.size();i++)
                        {

                        }



                        //pass reminders to Appointments Activity
                        Intent intent = getIntent();
                        intent.putExtra(INTENT_PRESCRIPTION_REMNDERD, RemindeBefore);
                        intent.putExtra(RENEWAL_DATE, DateTimeFormats.reverseDateFormatBack(renewalDate.getText().toString()));
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        try {
            switch (view.getId()) {
                case R.id.one_hour:
                    break;
                case R.id.one_day:
                    break;
                case R.id.one_week:
                    break;
                case R.id.two_week:
                    break;
                case R.id.All_Of_Them:
                    if (isChecked) {
                        OneDay.setChecked(true);
                        ThreeDays.setChecked(true);
                        OneWeek.setChecked(true);
                        AllOfThem.setChecked(true);
                    } else {
                        OneDay.setChecked(false);
                        ThreeDays.setChecked(false);
                        OneWeek.setChecked(false);
                        AllOfThem.setChecked(false);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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





    public static class DateRangePicker extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

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
                    .findViewById(R.id.renewalDate);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //  SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeFormats.isoDateFormat);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            Date date = new Date(c.getTimeInMillis());
            String formattedDate = dateFormat.format(date);
            editText.setText(formattedDate);
        }
    }


}
