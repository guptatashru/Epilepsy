package com.hp.epilepsy.widget;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.utils.FileUtils;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.model.MissedMedication;
import com.hp.epilepsy.widget.model.SeizureDetails;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class EmailSeizureEvents extends Fragment implements OnClickListener {
	
	
	private static final String customDatePattern="MMM. dd,yyyy";

	private static void
	writeContentsToCSV(File csvFile, List<SeizureDetails> mSeizureDetails, List<MissedMedication> missedMedications) throws IOException {
		String lineSeperator = "\n";

		FileWriter fileWriter = new FileWriter(csvFile);
		fileWriter.append(SeizureDetails.getCSVHeader());
		fileWriter.append(lineSeperator);

		for (SeizureDetails seizureDetails : mSeizureDetails) {
			fileWriter.append(seizureDetails.toCSV());
			fileWriter.append(lineSeperator);
		}

		fileWriter.append(lineSeperator);
		fileWriter.append(MissedMedication.getCSVHeader());
		fileWriter.append(lineSeperator);

		for (MissedMedication medication : missedMedications) {
			fileWriter.append(medication.toCSV());
			fileWriter.append(lineSeperator);
		}


		fileWriter.flush();
		fileWriter.close();
	}

	private static void writeMedicationContentsToCSV(File csvFile, List<MissedMedication> missedMedication) throws IOException {
		String lineSeperator = "\n";

		FileWriter fileWriter = new FileWriter(csvFile);
		fileWriter.append(lineSeperator);
		fileWriter.append(MissedMedication.getCSVHeader());
		fileWriter.append(lineSeperator);

		for (MissedMedication medication : missedMedication) {
			fileWriter.append(medication.toCSV());
			fileWriter.append(lineSeperator);
		}



		fileWriter.flush();
		fileWriter.close();
	}

//	private static final String hourPattern ="hh:mm a";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		try {
			View rootView = inflater.inflate(
                    R.layout.activity_email_events, container, false);

			rootView.findViewById(R.id.ae_save).setOnClickListener(this);
			rootView.findViewById(R.id.ae_from_date_box).setOnClickListener(this);
			rootView.findViewById(R.id.ae_to_date_box).setOnClickListener(this);
			return rootView;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.ae_save:
			sendEmail();
			break;
		case R.id.ae_from_date_box:
			showDatePicker(view);
			break;
		case R.id.ae_to_date_box:
			showDatePicker(view);
			break;
		default:
			break;
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private void sendEmail()
	{
		try {
			String fromDate=((EditText)getView().findViewById(R.id.ae_from_date_box)).getText().toString();
			String toDate=((EditText)getView().findViewById(R.id.ae_to_date_box)).getText().toString();

			if((fromDate==null||fromDate.isEmpty())||(toDate==null||toDate.isEmpty()))
            {
                Toast.makeText(getActivity(), getString(R.string.fill_dates) , Toast.LENGTH_LONG).show();
            }
            else {
                SimpleDateFormat customDateFormat= new SimpleDateFormat(customDatePattern);
                DBAdapter mAdapter=new DBAdapter(getActivity());
                try {
                    List<SeizureDetails> mSeizures=mAdapter.getSeizureDetailsInDateRange(customDateFormat.parse(fromDate), customDateFormat.parse(toDate));
                    Collections.sort(mSeizures);

					List<MissedMedication> medication=mAdapter.getMissedMedicationsDateRange(DateTimeFormats.convertDateToStringWithIsoFormat(customDateFormat.parse(fromDate)),DateTimeFormats.convertDateToStringWithIsoFormat(customDateFormat.parse(toDate)));

					File csvFile=FileUtils.CreateCSVFile("SeizureDiary("+fromDate+"-"+toDate+")");
                    writeContentsToCSV(csvFile, mSeizures, medication);
				//	writeMedicationContentsToCSV(csvFile,medication);

                   // Toast.makeText(getActivity(), getString(R.string.file_written), Toast.LENGTH_LONG).show();
                    openEmailClient(csvFile);

                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.file_error), Toast.LENGTH_LONG).show();
                }finally
                {

                }

            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("SimpleDateFormat")
	public void showDatePicker(View view) {
		try {
			int day, month, year;

			String text = ((EditText)view).getText().toString();
			SimpleDateFormat dateFormat = new SimpleDateFormat(customDatePattern);
			Calendar c = Calendar.getInstance();
			Date date;
			try {
                date = text != null && text.length() != 0 ? dateFormat.parse(text)
                        : new Date();
            } catch (ParseException e) {
                date = new Date();
            }
			c.setTime(date);
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			DateRangePicker datePicker = DateRangePicker.newInstance(day, month,
                    year,(EditText)view);
			datePicker.show(getFragmentManager(), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openEmailClient(File fileToSend)
	{
		try {
			Intent emailIntent=new Intent(Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Seizure Diary report");
			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileToSend));
			this.getActivity().startActivity(Intent.createChooser(emailIntent, "Send Email"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class DateRangePicker extends DialogFragment implements
	OnDateSetListener {

		EditText mEditText;

		private static DateRangePicker newInstance(int day, int month, int year,EditText mText) {
			
			DateRangePicker dateOfBirthPicker = new DateRangePicker();
			Bundle args = new Bundle();
			args.putInt("day", day);
			args.putInt("month", month);
			args.putInt("year", year);
			args.putInt("editText", mText.getId());
			dateOfBirthPicker.setArguments(args);
			return dateOfBirthPicker;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int day = getArguments().getInt("day");
			int month = getArguments().getInt("month");
			int year = getArguments().getInt("year");
			int editText=getArguments().getInt("editText");
			
			mEditText=(EditText)this.getActivity().findViewById(editText);
			DatePickerDialog dialog = new DatePickerDialog(getActivity(), this,
					year, month, day);
			return dialog;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {

			SimpleDateFormat dateFormat = new SimpleDateFormat(customDatePattern);
			Calendar c = Calendar.getInstance();
			c.set(year, month, day);
			Date date = new Date(c.getTimeInMillis());
			String formattedDate = dateFormat.format(date);
			mEditText.setText(formattedDate);
		}
	}


}
