package com.hp.epilepsy.widget;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.hp.epilepsy.Manifest;
import com.hp.epilepsy.R;
import com.hp.epilepsy.hardware.camera.CameraPreview;
import com.hp.epilepsy.utils.AlarmHelper;
import com.hp.epilepsy.utils.AlarmReceiver;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.utils.FileUtils;
import com.hp.epilepsy.utils.InstallationIdentity;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.model.EmergencyMedicationEntity;
import com.hp.epilepsy.widget.model.IimagesSetter;
import com.hp.epilepsy.widget.model.MedicationReminders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * -----------------------------------------------------------------------------
 * Class Name: MyEmergencyMedicationsFragment
 * Created By:Mahmoud
 * Modified By:Shruti and Nikunj
 * -----------------------------------------------------------------------------
 */

public class MyEmergencyMedicationsFragment extends Fragment implements View.OnClickListener, IimagesSetter {

    List<Date> medRemindersDate = new ArrayList<Date>();

    EmergencyMedicationEntity emergencyMedicationEntity;
    AlertDialog alertDialog;
    ImageButton ibtn_info;
    EditText et_medication_name, et_dosage, et_expirey_date;
    LinearLayout linear_btn_save, linear_btn_delete;
    Spinner sp_unit;
    LinearLayout reminderConfig_layout;
    RadioButton RB_1, RB_2, RB_3;
    SimpleDateFormat dateFormatter;
    Calendar newDate;
    DatePicker dpStartDate;
    ArrayAdapter<String> Spinneradapter;
    ImageView imgV_camera;
    ImageSwitcher mswitcher;
    com.hp.epilepsy.widget.NewMedication.ImageAdapter mImageadapter;
    Button switchh;
    List<String> images_paths;
    int currImage = 0;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    static final int REQUEST_CAMERA = 1;
    static final int REQUEST_WRITE_STORAGE = 6;
    static final int SELECT_FILE = 2;
    static final int REMINDERS_REQUEST = 3;
    static final int TITRATION_REQUEST = 4;
    String timestampp;
    ImageView ivImage;

    DBAdapter dbAdapter;
    private boolean Update = false;
    private int emergency_MedicationID;
    int where;
    String UserNme;
    String intentType;

    public MyEmergencyMedicationsFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = null;
        rootView = inflater.inflate(R.layout.fragment_my_emergency_medications2, container, false);
        initializeViews(rootView);
        onBackeKeyPressed(rootView);
        return rootView;
    }



    void initializeViews(View rootView) {
        try {
            emergencyMedicationEntity = new EmergencyMedicationEntity();
            images_paths = new ArrayList<String>();
            dbAdapter = new DBAdapter(this.getActivity());
            ibtn_info = (ImageButton) rootView.findViewById(R.id.ibtn_info);
            et_medication_name = (EditText) rootView.findViewById(R.id.et_medication_name);
            et_dosage = (EditText) rootView.findViewById(R.id.et_dosage);
            et_expirey_date = (EditText) rootView.findViewById(R.id.et_expirey_date);
            linear_btn_save = (LinearLayout) rootView.findViewById(R.id.linear_btn_save);
            linear_btn_delete = (LinearLayout) rootView.findViewById(R.id.linear_btn_delete);
            sp_unit = (Spinner) rootView.findViewById(R.id.sp_unit);
            Spinneradapter = new ArrayAdapter<>(this.getActivity(), R.layout.question_spinner_item, getResources().getStringArray(R.array.unit));
            sp_unit.setAdapter(Spinneradapter);
            imgV_camera = (ImageView) rootView.findViewById(R.id.imgV_camera);
            et_expirey_date.setOnClickListener(this);
            ibtn_info.setOnClickListener(this);
            linear_btn_save.setOnClickListener(this);
            linear_btn_delete.setOnClickListener(this);
            imgV_camera.setOnClickListener(this);
            imageGalleryHandle(rootView);


            emergency_MedicationID = (int) getArguments().getSerializable("MedicationID");
            where= (int) getArguments().getInt("where", 0);
            UserNme = getArguments().getString("userName");
            if (emergency_MedicationID != -1) {
                GetEmergencyMedicationDatefromDB(emergency_MedicationID);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    void NotificationDialog(String Title,String Message ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Message)
                .setCancelable(false)
                .setTitle(Title);

        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void GetEmergencyMedicationDatefromDB(int MedicationID) {
        try {
            Update = true;
            if (where==3){
                LoginActivity.userName=UserNme;

                intentType = getArguments().getString("Type");
                if(intentType.equals("EME"))
                {
                    String EmergencyMessage=getArguments().getString(AlarmReceiver.EMERGENCY_MEDICATION_MESSAGE);
                    NotificationDialog("Emergency Medication Alert",EmergencyMessage);
                }
            }
            emergencyMedicationEntity = dbAdapter.getEmergencyMedicationbyID(MedicationID);

            for (int j = 0; j < emergencyMedicationEntity.getMedicationImages().size(); j++) {
                if (emergencyMedicationEntity.getMedicationImages().size() >= 1) {
                    images_paths.add(emergencyMedicationEntity.getMedicationImages().get(j).toString());
                }

            }

            //check if the number of images in equal or greater than 2 to visable or collapse the Switch button.
            if(images_paths.size()>=2)
            {
                switchh.setVisibility(View.VISIBLE);
            }

            emergencyMedicationEntity.getId();
            fillFields(emergencyMedicationEntity.getEmergencyMedicationName(), emergencyMedicationEntity.getExpiryDate(), emergencyMedicationEntity.getDosage(), emergencyMedicationEntity.getUnit());
            MedicationReminders reminder;
            reminder = dbAdapter.getEmergencyMedicationReminders(MedicationID);
//        Calendar EndTime = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormats.isoDateFormat, Locale.ENGLISH);
            if (reminder != null) {
                medRemindersDate.clear();
                try {
                    medRemindersDate.add(sdf.parse(reminder.getMedReminderDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            setCurrentImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillFields(String MedicationName, String ex_Date, int Dosage, String Unit) {
        sp_unit.setSelection(Spinneradapter.getPosition(Unit));
        et_medication_name.setText(MedicationName);
        et_expirey_date.setText(DateTimeFormats.reverseDateFormat(ex_Date));
        et_dosage.setText(String.valueOf(Dosage));

    }


    /**
     * Displays the set Expiry date picker dialog
     */
    public void showDatePicker() {



        // Inflate your custom layout containing 2 DatePickers
        try {
            LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
            View customView = inflater.inflate(R.layout.custom_date_picker, null);


            // Define your date pickers
            dpStartDate = (DatePicker) customView.findViewById(R.id.dpStartDate);
            reminderConfig_layout = (LinearLayout) customView.findViewById(R.id.reminderConfig_layout);
            RB_1 = (RadioButton) customView.findViewById(R.id.RB_1);
            RB_2 = (RadioButton) customView.findViewById(R.id.RB_2);
            RB_3 = (RadioButton) customView.findViewById(R.id.RB_3);
            /******************************** Switch Button ********************************************/
            final Switch switch_reminder;
            switch_reminder = (Switch) customView.findViewById(R.id.switch_reminder);


            preferences=this.getActivity().getPreferences(MODE_PRIVATE);
            boolean checked = preferences.getBoolean("checked1", false);  //default is true
            if (checked == true) //if (tgpref) may be enough, not sure
            {
              //  Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                switch_reminder.setChecked(true);
                reminderConfig_layout.setVisibility(View.VISIBLE);
               // tg.setChecked(true);
            }
            else
            {
                switch_reminder.setChecked(false);
                reminderConfig_layout.setVisibility(View.GONE);
            }


            int radioValue = preferences.getInt("radio", 1);
            //Toast.makeText(this.getApplicationContext(), "radioValStored"+radioValue, Toast.LENGTH_SHORT).show();
            if(radioValue==1)
            {
              RB_1.setChecked(true);
            }
            else if(radioValue==2)
            {
                RB_2.setChecked(true);
            }

            else if(radioValue==3)

            {

                RB_3.setChecked(true);
            }


             // switch_reminder.isChecked()
            switch_reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    if (isChecked) {
                        reminderConfig_layout.setVisibility(View.VISIBLE);

                    } else {
                        reminderConfig_layout.setVisibility(View.GONE);
                    }

                }
            });
            /***************************** Iphone Like Toggle Button *********************************************/

            // Build the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(customView); // Set the view of the dialog to your custom layout
            builder.setTitle("Set Expiration Date");
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dateFormatter = new SimpleDateFormat(DateTimeFormats.isoDateFormat, Locale.US);
                    newDate = Calendar.getInstance();
                    newDate.set(dpStartDate.getYear(), dpStartDate.getMonth(), dpStartDate.getDayOfMonth());

                    int x = 0;
                    if (RB_1.isChecked())
                    {
                        x = 1;

                    }

                    else if (RB_2.isChecked())
                        x = 2;
                    else if (RB_3.isChecked())
                        x = 3;
                    setEmergencyMedicationObjectData(dateFormatter.format(newDate.getTime()).toString(), x, (x == 0) ? false : true);
                  /*  if(switch_reminder.isChecked()==true){
                        editor = preferences.edit();
                        editor.putBoolean("checked1", true); // value to store
                        editor.commit();
                    }
                    else{
                        editor = preferences.edit();
                        editor.putBoolean("checked1", false); // value to store
                        editor.commit();
                    }*/
                    editor = preferences.edit();
                    editor.putInt("radio",x);
                    editor.putBoolean("checked1", switch_reminder.isChecked());
                    editor.commit();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            // Create and show the dialog
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setEmergencyMedicationObjectData(String dateSelected, int reminderDayesBefore, boolean hasReminder) {

        try {
            et_expirey_date.setText(DateTimeFormats.reverseDateFormat(dateSelected));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (emergencyMedicationEntity != null) {
            emergencyMedicationEntity.setHasReminder(hasReminder);
            emergencyMedicationEntity.setReminderDayeBefore(reminderDayesBefore);
            emergencyMedicationEntity.setExpiryDate(dateSelected);
        }

    }


    void showProtocolInfoDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        }).setTitle("Protocol Info").setMessage("This is the Dialog message to show ");

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ibtn_info:
                    showProtocolInfoDialog();
                    break;
                case R.id.et_expirey_date:
                    showDatePicker();
                    break;
                case R.id.linear_btn_save:
                    try {
                        if (Update) {
                            DBAdapter adapter= new DBAdapter(getActivity());
                            adapter.getUpdateReminderFlag("EME", emergency_MedicationID,"0");
                            UpdateEmergencyMedication();
                        } else {
                            SaveEmergencyMedication();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    break;
                case R.id.linear_btn_delete:
                    if (Update) {
                        UpdateEmergencyMedicationToDeleted();
                    } else {
                        //return to medication list ...
                        Fragment fragment = new EmergencyMedicationsList();
                        FragmentManager fragmentManager = getActivity().getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                    break;
                case R.id.imgV_camera:
                    selectImage();
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /************************************************************************************************/
    private String validateInputs(String GpName, String date, String time) {
        if (GpName.length() == 0) {
            return "Medication Name can't be empty";
        } else if (date.length() == 0) {
            return "Doage  can't be empty";
        } else if (time.length() == 0) {
            return "ExpiryDate  can't be empty";
        }
        return "";
    }

    public void SaveEmergencyMedication() {
        String errorMessage = validateInputs(et_medication_name.getText().toString(), et_dosage.getText().toString(), et_expirey_date.getText().toString());
        if (errorMessage.length() == 0) {
            try {
                emergencyMedicationEntity.setEmergencyMedicationName(et_medication_name.getText().toString());
                emergencyMedicationEntity.setDosage(Integer.parseInt(et_dosage.getText().toString()));
                emergencyMedicationEntity.setUnit(sp_unit.getSelectedItem().toString());
                emergencyMedicationEntity.setDeleted(false);

                Date d = new SimpleDateFormat(DateTimeFormats.isoDateFormat).parse(emergencyMedicationEntity.getExpiryDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);

                if (emergencyMedicationEntity.getReminderDayeBefore() == 1)
                    cal.add(Calendar.DATE, -1);
                else if (emergencyMedicationEntity.getReminderDayeBefore() == 2)
                    cal.add(Calendar.DATE, -2);
                else if (emergencyMedicationEntity.getReminderDayeBefore() == 3)
                    cal.add(Calendar.DATE, -3);
                else ;

                Date dateBeforeOneDays = cal.getTime();

                Log.e("Date Reminder===", dateBeforeOneDays.toString() + "===" + new SimpleDateFormat(DateTimeFormats.isoDateFormat, Locale.US).format(dateBeforeOneDays));

                long emergency_medication_id = dbAdapter.SaveEmergencyMedication(emergencyMedicationEntity);
/****************************************************************************************************************************/
                // Save Emergency Medication Images

                if (emergency_medication_id == 0) {
                    Toast.makeText(getActivity(), "This medication already exist ", Toast.LENGTH_LONG).show();
                } else {
                    if (images_paths.size() == 0) {
                        dbAdapter.SaveEmergencyMedicationImages((int)emergency_medication_id, "drawable://" + R.drawable.noimages + "jpg");
                        Toast.makeText(getActivity(), "New Emergency medication added successfully ", Toast.LENGTH_LONG).show();
                    } else {
                        for (String file : images_paths) {
                            if (file != null) {
                                dbAdapter.SaveEmergencyMedicationImages((int)emergency_medication_id, file);
                            }
                        }
                        Toast.makeText(getActivity(), "New Emergency medication added successfully ", Toast.LENGTH_LONG).show();
                    }

                }

                /// Save Reminder

                if (emergencyMedicationEntity.getReminderDayeBefore() > 0) {
                    long emergency_medication_reminder_id = dbAdapter.SaveEmergenctMedicationReminder((int)emergency_medication_id, new SimpleDateFormat(DateTimeFormats.isoDateFormat, Locale.US).format(dateBeforeOneDays), emergencyMedicationEntity.getReminderDayeBefore());
                    Log.e("Reminder added", emergency_medication_reminder_id + "");

                    AlarmHelper alarmHelper = new AlarmHelper(getActivity());

                    alarmHelper.createEmergencyMedicationAlarm((int) emergency_medication_id, emergencyMedicationEntity.getEmergencyMedicationName(),emergencyMedicationEntity.getExpiryDate());

                }

                Fragment fragment = new EmergencyMedicationsList();
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            } catch (Exception ex) {
                Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**********************************
     * Image Gallery Handling
     ****************************************/
    void imageGalleryHandle(View rootView) {

        try {
            mImageadapter = new NewMedication.ImageAdapter(getActivity());
            mswitcher = (ImageSwitcher) rootView.findViewById(R.id.mSwitcher);

            mswitcher.setFactory(new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    ImageView imageView = new ImageView(getActivity());
                    imageView.setLayoutParams((new ImageSwitcher.LayoutParams(300, 300)));
                    return imageView;
                }
            });

            mswitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));
            mswitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right));

            switchh = (Button) rootView.findViewById(R.id.Switch);
            switchh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (images_paths.size() == 0) {
                    } else if (currImage < images_paths.size()) {
                        currImage++;
                        if (currImage == images_paths.size()) {
                            currImage = 0;
                        }
                        setCurrentImage();
                    } else {
                    }
                }
            });
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }


    private void setInitialImage() {
        setCurrentImage();
    }


    private void setCurrentImage() {
        try {

            if(images_paths.size()>=2)
            {
                switchh.setVisibility(View.VISIBLE);
            }
            if (currImage > images_paths.size()) {
                currImage = 0;
            } else {
                Uri uri = Uri.parse(images_paths.get(currImage).toString());
                mswitcher.setImageURI(uri);
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(),ex.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void selectImage() {
        try {
            final CharSequence[] items = {"Capture image", "Choose from your Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setTitle("Add images for your medicine");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Capture image")) {


                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                        {
                            boolean hasPermssion=  CheckCameraPermission();
                            if(hasPermssion)
                            {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, REQUEST_CAMERA);
                            }else
                            {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                            }
                        }else {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        }


                      /*  try {
                            startActivityForResult(intent, REQUEST_CAMERA);
                        }catch (Exception ex)
                        {
                            Toast.makeText(getActivity(), "an error occurred due to API compatibility , please contact your support.", Toast.LENGTH_LONG).show();
                        }*/

                    } else if (items[item].equals("Choose from your Gallery")) {
                        Bundle args = new Bundle();
                        ImageGalleryView fragment = new ImageGalleryView(getActivity());
                        if(FileUtils.getCapturedImagesFiles(LoginActivity.userName, getActivity()).size()!=0) {
                            args.putSerializable("ImageFiles", (Serializable) FileUtils.getCapturedImagesFiles(LoginActivity.userName, getActivity()));
                            if (fragment != null) {
                                fragment.setArguments(args);
                                FragmentManager frgManager = getFragmentManager();
                                String tagString = getResources().getString(R.string.Image_gallery_frag_tag);
                                frgManager.beginTransaction().add(R.id.content_frame, fragment, tagString).addToBackStack(tagString).commit();
                            }
                        }else
                        {
                            Toast.makeText(getActivity(),"There are no images in your gallery",Toast.LENGTH_LONG).show();
                        }
                    } else if (items[item].equals("Cancel")) {

                        dialog.dismiss();
                    }
                }
            });

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @TargetApi(Build.VERSION_CODES.M)
    public boolean CheckStoragePermission() {

        int permissionCheckCamera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            } else {
                // No explanation needed, we can request the permission.
                //  requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        } else
            return true;
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean CheckCameraPermission() {

        int permissionCheckCamera = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA);
        if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            } else {
                // No explanation needed, we can request the permission.


                //  requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        } else
            return true;
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(intent, REQUEST_CAMERA);
                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                    }
                }
                break;

            case  REQUEST_WRITE_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
                    }
                }
                break;


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }









    public File getOutputMediaFile(int type, Context context) {
        // To be safe, you check that the SDCard is mounted
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return null;
        }


        String lastLoginUser;
        DBAdapter adapter = new DBAdapter(this.getActivity());
        lastLoginUser = LoginActivity.userName == null ? adapter
                .getLastLoginUser() : LoginActivity.userName;

        lastLoginUser = lastLoginUser + InstallationIdentity.id(context);

        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(),
                CameraPreview.APP_DIR_Images
                        + (lastLoginUser != null ? File.separator
                        + lastLoginUser : ""));

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            boolean hasPermssion=  CheckStoragePermission();
            if(hasPermssion)
            {
                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {

                    if (!mediaStorageDir.mkdirs()) {
                        Log.d(CameraPreview.APP_DIR_Images, "failed to create directory");
                        return null;
                    }
                }
            }else
            {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            }
        }else {

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {

                if (!mediaStorageDir.mkdirs()) {
                    Log.d(CameraPreview.APP_DIR_Images, "failed to create directory");
                    return null;
                }
            }
        }


/*        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {

            if (!mediaStorageDir.mkdirs()) {
                Log.d(CameraPreview.APP_DIR_Images, "failed to create directory");
                return null;
            }
        }*/
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());

        timestampp = timeStamp;
        File mediaFile;
        if (type == CameraPreview.MEDIA_TYPE_Images) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + timeStamp + ".png");
        } else {
            return null;
        }
        return mediaFile;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {

                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File dest = getOutputMediaFile(CameraPreview.MEDIA_TYPE_Images, getActivity());

                images_paths.add(dest.toString());

                FileOutputStream fo;
                try {
                    dest.createNewFile();
                    fo = new FileOutputStream(dest);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setCurrentImage();
                Toast.makeText(getActivity(), "Image saved to your gallery", Toast.LENGTH_LONG).show();

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getActivity(), selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);

                ivImage.setImageBitmap(bm);
            } else if (requestCode == REMINDERS_REQUEST) {
//                updateReminders(data);
            } else if (requestCode == TITRATION_REQUEST) {
//                updateTitration(data);
            }
        }
    }



    @Override
    public void setImage(List<String> path, int numofcolumns) {
        images_paths.clear();
        try {




            for (String file : path) {
                images_paths.add(file);
            }

            //set the visabilty of switch button.
            if(images_paths.size()>=2)
            {
                switchh.setVisibility(View.VISIBLE);
            }


            if (images_paths.size() == 0) {
                images_paths.add("drawable://" + R.drawable.noimages + "jpg");

            } else {
                setInitialImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /************************************************************************************/


    public void UpdateEmergencyMedicationToDeleted() {
        try {
            DBAdapter adapter = new DBAdapter(this.getActivity());
            EmergencyMedicationEntity medication = new EmergencyMedicationEntity();
            medication.setId(emergency_MedicationID);
            medication.setDeleted(true);
            adapter.UpdateEmergencyMedicationToDeleted(medication);
            AlarmHelper alarmHelper=new AlarmHelper(getActivity());
            alarmHelper.cancelEmergencyMedicationAlarms(medication.getId(),medication.getEmergencyMedicationName(),medication.getExpiryDate());



            //return to medications list after update...
            Fragment fragment = new EmergencyMedicationsList();
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void UpdateEmergencyMedication() throws ParseException {
        com.hp.epilepsy.widget.model.EmergencyMedicationEntity medication = new com.hp.epilepsy.widget.model.EmergencyMedicationEntity();
        String errorMessage = validateInputs(et_medication_name.getText().toString(), et_dosage.getText().toString(), et_expirey_date.getText().toString());
        if (errorMessage.length() == 0) {
            emergencyMedicationEntity.setEmergencyMedicationName(et_medication_name.getText().toString());
            emergencyMedicationEntity.setDosage(Integer.parseInt(et_dosage.getText().toString()));
            emergencyMedicationEntity.setUnit(sp_unit.getSelectedItem().toString());
            emergencyMedicationEntity.setDeleted(false);
            AlarmHelper alarmHelper = new AlarmHelper(getActivity());

            try {
                long id = dbAdapter.UpdateEmergencyMedication(emergencyMedicationEntity);

                if (images_paths.size() == 0) {
                    dbAdapter.removeEmergencyMedImages(Integer.parseInt(String.valueOf(emergency_MedicationID)));
                    Toast.makeText(getActivity(), "Medication Updated successfully ", Toast.LENGTH_LONG).show();
                } else {
                    dbAdapter.removeEmergencyMedImages(Integer.parseInt(String.valueOf(emergency_MedicationID)));
                    for (String file : images_paths) {
                        if (file != null) {
                            dbAdapter.SaveEmergencyMedicationImages(Integer.parseInt(String.valueOf(emergency_MedicationID)), file);
                        }
                    }
                    Toast.makeText(getActivity(), "Medication Updated successfully ", Toast.LENGTH_LONG).show();
                }

                /***************************************************************************************************************************/
                Date d = new SimpleDateFormat(DateTimeFormats.isoDateFormat).parse(emergencyMedicationEntity.getExpiryDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                if (emergencyMedicationEntity.getReminderDayeBefore() == 1)
                    cal.add(Calendar.DATE, -1);

                else if (emergencyMedicationEntity.getReminderDayeBefore() == 2)
                    cal.add(Calendar.DATE, -2);
                else if (emergencyMedicationEntity.getReminderDayeBefore() == 3)
                    cal.add(Calendar.DATE, -3);
                else ;

                Date dateBeforeOneDays = cal.getTime();
                //save list of reminders related to medication ID
                if (emergencyMedicationEntity.getReminderDayeBefore() > 0) {
                    alarmHelper.cancelEmergencyMedicationAlarms((int) id, medication.getEmergencyMedicationName(),emergencyMedicationEntity.getExpiryDate());
                    boolean deleted = dbAdapter.removeEmergencyMedicationReminders((int)emergency_MedicationID);
                    dbAdapter.SaveEmergenctMedicationReminder((int)emergency_MedicationID, new SimpleDateFormat(DateTimeFormats.isoDateFormat, Locale.US).format(dateBeforeOneDays), emergencyMedicationEntity.getReminderDayeBefore());
                    alarmHelper.createEmergencyMedicationAlarm((int) emergency_MedicationID, emergencyMedicationEntity.getEmergencyMedicationName(), emergencyMedicationEntity.getExpiryDate());
                }

                Fragment fragment ;
                FragmentManager fragmentManager ;
                FragmentTransaction fragmentTransaction ;
                String tagString;
                switch (where){
                    case 0:
                        fragment = (Fragment) new EmergencyMedicationsList();
                        tagString = getString(R.string.EMERGENCY_MEDICATIONS_LIST);
                        fragmentManager = getActivity().getFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 1:
                             fragment = (Fragment) new CalenderEventsListFragment();
                             tagString = getString(R.string.EMERGENCY_MEDICATIONS_LIST);
                             fragmentManager = getActivity().getFragmentManager();
                             fragmentTransaction = fragmentManager.beginTransaction();
                             fragmentTransaction.replace(R.id.calender_list_event_fragment_container, fragment);
                             fragmentTransaction.addToBackStack(null);
                             fragmentTransaction.commit();
                      //  Toast.makeText(getActivity(), where+" == this is where", Toast.LENGTH_LONG).show();
                        break;
                }



            } catch (Exception ex) {
                Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    public void onBackeKeyPressed(View v) {
        //back to Appointment list when key back pressed
        try {
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Fragment fragment;
                        FragmentManager fragmentManager;
                        FragmentTransaction fragmentTransaction;
                        switch (where){
                            case 0:
                                 fragment =new EmergencyMedicationsList();
                                 fragmentManager = getActivity().getFragmentManager();
                                 fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.content_frame, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                break;
                            case 1:
                                 fragment = new CalenderEventsListFragment();
                                 fragmentManager = getActivity().getFragmentManager();
                                 fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.calender_list_event_fragment_container, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                               // Toast.makeText(getActivity(), where+" == this is where", Toast.LENGTH_LONG).show();
                                break;
                            case 3:
                                fragment =new CalendarFragmentV2();
                                fragmentManager = getActivity().getFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.content_frame, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                break;
                        }

                        return true;
                    } else {
                        return false;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
