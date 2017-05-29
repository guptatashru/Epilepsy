package com.hp.epilepsy.widget.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.MyReportsFragment;
import com.hp.epilepsy.widget.model.NewMedication;
import com.hp.epilepsy.widget.model.SeizureDetails;

import java.util.ArrayList;

/**
 * Created by elmalah on 12/2/2015.
 */
public class ReportsListAdapter extends BaseAdapter {
    public Activity context;
    public LayoutInflater inflater;
    ArrayList<SeizureDetails> siezuresList;
    ArrayList<NewMedication> medicationsList;
    public static ArrayList<SeizureDetails> selectedSiezuresList = new ArrayList<>();
    public static ArrayList<NewMedication> selectedMedicationsList = new ArrayList<>();
    int type;
    public static int checkBoxStateSeizure;
    public static int checkBoxStateMedication;
    private ArrayList<Boolean> toggle_state = new ArrayList<>();
    private String TAG="";

    public ReportsListAdapter(Activity context, ArrayList<SeizureDetails> siezuresList, ArrayList<NewMedication> medicationsList, int type) {
        super();
        this.context = context;
        this.siezuresList = siezuresList;
        this.medicationsList = medicationsList;
        this.type = type;

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (type == MyReportsFragment.SIEZURE_TYPE) {
            for (int i = 0; i < siezuresList.size(); i++) {
                toggle_state.add(i, false);
            }
        }
        else {
            for (int i = 0; i <medicationsList.size();i++){
                toggle_state.add(i,false);
            }
        }

    }

    @Override
    public int getCount() {

        if (type == MyReportsFragment.SIEZURE_TYPE)
            return siezuresList.size();
        else
            return medicationsList.size();

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        try {
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.report_item_layout, null);

                holder = new ViewHolder();
                holder.position = position;
                holder.switch_selection = (ToggleButton) convertView.findViewById(R.id.switch_selection);
                holder.title = (TextView) convertView.findViewById(R.id.title);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            if (type == MyReportsFragment.SIEZURE_TYPE) {
                holder.title.setText(siezuresList.get(position).getSeizureType().getName() + "");
            }
            else {
                holder.title.setText(medicationsList.get(position).getMedication_name() + "");
            }



            holder.switch_selection
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ToggleButton switchButton = (ToggleButton) v;
                            if (switchButton.isChecked()){
                                if (type == MyReportsFragment.SIEZURE_TYPE) {
                                    addToSelectedSiezures(siezuresList.get(position));
                                    toggle_state.set(position,true);
                                    checkBoxStateSeizure++;
                                }
                                else {
                                    addToSelectedMedications(medicationsList.get(position));
                                    toggle_state.set(position,true);
                                    checkBoxStateMedication++;
                                }


                            } else {
                                if (type == MyReportsFragment.SIEZURE_TYPE) {
                                    removeSiezureFromSelected(siezuresList.get(position));
                                    toggle_state.set(position,false);
                                    checkBoxStateSeizure--;
                                }
                                else {
                                    removeMedicationFromSelected(medicationsList.get(position));
                                    toggle_state.set(position,false);
                                    checkBoxStateMedication--;
                                }
                            }
                            if(checkBoxStateSeizure > 3)
                            {
                                toggle_state.set(position,false);
                                holder.switch_selection.setChecked(false);
                                checkBoxStateSeizure--;
                            }
                            if(checkBoxStateMedication > 6)
                            {
                                toggle_state.set(position,false);
                                holder.switch_selection.setChecked(false);
                                checkBoxStateMedication--;
                            }


                        }
                    });


            holder.switch_selection.setChecked(toggle_state.get(position));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public static class ViewHolder {
        ToggleButton switch_selection;
        TextView title;
        int position;

    }

    void addToSelectedSiezures(SeizureDetails seizureDetails) {
        if (selectedSiezuresList.size() < 3)
        {
            if (selectedSiezuresList.contains(seizureDetails))
            {

            }
            else {
                selectedSiezuresList.add(seizureDetails);
            }

        }

    }

    void addToSelectedMedications(NewMedication missedMedication) {
        if (selectedMedicationsList.size() < 6) {
            if (selectedMedicationsList.contains(missedMedication)) {
            } else {
                selectedMedicationsList.add(missedMedication);
            }
        }
    }

    void removeSiezureFromSelected(SeizureDetails siezureDetails) {
        selectedSiezuresList.remove(siezureDetails);
    }

    void removeMedicationFromSelected(NewMedication missedMedication) {
        selectedMedicationsList.remove(missedMedication);
    }
}
