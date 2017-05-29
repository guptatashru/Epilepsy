package com.hp.epilepsy.widget.adapter;

        import android.annotation.SuppressLint;
        import android.app.Fragment;
        import android.app.FragmentManager;
        import android.content.Context;
        import android.content.res.Resources;
        import android.os.Bundle;
        import android.text.format.DateFormat;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;

        import com.hp.epilepsy.R;
        import com.hp.epilepsy.widget.HomeView;
        import com.hp.epilepsy.widget.SeizureDetailsFragment;
        import com.hp.epilepsy.widget.SeizuresList;
        import com.hp.epilepsy.widget.model.SeizureDetails;

        import java.io.Serializable;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.GregorianCalendar;
        import java.util.HashMap;
        import java.util.List;

public class GridCellAdapter extends BaseAdapter implements OnClickListener {
    private static final int DAY_OFFSET = 1;
    private static String ISO8601DatePattern = "yyyy-MM-dd'T'HH:mm:ss";
    private final Context context;
    private final List<String> list;
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31,
            30, 31};
    List<String> seizuresWithVideo;
    List<String> seizuresWithoutVideo;
    private int monthDays;
    private int currentMonth;
    private int currentYear;
    private TextView gridcell;

    public GridCellAdapter(Context context, int textViewResourceId, int month,
                           int year) {
        super();
        this.context = context;
        this.list = new ArrayList<String>();
        this.currentMonth = month;
        this.currentYear = year;
        DBAdapter dbAdapter = new DBAdapter(context);
        HashMap<String, List<String>> items = dbAdapter.getSeizuresPerMonth(currentMonth - 1, currentYear);
        seizuresWithVideo = items.get("withVideo");
        seizuresWithoutVideo = items.get("withoutVideo");
        printMonth(currentMonth, currentYear);
    }

    @SuppressLint("SimpleDateFormat")
    private static boolean isDateInList(List<String> seizureList, Calendar currentDayCalendar) {
        boolean sameDay = false;

        for (String seizureDate : seizureList) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(ISO8601DatePattern);
            Calendar seizureCalendar = Calendar.getInstance();
            try {
                seizureCalendar.setTime(dateFormat.parse(seizureDate));
            } catch (ParseException e) {
                return sameDay;
            }
            if (currentDayCalendar.get(Calendar.YEAR) == seizureCalendar.get(Calendar.YEAR) && currentDayCalendar.get(Calendar.DAY_OF_YEAR) == seizureCalendar.get(Calendar.DAY_OF_YEAR)) {
                sameDay = true;
                return sameDay;
            }

        }

        return sameDay;
    }

    private int getNumberOfMonthDays(int i) {
        return daysOfMonth[i];
    }

    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void printMonth(int mm, int yy) {
        try {
            int trailingSpaces = 0;
            int daysInPrevMonth = 0;
            int prevMonth = 0;
            int month = mm - 1;
            monthDays = getNumberOfMonthDays(month);

            GregorianCalendar cal = new GregorianCalendar(yy, month, 1);

            if (month == 11) {
                prevMonth = month - 1;
                daysInPrevMonth = getNumberOfMonthDays(prevMonth);

            } else if (month == 0) {
                prevMonth = 11;
                daysInPrevMonth = getNumberOfMonthDays(prevMonth);

            } else {
                prevMonth = month - 1;
                daysInPrevMonth = getNumberOfMonthDays(prevMonth);

            }

            int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            trailingSpaces = currentWeekDay;

            if (cal.isLeapYear(cal.get(Calendar.YEAR)))
                if (mm == 2)
                    ++monthDays;
                else if (mm == 3)
                    ++daysInPrevMonth;

            for (int i = 0; i < trailingSpaces; i++) {
                list.add(String
                        .valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
                                + i)
                        + "-" + R.color.white + "-" + R.color.white);// white
            }
            Calendar calendar = Calendar.getInstance();
            for (int i = 1; i <= monthDays; i++) {
                calendar.setTime(new Date());
                String today = DateFormat
                        .format(ISO8601DatePattern, calendar.getTime()).toString();

                calendar.set(yy, month, i);
                String current = DateFormat.format(ISO8601DatePattern,
                        calendar.getTime()).toString();

                if (today.equalsIgnoreCase(current)) {// today
                    if (isDateInList(seizuresWithVideo, calendar)) {
                        // today with seizures with video
                        list.add(String.valueOf(i) + "-" + R.color.green + "-"
                                + R.drawable.solid_blue_red_outline);
                    } else if (isDateInList(seizuresWithoutVideo, calendar)) {
                        // today with seizures without video
                        list.add(String.valueOf(i) + "-" + R.color.green + "-"
                                + R.drawable.solid_blue);
                    } else {
                        // today
                        list.add(String.valueOf(i) + "-" + R.color.black + "-"
                                + R.drawable.white_blue_outline);
                    }
                } else {
                    if (isDateInList(seizuresWithVideo, calendar)) {
                        // seizures with video
                        list.add(String.valueOf(i) + "-" + R.color.white + "-"
                                + R.drawable.solid_blue_red_outline);
                    } else if (isDateInList(seizuresWithoutVideo, calendar)) {
                        // seizures without video
                        list.add(String.valueOf(i) + "-" + R.color.white + "-"
                                + R.drawable.solid_blue);
                    } else {
                        // no seizures
                        list.add(String.valueOf(i) + "-" + R.color.gray + "-"
                                + android.R.color.transparent);
                    }
                }
            }

            for (int i = 0; i < list.size() % 7; i++) {
                list.add(String.valueOf(i + 1) + "-" + R.color.white + "-"
                        + R.color.white);// white
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Integer> findNumberOfMonthEvents(int year, int month) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        return map;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        try {
            row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.gridcell_design, parent, false);
            }

            gridcell = (TextView) row.findViewById(R.id.day_gridcell);
            gridcell.setOnClickListener(this);
            String[] day_color = list.get(position).split("-");
            String theday = day_color[0];

            gridcell.setText(theday);
            gridcell.setTextColor(context.getResources().getColor(
                    (Integer.parseInt(day_color[1]))));
            row.setBackgroundResource(Integer.parseInt(day_color[2]));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return row;
    }

    @SuppressWarnings("unused")
    @SuppressLint("SimpleDateFormat")
    @Override
    public void onClick(View view) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(currentYear, currentMonth - 1,
                    Integer.parseInt(((TextView) view).getText().toString()));
            SimpleDateFormat dateFormat = new SimpleDateFormat(ISO8601DatePattern);
            DBAdapter adapter = new DBAdapter(context);

            List<SeizureDetails> seizures = adapter.getSeizuresPerDate(calendar.getTime());

            Bundle args = new Bundle();

            if (seizures.size() == 1) {
                args.putSerializable("seizureDetails", seizures.get(0));
                Fragment fragment = new SeizureDetailsFragment();
                if (fragment != null) {
                    fragment.setArguments(args);
                    FragmentManager frgManager = ((HomeView) context)
                            .getFragmentManager();
                    frgManager.beginTransaction().replace(R.id.content_frame, fragment, this.context.getString(R.string.seizure_detail_frag_tag)).addToBackStack(this.context.getString(R.string.seizure_detail_frag_tag))
                            .commit();
                }

            } else if (seizures.size() != 0) {
                args.putSerializable("seizureDetails", (Serializable) seizures);
                args.putString("SeizureListDate", seizures.get(1).getDate());
                Fragment fragment = new SeizuresList();
                if (fragment != null) {
                    fragment.setArguments(args);
                    FragmentManager frgManager = ((HomeView) context)
                            .getFragmentManager();
                    frgManager.beginTransaction().replace(R.id.content_frame, fragment, this.context.getString(R.string.seizure_list_frag_tag)).addToBackStack(this.context.getString(R.string.seizure_list_frag_tag))
                            .commit();
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }
}
