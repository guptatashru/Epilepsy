package com.hp.epilepsy.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.model.MissedMedication;

import java.util.ArrayList;
import java.util.Collections;
public class CustomReportLineChartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<Boolean> TakenMedicationsPositions;

    public CustomReportLineChartAdapter(ArrayList<Boolean> TakenMedicationsPositions) {
        this.TakenMedicationsPositions = TakenMedicationsPositions;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_report_row, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Holder holderObj = (Holder) holder;

   if(position<TakenMedicationsPositions.size()) {
       try {
           //TODO replace the colors with accurate colors of medications
           if (TakenMedicationsPositions.get(position)) {
               holderObj.viewSeparator.setVisibility(View.VISIBLE);
               if (position >= 0 && position <= 5) {
                   holderObj.viewSeparator.setBackgroundResource(R.color.Medication1_color);
               }  else if (position >= 6 && position <= 11) {
                   holderObj.viewSeparator.setBackgroundResource(R.color.Medication2_color);
               } else if (position >= 12 && position <= 17) {
                   holderObj.viewSeparator.setBackgroundResource(R.color.Medication3_color);
               } else if (position >= 18 && position <= 23) {
                   holderObj.viewSeparator.setBackgroundResource(R.color.Medication4_color);
               } else if (position >= 24 && position <= 29) {
                   holderObj.viewSeparator.setBackgroundResource(R.color.Medication5_color);
               } else if (position >= 30 && position <= 35) {
                   holderObj.viewSeparator.setBackgroundResource(R.color.Medication6_color);
               }
           } else {
               holderObj.viewSeparator.setVisibility(View.GONE);

           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
    }


    @Override
    public int getItemCount() {
        return TakenMedicationsPositions.size();
    }
    public static class Holder extends RecyclerView.ViewHolder {
        public View viewSeparator;
        public Holder(View v) {
            super(v);
            viewSeparator =  v.findViewById(R.id.item_separator);

        }
    }
}
