package com.hp.epilepsy.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.IRESTWebServiceCaller;
import com.hp.epilepsy.widget.adapter.DBAdapter;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends Activity implements OnClickListener, IRESTWebServiceCaller {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_password);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Button login = (Button) findViewById(R.id.submit);
        login.setOnClickListener(this);
    }
    @Override
    public  void onPause()
    {
        super.onPause();
     /*   ApplicationBackgroundCheck.getDecrement(getApplicationContext());
        System.out.println("getCurrentValue23"+ApplicationBackgroundCheck.getCurrentValue(getApplicationContext()));*/
    }
    @Override
    protected void onResume()
    {
        super.onResume();
     /*   ApplicationBackgroundCheck.setIncrement(getApplicationContext());
        System.out.println("getCurrentValue24"+ApplicationBackgroundCheck.getCurrentValue(getApplicationContext()));*/
    }

    @Override
    public void onClick(View view) {
        try {
            if (view.getId() == R.id.submit) {
                String password = ((TextView) findViewById(R.id.password)).getText().toString();
                String confirmPassword = ((TextView) findViewById(R.id.passowd_confirm)).getText().toString();
                if (password.equals(confirmPassword)) {
                    if (password.length() == 0 || !LoginActivity.checkValidation(password) || password.indexOf('\'') != -1
                            || password.indexOf(',') != -1) {
                        Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_LONG).show();
                    } else {
                        handleSubmit(password);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.password_not_match), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleSubmit(String password) {
        String userName = getIntent().getExtras().getString("userName");
        String email = getIntent().getExtras().getString("email");
        DBAdapter adapter = new DBAdapter(this);

        adapter.handleChangePassword(userName, email, password, this);

    }

    @Override
    public void onResponseReceived(String jsonServiceResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonServiceResponse);
            String status = jsonObject.getString("status");
            if (status.equals("true")) {
                Intent mainView = new Intent(this, LoginActivity.class);
                startActivity(mainView);
                Toast.makeText(this, getString(R.string.password_success_change), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.password_change_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), "Error Parsing JSON object in Response");
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

}
