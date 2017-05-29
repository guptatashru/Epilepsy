package com.hp.epilepsy.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.DBAdapter;

public class ConfirmationCodeActivity extends Activity implements
        OnClickListener {

    DBAdapter adapter;
    private String userName;
    private String question;
    private String answer;
    private String question2;
    private String answer2;
    private String question3;
    private String answer3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_confirmation_code);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            TextView textView = (TextView) findViewById(R.id.question);
            TextView textView3 = (TextView) findViewById(R.id.question3);
            TextView textView2 = (TextView) findViewById(R.id.question2);

            EditText answer22 = (EditText) findViewById(R.id.answer2);
            EditText answer33 = (EditText) findViewById(R.id.answer3);


            adapter = new DBAdapter(this);
            userName = getIntent().getExtras().get("userName").toString();

            question = getIntent().getExtras().get("question").toString();
            answer = getIntent().getExtras().get("answer").toString();

            question2 = getIntent().getExtras().get("question2").toString();
            answer2 = getIntent().getExtras().get("answer2").toString();

            question3 = getIntent().getExtras().get("question3").toString();
            answer3 = getIntent().getExtras().get("answer3").toString();


            //check if the question2 || question 3|| answer2||answer3 is null this mean the user register befor update with one question not three
            if (!question2.equals("null") && !question3.equals("null") && !answer3.equals("null") && !answer2.equals("null")) {
                textView.setText(question);
                textView2.setText(question2);
                textView3.setText(question3);
            } else {
                textView.setText(question);
                textView2.setVisibility(View.GONE);
                textView3.setVisibility(View.GONE);
                answer22.setVisibility(View.GONE);
                answer33.setVisibility(View.GONE);

            }

            Button login = (Button) findViewById(R.id.submit);
            login.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public  void onPause()
    {
        super.onPause();
        /*ApplicationBackgroundCheck.getDecrement(getApplicationContext());
        System.out.println("getCurrentValue21"+ApplicationBackgroundCheck.getCurrentValue(getApplicationContext()));*/
    }
    @Override
    protected void onResume()
    {
        super.onResume();
      /*  ApplicationBackgroundCheck.setIncrement(getApplicationContext());
        System.out.println("getCurrentValue22"+ApplicationBackgroundCheck.getCurrentValue(getApplicationContext()));*/
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.submit) {
            String answer = ((EditText) findViewById(R.id.answer))
                    .getText().toString();

            String answer2 = ((EditText) findViewById(R.id.answer2))
                    .getText().toString();

            String answer3 = ((EditText) findViewById(R.id.answer3))
                    .getText().toString();


            try {

                if (question2.equals("null") || question3.equals("null") || answer3.equals("null") || answer2.equals("null")) {

                    if ((answer != null && answer.equals(this.answer))) {

                        ShownUserPassword(userName);

                    } else {
                        Toast.makeText(this, getString(R.string.wrong_answer), Toast.LENGTH_SHORT).show();
                    }
                } else {

                    if (((answer != null && answer.equals(this.answer)) && (answer2 != null && answer2.equals(this.answer2)))) {
                        ShownUserPassword(userName);

                    } else if ((answer2 != null && answer2.equals(this.answer2)) && (answer3 != null && answer3.equals(this.answer3))) {
                        ShownUserPassword(userName);
                    } else if ((answer != null && answer.equals(this.answer)) && (answer3 != null && answer3.equals(this.answer3))) {
                        ShownUserPassword(userName);
                    } else {
                        Toast.makeText(this, getString(R.string.wrong_answer), Toast.LENGTH_SHORT).show();
                    }

                }

            } catch (Exception ex) {
                //Toast.makeText(getApplication(), ex.toString(), Toast.LENGTH_LONG).show();
            }


        }
    }


    public void ShownUserPassword(String Username) {
        try {
            String Password = adapter.GetPasswordbyUsername(Username);

            AlertDialog.Builder builderInner = new AlertDialog.Builder(
                    ConfirmationCodeActivity.this);
            builderInner.setMessage(Password);
            builderInner.setTitle("Please save your password below!");
            builderInner.setPositiveButton(
                    "Redirect to Login Page",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RedirecttoLoginPage();
                        }
                    });
            builderInner.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void RedirecttoLoginPage() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }


}
