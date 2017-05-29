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
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.IRESTWebServiceCaller;
import com.hp.epilepsy.widget.adapter.DBAdapter;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetPasswordActivity extends Activity implements OnClickListener, IRESTWebServiceCaller {
    private String userName;
    private String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forget_password);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Button login = (Button) findViewById(R.id.submit);
        login.setOnClickListener(this);

        userName = getIntent().getExtras().get("userName").toString();
        email = getIntent().getExtras().get("email").toString();

        try {
            handleSubmit();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }
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
        if (view.getId() == R.id.submit) {
        }
    }

    public void handleSubmit() {

        try {
            String errorMessage = validateInputs(userName, email);
            if (errorMessage.length() == 0) {
                DBAdapter adapter = new DBAdapter(this);

                try {
                    adapter.handleForgetPasswordOffline(userName, email, this);
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseReceived(String jsonServiceResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonServiceResponse);
            String status = jsonObject.getString("status");
            if (status.equals("true")) {
                String question = jsonObject.getString("question");
                String answer = jsonObject.getString("answer");

                String question2 = jsonObject.getString("question2");
                String answer2 = jsonObject.getString("answer2");

                String question3 = jsonObject.getString("question3");
                String answer3 = jsonObject.getString("answer3");

                Intent intent = new Intent(this, ConfirmationCodeActivity.class);

                intent.putExtra("userName", userName);
                intent.putExtra("email", email);
                intent.putExtra("question", question);
                intent.putExtra("answer", answer);

                intent.putExtra("question2", question2);
                intent.putExtra("answer2", answer2);

                intent.putExtra("question3", question3);
                intent.putExtra("answer3", answer3);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.username_email_validation_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), "Error Parsing JSON object in Response");
        }
    }

    public String validateInputs(String userName, String email) {
        if (userName.length() == 0 || userName.indexOf(' ') != -1 || userName.indexOf('\'') != -1
                || userName.indexOf(',') != -1) {
            return getString(R.string.invalid_username);
        }
        if (email.length() == 0 || email.indexOf('\'') != -1 || email.indexOf(',') != -1) {
            return getString(R.string.invalid_email);
        }
        return "";
    }

    @Override
    public Context getContext() {
        return this;
    }

}
