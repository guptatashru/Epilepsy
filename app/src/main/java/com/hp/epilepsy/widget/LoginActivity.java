package com.hp.epilepsy.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.hp.epilepsy.R;
import com.hp.epilepsy.Services.OnClearFromRecentService;
import com.hp.epilepsy.utils.AnalyticsApplication;
import com.hp.epilepsy.utils.ApplicationBackgroundCheck;
import com.hp.epilepsy.utils.IRESTWebServiceCaller;
import com.hp.epilepsy.utils.RESTWebServiceConnector;
import com.hp.epilepsy.utils.ServiceUtil;
import com.hp.epilepsy.widget.adapter.DBAdapter;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends Activity implements OnClickListener, IRESTWebServiceCaller {

    public static String userName;
    DBAdapter adapter;
    private Tracker mTracker;
    int medId,medReminderId,NotificationID,presNotificationId,appNotificationId;
    String medTypeFlag,title,UserNAme,MedicatioReminderId,MedicationReminderDate,date,time,medication_type,medication_message;
    Date datetime;
    boolean flagVal=false;
    public static String MED_TYPE = "MED_TYPE";
    public static String MED_TYPE_FLAG = "MED_TYPE_FLAG";
    public static String MED_ID_Notify = "MED_ID_Notify";
    public static String MED_NAME = "MED_NAME";
    public static String MED_REMINDER_DATE = "MED_REMINDER_DATE";
    public static String MED_REMINDER_TIME = "MED_REMINDER_TIME";
    public static String USER_NAME = "USER_NAME";
    public static String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static String MED_REMINDER_ID = "MED_REMINDER_ID";
    public static boolean checkValidation(String password) {
        String regex = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z]).{8,}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: onCreate login Activity
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * -----------------------------------------------------------------------------
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        if(!ApplicationBackgroundCheck.isMyServiceRunning(OnClearFromRecentService.class,this));
        {
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
            ApplicationBackgroundCheck.setIncrement(getApplicationContext());
        }

        getBackupDb();
        // Obtain the shared Tracker instance.
        // AnalyticsApplication application = (AnalyticsApplication) getApplication();
        //mTracker = application.getDefaultTracker();
        adapter = new DBAdapter(this);
        // adapter.displayAllUsers();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
        TextView signUp = (TextView) findViewById(R.id.signup_button);
        String htmlString = getString(R.string.sign_up);
        signUp.setText(Html.fromHtml(htmlString));
        signUp.setOnClickListener(this);
        TextView forgetPassword = (TextView) findViewById(R.id.forget_password);
        htmlString = getString(R.string.forget_password);
        forgetPassword.setText(Html.fromHtml(htmlString));
        forgetPassword.setOnClickListener(this);


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
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.login:
                handleLogin();
                break;
            case R.id.signup_button:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.forget_password:
                //TODO  show dialog with all users that registered on the app
                List<String> users;
                users = adapter.displayAllUsers();
                showDialogUsers(users);

                break;
        }
    }

    public void showDialogUsers(List<String> usersss) {
        try {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(LoginActivity.this);
            builderSingle.setIcon(R.drawable.ic_launcher);
            builderSingle.setTitle("Select your Username");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    LoginActivity.this,
                    android.R.layout.select_dialog_singlechoice, usersss);

            builderSingle.setNegativeButton(
                    "cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builderSingle.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String strName = arrayAdapter.getItem(which);
                                forgetPassword(strName);
                            }catch (Exception ex)
                            {
                                Toast.makeText(getApplicationContext(),ex.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            builderSingle.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forgetPassword(String strName) {
        //TODO check if the user already registered on the version 1 can forget password scenario or not?
        try {
            String Email = adapter.GetEmailbyUsername(strName);
            if(Email!=null) {
                Intent intent = new Intent(this, ForgetPasswordActivity.class);
                intent.putExtra("userName", strName);
                intent.putExtra("email", Email);
                startActivity(intent);
            }else
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
                alertDialogBuilder.setTitle("No data related to this user");
                alertDialogBuilder
                        .setMessage("This account not created on this device, you can't get your password from this device,Please contact Epilepsy Ireland for support: info@epilepsy.ie")
                        .setCancelable(true)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }catch(Exception ex)
        {
        }
    }

    public void handleLogin() {
        // TODO Auto-generated method stub
        try {
            EditText editText = (EditText) findViewById(R.id.userName);
            String username = editText.getText().toString();
            editText = (EditText) findViewById(R.id.password);
            String password = editText.getText().toString();
            String errorMessage = validateInputs(username, password);
            if (errorMessage.length() == 0) {
                DBAdapter adapter = new DBAdapter(this);
                SharedPreferences sharedPreff = this.getPreferences(Context.MODE_PRIVATE);
                boolean submitted = sharedPreff.getBoolean("submitted", false);
                adapter.handleLogin(username, password, this);
                // if(submitted) {
                if (ServiceUtil.isNetworkConnected(this)) {
                    AnalyticsApplication.getInstance().trackEvent("UX", username + " Log in", "User Login");
                    SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("submitted", true);
                    editor.commit();
                }
                // }on
            } else {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * -----------------------------------------------------------------------------
     * Class Name: onNewIntent(Login Activity)
     * Created By:Nikunj & Shruti
     * -----------------------------------------------------------------------------
     */

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle passedData = intent.getExtras();
        if (passedData != null) {

            this.setIntent(intent);
            flagVal=true;
            if (passedData.containsKey(RecordVideoActivity.MED_TYPE)) {
                DBAdapter adapter = new DBAdapter(getApplicationContext());
                medId = passedData.getInt(RecordVideoActivity.MED_ID_Notify);
                medTypeFlag = (String) passedData.get(RecordVideoActivity.MED_TYPE_FLAG);
                title = (String) passedData.get(RecordVideoActivity.MED_NAME);
                UserNAme = (String) passedData.get(RecordVideoActivity.USER_NAME);
                MedicatioReminderId = ((String) passedData.get(RecordVideoActivity.MED_TYPE)).substring(3);
                date = (String) passedData.get(RecordVideoActivity.MED_REMINDER_DATE);
                time = (String) passedData.get(RecordVideoActivity.MED_REMINDER_TIME);
                LoginActivity.userName = UserNAme;
                medReminderId = passedData.getInt(RecordVideoActivity.MED_REMINDER_ID);
                NotificationID = (int) passedData.get(RecordVideoActivity.NOTIFICATION_ID);

            } else if (passedData.getString("type").equals("App")) {
                medication_type = (String) passedData.getString("type");
                medication_message = (String) passedData.getString("appMessage");
                appNotificationId=(int) passedData.getInt("appUniqueId");

            } else if (passedData.getString("type").equals("EME")) {
                medication_type = (String) passedData.getString("type");
                medication_message = (String) passedData.getString("emeMessage");
                medId=(int)passedData.getInt("emeReminderId");
            } else if (passedData.getString("type").equals("PRES")) {
                medication_type = (String) passedData.getString("type");
                medication_message = (String) passedData.getString("presMessage");
                presNotificationId=(int)passedData.getInt("presUniqueId");
            } else {
                medication_type = (String) passedData.getString("type");
                medication_message = (String) passedData.getString("titMessage");
            }
        }
    }




    public String validateInputs(String userName, String password) {
        if (userName.length() == 0 || userName.indexOf(' ') != -1 || userName.indexOf('\'') != -1
                || userName.indexOf(',') != -1) {
            return getString(R.string.invalid_username);
        }
        if (password.length() == 0 || !LoginActivity.checkValidation(password) || password.indexOf('\'') != -1
                || password.indexOf(',') != -1) {
            return getString(R.string.invalid_password);
        }
        return "";
    }
    /**
     * -----------------------------------------------------------------------------
     * Class Name: onResponseReceived(Login Activity)
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * -----------------------------------------------------------------------------
     */

    @Override
    public void onResponseReceived(String jsonServiceResponse) {
        try {
            final DBAdapter adapter = new DBAdapter(this);
            EditText userName = (EditText) findViewById(R.id.userName);
            EditText editText = (EditText) findViewById(R.id.password);
            String password = editText.getText().toString();
            try {
                JSONObject jsonObject = new JSONObject(jsonServiceResponse);
                String status = jsonObject.getString("status");
                if (status.equals("true")) {
                    LoginActivity.userName = userName.getText().toString();
                    adapter.saveuserData(LoginActivity.userName, password);
                    if(!flagVal)
                    {
                        final Intent mainView = new Intent(this,
                                adapter.isFirstLogin(LoginActivity.userName) ? TermsConditionsActivity.class : HomeView.class);
                        String offlineChangeEmail = adapter.isOfflinePasswordUpdated();
                        if (offlineChangeEmail.length() != 0 && ServiceUtil.isNetworkConnected(this)) {
                            RESTWebServiceConnector connector = new RESTWebServiceConnector(new IRESTWebServiceCaller() {
                                @Override
                                public void onResponseReceived(String jsonServiceResponse) {
                                    // TODO Auto-generated method stub
                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonServiceResponse);
                                        String status = jsonObject.getString("status");
                                        if (status.equals("true")) {

                                            adapter.resetOfflineUpdateFlag();
                                        }


                                        startActivity(mainView);

                                    } catch (JSONException e) {
                                        Log.e(LoginActivity.class.getName(), "Error Parsing JSON object in Response");
                                    } finally {

                                    }
                                }

                                @Override
                                public Context getContext() {
                                    // TODO Auto-generated method stub
                                    return LoginActivity.this;
                                }
                            });
                            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

                        } else {

                            startActivity(mainView);


                        }
                    }
                    else if(flagVal)
                    {
                        if(!TextUtils.isEmpty(medTypeFlag))
                        {

                        Intent intent=new Intent(this,MedicationAlarmsActivity.class);
                        intent.putExtra(MED_TYPE, "Med" + medReminderId);
                        intent.putExtra(MED_TYPE_FLAG, medTypeFlag);
                        intent.putExtra(MED_NAME, title);
                        intent.putExtra(MED_REMINDER_DATE, date);
                        intent.putExtra(MED_REMINDER_TIME, time);
                        intent.putExtra(USER_NAME, LoginActivity.userName);
                        intent.putExtra(MED_ID_Notify, medId);
                        intent.putExtra(NOTIFICATION_ID, medReminderId);
                        intent.putExtra(MED_REMINDER_ID, medReminderId);
                        intent.putExtra("flag","Abc1");
                        startActivity(intent);


                    }
                        else if(!TextUtils.isEmpty(medication_type))
                        {
                            Intent intent=new Intent(this,HomeView.class);
                            if(medication_type.equals("App"))
                            {
                                intent.putExtra("type",medication_type);
                                intent.putExtra("message",medication_message);
                                intent.putExtra("uniqueAppId",appNotificationId);
                            }
                            else if(medication_type.equals("EME"))
                            {
                                intent.putExtra("type",medication_type);
                                intent.putExtra("message",medication_message);
                                intent.putExtra("id",medId);
                            }
                            else if(medication_type.equals("PRES"))
                            {
                                intent.putExtra("type",medication_type);
                                intent.putExtra("message",medication_message);
                                intent.putExtra("presUniqueId",presNotificationId);
                            }
                            else{
                                intent.putExtra("type",medication_type);
                                intent.putExtra("message",medication_message);
                            }
                            startActivity(intent);

                        }


                    }

                }

                else {
                    Toast.makeText(this, getString(R.string.invalid_username_password), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.e(getClass().getName(), "Error Parsing JSON object in Response " + jsonServiceResponse);
                Toast.makeText(this, getString(R.string.invalid_username_password), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }
    public void getBackupDb()
    {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + getPackageName() + "/databases/db.Epilepsy";
                String backupDBPath = "backupname.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }

    }

}
