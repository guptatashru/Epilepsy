package com.hp.epilepsy.widget;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.utils.FileUtils;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.adapter.SpinnerAdapter;
import com.hp.epilepsy.widget.model.ISpinnerItem;
import com.hp.epilepsy.widget.model.IVideoSetter;
import com.hp.epilepsy.widget.model.InfoMultiSpinner;
import com.hp.epilepsy.widget.model.RecordedVideo;
import com.hp.epilepsy.widget.model.SeizureDetails;
import com.hp.epilepsy.widget.model.SeizureDuration;
import com.hp.epilepsy.widget.model.SeizureFeature;
import com.hp.epilepsy.widget.model.SeizurePostFeature;
import com.hp.epilepsy.widget.model.SeizurePreFeature;
import com.hp.epilepsy.widget.model.SeizureTrigger;
import com.hp.epilepsy.widget.model.SeizureType;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;




/**
 * -----------------------------------------------------------------------------
 * Class Name: RecordSeizureDetailsFragment
 * Created By:Mahmoud
 * Modified By:Shruti and Nikunj
 * Purpose: Recording seizures
 * -----------------------------------------------------------------------------
 */
@SuppressLint("SimpleDateFormat")
public class RecordSeizureDetailsFragment extends Fragment implements
OnClickListener,IVideoSetter,IStepScreen {

	private SpinnerAdapter mSeizureTypeAdapter;
	private SpinnerAdapter mSeizureDurationAdapter;
	private RecordedVideo recordedVideo;
	private SeizureType initalSeizureType;
	private Button videoButton;
	private transient InfoMultiSpinner postFeatures;
	private transient InfoMultiSpinner triggers;
	private transient InfoMultiSpinner preFeatures;
	private transient InfoMultiSpinner seizureFeatures;
	SimpleDateFormat dateFormatter;


	public RecordSeizureDetailsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView=null;

		rootView = inflater.inflate(
				R.layout.activity_record_seizure_details, container, false);

		fillDropDownLists(rootView);
		return rootView;
	}

	@Override
	public void setVideo(RecordedVideo video) {
		this.recordedVideo=video;	
		videoButton.setText(video.getCreationDate() + " "
				+ video.getDuration());
	}

	private void getVideoButton(View view)
	{
		videoButton = (Button) view.findViewById(R.id.link_video);
		videoButton.setOnClickListener(this);
	}

	private void setUpOnClickListeners(View view)
	{
		try {
			EditText date = (EditText) view.findViewById(R.id.date);
			if (date != null)
                date.setOnClickListener(this);
			EditText time = (EditText) view.findViewById(R.id.time);
			if (time != null)
                time.setOnClickListener(this);
			LinearLayout save = (LinearLayout) view.findViewById(
                    R.id.rec_seiz_save);
			if (save != null)
                save.setOnClickListener(this);
			LinearLayout delete = (LinearLayout) view.findViewById(
                    R.id.rec_seiz_delete);
			if (delete != null)
                delete.setOnClickListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public Context getContext()
	{
		return this.getActivity();
	}

	private void fillDropDownLists(View rootView) {
		try {
			DBAdapter dbAdapter = new DBAdapter(this.getContext());
			dbAdapter.open();

			getVideoButton(rootView);

			setUpOnClickListeners(rootView);
			//Set up the spinner Items
			setUpSeizureTypeSpinner(dbAdapter,rootView);
			//Set up the info for a trigger item
			setUpTriggerInfoCheckList(dbAdapter,rootView);
			//Set up the pre-features
			setUpPreFeatures(dbAdapter,rootView);
			//Set up the post-features
			setUpPostFeatures(dbAdapter,rootView);
			//Set up the features
			setUpFeatures(dbAdapter,rootView);
			//Set the duration spinner
			setSiezureDurations(dbAdapter,rootView);

			EditText date = (EditText) rootView.findViewById(R.id.date);
			EditText time = (EditText) rootView.findViewById(R.id.time);

			Calendar newDate;
			dateFormatter = new SimpleDateFormat(DateTimeFormats.isoDateFormatReverse, Locale.US);
			Calendar c = Calendar.getInstance();
			String todayDate = dateFormatter.format(c.getTime());

			date.setText(todayDate);
			dateFormatter = new SimpleDateFormat(DateTimeFormats.isoTimeFormat, Locale.US);
			Calendar cc = Calendar.getInstance();
			String todatime = dateFormatter.format(cc.getTime());

			time.setText(todatime);

			//Set up the spinner of Emergency Medication
			dbAdapter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private void setSiezureDurations(DBAdapter dbAdapter,View view)
	{
		try {
			List<SeizureDuration> seizureDurations=dbAdapter.getSeizureDurations();
			Spinner spinner = (Spinner) view.findViewById(R.id.seizure_duration);
			mSeizureDurationAdapter= new SpinnerAdapter(new ArrayList<ISpinnerItem>(seizureDurations), this.getActivity());
			spinner.setAdapter(mSeizureDurationAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpFeatures(DBAdapter dbAdapter,View view)
	{
		try {
			seizureFeatures = (InfoMultiSpinner) view.findViewById(R.id.features);
			List<SeizureFeature> mFeatures=dbAdapter.getSeizureFeatures();
			seizureFeatures.setSpinnerItems(new ArrayList<ISpinnerItem>(mFeatures), getString(R.string.select_seizures));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpPostFeatures(DBAdapter dbAdapter,View view)
	{
		try {
			postFeatures = (InfoMultiSpinner) view.findViewById(R.id.post_features);
			List<SeizurePostFeature> mFeatures=dbAdapter.getSeizurePostFeatures();
			postFeatures.setSpinnerItems(new ArrayList<ISpinnerItem>(mFeatures),getString(R.string.select_post_features));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpPreFeatures(DBAdapter dbAdapter,View view)
	{
		try {
			preFeatures = (InfoMultiSpinner) view.findViewById(R.id.pre_features);
			List<SeizurePreFeature> mFeatures=dbAdapter.getSeizurePreFeatures();
			preFeatures.setSpinnerItems(new ArrayList<ISpinnerItem>(mFeatures),getString(R.string.select_pre_features));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpTriggerInfoCheckList(DBAdapter dbAdapter,View view)
	{
		try {
			triggers = (InfoMultiSpinner) view.findViewById(
                    R.id.triggers);

			List<SeizureTrigger> mTriggers=dbAdapter.getSeizureTriggers();
			triggers.setSpinnerItems(new ArrayList<ISpinnerItem>(mTriggers),"Triggers");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpSeizureTypeSpinner(DBAdapter dbAdapter,View view){

		try {
			Spinner spinner = (Spinner) view.findViewById(
                    R.id.seizure_type);
			List<SeizureType> seizureTypes=dbAdapter.getSeizureTypes();
			List<SeizureType> items = new ArrayList<SeizureType>(seizureTypes);
			initalSeizureType=new SeizureType();
			initalSeizureType.setDescription("");
			initalSeizureType.setName(getActivity().getString(R.string.seizure_type));
			items.add(0,initalSeizureType);

			mSeizureTypeAdapter= new SpinnerAdapter(new ArrayList<ISpinnerItem>(items), this.getActivity());

			if (spinner != null) {
                spinner.setAdapter(mSeizureTypeAdapter);
                spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                            int position, long id) {
						ImageView mImageView=(ImageView)(view.findViewById(R.id.info_icon));
                        mImageView.setVisibility(ImageView.INVISIBLE);
                        mImageView.setClickable(false);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleSave(View view) {
		try {
			SeizureDetails seizureDetails = getSeizureDetails();
			DBAdapter adapter = new DBAdapter(this.getContext());
			if(seizureDetails.getSeizureType()==initalSeizureType)
			{
			Toast.makeText(this.getContext(), getString(R.string.select_seizure_type),Toast.LENGTH_LONG).show();
			}
			else if (seizureDetails.validate()) {
			boolean success = false;
			try {
			   adapter.open();
			   success = adapter.saveSeizureDetails(seizureDetails);
	        } catch (Exception e) {
	           Toast.makeText(getActivity(), getString(R.string.operation_fail), Toast.LENGTH_LONG).show();
	       }
	       Toast.makeText(getActivity(), success?getString(R.string.operation_done):getString(R.string.operation_fail), Toast.LENGTH_LONG).show();
			//Must be last otherwise the context is gone
			handleDelete(view);
		} else {
			Toast.makeText(this.getContext(), getString(R.string.fill_missing_fields),Toast.LENGTH_LONG).show();
		}
			adapter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SeizureDetails getSeizureDetails() {
		// from view to seizure details
		try {
			SeizureDetails seizureDetails = new SeizureDetails();

			Spinner spinner = (Spinner) getActivity().findViewById(R.id.seizure_type);

			seizureDetails.setSeizureType((SeizureType)mSeizureTypeAdapter.getItem(spinner.getSelectedItemPosition()));

			spinner = (Spinner) getActivity().findViewById(R.id.seizure_duration);
			seizureDetails.setSeizureDuration((SeizureDuration)mSeizureDurationAdapter.getItem(spinner.getSelectedItemPosition()));

			seizureDetails.setSeizureTriggers((List<SeizureTrigger>)(triggers.getSelectedItems()));

			seizureDetails.setSeizureFeatures((List<SeizureFeature>)(seizureFeatures.getSelectedItems()));
			seizureDetails.setSeizurePreFeatures((List<SeizurePreFeature>)(preFeatures.getSelectedItems()));
			seizureDetails.setSeizurePostFeatures((List<SeizurePostFeature>)(postFeatures.getSelectedItems()));
			EditText editText = (EditText) getActivity().findViewById(
                    R.id.other_info);
			seizureDetails.setDesc(editText.getText().toString());
			editText = (EditText) getActivity().findViewById(R.id.date);

			seizureDetails.setDate( DateTimeFormats.reverseDateFormatBack(editText.getText().toString()));
			editText = (EditText) getActivity().findViewById(R.id.time);
			seizureDetails.setTime(editText.getText().toString());

			Switch mSwitch = (Switch) this.getActivity().findViewById(R.id.emergency_medication_given);
			seizureDetails.setEmegencyMedicationGiven(mSwitch.isChecked()?true:false);

			mSwitch = (Switch) getActivity().findViewById(R.id.ambulance_called);
			seizureDetails.setAmbulanceCalled(mSwitch.isChecked()?true:false);
			if (recordedVideo != null)
                seizureDetails.setRecordedVideo(recordedVideo);
			return seizureDetails;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void handleDelete(View view) {
		getActivity().onBackPressed();
	}

	public void linkVideo(View view) {

		try {
			Bundle args = new Bundle();
			VideoGalleryView fragment = new VideoGalleryView(getActivity());
			args.putSerializable("videoFiles",
                    (Serializable) FileUtils.getRecordedVideoFiles(LoginActivity.userName,getActivity()));
			args.putSerializable("seizureDetails", getSeizureDetails());
			if (fragment != null) {
                fragment.setArguments(args);
                FragmentManager frgManager = getFragmentManager();
                String tagString=getResources().getString(R.string.video_gallery_frag_tag);
                frgManager.beginTransaction().add(R.id.content_frame, fragment,tagString ).addToBackStack(tagString).commit();
            }
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
		}
	}


	public void onResume()
	{
		super.onResume();
	}

	public void showDatePicker(View view) {
		int day, month, year;

		try {
			String text = ((EditText)view).getText().toString();

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
		int hour, minute;

		try {
			String text = ((EditText) view).getText().toString();
			SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
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

	@Override
	public void onClick(View view) {
		try {
			switch (view.getId()) {
                case R.id.link_video:
                    linkVideo(view);
                    break;
                case R.id.date:
                    showDatePicker(view);
                    break;
                case R.id.time:
                    showTimePicker(view);
                    break;
                case R.id.rec_seiz_save:
                    handleSave(view);
                    break;
                case R.id.rec_seiz_delete:
                    handleDelete(view);
                    break;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

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
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			c.set(year, month, day);
			Date date = new Date(c.getTimeInMillis());

		    Date todayDate=new Date();

			if(date.after(todayDate))
			{
				//put today date to the edite text
				String formattedDate = dateFormat.format(todayDate);
				editText.setText(DateTimeFormats.reverseDateFormat(formattedDate));
				Toast.makeText(getActivity(), "Seizure date can't exceed today date.", Toast.LENGTH_SHORT).show();
			}else
			{
				//put the selected date to edittext
				String formattedDate = dateFormat.format(date);
				editText.setText(DateTimeFormats.reverseDateFormat(formattedDate));
			}
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
			SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			c.set(Calendar.MINUTE, minute);
			Date date = new Date(c.getTimeInMillis());
			String formattedDate = dateFormat.format(date);
			editText.setText(formattedDate);

		}
	}





}
