package com.hp.epilepsy.widget.model;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@SuppressLint("SimpleDateFormat")
public class SeizureDetails implements Serializable,Comparable<SeizureDetails>{

	private static final String ISO8601DatePattern="yyyy-MM-dd";
	private static final String customDatePattern="yyyy-MM-dd";
	private static final String hourPattern ="hh:mm a";
	private static final long serialVersionUID = 8954039813536429719L;
	private static final String[] weekDays = { "SATURDAY", "SUNDAY", "MONDAY", "TUESDAY",
		"WEDNESDAY", "THURSDAY", "FRIDAY" };
	
	
	
	private long id;
	private SeizureType mSeizureType;
	private SeizureDuration mSeizureDuration;
	private List<SeizureTrigger> mSeizureTriggers;
	private List<SeizureFeature> mSeizureFeatures;
	private List<SeizurePreFeature> mSeizurePreFeatures;
	private List<SeizurePostFeature> mSeizurePostFeatures;
	private String desc;
	private String date;
	private String time;
	private boolean mEmegencyMedicationGiven;
	private boolean mAmbulanceCalled;
	private RecordedVideo recordedVideo;
	boolean isChecked;


	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean hecked) {
		isChecked = hecked;
	}

	public SeizureDetails() {

	}

	public static String[] getWeekdays() {
		return weekDays;
	}

	public static String getCSVHeader() {
		String commaDelimiter = ",";
		StringBuilder builder = new StringBuilder();
		builder.append("Seizures");
		builder.append("\n");

		builder.append("Date");
		builder.append(commaDelimiter);
		builder.append("Time");
		builder.append(commaDelimiter);
		builder.append("Type");
		builder.append(commaDelimiter);
		builder.append("Duration");
		builder.append(commaDelimiter);
		builder.append("Emergency Medication Taken");
		builder.append(commaDelimiter);
		builder.append("Ambulance Called");
		builder.append(commaDelimiter);
		builder.append("Triggers");
		builder.append(commaDelimiter);
		builder.append("Pre Features");
		builder.append(commaDelimiter);
		builder.append("Features");
		builder.append(commaDelimiter);
		builder.append("Post Features");
		builder.append(commaDelimiter);
		builder.append("Other Information");
		return builder.toString();
	}

	private static String buildMultiSelector(List<ISpinnerItem> itemList) {
		StringBuilder builder = new StringBuilder();
		for (ISpinnerItem iSpinnerItem : itemList) {
			builder.append(getSpinnerItemString(iSpinnerItem));
			builder.append("/");
		}
		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}

	private static String getSpinnerItemString(ISpinnerItem type) {
		String returnString = "";
		if (type.getDescription() != null && !type.getDescription().trim().isEmpty()) {
			returnString = type.getName() + " (" + type.getDescription() + ")";
		} else {
			returnString = type.getName();
		}
		return returnString;
	}

	public boolean validate() {
		// TODO Auto-generated method stub
		return this.mSeizureType!=null&&this.mSeizureType.getId()>0
				&& mSeizureDuration!=null&&this.mSeizureDuration.getId()>0
				&& this.getDate().length() != 0&&this.getTime().length()>0&&this.getDate().length()>0;
	}

	public RecordedVideo getRecordedVideo() {
		return recordedVideo;
	}

	public void setRecordedVideo(RecordedVideo recordedVideo) {
		this.recordedVideo = recordedVideo;
	}

	public String getDayOfWeek()
	{
		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM. dd,yyyy");
			Date lDate;

			lDate = dateFormat.parse(this.date);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lDate);
			return weekDays[calendar.get(Calendar.DAY_OF_WEEK)%7];
		}
		catch (ParseException e) {
			e.toString();
		}
		return "";
	}

	public String getDayOfMonth() {
		return getDate().substring(0, getDate().indexOf(',')).replace('.', ' ');
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public SeizureType getSeizureType() {
		return mSeizureType;
	}

	public void setSeizureType(SeizureType mSeizureType) {
		this.mSeizureType = mSeizureType;
	}

	public SeizureDuration getSeizureDuration() {
		return mSeizureDuration;
	}

	public void setSeizureDuration(SeizureDuration mSeizureDuration) {
		this.mSeizureDuration = mSeizureDuration;
	}

	public List<SeizureTrigger> getSeizureTriggers() {
		return mSeizureTriggers;
	}

	public void setSeizureTriggers(List<SeizureTrigger> mSeizureTriggers) {
		this.mSeizureTriggers = mSeizureTriggers;
	}

	public List<SeizureFeature> getSeizureFeatures() {
		return mSeizureFeatures;
	}

	public void setSeizureFeatures(List<SeizureFeature> mSeizureFeatures) {
		this.mSeizureFeatures = mSeizureFeatures;
	}

	public List<SeizurePreFeature> getSeizurePreFeatures() {
		return mSeizurePreFeatures;
	}

	public void setSeizurePreFeatures(List<SeizurePreFeature> mSeizurePreFeatures) {
		this.mSeizurePreFeatures = mSeizurePreFeatures;
	}

	public List<SeizurePostFeature> getSeizurePostFeatures() {
		return mSeizurePostFeatures;
	}

	public void setSeizurePostFeatures(
			List<SeizurePostFeature> mSeizurePostFeatures) {
		this.mSeizurePostFeatures = mSeizurePostFeatures;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDate() {
		System.out.println("the sql date is"+date);
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isEmegencyMedicationGiven() {
		return mEmegencyMedicationGiven;
	}

	public void setEmegencyMedicationGiven(boolean mEmegencyMedicationGiven) {
		this.mEmegencyMedicationGiven = mEmegencyMedicationGiven;
	}

	public int getSqlEmegencyMedicationGiven()
	{
		return (this.mEmegencyMedicationGiven)? 1 : 0;
	}

	public void setSqlEmegencyMedicationGiven(int i)
	{
		this.mEmegencyMedicationGiven=((i == 1)? true : false);
	}

	public boolean isAmbulanceCalled() {
		return mAmbulanceCalled;
	}

	public void setAmbulanceCalled(boolean mAmbulanceCalled) {
		this.mAmbulanceCalled = mAmbulanceCalled;
	}

	public int getSqlAmbulanceCalled()
	{
		return (this.mAmbulanceCalled)? 1 : 0;
	}

	public void setSqlAmbulanceCalled(int i)
	{
		this.mAmbulanceCalled=((i == 1)? true : false);
	}

	public String getSQLDateTime() {

		String returnVal=null;

		try {

			Calendar calendar=Calendar.getInstance();
			Calendar timeCalendar=Calendar.getInstance();

			SimpleDateFormat dateFormat = new SimpleDateFormat(customDatePattern);

			calendar.setTime(dateFormat.parse(this.date));


			if (this.time != null && !this.time.isEmpty()) {
			DateFormat formatter = new SimpleDateFormat(hourPattern);
				Date thisDate = formatter.parse(this.time);
			timeCalendar.setTime(thisDate);
			calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		}


			//SqlLite Likes this format
		SimpleDateFormat newDateFormat=new SimpleDateFormat(ISO8601DatePattern);
			returnVal=newDateFormat.format(calendar.getTime());
		} catch (ParseException e) {

		}


		return returnVal;
	}
	
	public void setSqlDateTime(String sqlDate)
	{

		try {
		SimpleDateFormat dateFormat=new SimpleDateFormat(ISO8601DatePattern);
		Date lDate;

		lDate=dateFormat.parse(sqlDate);
		SimpleDateFormat setDateFormat=new SimpleDateFormat(customDatePattern);
		this.date=setDateFormat.format(lDate);
			System.out.println("the d is"+this.date);
		}catch (ParseException e) {

		}
	}
	
	@Override
	public int compareTo(SeizureDetails another) {
		DateFormat newFormatter = new SimpleDateFormat(customDatePattern);
		DateFormat formatter = new SimpleDateFormat(hourPattern);


		Calendar thisCalendar=Calendar.getInstance();
		Calendar anotherCalendar=Calendar.getInstance();


		int returnInt=0;
		try{
			Date thisDate = newFormatter.parse(this.date);
			Date thisTimeDate = formatter.parse(this.time);

			Calendar timeCalendar=Calendar.getInstance();
			timeCalendar.setTime(thisTimeDate);
			thisCalendar.setTime(thisDate);
			thisCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
			thisCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		}
		catch(Exception e){
			returnInt=1;
		}
		try {

			Date anotherDate = newFormatter.parse(another.date);
			Date anotherTimeDate = formatter.parse(another.time);

			Calendar anotherTimeCalendar=Calendar.getInstance();
			anotherTimeCalendar.setTime(anotherTimeDate);
			anotherCalendar.setTime(anotherDate);
			anotherCalendar.set(Calendar.HOUR_OF_DAY, anotherTimeCalendar.get(Calendar.HOUR_OF_DAY));
			anotherCalendar.set(Calendar.MINUTE, anotherTimeCalendar.get(Calendar.MINUTE));

		} catch (Exception e) {
			returnInt=-1;
		}

		if(thisCalendar.getTime()==null&&anotherCalendar.getTime()==null){
			returnInt= 0;
		}
		else if (thisCalendar.getTime()!=null&&anotherCalendar.getTime()!=null)
		{
			returnInt=thisCalendar.getTime().compareTo(anotherCalendar.getTime());
		}
		return returnInt;
	}

	public String toCSV() {
		//If Changing the structure of this also change the structure of the

		String commaDelimiter=",";
		StringBuilder builder=new StringBuilder();

		builder.append(date.replace(",", " "));
		builder.append(commaDelimiter);

		builder.append(time.replace(",", " "));
		builder.append(commaDelimiter);

		builder.append(getSpinnerItemString(this.mSeizureType).replace(",", " "));
		builder.append(commaDelimiter);

		builder.append(mSeizureDuration.getName().replace(",", " "));
		builder.append(commaDelimiter);

		builder.append(mEmegencyMedicationGiven);
		builder.append(commaDelimiter);

		builder.append(mAmbulanceCalled);
		builder.append(commaDelimiter);

		builder.append(buildMultiSelector(new ArrayList<ISpinnerItem>(mSeizureTriggers)));
		builder.append(commaDelimiter);

		builder.append(buildMultiSelector(new ArrayList<ISpinnerItem>(mSeizurePreFeatures)));
		builder.append(commaDelimiter);

		builder.append(buildMultiSelector(new ArrayList<ISpinnerItem>(mSeizureFeatures)));
		builder.append(commaDelimiter);

		builder.append(buildMultiSelector(new ArrayList<ISpinnerItem>(mSeizurePostFeatures)));
		builder.append(commaDelimiter);
		builder.append(desc);

		return builder.toString();
	}

}
