package com.hp.epilepsy.widget;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MedicationTitrationActivity extends Activity
{
    public static String INTENT_TITRATION_ON = "INTENT_TITRATION_ON";
    public static String INTENT_TITRATION_START_DATE = "INTENT_TITRATION_START_DATE";
    public static String INTENT_TITRATION_NUM_WEEKS = "INTENT_TITRATION_NUM_WEEKS";

    Switch switchTitration;
    LinearLayout llStartDate;
    LinearLayout llNumWeeks;
    EditText txtTitrationStartDate;
    DialogFrag_DatePicker dpTitrationStartDate;
    NumberPicker numWeeks;
    Button btnOk;
    public static boolean IsTitrationUpdated = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_titration);
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

    private void initViews()
    {
        try {
            llStartDate = (LinearLayout)findViewById(R.id.llStartDate);
            llNumWeeks = (LinearLayout)findViewById(R.id.llNumWeeks);

            Bundle passedData = this.getIntent().getExtras();
            btnOk = (Button)this.findViewById(R.id.btnOk);
            btnOk.setOnClickListener(btnOkOnClickListener);
            switchTitration = (Switch)this.findViewById(R.id.switchTitration);
            switchTitration.setOnCheckedChangeListener(switchTitrationOnCheckedChangeListener);
            txtTitrationStartDate = (EditText)this.findViewById(R.id.txtTitrationStartDate);
            numWeeks = (NumberPicker)this.findViewById(R.id.numWeeks);
            Calendar startDateCal = Calendar.getInstance();
            numWeeks.setMinValue(1);
            numWeeks.setMaxValue(8);
            numWeeks.setValue(1);

            if(passedData != null)
            {
                //Getting Titration On Off...
                if (passedData.containsKey(INTENT_TITRATION_ON))
                {
                    switchTitration.setChecked((Boolean) passedData.get(INTENT_TITRATION_ON));
                }

                //Getting Titration Start Date...
                if(passedData.containsKey(INTENT_TITRATION_START_DATE))
                {
                    startDateCal = (Calendar)passedData.get(INTENT_TITRATION_START_DATE);
                }

                //Getting Titration Number of Weeks...
                if(passedData.containsKey(INTENT_TITRATION_NUM_WEEKS))
                {
                    numWeeks.setValue((int) passedData.get(INTENT_TITRATION_NUM_WEEKS));
                }
            }

            startDateCal.set(Calendar.HOUR,0);
            startDateCal.set(Calendar.HOUR_OF_DAY,0);
            startDateCal.set(Calendar.MINUTE,0);
            startDateCal.set(Calendar.SECOND,0);
            startDateCal.set(Calendar.MILLISECOND,0);

            dpTitrationStartDate = new DialogFrag_DatePicker();
            dpTitrationStartDate.initialize(txtTitrationStartDate, startDateCal, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CompoundButton.OnCheckedChangeListener switchTitrationOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged (CompoundButton buttonView, boolean isChecked)
        {
            if(isChecked)
            {
                llStartDate.setVisibility(View.VISIBLE);
                llNumWeeks.setVisibility(View.VISIBLE);
            }
            else
            {
                llStartDate.setVisibility(View.GONE);
                llNumWeeks.setVisibility(View.GONE);
            }
        }
    };
    View.OnClickListener btnOkOnClickListener = new View.OnClickListener ()
    {
        @Override
        public void onClick(View v)
        {
            IsTitrationUpdated=true;

            Intent intent = getIntent();
            intent.putExtra(INTENT_TITRATION_ON, switchTitration.isChecked());
            if(switchTitration.isChecked()) {
                intent.putExtra(INTENT_TITRATION_START_DATE, dpTitrationStartDate.calendar);
                intent.putExtra(INTENT_TITRATION_NUM_WEEKS, numWeeks.getValue());
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    public static class DialogFrag_DatePicker extends DialogFragment
            implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

        private Context context;
        private EditText editText;
        private Calendar calendar;

        public void initialize(EditText editText, Calendar calendar, Context context)
        {
            this.context = context;
            this.editText = editText;
            this.calendar = calendar;

            editText.setOnClickListener(this);
            this.updateDateText();
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            this.updateDate(year, monthOfYear, dayOfMonth);
            this.updateDateText();
        }

        private void updateDate(int year, int monthOfYear, int dayOfMonth)
        {
            this.calendar.set(Calendar.YEAR, year);
            this.calendar.set(Calendar.MONTH, monthOfYear);
            this.calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }

        private void updateDateText()
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            this.editText.setText(DateTimeFormats.reverseDateFormat(format.format(calendar.getTime())));
        }

        @Override
        public void onClick(View v)
        {
            int year = calendar.get(Calendar.YEAR);
            int monthofYear = calendar.get(Calendar.MONTH);
            int dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, this, year, monthofYear, dayofMonth);
            datePickerDialog.show();
        }
    }
}
