package com.hp.epilepsy.widget;

import android.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.GridCellAdapter;

import java.util.Calendar;
import java.util.Locale;

public class CalendarFragment extends Fragment implements OnClickListener {

	private static final String monthTemplate = " MMMM ";
	private static final String yearTemplate = " yyy ";
	float x1, x2;
	float y1, y2;
	private Spinner monthList;
	private TextView currentYear;
	private TextView currentMonth;
	private Button prevMonth;
	private Button nextMonth;
	private GridCellAdapter gridCellAdapter;
	private GridView calendarView;
	private Calendar calendar;
	private int month, year;
	public CalendarFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_calendar, container,
				false);
		return rootView;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setUpCalendar(getView());
	}
	
	
	public void setUpCalendar(View view)
	{
		try {
			calendar = Calendar.getInstance(Locale.getDefault());
			month = calendar.get(Calendar.MONTH) + 1;
			year = calendar.get(Calendar.YEAR);

			monthList = (Spinner) getView().findViewById(R.id.monthList);
			prevMonth = (Button) view.findViewById(R.id.prevMonth);
			prevMonth.setOnClickListener(this);
			currentYear = (TextView) view.findViewById(R.id.year);
			currentYear
                    .setText(DateFormat.format(yearTemplate, calendar.getTime()));
			currentMonth = (TextView) view.findViewById(R.id.currentMonth);
			currentMonth.setText(DateFormat.format(monthTemplate,
                    calendar.getTime()));
			nextMonth = (Button) view.findViewById(R.id.nextMonth);
			nextMonth.setOnClickListener(this);
			calendarView = (GridView) view.findViewById(R.id.calendar);
			gridCellAdapter = new GridCellAdapter(getActivity(), R.id.day_gridcell,
                    month, year);
			gridCellAdapter.notifyDataSetChanged();
			calendarView.setAdapter(gridCellAdapter);

			final String items[] = { "January", "February", "March", "April",
                    "May", "June", "July", "August", "September", "October",
                    "November", "December" };
			ArrayAdapter<String> adapt = new ArrayAdapter<String>(getActivity(),
                    R.layout.spinner_design, items);

			monthList.setAdapter(adapt);
			int selectedMonth = month;
			monthList.setSelection(selectedMonth - 1);
			monthList
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        public void onItemSelected(AdapterView<?> parent,
                                View view, int position,

                                long id) {

                            currentMonth.setText(items[position]);

                            setGridCellAdapterDate(position + 1, year);

                            if (position >= 0 && position <= 11) {
                                month = position + 1;
                            }
                        }

                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void setGridCellAdapterDate(int month, int year) {
		try {
			gridCellAdapter = new GridCellAdapter(getActivity(), R.id.day_gridcell,
                    month, year);
			calendar.set(year, month - 1, calendar.get(Calendar.DAY_OF_MONTH));
			currentMonth.setText(DateFormat.format(monthTemplate,
                    calendar.getTime()));
			currentYear
                    .setText(DateFormat.format(yearTemplate, calendar.getTime()));
			gridCellAdapter.notifyDataSetChanged();
			calendarView.setAdapter(gridCellAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onClick(View v) {
		try {
			if (v.getId() == R.id.prevMonth) {
                if (month == 1) {
                    month = 12;
                    year--;
                } else {
                    month--;

                }

            } else if (v.getId() == R.id.nextMonth) {
                if (month == 12) {
                    month = 1;
                    year++;
                } else {
                    month++;
                }

            }
			setGridCellAdapterDate(month, year);
			int selectedMonth = month;
			monthList.setSelection(selectedMonth-1);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
