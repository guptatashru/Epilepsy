package com.hp.epilepsy.widget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hp.epilepsy.R;

public class CalenderEventsListActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_events_list);
        initToolBar();

        initializeView(savedInstanceState);

    }
    @Override
    public  void onPause()
    {
        super.onPause();

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
               // Toast.makeText(CalenderEventsListActivity.this, "clicking the toolbar!", Toast.LENGTH_SHORT).show();
                CalenderEventsListActivity.this.finish();
            }
        }

        );


    }


    public void initializeView(Bundle savedInstanceState)
    {
        try {
            if (findViewById(R.id.calender_list_event_fragment_container) != null) {


                // However, if we're being restored from a previous state,
                // then we don't need to do anything and should return or else
                // we could end up with overlapping fragments.
                if (savedInstanceState != null) {

                    return;
                }

                // Create an instance of ExampleFragment
                Bundle args = new Bundle();
                CalenderEventsListFragment calenderEventsListFragment = new CalenderEventsListFragment();

                // Add the fragment to the 'fragment_container' FrameLayout
                getFragmentManager().beginTransaction()
                        .replace(R.id.calender_list_event_fragment_container, calenderEventsListFragment).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
