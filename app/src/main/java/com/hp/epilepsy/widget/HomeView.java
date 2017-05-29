package com.hp.epilepsy.widget;

/**
 * @author Said Gamal
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.BackStackEntry;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hp.epilepsy.Manifest;
import com.hp.epilepsy.R;
import com.hp.epilepsy.Services.OnClearFromRecentService;
import com.hp.epilepsy.utils.AlarmReceiver;
import com.hp.epilepsy.utils.ApplicationBackgroundCheck;
import com.hp.epilepsy.utils.FileUtils;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.adapter.DrawerMenuAdapter;
import com.hp.epilepsy.widget.model.DrawerMenuItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class HomeView.
 */
public class HomeView extends Activity implements Runnable, CustomPopupWindow.DismissPopupWindowListener {
    private static final int IDLE_TIME = 3600000;
    private static final int NEW_MENU_ITEM = Menu.FIRST;
    private boolean backPressedToExitOnce = false;
    private static final int SAVE_MENU_ITEM = NEW_MENU_ITEM + 1;

    DBAdapter adapterr;
    int dialogFlag=0;
    public static CustomPopupWindow cpw;
    /**
     * The nav menu titles.
     */
    private String[] navMenuTitles;

    /**
     * The m drawer layout.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * The m drawer list.
     */
    private ListView mDrawerList;

    /**
     * The m drawer toggle.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * The m drawer title.
     */
    private CharSequence mDrawerTitle;

    /**
     * The m title.
     */
    private CharSequence mTitle;

    /**
     * The adapter.
     */
    private DrawerMenuAdapter adapter;

    /**
     * The data list.
     */
    private List<DrawerMenuItem> dataList;

    // UI components index
    /**
     * The Constant CALENDAR_VIEW.
     */
    public static final int MY_EPILEPSY_DIARY = 0;
    public static final int RECORD_SEIZURE_DETAILS_VIEW = 1;
    public static final int MY_DIALY_MEDICATION = 2;
    public static final int MY_EMERGENCY_MEDICATION = 3;
    public static final int MY_CONTACTS = 4;
    public static final int MY_APPOINTMENTS = 5;

    public static final int MY_VIDEO_GALLARY = 6;
    public static final int MY_REPORTS = 7;
    public static final int EMAIL_EVENTS = 8;
    public static final int CONTACT_US = 9;
    public static final int TERMS_AND_CONDITIONS = 10;
//    public static final int MY_CONTACTS = 8;

    public static final int LOGOUT = 11;


    private int currentPosition = 1;
    public static Handler customHandler = new Handler();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more infdormation.
     */
    private GoogleApiClient client;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    /**
     * -----------------------------------------------------------------------------
     * Class Name: onCreate
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * -----------------------------------------------------------------------------
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!ApplicationBackgroundCheck.isMyServiceRunning(OnClearFromRecentService.class,this));
        {
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
            ApplicationBackgroundCheck.setIncrement(getApplicationContext());
        }
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.home_view_layout);
            adapterr = new DBAdapter(this);
            adapterr.displayAllUsers();
            checkAppPermissions();
            navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
            dataList = new ArrayList<>();
            mTitle = mDrawerTitle = getTitle();
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerList = (ListView) findViewById(R.id.left_drawer);
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            dataList.add(new DrawerMenuItem(navMenuTitles[0]));
            dataList.add(new DrawerMenuItem(navMenuTitles[1]));
            dataList.add(new DrawerMenuItem(navMenuTitles[2]));
            dataList.add(new DrawerMenuItem(navMenuTitles[3]));
            dataList.add(new DrawerMenuItem(navMenuTitles[4]));
            dataList.add(new DrawerMenuItem(navMenuTitles[5]));
            dataList.add(new DrawerMenuItem(navMenuTitles[6]));
            dataList.add(new DrawerMenuItem(navMenuTitles[7]));
            dataList.add(new DrawerMenuItem(navMenuTitles[8]));
            dataList.add(new DrawerMenuItem(navMenuTitles[9]));
            dataList.add(new DrawerMenuItem(navMenuTitles[10]));
            dataList.add(new DrawerMenuItem(navMenuTitles[11]));

            adapter = new DrawerMenuAdapter(this, R.layout.drawer_item_layout, dataList);
            mDrawerList.setAdapter(adapter);
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);

            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name,
                    R.string.app_name) {
                public void onDrawerClosed(View view) {
                    getActionBar().setTitle(mTitle);
                    invalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                    getActionBar().setTitle(mDrawerTitle);
                    invalidateOptionsMenu();
                }
            };

            mDrawerLayout.setDrawerListener(mDrawerToggle);
            if (savedInstanceState == null) {
                String  userName = getIntent().getStringExtra(LoginActivity.USER_NAME);
                String type = getIntent().getStringExtra("type");
                String renewalMessage=getIntent().getStringExtra("message");
                int medId = getIntent().getIntExtra("id", 0);
                int presUniqueId=getIntent().getIntExtra("presUniqueId",0);
                int uniqueAppId=getIntent().getIntExtra("uniqueAppId",0);
                if (type != null) {
                    if (type.equalsIgnoreCase("Med")) {
                        Bundle args = new Bundle();
                        Fragment frag = new NewMedication();
                        String tagString = getString(R.string.NEW_MEDICATION_FRAGMENT);
                        args.putSerializable("MedicationID", medId);
                        args.putSerializable("userName", userName);
                        args.putSerializable("Type", type);
                        //Toast.makeText(getApplicationContext(),"Username inside main activity"+userName,Toast.LENGTH_LONG).show();
                        args.putInt("where", 3);
                        frag.setArguments(args);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, frag, tagString);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                        ft.addToBackStack(null);
                        ft.commit();
                    } else if (type.equalsIgnoreCase("PRES")) {
                        DBAdapter adapter= new DBAdapter(getApplicationContext());
                       adapter.getUpdatePresFlag(type,presUniqueId,"1");
                       ApplicationBackgroundCheck.showNotificationDialog("Prescription Renewal Alert", renewalMessage, "PRES",dialogFlag,getApplicationContext(),getFragmentManager());

                    } else if (type.equalsIgnoreCase("MED_TIT")) {
                        ApplicationBackgroundCheck.showNotificationDialog("Medication Titration Alert", renewalMessage, "MED_TIT",dialogFlag,getApplicationContext(),getFragmentManager());
                    } else if (type.equalsIgnoreCase("App")) {
                        DBAdapter adapter= new DBAdapter(getApplicationContext());
                        adapter.getUpdatePresFlag(type, uniqueAppId,"1");
                        String AppointmentMessage = getIntent().getStringExtra(AlarmReceiver.APPOINTMENT_MESSAGE);
                        ApplicationBackgroundCheck.showNotificationDialog("Appointment Alert", renewalMessage, "App",dialogFlag,getApplicationContext(),getFragmentManager());

                    } else if (type.equalsIgnoreCase("EME")) {
                        DBAdapter adapter= new DBAdapter(getApplicationContext());
                        adapter.getUpdateReminderFlag(type, medId,"1");
                        ApplicationBackgroundCheck.showNotificationDialog("Emergency Medication Alert", renewalMessage, "EME",dialogFlag,getApplicationContext(),getFragmentManager());

                    }
                    mDrawerList.setItemChecked(0, true);
                    mDrawerList.setSelection(0);
                } else {

                    Fragment fragment = new CalendarFragmentV2();
                    FragmentManager frgManager = getFragmentManager();
                    frgManager.beginTransaction().replace(R.id.content_frame, fragment, getString(R.string.calendar_frag_tag))
                            .addToBackStack(null).commit();
                    mDrawerList.setItemChecked(0, true);mDrawerList.setSelection(0);
                }

            } else {

                currentPosition = savedInstanceState.getInt("currentPosition");
                selectItem(currentPosition);
            }
            onUserInteraction();
        } catch (Exception e) {
            e.toString();
        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentPosition", currentPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPosition = savedInstanceState.getInt("currentPosition");
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public void checkAppPermissions() {

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //The user have conceded permission
                //Toast.makeText(HomeView.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //close the app or do whatever you want
                Toast.makeText(HomeView.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
//  .setDeniedMessage("If you reject permission,you can not use this service\nPlease turn on permissions at [Setting] > [Permission]")
        new TedPermission(this)
                .setPermissionListener(permissionlistener)

                .setPermissions(Manifest.permission.CALL_PHONE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,

                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }


    public void selectItem(int position) {
        try {
            currentPosition = position;
            Fragment fragment = null;
            Bundle args = new Bundle();
            Intent intent = null;
            String tagString = null;
            FragmentManager frgManager = getFragmentManager();
            mDrawerList.setItemChecked(0, true);
            goBackToCalenderView();
            switch (position) {
                case MY_EPILEPSY_DIARY:
                    fragment = new CalendarFragmentV2();
                    tagString = getString(R.string.record_siezure_detail_frag_tag);
                    break;
                case RECORD_SEIZURE_DETAILS_VIEW:
                    fragment = new RecordSeizureDetailsFragment();
                    tagString = getString(R.string.record_siezure_detail_frag_tag);
                    break;
                case MY_DIALY_MEDICATION:
                    fragment = new MedicationDairy();
                    tagString = getString(R.string.record_siezure_detail_frag_tag);
                    break;
                case MY_EMERGENCY_MEDICATION:

                    fragment = new EmergencyMedicationsList();
                    tagString = getString(R.string.EMERGENCY_MEDICATIONS_LIST);
                    //                Toast.makeText(this.getApplicationContext(), "My emergency medication ", Toast.LENGTH_LONG).show();
                    break;
                case MY_APPOINTMENTS:
                    fragment = new MyAppointments();
                    tagString = getString(R.string.Dairy_fragment);
                    break;
                case MY_REPORTS:
                    fragment = new MyReportsFragment();
                    tagString = getString(R.string.MY_REPORTS);
                    // Toast.makeText(this.getApplicationContext(), "My Reports ", Toast.LENGTH_LONG).show();
                    break;
                case EMAIL_EVENTS:
                    fragment = new EmailSeizureEvents();
                    tagString = getString(R.string.email_csv_frag_tag);
                    break;
                case MY_VIDEO_GALLARY:
                    args.putSerializable("videoFiles", (Serializable) FileUtils.getRecordedVideoFiles(LoginActivity.userName, this));
                    fragment = new VideoGalleryView(this);
                    tagString = getString(R.string.video_gallery_main_frag_tag);
                    break;
                case MY_CONTACTS:
                    fragment = new ContactsList();
                    tagString = getString(R.string.CONTACTS_LIST);
                    // Toast.makeText(this.getApplicationContext(), "My contacts ", Toast.LENGTH_LONG).show();
                    break;
                case TERMS_AND_CONDITIONS:
                    fragment = new TermsConditionsFragment();
                    tagString = getString(R.string.terms_and_conditions_frag_tag);
                    break;
                case CONTACT_US:
                    fragment = new ContactUsFragment();
                    tagString = getString(R.string.contact_us_frag_tag);
                    break;
                case LOGOUT:
                    LoginActivity.userName = null;
                    intent = new Intent(this, RecordVideoActivity.class);
                    startActivity(intent);
                    this.finish();
                    break;
                default:
                    break;
            }

            if (fragment != null) {
                fragment.setArguments(args);
                frgManager.beginTransaction().replace(R.id.content_frame, fragment, tagString).addToBackStack(null)
                        .commit();
            }
            mDrawerList.setItemChecked(position, true); // setTitle(dataList.get(possition).getItemName());

            mDrawerLayout.closeDrawer(mDrawerList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goBackToCalenderView() {
        try {
            while (getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().popBackStackImmediate();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                CalendarFragmentV2  fragment = new CalendarFragmentV2();
                String tagString = getString(R.string.record_siezure_detail_frag_tag);
                ft.replace(R.id.content_frame, fragment, tagString);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                mDrawerList.setItemChecked(0, true);
            }
        } catch (Exception ex) {
            Toast.makeText(this.getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }
    }


    /*
     * (non-Javadoc)
     * @see android.app.Activity#setTitle(java.lang.CharSequence)
     */
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onPostCreate(android.os.Bundle)
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration )
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        try {
            FragmentManager frgManager = getFragmentManager();
            int backStackCount = frgManager.getBackStackEntryCount();
            if (backStackCount > 1) {
                BackStackEntry bEnt = frgManager.getBackStackEntryAt(backStackCount - 1);
                Fragment mFragment = frgManager.findFragmentByTag(bEnt.getName());


                if (mFragment instanceof IStepScreen && (bEnt.getName().equals(getString(R.string.video_gallery_frag_tag)))) {
                    frgManager.popBackStack();
                } else if (mFragment instanceof IStepScreen && (bEnt.getName().equals(getString(R.string.Image_gallery_frag_tag)))) {
                    frgManager.popBackStack();
                } else {

                    goBackToCalenderView();
                }
            } else if (currentPosition == 0) {
                this.backPressedToExitOnce = true;
                Toast.makeText(this.getApplicationContext(), "Press again to exit", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backPressedToExitOnce = false;
                    }
                }, 3000);
            } else {
                //this code to exit the app if the user pressed twice on back button
                if (backPressedToExitOnce) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    this.backPressedToExitOnce = true;
                    Toast.makeText(this.getApplicationContext(), "Press again to exit", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            backPressedToExitOnce = false;
                        }
                    }, 3000);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.record_seizure_menu_item:
                startRecording();

                break;
            case R.id.more:

                cpw = CustomPopupWindow.getCustomPopWindowInstance();
                cpw.getCustomPopupWindow(this, findViewById(R.id.more));
                cpw.showPopupWindow();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public void onDismissPopupWindowListener(boolean flag) {
        cpw.dismiss();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("HomeView Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
    }



     @Override
    protected void onDestroy() {
         super.onDestroy();

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void startRecording() {
        Intent intent = new Intent(this, VideoRecorderView.class);
        startActivity(intent);
    }

    @Override
    public void onUserInteraction() {
        customHandler.removeCallbacks(this);
        customHandler.postDelayed(this, IDLE_TIME);
    }

    @Override
    protected void onUserLeaveHint() {
        onUserInteraction();
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: onNewIntent
     * Created By:Shruti and Nikunj
     * -----------------------------------------------------------------------------
     */
    @Override
    protected void onNewIntent(Intent intent) {

        Bundle bundle=intent.getExtras();
        int uniqueFlagID=1;
        if(bundle!=null && (bundle.getInt("uniqueAppId") > 0)) {
            DBAdapter adapter = new DBAdapter(getApplicationContext());
            adapter.getUpdatePresFlag(bundle.getString("type"), bundle.getInt("uniqueAppId"), "1");
            String AppointmentMessage1 = bundle.getString(AlarmReceiver.APPOINTMENT_MESSAGE);
            ApplicationBackgroundCheck.showNotificationDialog("Appointment Alert", AppointmentMessage1, "App",uniqueFlagID,getApplicationContext(),getFragmentManager());
        }
        if(bundle!=null && (bundle.getInt("presUniqueId") > 0)) {
            DBAdapter adapter = new DBAdapter(getApplicationContext());
            adapter.getUpdatePresFlag(bundle.getString("type"),bundle.getInt("presUniqueId"),"1");
            String RenewalMessage = bundle.getString(AlarmReceiver.PRESCRIPTION_RENEWAL_MESSAGE);
            ApplicationBackgroundCheck.showNotificationDialog("Prescription Renewal Alert", RenewalMessage, "PRES",uniqueFlagID,getApplicationContext(),getFragmentManager());
        }
        if(bundle!=null && (bundle.getString("type").equals("MED_TIT"))) {
            String TitrationMessage = bundle.getString(AlarmReceiver.MEICATION_TITRATION_MESSAGE);
            ApplicationBackgroundCheck.showNotificationDialog("Medication Titration Alert", TitrationMessage, "MED_TIT", uniqueFlagID,getApplicationContext(),getFragmentManager());
        }
        if(bundle!=null && (bundle.getString("type").equals("EME"))) {
            DBAdapter adapter= new DBAdapter(getApplicationContext());
            adapter.getUpdateReminderFlag(bundle.getString("type"), bundle.getInt("id"),"1");
            String EmergencyMedicationMessage = bundle.getString(AlarmReceiver.EMERGENCY_MEDICATION_MESSAGE);
            ApplicationBackgroundCheck.showNotificationDialog("Emergency Medication Alert", EmergencyMedicationMessage, "EME",uniqueFlagID,getApplicationContext(),getFragmentManager());

        }


    }

    @Override
    public void run() {
        customHandler.removeCallbacks(this);
        selectItem(LOGOUT);
    }



}
