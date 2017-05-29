package com.hp.epilepsy.widget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.hp.epilepsy.R;

import java.util.ArrayList;

public class AppointmentReminderActivity extends Activity implements CheckBox.OnCheckedChangeListener {

    public static String INTENT_APPINTMENT_REMNDERD = "INTENT_APPINTMENT_REMNDERD";
    static final String APPOINTMENT_REMINDERS_ALERTS = "APPOINTMENT_REMINDERS_ALERTS";
    ArrayList<String> ReminderAlerts;
    public static boolean IsAppointmentUpdated = false;

    CheckBox OneHour;
    CheckBox OneDay;
    CheckBox OneWeek;
    CheckBox TwoWeeks;
    CheckBox AllOfThem;

    ArrayList<String> RemindeBefore=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_reminder);

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



    public void initializeView()
    {
        try {
            Bundle passedData = this.getIntent().getExtras();
            ReminderAlerts=new ArrayList<String>();
            OneHour=(CheckBox) findViewById(R.id.one_hour);
            OneDay=(CheckBox) findViewById(R.id.one_day);
            OneWeek=(CheckBox) findViewById(R.id.one_week);
            TwoWeeks=(CheckBox) findViewById(R.id.two_week);
            AllOfThem=(CheckBox) findViewById(R.id.All_Of_Them);

            OneHour.setOnCheckedChangeListener(this);
            OneDay.setOnCheckedChangeListener(this);
            OneWeek.setOnCheckedChangeListener(this);
            TwoWeeks.setOnCheckedChangeListener(this);
            AllOfThem.setOnCheckedChangeListener(this);

            if (passedData != null) {
                if (passedData.containsKey(APPOINTMENT_REMINDERS_ALERTS)) {
                    ReminderAlerts = (ArrayList<String>) passedData.get(APPOINTMENT_REMINDERS_ALERTS);
                    if (ReminderAlerts != null) {
                        for (int i = 0; i < ReminderAlerts.size(); i++) {
                            String Alert = ReminderAlerts.get(i);

                            if (Alert.equals("OneHour")) {
                                OneHour.setChecked(true);
                            }
                            if (Alert.equals("OneDay")) {
                                OneDay.setChecked(true);
                            }
                            if (Alert.equals("OneWeek")) {
                                OneWeek.setChecked(true);
                            }
                            if (Alert.equals("TwoWeeks")) {
                                TwoWeeks.setChecked(true);
                            }
                        }
                    }
                }





            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void Done_btnClickListener(View view) {
        try {
            switch(view.getId()) {
                case R.id.done_btn:
                    IsAppointmentUpdated=true;
                    if (OneHour.isChecked())
                    {
                        RemindeBefore.add("OneHour");
                    } if(OneDay.isChecked())
                    {
                        RemindeBefore.add("OneDay");
                    } if(OneWeek.isChecked())
                    {
                        RemindeBefore.add("OneWeek");
                    } if(TwoWeeks.isChecked())
                    {
                        RemindeBefore.add("TwoWeeks");
                    }
                    //pass reminders to Appointments Activity
                    Intent intent = getIntent();
                    intent.putExtra(INTENT_APPINTMENT_REMNDERD,RemindeBefore);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        try {
            switch(view.getId()) {
                case R.id.one_hour:
                    break;
                case R.id.one_day:
                    break;
                case R.id.one_week:
                    break;
                case R.id.two_week:
                    break;
                case R.id.All_Of_Them:
                    if (isChecked)
                    {
                        OneHour.setChecked(true);
                        OneDay.setChecked(true);
                        OneWeek.setChecked(true);
                        TwoWeeks.setChecked(true);
                        AllOfThem.setChecked(true);
                    } else {
                        OneHour.setChecked(false);
                        OneDay.setChecked(false);
                        OneWeek.setChecked(false);
                        TwoWeeks.setChecked(false);
                        AllOfThem.setChecked(false);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
