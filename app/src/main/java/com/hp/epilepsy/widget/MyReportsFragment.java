package com.hp.epilepsy.widget;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.adapter.ReportsListAdapter;
import com.hp.epilepsy.widget.model.MissedMedication;
import com.hp.epilepsy.widget.model.SeizureDetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyReportsFragment extends Fragment implements View.OnClickListener {

    public static int SIEZURE_TYPE = 0;
    public static int MISSED_MEDICATIONS_TYPE = 1;
    public static String SELECTED_SIEZURES = "selectedSiezures";
    public static String SELECTED_MEDICATIONS = "selectedMedications";

    ReportsListAdapter reportsListAdapter;
    ArrayList<SeizureDetails> seizuresList;
    ArrayList<com.hp.epilepsy.widget.model.NewMedication> MedicationsList;
    ArrayList<MissedMedication> missedMedicationsList;
    EditText et_start_date, et_end_date;
    ListView siezures_list, medications_list;
    Button generate_report_button, email_report_button;
    TableRow lists_container;

    SimpleDateFormat dateFormatter;
    Calendar newDate;
    DatePicker dpStartDate;


    DBAdapter dbAdapter;
    public MyReportsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ReportsListAdapter.checkBoxStateSeizure=0;
        ReportsListAdapter.checkBoxStateMedication=0;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = null;
        rootView = inflater.inflate(R.layout.fragment_my_reports, container, false);
        initializeViews(rootView);

        SetReportDates();


        return rootView;
    }
     @Override
    public void onResume()
     {
         super.onResume();
        // ReportsListAdapter.checkBoxStateSeizure=0;
        // ReportsListAdapter.checkBoxStateMedication=0;
     }

    void initializeViews(View rootView) {

        try {
            ReportsListAdapter.selectedMedicationsList = new ArrayList<>();
            ReportsListAdapter.selectedSiezuresList = new ArrayList<>();

            dbAdapter = new DBAdapter(this.getActivity());

            et_start_date = (EditText) rootView.findViewById(R.id.et_start_date);
            et_end_date = (EditText) rootView.findViewById(R.id.et_end_date);
            generate_report_button = (Button) rootView.findViewById(R.id.generate_report_button);
            email_report_button = (Button) rootView.findViewById(R.id.email_report_button);
            siezures_list = (ListView) rootView.findViewById(R.id.siezures_list);
            siezures_list.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });

            medications_list = (ListView) rootView.findViewById(R.id.medications_list);
            medications_list.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });





            lists_container = (TableRow) rootView.findViewById(R.id.lists_container);

            et_start_date.setOnClickListener(this);
            et_end_date.setOnClickListener(this);
            generate_report_button.setOnClickListener(this);
            email_report_button.setOnClickListener(this);

            et_end_date.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (et_start_date.getText().toString().length() > 0) {
                        getDataBetweenFromDB(et_start_date.getText().toString(), et_end_date.getText().toString());
                    } else {
                        lists_container.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ArrayList<SeizureDetails> removeDuplicates(ArrayList<SeizureDetails> l) {

        try {
            for (int x = 0; x < l.size(); x++) {
                String x1 = l.get(x).getSeizureType().getName();
                for (int y = x+1; y < l.size(); y++) {

                    String x2 = l.get(y).getSeizureType().getName();
                    if (x1.equalsIgnoreCase(x2) && x != y) {
                        l.remove(l.get(y));
                        y--;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

public ArrayList<com.hp.epilepsy.widget.model.NewMedication> removeMedicationDuplicates(ArrayList<com.hp.epilepsy.widget.model.NewMedication> l) {
    try {
        for (int x = 0; x < l.size(); x++) {
            String x1 = l.get(x).getMedication_name();
            for (int y = 1; y < l.size(); y++) {

                String x2 = l.get(y).getMedication_name();
                if (x1.equalsIgnoreCase(x2) && x != y) {
                    l.remove(l.get(y));
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return l;
}

    void getDataBetweenFromDB(String startDate, String endDate) {
        try {

            seizuresList = removeDuplicates(dbAdapter.getSeizuresBetweenDates(startDate, endDate));
            MedicationsList = removeMedicationDuplicates(dbAdapter.getMedicationsBetweenDates(DateTimeFormats.reverseDateFormatBack(startDate),DateTimeFormats.reverseDateFormatBack( endDate)));

            if (seizuresList.size() > 0 || MedicationsList.size() > 0) {
                lists_container.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_LONG).show();
                lists_container.setVisibility(View.GONE);
            }

            reportsListAdapter = new ReportsListAdapter(this.getActivity(), seizuresList, null, SIEZURE_TYPE);
            siezures_list.setAdapter(reportsListAdapter);

            reportsListAdapter = new ReportsListAdapter(this.getActivity(), null, MedicationsList, MISSED_MEDICATIONS_TYPE);
            medications_list.setAdapter(reportsListAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Displays the set Expiry date picker dialog
     */
    public void showDatePicker(final int type) {
        // Inflate your custom layout containing 2 DatePickers
        try {
            LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
            View customView = inflater.inflate(R.layout.custom_date_picker, null);

            // Define your date pickers
            dpStartDate = (DatePicker) customView.findViewById(R.id.dpStartDate);
            LinearLayout reminder_layout = (LinearLayout) customView.findViewById(R.id.reminder_layout);
            reminder_layout.setVisibility(View.GONE);

            ImageView image_separator = (ImageView) customView.findViewById(R.id.image_separator);
            image_separator.setVisibility(View.VISIBLE);


            // Build the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(customView); // Set the view of the dialog to your custom layout
            builder.setTitle("Set Date");
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //TODO Change date format here
                    //dateFormatter = new SimpleDateFormat(DateTimeFormats.isoDateFormat, Locale.US);
                    dateFormatter = new SimpleDateFormat(DateTimeFormats.isoDateFormatReverse, Locale.US);
                    newDate = Calendar.getInstance();
                    newDate.set(dpStartDate.getYear(), dpStartDate.getMonth(), dpStartDate.getDayOfMonth());
                  //  newDate.set(dpStartDate.getDayOfMonth(), dpStartDate.getMonth(), dpStartDate.getYear());

                    /**************************************************************/
                    Calendar c = Calendar.getInstance();
                    String todayDate1 = dateFormatter.format(c.getTime());
                   // Toast.makeText(getActivity(), todayDate1, Toast.LENGTH_LONG).show();

                    /***************************************************************/

                    if (type == 1) {
                        try {



                            if ((dateFormatter.parse(todayDate1)).compareTo((dateFormatter.parse(calculateDateUpDown(dateFormatter.format(newDate.getTime()).toString(), 1))))<0)
                            {
                                Toast.makeText(getActivity(), "End Date shouldn't exceed Today Date", Toast.LENGTH_LONG).show();
                                et_start_date.setText("");
                                et_end_date.setText("");

                            }else {
                                et_start_date.setText(dateFormatter.format(newDate.getTime()).toString());
                                et_end_date.setText(calculateDateUpDown(et_start_date.getText().toString(), 1));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else if (type == -1) {

                        try {
                            if ((dateFormatter.parse(todayDate1)).compareTo((dateFormatter.parse(dateFormatter.format(newDate.getTime()).toString())))<0)
                            {
                                Toast.makeText(getActivity(), "End Date shouldn't exceed Today Date", Toast.LENGTH_LONG).show();
                                et_start_date.setText("");
                                et_end_date.setText("");
                            }else {
                                et_start_date.setText(calculateDateUpDown(et_end_date.getText().toString(), -1));

                                String x=dateFormatter.format(newDate.getTime()).toString();
                                et_end_date.setText(dateFormatter.format(newDate.getTime()).toString());

                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            // Create and show the dialog
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void SetReportDates()
    {
        dateFormatter = new SimpleDateFormat(DateTimeFormats.isoDateFormatReverse, Locale.US);
        Calendar c = Calendar.getInstance();
        String todayDate1 = dateFormatter.format(c.getTime());

        et_end_date.setText(todayDate1);
        et_start_date.setText(calculateDateUpDown(et_end_date.getText().toString(), -1));
        getDataBetweenFromDB(et_start_date.getText().toString(), et_end_date.getText().toString());
    }






    String calculateDateUpDown(String date, int type) {
        String newDateString = null;
        try {
            Calendar cal = Calendar.getInstance();
            newDateString = "";
            Date temp_date = null;
           // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format = new SimpleDateFormat(DateTimeFormats.isoDateFormatReverse);
            try {
                temp_date = format.parse(date);
                cal.setTime(temp_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (type == 1) {
                cal.add(Calendar.MONTH, 6);
                newDateString = DateTimeFormats.convertReportDateToStringWithIsoFormat(cal.getTime());
            } else if (type == -1) {
                cal.add(Calendar.MONTH, -6);
                newDateString = DateTimeFormats.convertReportDateToStringWithIsoFormat(cal.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newDateString;
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.et_start_date:
                    showDatePicker(1);
                    break;
                case R.id.et_end_date:
                    showDatePicker(-1);
                    break;
                case R.id.generate_report_button:
                    if(ReportsListAdapter.checkBoxStateSeizure>0 || ReportsListAdapter.checkBoxStateMedication>0)
                    {
                        Intent myIntent = new Intent(getActivity(), ReportResultActivity.class);
                        myIntent.putExtra(SELECTED_SIEZURES, ReportsListAdapter.selectedSiezuresList);
                        myIntent.putExtra(SELECTED_MEDICATIONS, ReportsListAdapter.selectedMedicationsList);
                        myIntent.putExtra("start", DateTimeFormats.reverseDateFormatBack(et_start_date.getText().toString()) );
                        myIntent.putExtra("end", DateTimeFormats.reverseDateFormatBack( et_end_date.getText().toString()) );
                        myIntent.putExtra("where", 0);
                        startActivity(myIntent);
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Check your selection",Toast.LENGTH_LONG).show();
                    }
                //    if ((ReportsListAdapter.selectedSiezuresList.size() > 0 && ReportsListAdapter.selectedSiezuresList.size() < 4) || (ReportsListAdapter.selectedMedicationsList.size() > 0 && ReportsListAdapter.selectedMedicationsList.size() < 7)) {

                    break;
                case R.id.email_report_button:
                    if ((ReportsListAdapter.selectedSiezuresList.size() > 0 && ReportsListAdapter.selectedSiezuresList.size() < 4) || (ReportsListAdapter.selectedMedicationsList.size() > 0 && ReportsListAdapter.selectedMedicationsList.size() < 4)) {
                         Intent myIntent = new Intent(getActivity(), ReportResultActivity.class);
                        myIntent.putExtra(SELECTED_SIEZURES, ReportsListAdapter.selectedSiezuresList);
                        myIntent.putExtra(SELECTED_MEDICATIONS, ReportsListAdapter.selectedMedicationsList);
                        myIntent.putExtra("start", DateTimeFormats.reverseDateFormatBack(et_start_date.getText().toString()));
                        myIntent.putExtra("end", DateTimeFormats.reverseDateFormatBack(et_end_date.getText().toString()));
                        myIntent.putExtra("where", 1);
                       // Toast.makeText(getActivity(), "Generate Report" + ReportsListAdapter.selectedSiezuresList.size(), Toast.LENGTH_LONG).show();
                        startActivity(myIntent);
                    } else {
                        Toast.makeText(getActivity(), "Check Selection to Generate Report", Toast.LENGTH_LONG).show();
                    }

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}