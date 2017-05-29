package com.hp.epilepsy.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.widget.adapter.CustomReportLineChartAdapter;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.adapter.SimpleDividerItemDecoration;
import com.hp.epilepsy.widget.customComponents.MyMarkerView;
import com.hp.epilepsy.widget.model.MissedMedication;
import com.hp.epilepsy.widget.model.SeizureDetails;
import com.hp.epilepsy.widget.model.TakenMedication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ReportResultActivity extends AppCompatActivity {
    Toolbar toolbar;
    LineChart lineChart;
    BarChart stackedBarChart;
    ArrayList<SeizureDetails> seizuresList;
    ArrayList<MissedMedication> missedMedicationsList;
    int where;
    String start_Date, end_Date;
    TextView siezure1, siezure2, siezure3;
    TextView medication1, medication2, medication3, medication4, medication5, medication6;

    ArrayList<String> xVals;
    ArrayList<BarEntry> yVals1;
    ArrayList<Entry> lineyVals1;
    HashMap<Integer, ArrayList<com.hp.epilepsy.widget.model.NewMedication>> medicationsMap;
    HashMap<Integer, HashMap<Integer, ArrayList<com.hp.epilepsy.widget.model.NewMedication>>> ParentMedicationsMap;
    DBAdapter dbAdapter;

    String[] strMonth = {"Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"};
    Bitmap mbitmap;


    ArrayList<Integer> TakenMedicationsPositions = new ArrayList<>();
    CustomReportLineChartAdapter customReportLineChartAdapter;
    ArrayList<Boolean> TakenValues = new ArrayList<>();
    ArrayList<TextView> medicationLabels = new ArrayList<>();
    ArrayList<TextView> MonthsLabels = new ArrayList<>();

    TextView month1, month2, month3, month4, month5, month6;
    RecyclerView mRecyclerView;
    GridLayoutManager gridLayoutManager;

    ArrayList<com.hp.epilepsy.widget.model.NewMedication> MedicationsListObject;
    ArrayList<TakenMedication> TakenMedicationsList = new ArrayList<>();

    float lineChartMaxValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_result_activity);

        try {
            seizuresList = (ArrayList<SeizureDetails>) getIntent().getSerializableExtra(MyReportsFragment.SELECTED_SIEZURES);
            MedicationsListObject = (ArrayList<com.hp.epilepsy.widget.model.NewMedication>) getIntent().getSerializableExtra(MyReportsFragment.SELECTED_MEDICATIONS);
            this.where = getIntent().getIntExtra("where", 0);
            this.start_Date = getIntent().getStringExtra("start");
            this.end_Date = getIntent().getStringExtra("end");
            //ReportsListAdapter.selectedSiezuresList.clear();
            //ReportsListAdapter.selectedMedicationsList.clear();

            initializeView();

            //region calling Taken Medication Methods
            prepareMedicationMonths();
            setMedicationMonthsValues();
            PrepareTakenMedicationList(MedicationsListObject);
            setMedicationsLabels(MedicationsListObject);
            //endregion

            initToolBar();
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                    getApplicationContext()
            ));
            gridLayoutManager = new GridLayoutManager(this, 6);

            mRecyclerView.setLayoutManager(gridLayoutManager);
            //mRecyclerView.addItemDecoration(new DividerItemDecoration());
            customReportLineChartAdapter = new CustomReportLineChartAdapter(TakenValues);
            mRecyclerView.setAdapter(customReportLineChartAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public  void onPause()
    {
        super.onPause();
      /*  ApplicationBackgroundCheck.getDecrement(getApplicationContext());
        System.out.println("getCurrentValue1"+ApplicationBackgroundCheck.getCurrentValue(getApplicationContext()));*/
    }
    @Override
    protected void onResume()
    {
        super.onResume();
    }
    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_sort_down_48);
        toolbar.setLogo(R.drawable.logo);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toast.makeText(ReportResultActivity.this, "clicking the toolbar!", Toast.LENGTH_SHORT).show();
                        ReportResultActivity.this.finish();
                    }
                }
        );
    }


    public void initializeView() {

        try {
            dbAdapter = new DBAdapter(this);

            siezure1 = (TextView) findViewById(R.id.siezure1);
            siezure2 = (TextView) findViewById(R.id.siezure2);
            siezure3 = (TextView) findViewById(R.id.siezure3);

            medication1 = (TextView) findViewById(R.id.medication1);
            medication2 = (TextView) findViewById(R.id.medication2);
            medication3 = (TextView) findViewById(R.id.medication3);
            medication4 = (TextView) findViewById(R.id.medication4);
            medication5 = (TextView) findViewById(R.id.medication5);
            medication6 = (TextView) findViewById(R.id.medication6);

            medicationLabels.add(medication1);
            medicationLabels.add(medication2);
            medicationLabels.add(medication3);
            medicationLabels.add(medication4);
            medicationLabels.add(medication5);
            medicationLabels.add(medication6);

            month1 = (TextView) findViewById(R.id.month1);
            month2 = (TextView) findViewById(R.id.month2);
            month3 = (TextView) findViewById(R.id.month3);
            month4 = (TextView) findViewById(R.id.month4);
            month5 = (TextView) findViewById(R.id.month5);
            month6 = (TextView) findViewById(R.id.month6);

            MonthsLabels.add(month1);
            MonthsLabels.add(month2);
            MonthsLabels.add(month3);
            MonthsLabels.add(month4);
            MonthsLabels.add(month5);
            MonthsLabels.add(month6);

            xVals = new ArrayList<>();
            yVals1 = new ArrayList<>();
            lineyVals1 = new ArrayList<>();
            ParentMedicationsMap = new HashMap<>();

            bindXVals();
            bindLineChartYVals();

            lineChart = (LineChart) findViewById(R.id.lineChart);
            drawLineChart(missedMedicationsList);

            stackedBarChart = (BarChart) findViewById(R.id.stackedBarChart);
            if (seizuresList != null && seizuresList.size() > 0) {
                drawStackedBaChart(seizuresList);
            }else
            {
               // siezure1.setVisibility(View.GONE);
               // siezure2.setVisibility(View.GONE);
               // siezure3.setVisibility(View.GONE);
            }
            if (where == 1) {
                //take screen shot from the screen
                final LinearLayout layout = (LinearLayout) findViewById(R.id.contentParentView);
                layout.post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap pic = TakeScreenShot(layout);
                        try {
                            if (pic != null) {
                                saveScreenShoot(pic);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //region Taken Medications Methods
    //this method to set monthes text to textViews controls
    void prepareMedicationMonths() {
        xVals = new ArrayList<String>();
        int count = 0;
        int x = Integer.valueOf(start_Date.substring(5, 7));
        do {
            if (x >= 12)
                x = 0;
            xVals.add(strMonth[x]);
            x++;
            count++;
        } while (count < 6);
    }

    //endregion

    //this method to add monthes of taken medications
    void setMedicationMonthsValues() {
        for (int i = 0; i < 6; i++) {
            MonthsLabels.get(i).setText(xVals.get(i));
        }
    }

    //This method to prepare the positions of taken medu=ication that will be drawn on the recycleview adapter
    void PrepareTakenMedicationList(ArrayList<com.hp.epilepsy.widget.model.NewMedication> MedicationsList) {

        DBAdapter adapter = new DBAdapter(this);

        //this loop to get only taken medications from all medications
        //getMedicationStatus=1 means the medication is taken in its time
        for (int index = 0; index < MedicationsList.size(); index++) {
            ArrayList<TakenMedication> medication = adapter.getTakenMedications(MedicationsList.get(index).getMedication_name());
            // ArrayList<TakenMedication> medication = adapter.getTakenMedicationsTest(MedicationsList.get(index).getMedication_name());
            for (int i = 0; i < medication.size(); i++) {

                TakenMedicationsList.add(medication.get(i));
            }
        }

        //this loop to set the row and column for prparing the booleans list will be used in recycleView adapter
        for (int i = 0; i < TakenMedicationsList.size(); i++) {
            for (int j = 0; j < xVals.size(); j++) {
                if (DateTimeFormats.fromIsoFormatToDeafultFormat(TakenMedicationsList.get(i).getMedicationReminderDate()).contains(xVals.get(j))) {
                    TakenMedicationsList.get(i).setMonth(j);
                }
            }
        }

        int count = TakenMedicationsList.size();
        //this loop to check if there are multiple reminders in the same month related to the same medication..
        // if exist it will remove the redundent reminders because there is one line graph related to one medication per month..
        // even there are multiple reminders for this medication in the same month.
        for (int i = 0; i < count - 1; i++) {
            for (int j = i + 1; j < count; j++) {
                if (TakenMedicationsList.get(i).getMedicationName().contentEquals(TakenMedicationsList.get(j).getMedicationName()) && TakenMedicationsList.get(i).getMonth() == TakenMedicationsList.get(j).getMonth()) {
                    TakenMedicationsList.remove(j);
                    j--;
                    count--;
                }
            }
        }

        //this loop for geeting recycleView positions value from the row and column using this equation [(row+1)*6]-[6-column]
        //row = index of TakenMedicationsList
        //column = month number that gated from getMonth() property
        for (int k = 0; k < TakenMedicationsList.size(); k++) {
            int row = getIndexofMedication(TakenMedicationsList.get(k).getMedicationName());
            TakenMedicationsPositions.add(((row + 1) * 6) - (6 - TakenMedicationsList.get(k).getMonth()));
        }


        //this loop to initialize the TakenMedicationsPositions with its length
        //this loop will work till  Collections.max(TakenMedicationsPositions)+1 just in case will need to set the lase value with true if we don't add +1 to the loop count it will throw indexoutofboundException
        if (TakenMedicationsPositions.size() > 0) {
            for (int i = 0; i < Collections.max(TakenMedicationsPositions) + 1; i++) {

                TakenValues.add(false);
            }
        }

        //this loop for putting true value for positions that we will appear the graph in it
        for (int x = 0; x < TakenMedicationsPositions.size(); x++) {
            TakenValues.set(TakenMedicationsPositions.get(x), true);
        }

    }


    public int getIndexofMedication(String MedicationName) {
        int value = 0;

        for (int i = 0; i < MedicationsListObject.size(); i++) {
            if (MedicationsListObject.get(i).getMedication_name().contentEquals(MedicationName)) {
                value = i;
                continue;
            }
        }
        return value;
    }

    //this method to set medication names to textViews controls and gone others empty textviews
    void setMedicationsLabels(List<com.hp.epilepsy.widget.model.NewMedication> MedicationsList) {

        for (int i = 0; i < MedicationsList.size(); i++) {
            medicationLabels.get(i).setText(MedicationsList.get(i).getMedication_name());
            medicationLabels.get(i).setVisibility(View.VISIBLE);
        }
    }
    //endregion

    void bindXVals() {
        int count = 0;
        int x = Integer.valueOf(start_Date.substring(5, 7));
        do {
            count++;

            if (x >= 12)
                x = 0;
            xVals.add(strMonth[x]);
            x++;

        } while (count < 6);
    }

    void bindStackedBarChartYVals() {

        int startCountInd = 0;
        int endCountInd = 1;
        for (int i = 0; i < 6; i++) {

            Calendar startTempCal = Calendar.getInstance();
            startTempCal.setTime(DateTimeFormats.convertStringToDate(start_Date));
            startTempCal.add(Calendar.MONTH, startCountInd);

            Calendar endTempCal = Calendar.getInstance();
            endTempCal.setTime(DateTimeFormats.convertStringToDate(start_Date));
            endTempCal.add(Calendar.MONTH, endCountInd);

            startCountInd++;
            endCountInd++;
            ArrayList<Float> xvals = new ArrayList<>();
            for (int j = 0; j < seizuresList.size(); j++) {
                // get seizure osscurance for three seizures per month
                ArrayList<SeizureDetails> resultArray = dbAdapter.getOccurenceSeizuresBetweenDates(DateTimeFormats.convertDateToStringWithIsoFormat(startTempCal.getTime()), DateTimeFormats.convertDateToStringWithIsoFormat(endTempCal.getTime()), (int) seizuresList.get(j).getSeizureType().getId());
                int seizureOneOccurence = resultArray.size();
                xvals.add((float) seizureOneOccurence);

            }
            float[] namesArr = new float[xvals.size()];
            BarEntry barEntry;
            for (int k = 0; k < xvals.size(); k++) {
                namesArr[k] = xvals.get(k);
            }

            barEntry = new BarEntry(namesArr, i);
            yVals1.add(barEntry);
        }
    }


    void bindLineChartYVals() {

        int startCountInd = 0;
        int endCountInd = 1;
        for (int i = 0; i < 6; i++) {

            Calendar startTempCal = Calendar.getInstance();
            startTempCal.setTime(DateTimeFormats.convertStringToDate(start_Date));
            startTempCal.add(Calendar.MONTH, startCountInd);

            Calendar endTempCal = Calendar.getInstance();
            endTempCal.setTime(DateTimeFormats.convertStringToDate(start_Date));
            endTempCal.add(Calendar.MONTH, endCountInd);

            startCountInd++;
            endCountInd++;
            float namesArr = 0;
            medicationsMap = new HashMap<>();
            for (int j = 0; j < MedicationsListObject.size(); j++) {
                // get seizure osscurance for three seizures per month
                ArrayList<com.hp.epilepsy.widget.model.NewMedication> resultArray = dbAdapter.getSpecificMissedMedicationsBetweenDates(DateTimeFormats.convertDateToStringWithIsoFormat(startTempCal.getTime()), DateTimeFormats.convertDateToStringWithIsoFormat(endTempCal.getTime()), MedicationsListObject.get(j).getMedication_name());
                int medicationOccurence = resultArray.size();
//                xvals.add((float)seizureOneOccurence);

                medicationsMap.put(j, resultArray);
                namesArr += (float) medicationOccurence;

            }
            if (namesArr > lineChartMaxValue)
                lineChartMaxValue = namesArr;

            ParentMedicationsMap.put(i, medicationsMap);
            Entry lineEntry;
            lineEntry = new Entry(namesArr, i);
            lineyVals1.add(lineEntry);
        }
    }

    void drawStackedBaChart(ArrayList<SeizureDetails> seizuresList) {


        bindStackedBarChartYVals();
        setSiezuresNames(seizuresList);
        BarDataSet set1;

        if (stackedBarChart.getData() != null &&
                stackedBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) stackedBarChart.getData().getDataSetByIndex(0);

            stackedBarChart.getData().notifyDataChanged();
            stackedBarChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "");
            set1.setColors(getColors());

            String[] namesArr = new String[seizuresList.size()];

            for (int k = 0; k < seizuresList.size(); k++) {
                namesArr[k] = seizuresList.get(k).getSeizureType().getName();
            }
            set1.setStackLabels(namesArr);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(xVals, dataSets);

            stackedBarChart.setData(data);
        }

        stackedBarChart.invalidate();
        stackedBarChart.getLegend().setEnabled(false);
        stackedBarChart.setDescription("");
        // drawn
        stackedBarChart.setMaxVisibleValueCount(50);
        stackedBarChart.setPinchZoom(false);

        stackedBarChart.setDrawGridBackground(true);
        stackedBarChart.setDrawBarShadow(false);
        stackedBarChart.getAxisRight().setEnabled(false);
        stackedBarChart.setDrawValueAboveBar(false);

        // change the position of the y-labels
        YAxis leftAxis = stackedBarChart.getAxisLeft();
        leftAxis.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return new DecimalFormat("###,###,###,##0.0").format(v);
            }
        });
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        XAxis xLabels = stackedBarChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);

    }

    void drawLineChart(ArrayList<MissedMedication> missedMedicationsList) {

        LineDataSet dataset = new LineDataSet(lineyVals1, "");

        dataset.setCircleColor(Color.RED);
        dataset.setColor(Color.RED);
        dataset.setLineWidth(1.5f);
        dataset.setCircleRadius(5.0f);

        LineData data = new LineData(xVals, dataset);
        lineChart.setData(data); // set the data and list of lables into chart
        final MyMarkerView marker = new MyMarkerView(this, R.layout.marker_layout, ParentMedicationsMap);

        lineChart.setMarkerView(marker);
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                //display msg when value selected
                if (entry == null)
                    return;
            }
            @Override
            public void onNothingSelected() {

            }
        });
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return new DecimalFormat("###,###,###,##0.0").format(v);
            }
        });
        leftAxis.setAxisMinValue(0f);
        leftAxis.setAxisMaxValue(lineChartMaxValue + 5);
        lineChart.setDescription("");  // set the description
        XAxis xLabels = lineChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);
    }


    private int[] getColors() {
        int stacksize = seizuresList.size();
        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];
        for (int i = 0; i < stacksize; i++) {
            colors[i] = getResources().getIntArray(R.array.colorNameList)[i];
        }
        return colors;
    }

    void setSiezuresNames(ArrayList<SeizureDetails> seizuresList) {
        if (seizuresList.size() > 0) {
            if (seizuresList.get(0).getSeizureType().getName().length() > 0 && seizuresList.get(0).getSeizureType().getName() != null)
                siezure1.setText(seizuresList.get(0).getSeizureType().getName());
            else
                //siezure1.setText("");
                siezure1.setVisibility(View.GONE);
            // setMedication2
            if (seizuresList.size() > 1 && seizuresList.get(1).getSeizureType().getName().length() > 0 && seizuresList.get(1).getSeizureType().getName() != null)
                siezure2.setText(seizuresList.get(1).getSeizureType().getName());
            else
               // siezure2.setText("");
                siezure2.setVisibility(View.GONE);
            //setMedication 3
            if (seizuresList.size() > 2 && seizuresList.get(2).getSeizureType().getName().length() > 0 && seizuresList.get(2).getSeizureType().getName() != null)
                siezure3.setText(seizuresList.get(2).getSeizureType().getName());
            else
               // siezure3.setText("");
                siezure3.setVisibility(View.GONE);
        }
    }

    //sending email dialog
    void showSendMailDialog(String path) {
        ReportMailDialogFragment reportMailDialogFragment = new ReportMailDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("PATH", path);
        bundle.putString("FromDate", start_Date);
        bundle.putString("ToDate", end_Date);
        reportMailDialogFragment.setArguments(bundle);
        reportMailDialogFragment.show(getSupportFragmentManager(), "");
    }


    private Bitmap TakeScreenShot(View v) {
        Bitmap screebshot = null;
        //Get width and hight from view
        try {
            int width = v.getMeasuredWidth();
            int hight = v.getMeasuredHeight();
            screebshot = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888);
            //Draw the canvas
            Canvas c = new Canvas(screebshot);
            v.draw(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screebshot;
    }

    private void saveScreenShoot(Bitmap bm) {
        ByteArrayOutputStream bao = null;
        File file = null;
        String uuid = UUID.randomUUID().toString();
        try {
            bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 50, bao);
            file = new File(Environment.getExternalStorageDirectory() + File.separator +uuid+".png");
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bao.toByteArray());
            fos.close();
            //send the screen shot by email
            showSendMailDialog(Environment.getExternalStorageDirectory() + File.separator +uuid+".png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****************************************************************************************************/
}
