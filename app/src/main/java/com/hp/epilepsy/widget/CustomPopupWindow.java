package com.hp.epilepsy.widget;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableRow;
//import android.widget.Toast;
//
import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by elmalah on 3/2/2016.
 */
public class CustomPopupWindow extends PopupWindow implements View.OnClickListener {
    Context ctx;
    View anchoreView;
    PopupWindow popupWindow;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    static CustomPopupWindow customPopupWindow;
    TableRow tr_create_seizure_type, tr_create_trigger, tr_create_sms,tr_record_seizure;
    //    , tr_menu_email_csv, tr_record_seizure;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;


    public static CustomPopupWindow getCustomPopWindowInstance(){
        if (customPopupWindow==null){
            customPopupWindow=new CustomPopupWindow();
        }
        return customPopupWindow;
    }
    private CustomPopupWindow(){

    }

    public void getCustomPopupWindow(Context ctx, View achorView) {
        this.ctx = ctx;
        this.anchoreView = achorView;
    }

    public void showPopupWindow() {

        try {
            View popupView = ((Activity) ctx).getLayoutInflater().inflate(R.layout.popup_window, null);

            popupWindow = new PopupWindow(popupView,
                    700, LinearLayout.LayoutParams.WRAP_CONTENT);

            tr_create_seizure_type = (TableRow) popupView.findViewById(R.id.tr_create_seizure_type);
            tr_create_trigger = (TableRow) popupView.findViewById(R.id.tr_create_trigger);
            tr_create_sms = (TableRow) popupView.findViewById(R.id.tr_create_sms);
            //  tr_menu_email_csv = (TableRow) popupView.findViewById(R.id.tr_menu_email_csv);
            tr_record_seizure = (TableRow) popupView.findViewById(R.id.tr_record_seizure);

            tr_create_seizure_type.setOnClickListener(this);
            tr_create_trigger.setOnClickListener(this);
            tr_create_sms.setOnClickListener(this);
            //  tr_menu_email_csv.setOnClickListener(this);
            tr_record_seizure.setOnClickListener(this);


            expListView = (ExpandableListView) popupView.findViewById(R.id.lvExp);

            // preparing list data
            prepareListData();

            listAdapter = new ExpandableListAdapter(ctx, listDataHeader, listDataChild,popupWindow);

            // setting list adapter
            expListView.setAdapter(listAdapter);

            // Initialize more widgets from `popup_layout.xml`


            // If the PopupWindow should be focusable
            popupWindow.setFocusable(true);

            // If you need the PopupWindow to dismiss when when touched outside
            popupWindow.setBackgroundDrawable(new ColorDrawable());

            int location[] = new int[2];

            // Get the View's(the one that was clicked in the Fragment) location
            anchoreView.getLocationOnScreen(location);

            // Using location, the PopupWindow will be displayed right under anchorView

            popupWindow.showAtLocation(anchoreView, Gravity.NO_GRAVITY,
                    location[0], location[1] + anchoreView.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        try {
            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<String, List<String>>();

            // Adding child data
            listDataHeader.add(ctx.getString(R.string.seizure_Feature));
            // listDataHeader.add(ctx.getString(R.string.medication_Type));

            // Adding child data
            List<String> Seizure_Feature = new ArrayList<String>();
            Seizure_Feature.add(ctx.getString(R.string.create_pre_feature));
            Seizure_Feature.add(ctx.getString(R.string.create_During_Feature));
            Seizure_Feature.add(ctx.getString(R.string.create_post_feature));


            List<String> Medication_Type = new ArrayList<String>();
            Medication_Type.add(ctx.getString(R.string.create_Daily));
            Medication_Type.add(ctx.getString(R.string.create_Emergency));


            listDataChild.put(listDataHeader.get(0), Seizure_Feature);
            listDataChild.put(listDataHeader.get(1), Medication_Type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        try {
            Fragment fragment = null;
            Bundle args = new Bundle();
            String tagString = "";

            switch (v.getId()) {
                case R.id.tr_create_seizure_type:
                    fragment = new CreateSeizureTypeFragment();
                    tagString = ctx.getString(R.string.create_seizure_type_frag_tag);

                    popupWindow.dismiss();
                    break;

                case R.id.tr_create_trigger:
                    fragment =new CreateTriggerFragment();
                    tagString = ctx.getString(R.string.create_trigger_frag_tag);
                    popupWindow.dismiss();
                    break;
                case R.id.tr_create_sms:
                    fragment = new CreateSMSFragment();
                    tagString = ctx.getString(R.string.create_sms_frag_tag);
                    popupWindow.dismiss();
                    break;
               /* case R.id.tr_menu_email_csv:
                    //TODO here
                    fragment = new EmailSeizureEvents();
                    tagString = ctx.getString(R.string.email_csv_frag_tag);

                    popupWindow.dismiss();
                    break;
*/
                case R.id.tr_record_seizure:
                    Intent intent = new Intent(ctx, VideoRecorderView.class);
                    ctx.startActivity(intent);
                    popupWindow.dismiss();
                    break;

            }

            if (fragment != null) {
                fragment.setArguments(args);
                FragmentManager frgManager = ((HomeView) ctx).getFragmentManager();

                frgManager.beginTransaction().add(R.id.content_frame, fragment, tagString).addToBackStack(tagString).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    interface DismissPopupWindowListener{
        void onDismissPopupWindowListener(boolean flag);
    }
}
