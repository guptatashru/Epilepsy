package com.hp.epilepsy.widget;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.hp.epilepsy.widget.model.IimagesSetter;
import com.hp.epilepsy.widget.model.MedicationReminders;
import com.hp.epilepsy.widget.model.PrescriptionReminders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * -----------------------------------------------------------------------------
 * Class Name: NewMedication
 * Created By:Mahmoud
 * Modified By:Shruti and Nikunj
 * Purpose :to create new medications
 * -----------------------------------------------------------------------------
 */
public class NewMedication extends Fragment implements
        View.OnClickListener, IimagesSetter {
    static final int REQUEST_CAMERA = 1;
    static final int REQUEST_WRITE_STORAGE = 6;
    static final int SELECT_FILE = 2;
    static final int REMINDERS_REQUEST = 3;
    static final int TITRATION_REQUEST = 4;
    static final int PRESCRIPTION_REQUEST = 5;

    public View view;
    ImageView ivImage;
    ImageSwitcher mswitcher;
    ImageAdapter mImageadapter;
    List<String> images_paths;
    ArrayAdapter<String> Spinneradapter;
    String timestampp;
    Button switchh;
    Spinner spinner;
    EditText medication_name;
    EditText medication_desc;
    EditText medication_dosage;
    EditText start_data;
    String MedstartDate = null;
    SimpleDateFormat dateFormatter;
    int where;

    //Prescription region
    ArrayList<String> PrescriptionReminderTime = new ArrayList<String>();
    String PrescrptionRenewalDate = null;

    private String usrtName;
    DBAdapter adapter;
    private int currImage = 0;
    private int MedicationID;
    private boolean Update = false;
    boolean TitrationON;
    String TitrationStartDatE = null;
    String UserNme;
    int TitrationNumWeekS = 0;
    com.hp.epilepsy.widget.model.NewMedication medication = new com.hp.epilepsy.widget.model.NewMedication();
    List<Calendar> medReminders = new ArrayList<Calendar>();
    List<Date> medRemindersDate = new ArrayList<Date>();
    DateTimeFormats format;

    View rootView;

    public NewMedication() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = null;
        rootView = inflater.inflate(R.layout.fragment_new_medication, container, false);
        initViews();
        MedicationReminderActivity.IsReminderUpdated = false;
        MedicationTitrationActivity.IsTitrationUpdated = false;
        PrescriptionRenewalReminderActivity.IsPrescriptionUpdated = false;
        images_paths = new ArrayList<>();
        adapter = new DBAdapter(this.getActivity());
        spinner = (Spinner) rootView.findViewById(R.id.unit_spinner);
        medication_name = (EditText) rootView.findViewById(R.id.medication_name_txt);
        medication_desc = (EditText) rootView.findViewById(R.id.medication_description);
        medication_dosage = (EditText) rootView.findViewById(R.id.dosage_txt);
        start_data = (EditText) rootView.findViewById(R.id.start_data);
        format = new DateTimeFormats();
        Spinneradapter = new ArrayAdapter<>(this.getActivity(), R.layout.question_spinner_item, getResources().getStringArray(R.array.unit));
        spinner.setAdapter(Spinneradapter);
        try {
            MedicationID = (int) getArguments().getSerializable("MedicationID");
            where = getArguments().getInt("where");
            UserNme = getArguments().getString("userName");
            
            Serializable serializable = getArguments().getSerializable("TitrationData");
            Map<String, HashMap<String, List>> datacollection;
           // if(serializable!=null)
            //    datacollection = (Map<String, HashMap<String, List>>)serializable;
// 
//          usrtName =getArguments().getString("userName");
            GetMedicationDataFromDB(MedicationID);
            fillDropDownLists(rootView);

            mImageadapter = new ImageAdapter(this.getActivity());
            mswitcher = (ImageSwitcher) rootView.findViewById(R.id.mSwitcher);
            mswitcher.setFactory(new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setLayoutParams((new ImageSwitcher.LayoutParams(300, 300)));
                    return imageView;
                }
            });
            mswitcher.setInAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left));
            mswitcher.setOutAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right));
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


            //check if no images set switch bitton unvisible
            if (images_paths.size() <= 0)
                switchh.setVisibility(View.INVISIBLE);

            onBackeKeyPressed(rootView);
            view = rootView;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void setInitialImage() {
        setCurrentImage();
    }

    private void setCurrentImage() {
        try {
            if (currImage > images_paths.size()) {
                currImage = 0;
            } else {

                //TODO Exception here in the URI.Parse
                Uri uri = Uri.parse(images_paths.get(currImage).toString());
                mswitcher.setImageURI(uri);
                switchh.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
        }
    }


    void NotificationDialog(String Title, String Message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Message)
                .setCancelable(false)
                .setTitle(Title);

        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    ///this method to get specific medication data by ID and fill fileds with this data in case we need to update medication
    private void GetMedicationDataFromDB(int MedicationID) {
        try {
            if (MedicationID != -1) {
                Update = true;
                if (where == 3) {
                    LoginActivity.userName = UserNme;
                    String intentType = getArguments().getString("Type");
                    if (intentType.equals("PRES")) {

                        String RenewalMessage = getArguments().getString(AlarmReceiver.PRESCRIPTION_RENEWAL_MESSAGE);
                        NotificationDialog("Prescription Renewal", RenewalMessage);

                    } else if (intentType.equals("MED_TIT")) {
                        String TitationMessage = getArguments().getString(AlarmReceiver.MEICATION_TITRATION_MESSAGE);
                        NotificationDialog("Titration", TitationMessage);
                    }

                }


                ArrayList<com.hp.epilepsy.widget.model.NewMedication> medications = new ArrayList<com.hp.epilepsy.widget.model.NewMedication>();


                // Toast.makeText(getActivity(),"User name from New medications "+UserNme,Toast.LENGTH_LONG).show();


                medications = adapter.getMedicationbyID(MedicationID);

                for (int i = 0; i < medications.size(); i++) {
                    com.hp.epilepsy.widget.model.NewMedication newMedication = new com.hp.epilepsy.widget.model.NewMedication();
                    newMedication = medications.get(i);
                    for (int j = 0; j < newMedication.getMedicationImages().size(); j++) {
                        if (newMedication.getMedicationImages().size() >= 1) {
                            images_paths.add(newMedication.getMedicationImages().get(j).toString());
                        }
                    }

                    //newMedication.getId();
                    MedstartDate = newMedication.getMedStartDate();
                    TitrationON = newMedication.isTitrationOn();
                    TitrationStartDatE = newMedication.getTitrationStartDate();
                    TitrationNumWeekS = newMedication.getTitrationNumWeeks();
                    PrescrptionRenewalDate = newMedication.getPrescriptionRenewalDate();
                    fillFields(newMedication.getMedication_name(), newMedication.getMedication_description(), newMedication.getStart_date(), newMedication.getDosage(), newMedication.getUnit());
                    ArrayList<MedicationReminders> remindersList = new ArrayList<MedicationReminders>();
                    remindersList = adapter.getMedicationReminders(MedicationID);


                    //the original date format recived from the database
                    DateFormat originalFormat = new SimpleDateFormat(DateTimeFormats.deafultDateFormat, Locale.ENGLISH);
                    //the target dateformat the we should convert to it.
                    DateFormat targetFormat = new SimpleDateFormat(DateTimeFormats.isoDateFormat2);


                    if (remindersList != null) {
                        medRemindersDate.clear();
                        for (int j = 0; j < remindersList.size(); j++) {
                            try {

                                //parse the original  dateformat from the database and convert it to date object to be convert to the target format.
                                Date date = originalFormat.parse(remindersList.get(j).getMedReminderDate());
                                //change to the target format
                                String formattedDate = targetFormat.format(date);
                                //the issue here
                                //TODO parse the string to dateformat
                                medRemindersDate.add(DateTimeFormats.convertStringToDateTime(formattedDate));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    //TODO add prescription reminder to the calender.
                    //Prescription reigon  methods to get data from database and fill required variables
                    ArrayList<PrescriptionReminders> prescreminders = adapter.getPrescriptionReminders(MedicationID);
                    if (prescreminders.size() != 0) {
                        if (prescreminders != null) {
                            PrescriptionReminderTime.clear();
                            for (int j = 0; j < prescreminders.size(); j++) {
                                PrescriptionReminderTime.add(prescreminders.get(j).getAlertbefore());

                            }

                        }
                    }
                }
                setCurrentImage();
            } else {

                dateFormatter = new SimpleDateFormat(DateTimeFormats.isoDateFormatReverse, Locale.US);
                Calendar c = Calendar.getInstance();
                String todayDate = dateFormatter.format(c.getTime());
                start_data.setText(todayDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setUpOnClickListeners(View view) {
        EditText date = (EditText) view.findViewById(R.id.start_data);
        if (date != null)
            date.setOnClickListener(this);

        TextView choose = (TextView) view.findViewById(R.id.video);
        if (choose != null)
            choose.setOnClickListener(this);
        LinearLayout save = (LinearLayout) view.findViewById(
                R.id.rec_seiz_save);
        if (save != null)
            save.setOnClickListener(this);
        LinearLayout delete = (LinearLayout) view.findViewById(
                R.id.rec_seiz_delete);
        if (delete != null)
            delete.setOnClickListener(this);
    }

    private void fillDropDownLists(View rootView) {
        try {
            DBAdapter dbAdapter = new DBAdapter(this.getContext());
            dbAdapter.open();
            setUpOnClickListeners(rootView);
            dbAdapter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Context getContext() {
        return this.getActivity();
    }

    public void showDatePicker(View view) {
        int day, month, year;

        try {
            String text = ((EditText) view).getText().toString();
            String text2 = DateTimeFormats.reverseDateFormat(text);
            SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeFormats.isoDateFormat);
            Calendar c = Calendar.getInstance();
            Date date;
            try {
                date = text2 != null && text2.length() != 0 ? dateFormat.parse(text2)
                        : new Date();
            } catch (ParseException e) {
                date = new Date();
            }
            c.setTime(date);
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            DateRangePicker datePicker = DateRangePicker.newInstance(day, month,
                    year);
            // datePicker.show
            datePicker.show(getFragmentManager(), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.start_data:
                showDatePicker(v);
                break;
            case R.id.rec_seiz_delete:
                if (Update) {

                    UpdateMedicationToDeleted();
                } else {

                    Fragment fragment = new MedicationDairy();
                    FragmentManager fragmentManager = getActivity().getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.rec_seiz_save:
                try {
                    if (Update) {

                        DBAdapter adapter = new DBAdapter(getActivity());
                        adapter.getUpdateReminderFlag("PRES", MedicationID, "0");

                        UpdateMedication();
                    } else {
                        AddMedication();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.video:
                selectImage();
                break;
        }

    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean CheckCameraPermission() {

        int permissionCheckCamera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
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

            case REQUEST_WRITE_STORAGE:
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


    private void selectImage() {
        try {
            final CharSequence[] items = {"Capture image", "Choose from your Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            builder.setTitle("Add images for your medicine");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (items[item].equals("Capture image")) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            boolean hasPermssion = CheckCameraPermission();
                            if (hasPermssion) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, REQUEST_CAMERA);
                            } else {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                            }
                        } else {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        }
                      /*  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);
*/
                    } else if (items[item].equals("Choose from your Gallery")) {
                        Bundle args = new Bundle();
                        ImageGalleryView fragment = new ImageGalleryView(getActivity());
                        if (FileUtils.getCapturedImagesFiles(LoginActivity.userName, getActivity()).size() != 0) {
                            args.putSerializable("ImageFiles", (Serializable) FileUtils.getCapturedImagesFiles(LoginActivity.userName, getActivity()));
                            if (fragment != null) {
                                fragment.setArguments(args);
                                FragmentManager frgManager = getFragmentManager();
                                String tagString = getResources().getString(R.string.Image_gallery_frag_tag);
                                frgManager.beginTransaction().add(R.id.content_frame, fragment, tagString).addToBackStack(tagString).commit();
                            }
                        } else {
                            Toast.makeText(getActivity(), "There are no images in your gallery", Toast.LENGTH_LONG).show();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {

                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);


                File dest = getOutputMediaFile(CameraPreview.MEDIA_TYPE_Images, this.getContext());


                if (dest != null) {
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
                    Toast.makeText(getContext(), "Image saved to your gallery", Toast.LENGTH_LONG).show();
                }

            } else if (requestCode == SELECT_FILE) {
//
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
                updateReminders(data);
            } else if (requestCode == TITRATION_REQUEST) {
                updateTitration(data);
            } else if (requestCode == PRESCRIPTION_REQUEST) {
                PrescriptionSelectedReminders(data);
            } else {
                //  Toast.makeText(getContext(), "Request Cannot found", Toast.LENGTH_LONG).show();
            }
        } else {
            // Toast.makeText(getContext(), "Response Code not OK", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void AddMedication() throws ParseException {
        String unit = spinner.getSelectedItem().toString();
        String errorMessage = ValidateInputs(unit, medication_name.getText().toString(), medication_desc.getText().toString(), medication_dosage.getText().toString(), start_data.getText().toString());

        if (errorMessage.length() == 0) {

            medication.setStart_date(DateTimeFormats.reverseDateFormatBack(start_data.getText().toString()));
            medication.setUnit(unit);
            medication.setMedication_name(medication_name.getText().toString());
            medication.setMedication_description(medication_desc.getText().toString());
            medication.setDosage(Integer.parseInt(medication_dosage.getText().toString()));
            medication.setTitrationStartDate(TitrationStartDatE);
            medication.setMedStartDate(MedstartDate);
            medication.setPrescriptionRenewalDate(PrescrptionRenewalDate);
            medication.setTitrationNumWeeks(TitrationNumWeekS);
            medication.setTitrationOn(TitrationON);
            medication.setDelete(false);
            try {
                long id = adapter.SaveMedication(medication);
                if (id == 0) {
                    Toast.makeText(getContext(), "This medication already exist ", Toast.LENGTH_LONG).show();
                } else {
                    if (images_paths.size() == 0) {
                        adapter.SaveMedicationImages((int) id, "drawable://" + R.drawable.noimages + "jpg");
                        Toast.makeText(getContext(), "New medication added successfully ", Toast.LENGTH_LONG).show();
                    } else {
                        for (String file : images_paths) {
                            if (file != null) {
                                adapter.SaveMedicationImages((int) id, file);
                            }
                        }
                        Toast.makeText(getContext(), "New medication added successfully ", Toast.LENGTH_LONG).show();
                    }

                    AlarmHelper alarmHelper = new AlarmHelper(getContext());
                    //save list of reminders related to medication ID
                    if (medRemindersDate.size() != 0) {
                        for (int i = 0; i < medRemindersDate.size(); i++) {
                            adapter.SaveMedicationReminders((int) id, medRemindersDate.get(i).toString());
                        }
                        //Create Medication Alarms...
                        alarmHelper.createMedicationAlarms((int) id, medication.getMedication_name(), null);
                    }


                    //save the prescription data to the database and create reminders for it
                    if (PrescriptionReminderTime.size() != 0) {
                        SavePrescriptionReminders(DateTimeFormats.convertStringToDate(PrescrptionRenewalDate), (int) id);
                        //create prescription renewal alarms
                        alarmHelper.createMedPrescriptionAlarm((int) id, medication.getMedication_name());
                    }

                    //Create Titration Alarms...
                    if (medication.isTitrationOn()) {
                        alarmHelper.createMedicationTitration(
                                (int) id,
                                medication.getMedication_name(),
                                format.convertStringToDate(medication.getTitrationStartDate()),
                                medication.getTitrationNumWeeks());
                    }


                    Fragment fragment = new MedicationDairy();
                    FragmentManager fragmentManager = getActivity().getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();


                    //return to the list on medications fragment after saving

                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    public void UpdateMedication() throws ParseException {
        com.hp.epilepsy.widget.model.NewMedication medication = new com.hp.epilepsy.widget.model.NewMedication();
        String unit = spinner.getSelectedItem().toString();
        String errorMessage = ValidateInputs(unit, medication_name.getText().toString(), medication_desc.getText().toString(), medication_dosage.getText().toString(), start_data.getText().toString());


        if (errorMessage.length() == 0) {
            medication.setId(MedicationID);
            medication.setStart_date(DateTimeFormats.reverseDateFormatBack(start_data.getText().toString()));
            medication.setUnit(unit);
            medication.setMedication_name(medication_name.getText().toString());
            medication.setMedication_description(medication_desc.getText().toString());
            medication.setDosage(Integer.parseInt(medication_dosage.getText().toString()));
            medication.setMedStartDate(MedstartDate);
            medication.setPrescriptionRenewalDate(PrescrptionRenewalDate);
            medication.setTitrationStartDate(TitrationStartDatE);
            medication.setTitrationNumWeeks(TitrationNumWeekS);
            medication.setTitrationOn(TitrationON);
            medication.setDelete(false);

            AlarmHelper alarmHelper = new AlarmHelper(getContext());

            //Cancel old titration before adding a new one...


            try {
                long id = adapter.UpdateMedication(medication);
                if (images_paths.size() == 0) {
                    adapter.removeMedImages(medication.getId());
                    Toast.makeText(getContext(), "Medication Updated successfully ", Toast.LENGTH_LONG).show();
                } else {
                    adapter.removeMedImages(medication.getId());
                    for (String file : images_paths) {
                        if (file != null) {
                            adapter.SaveMedicationImages(medication.getId(), file);
                        }
                    }
                    Toast.makeText(getContext(), "Medication Updated successfully ", Toast.LENGTH_LONG).show();
                }

                //save list of reminders related to medication ID


                if (MedicationReminderActivity.IsReminderUpdated) {

                    if (medRemindersDate.size() != 0) {
                        //Cancel existing medication alarms
                        alarmHelper.cancelMedicationAlarms(medication.getId(), medication.getMedication_name(), null);
                        boolean deleted = adapter.removeMedicationReminders(medication.getId());
                        for (int i = 0; i < medRemindersDate.size(); i++) {
                            adapter.SaveMedicationReminders(medication.getId(), medRemindersDate.get(i).toString());
                        }
                        //Create updated medication alarms
                        alarmHelper.createMedicationAlarms(medication.getId(), medication.getMedication_name(), null);
                    }
                }


                if (MedicationTitrationActivity.IsTitrationUpdated) {
                    alarmHelper.cancelMedicationTitration(medication.getId());
                    //Create Titration Alarms...
                    if (medication.isTitrationOn()) {

                        alarmHelper.createMedicationTitration(
                                medication.getId(),
                                medication.getMedication_name(),
                                DateTimeFormats.convertStringToDate(medication.getTitrationStartDate()),
                                medication.getTitrationNumWeeks());
                    }
                }

                if (PrescriptionRenewalReminderActivity.IsPrescriptionUpdated) {
                    ///update Medication prescription data by removing the existing data and insert the updated data///////////
                    if (PrescriptionReminderTime.size() != 0) {
                        alarmHelper.cancelMedPrescriptionAlarm(medication.getId(), medication.getMedication_name());
                        adapter.removeMedPrescriptions(medication.getId());
                        SavePrescriptionReminders(DateTimeFormats.convertStringToDate(PrescrptionRenewalDate), medication.getId());
                        alarmHelper.createMedPrescriptionAlarm(medication.getId(), medication.getMedication_name());
                    }
                }

                MedicationReminderActivity.IsReminderUpdated = false;
                MedicationTitrationActivity.IsTitrationUpdated = false;
                PrescriptionRenewalReminderActivity.IsPrescriptionUpdated = false;

                Fragment fragment;
                String tagString;
                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;
                switch (where) {

                    case 0:
                        fragment = new MedicationDairy();
                        fragmentManager = getActivity().getFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 1:
                        fragment = new CalenderEventsListFragment();
//                        tagString = getString(R.string.EMERGENCY_MEDICATIONS_LIST);
                        fragmentManager = getActivity().getFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.calender_list_event_fragment_container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        // Toast.makeText(getActivity(), where+" == this is where", Toast.LENGTH_LONG).show();
                        break;


                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    public void SavePrescriptionReminders(Date renewalDate, int medication_Id) {

        try {
            DBAdapter adapter = new DBAdapter(this.getActivity());
            PrescriptionReminders reminderObj = new PrescriptionReminders();
            for (int i = 0; i < PrescriptionReminderTime.size(); i++) {
                PrescriptionReminders prescription_reminder;
                Calendar cal = Calendar.getInstance();
                cal.setTime(renewalDate);
                if (PrescriptionReminderTime.get(i).contains("OneDay")) {
                    cal.add(Calendar.DATE, -1);
                    Date date = cal.getTime();
                    prescription_reminder = new PrescriptionReminders();

                    prescription_reminder.setMedicationId(medication_Id);
                    prescription_reminder.setAlertbefore("OneDay");
                    prescription_reminder.setPrescriptionReminderDate(DateTimeFormats.convertDateToStringWithIsoFormat(date));
                    adapter.SavePrescriptionReminders(prescription_reminder);
                }
                if (PrescriptionReminderTime.get(i).contains("ThreeDays")) {
                    cal.add(Calendar.DATE, -3);
                    Date date = cal.getTime();

                    prescription_reminder = new PrescriptionReminders();

                    prescription_reminder.setMedicationId(medication_Id);
                    prescription_reminder.setAlertbefore("ThreeDays");
                    prescription_reminder.setPrescriptionReminderDate(DateTimeFormats.convertDateToStringWithIsoFormat(date));
                    adapter.SavePrescriptionReminders(prescription_reminder);

                }
                if (PrescriptionReminderTime.get(i).contains("OneWeek")) {
                    cal.add(Calendar.DATE, -7);
                    Date date = cal.getTime();

                    prescription_reminder = new PrescriptionReminders();
                    prescription_reminder.setMedicationId(medication_Id);
                    prescription_reminder.setAlertbefore("OneWeek");
                    prescription_reminder.setPrescriptionReminderDate(DateTimeFormats.convertDateToStringWithIsoFormat(date));
                    adapter.SavePrescriptionReminders(prescription_reminder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void UpdateMedicationToDeleted() {
        try {
            DBAdapter adapter = new DBAdapter(this.getActivity());
            AlarmHelper alarmHelper = new AlarmHelper(getContext());
            com.hp.epilepsy.widget.model.NewMedication medication = new com.hp.epilepsy.widget.model.NewMedication();
            medication.setId(MedicationID);
            medication.setMedication_name(medication_name.getText().toString());
            medication.setDelete(true);
            // adapter.UpdateMedicationToDeleted(medication);
            alarmHelper.cancelMedicationAlarms(medication.getId(), medication.getMedication_name(), null);
            adapter.removeMedication(MedicationID);
            adapter.removeMedicationReminders(MedicationID);

            //TODO cancel Medication Reminder
            //return to medications list after update...
            Fragment fragment = new MedicationDairy();
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillFields(String MedicationName, String MedicationDesc, String startDate, int Dosage, String Unit) {
        spinner.setSelection(Spinneradapter.getPosition(Unit));
        medication_name.setText(MedicationName);
        medication_desc.setText(MedicationDesc);
        start_data.setText(DateTimeFormats.reverseDateFormat(startDate));
        medication_dosage.setText(String.valueOf(Dosage));

    }

    private String ValidateInputs(String unit, String medication_name, String medication_desc, String dosage, String medicationDate) {
        if (unit.length() == 0) {
            return "unit can't be empty";
        } else if (medication_name.length() == 0) {
            return "Medication name can't be empty";
        } else if (medication_desc.length() == 0) {
            return "Medication description can't be empty";
        } else if (dosage.length() == 0) {
            return "dosage can't be empty";
        } else if (medicationDate.length() == 0) {
            return "Start date can't be empty";
        }
        return "";
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermssion = CheckStoragePermission();
            if (hasPermssion) {
                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {

                    if (!mediaStorageDir.mkdirs()) {
                        Log.d(CameraPreview.APP_DIR_Images, "failed to create directory");
                        return null;
                    }
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            }
        } else {

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
    public void setImage(List<String> path, int numofcolumns) {
        images_paths.clear();
        try {
            for (String file : path) {
                images_paths.add(file);
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

    public static class DateRangePicker extends DialogFragment implements
            OnDateSetListener {

        private static DateRangePicker newInstance(int day, int month, int year) {
            DateRangePicker dateOfBirthPicker = new DateRangePicker();
            Bundle args = new Bundle();
            args.putInt("day", day);
            args.putInt("month", month);
            args.putInt("year", year);
            dateOfBirthPicker.setArguments(args);
            return dateOfBirthPicker;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int day = getArguments().getInt("day");
            int month = getArguments().getInt("month");
            int year = getArguments().getInt("year");
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this,
                    year, month, day);
            return dialog;
        }


        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            EditText editText = (EditText) getActivity()
                    .findViewById(R.id.start_data);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM. dd,yyyy");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            Date date = new Date(c.getTimeInMillis());
            String formattedDate = dateFormat.format(date);
            editText.setText(formattedDate);
        }
    }

    public static class ImageAdapter extends BaseAdapter {

        ArrayList<String> itemList = new ArrayList<String>();
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        void add(String path) {
            itemList.add(path);
        }

        public int getCount() {
            return itemList.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            try {
                if (convertView == null) {  // if it's not recycled, initialize some attributes
                    imageView = new ImageView(mContext);
                    imageView.setLayoutParams(new GridView.LayoutParams(220, 220));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setPadding(8, 8, 8, 8);
                } else {
                    imageView = (ImageView) convertView;
                }

                Bitmap bm = decodeSampledBitmapFromUri(itemList.get(position), 220, 220);
                imageView.setImageBitmap(bm);
                return imageView;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
            Bitmap bm = null;
            // First decode with inJustDecodeBounds=true to check dimensions
            try {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(path, options);

                return bm;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public int calculateInSampleSize(

                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round((float) height / (float) reqHeight);
                } else {
                    inSampleSize = Math.round((float) width / (float) reqWidth);
                }
            }
            return inSampleSize;
        }

    }

    private void initViews() {
        try {

            Button btnReminder = (Button) rootView.findViewById(R.id.btnReminder);
            btnReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(getContext(), MedicationReminderActivity.class);
                        if (MedicationID == -1) {
                            //Starting Reminders without sending anything to edit...
                            ////////////////////////////////////////////////////////
                            startActivityForResult(intent, REMINDERS_REQUEST);
                        } else {
                            DateTimeFormats format = new DateTimeFormats();
                            //Starting reminders with some items to edit...
                            Calendar calendar = Calendar.getInstance();
                            Calendar calendarObjc = Calendar.getInstance();
                            if (MedstartDate != null) {


                                calendar.setTime(format.convertStringToDate(MedstartDate));

                                calendarObjc.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                            } else {
                                calendarObjc.set(Calendar.DAY_OF_MONTH, new Date().getDay());
                                calendarObjc.set(Calendar.MONTH, new Date().getMonth());
                                calendarObjc.set(Calendar.YEAR, new Date().getYear());
                            }
                            //Putting Medication Start Date...
                            intent.putExtra(MedicationReminderActivity.INTENT_MED_START_DATE, calendarObjc);
                            //pass the start date to the
                            intent.putExtra(MedicationReminderActivity.INTENT_MED_START_DATE_TEXT, MedstartDate);
                            //here fetch list of Reminders from database and put it to
                            List<Calendar> reminders = new ArrayList<Calendar>();
                            if (medRemindersDate.size() != 0) {
                                for (int k = 0; k < medRemindersDate.size(); k++) {
                                    Calendar calendarr = Calendar.getInstance();
                                    calendarr.setTime(medRemindersDate.get(k));
                                    reminders.add(calendarr);
                                }
                            }
                            //Putting Medication Reminders...
                            intent.putExtra(MedicationReminderActivity.INTENT_MED_REMINDER_TIMES, reminders.toArray());
                            intent.putExtra("medId", MedicationID);


                            //Starting Reminders...
                            startActivityForResult(intent, REMINDERS_REQUEST);
                        }

                    } catch (Exception e) {
                        String s = e.getMessage();
                        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
                    }
                }
            });


            Button btnTitration = (Button) rootView.findViewById(R.id.btnTitration);
            btnTitration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(getContext(), MedicationTitrationActivity.class);
                        if (MedicationID == -1) {
                            //Starting titrations without sending anything to edit...
                            startActivityForResult(intent, TITRATION_REQUEST);
                        } else {
                            //Starting titrations with some items to edit...
                            ////////////////////////////////////////////////
                            Calendar calendar = Calendar.getInstance();
                            if (TitrationStartDatE != null) {
                                DateTimeFormats format = new DateTimeFormats();
                                calendar.setTime(format.convertStringToDate(TitrationStartDatE));
                            } else {
                                calendar.set(Calendar.DAY_OF_MONTH, 3);
                                calendar.set(Calendar.MONTH, 3);
                                calendar.set(Calendar.YEAR, 2018);
                            }
                            //Putting Medication Start Date...
                            intent.putExtra(MedicationTitrationActivity.INTENT_TITRATION_START_DATE, calendar);
                            intent.putExtra(MedicationTitrationActivity.INTENT_TITRATION_ON, TitrationON);
                            intent.putExtra(MedicationTitrationActivity.INTENT_TITRATION_NUM_WEEKS, TitrationNumWeekS);
                            //Starting Reminders...
                            startActivityForResult(intent, TITRATION_REQUEST);
                        }
                    } catch (Exception e) {
                        String s = e.getMessage();
                        e.printStackTrace();
                    }
                }
            });


            Button btnPrescription = (Button) rootView.findViewById(R.id.btnPrescrition);
            btnPrescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(getContext(), PrescriptionRenewalReminderActivity.class);
                        if (MedicationID == -1) {
                            startActivityForResult(intent, PRESCRIPTION_REQUEST);
                        } else {
                            intent.putExtra(PrescriptionRenewalReminderActivity.PRESCRIPTION_REMINDERS_ALERTS, PrescriptionReminderTime);
                            intent.putExtra(PrescriptionRenewalReminderActivity.RENEWAL_DATE, PrescrptionRenewalDate);
                            startActivityForResult(intent, PRESCRIPTION_REQUEST);
                        }
                    } catch (Exception e) {
                        String s = e.getMessage();
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateReminders(Intent intent) {
        try {

            medReminders.clear();
            medRemindersDate.clear();
            Bundle passedData = intent.getExtras();
            Calendar medStartDate = (Calendar) passedData.get(MedicationReminderActivity.INTENT_MED_START_DATE);
            MedstartDate = new SimpleDateFormat("yyyy-MM-dd").format(medStartDate.getTime());
            Object[] objMedReminders = (Object[]) passedData.get(MedicationReminderActivity.INTENT_MED_REMINDER_TIMES);
            for (int z = 0; z < objMedReminders.length; z++) {
                medReminders.add((Calendar) objMedReminders[z]);
            }

            //convert calendar to date
            for (int i = 0; i < medReminders.size(); i++) {
                medRemindersDate.add(medReminders.get(i).getTime());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateTitration(Intent intent) {
        try {

            Bundle passedData = intent.getExtras();

            boolean titrationOn = (Boolean) passedData.get(MedicationTitrationActivity.INTENT_TITRATION_ON);
            Calendar titrationStartDate = (Calendar) passedData.get(MedicationTitrationActivity.INTENT_TITRATION_START_DATE);
            int titrationNumWeeks = (int) passedData.get(MedicationTitrationActivity.INTENT_TITRATION_NUM_WEEKS);
            TitrationON = titrationOn;
            TitrationNumWeekS = titrationNumWeeks;
            TitrationStartDatE = new SimpleDateFormat("yyyy-MM-dd").format(titrationStartDate.getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void PrescriptionSelectedReminders(Intent intent) {
        try {
            if (intent != null) {
                Bundle passedData = intent.getExtras();
                PrescriptionReminderTime = new ArrayList<String>();
                PrescriptionReminderTime = (ArrayList<String>) passedData.get(PrescriptionRenewalReminderActivity.INTENT_PRESCRIPTION_REMNDERD);
                PrescrptionRenewalDate = (String) passedData.get(PrescriptionRenewalReminderActivity.RENEWAL_DATE);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                        switch (where) {
                            case 0:
                                fragment = new MedicationDairy();
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
                                break;
                            case 3:
                                fragment = new CalendarFragmentV2();
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

