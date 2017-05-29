package com.hp.epilepsy.widget;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.CalenderEventsListCustomAdapter;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.adapter.MedicationsListViewCustomAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalenderEventsListFragment extends Fragment {

    Map<String, HashMap<String, List>> datacollection ;
    ListView event_group_Listview;
    CalenderEventsListCustomAdapter calenderEventsListCustomAdapter;
    DBAdapter adapter;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        rootView = inflater.inflate(R.layout.activity_calender_events_list_fragment, container, false);
        initializeViews(rootView);
        return rootView;
    }

    void initializeViews(View rootView){
        try {
            adapter=new DBAdapter(this.getActivity());
            datacollection= adapter.getCalendarListData();
            event_group_Listview=(ListView)rootView.findViewById(R.id.list_view);
            calenderEventsListCustomAdapter=new CalenderEventsListCustomAdapter(this.getActivity(),datacollection);
            event_group_Listview.setAdapter(calenderEventsListCustomAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
