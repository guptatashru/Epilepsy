package com.hp.epilepsy.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.hp.epilepsy.utils.AnalyticsApplication;
import com.hp.epilepsy.utils.IRESTWebServiceCaller;
import com.hp.epilepsy.utils.ServiceUtil;
import com.hp.epilepsy.widget.adapter.DBAdapter;

public class RegisterActivity extends Activity implements OnClickListener, IRESTWebServiceCaller
{
   DBAdapter adapter;
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      setContentView(R.layout.activity_register);

      adapter = new DBAdapter(this);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      Button login = (Button)findViewById(R.id.register);
      login.setOnClickListener(this);

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
   public void onClick(View view)
   {

      if (view.getId() == R.id.register)
      {

            handleRegister();

      }
   }

   private void handleRegister()
   {

      EditText editText = (EditText)findViewById(R.id.password);
      String password = editText.getText().toString();
      editText = (EditText)findViewById(R.id.password_confirm);
      String passwordConfirm = editText.getText().toString();
      editText = (EditText)findViewById(R.id.userName);
      String username = editText.getText().toString();
      editText = (EditText)findViewById(R.id.answer);
      String answer = editText.getText().toString();
      editText = (EditText)findViewById(R.id.confirm_answer);
      String confirmAnswer = editText.getText().toString();
      editText = (EditText)findViewById(R.id.answer2);
      String answer2 = editText.getText().toString();
      editText = (EditText)findViewById(R.id.confirm_answer2);
      String confirmAnswer2 = editText.getText().toString();
      editText = (EditText)findViewById(R.id.answer3);
      String answer3 = editText.getText().toString();
      editText = (EditText)findViewById(R.id.confirm_answer3);
      String confirmAnswer3 = editText.getText().toString();
      editText = (EditText)findViewById(R.id.email);
      String email = editText.getText().toString();



      String errorMessage = validateInputs(answer, confirmAnswer, username, email, password, passwordConfirm,answer2,confirmAnswer2,answer3,confirmAnswer3);

      if (errorMessage.length() == 0)
      {

         try {
            handleRegisterLocally();
         }catch (Exception ex)
         {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
         }


      }
      else
      {
         Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
      }
   }

   private String validateInputs(String answer, String confirmAnswer, String username, String email, String password,
      String passwordConfirm,String answer2,String confirmAnswer2,String answer3,String confirmAnswer3)
   {
      if (username.length() == 0 || username.indexOf(' ') != -1 || username.indexOf('\'') != -1
         || username.indexOf(',') != -1)
      {
         return getString(R.string.invalid_username);
      }
      if (password.length() == 0 || !LoginActivity.checkValidation(password) || password.indexOf('\'') != -1
         || password.indexOf(',') != -1)
      {
         return getString(R.string.invalid_password);
      }
      if (!password.equals(passwordConfirm))
      {
         return getString(R.string.password_not_match);
      }
      if (email.length() == 0 || email.indexOf('\'') != -1 || email.indexOf(',') != -1)
      {
         return getString(R.string.invalid_email);
      }
      if (answer.length() == 0 || !answer.equals(confirmAnswer) || answer.indexOf('\'') != -1
         || answer.indexOf(',') != -1)
      {
         return getString(R.string.invalid_answer);
      }
      if (answer2.length() == 0 || !answer2.equals(confirmAnswer2) || answer2.indexOf('\'') != -1
              || answer2.indexOf(',') != -1)
      {
         return getString(R.string.invalid_answer);
      }
      if (answer3.length() == 0 || !answer3.equals(confirmAnswer3) || answer3.indexOf('\'') != -1
              || answer3.indexOf(',') != -1)
      {
         return getString(R.string.invalid_answer);
      }

      return "";
   }

   @Override
   public void onResponseReceived(String jsonServiceResponse)
   {
   }




   public void handleRegisterLocally()
   {
      try
      {


         EditText editText = (EditText)findViewById(R.id.password);
         String password = editText.getText().toString();
         editText = (EditText)findViewById(R.id.userName);
         String userName = editText.getText().toString();
         editText = (EditText)findViewById(R.id.email);
         String email = editText.getText().toString();
         editText = (EditText)findViewById(R.id.name);
         String name = editText.getText().toString();
         editText = (EditText)findViewById(R.id.phone);
         String phone = editText.getText().toString();


         //Update the three questions instead of spinner

         TextView question1=(TextView)findViewById(R.id.question1);
         String question=question1.getText().toString();
         editText = (EditText)findViewById(R.id.answer);
         String answer = editText.getText().toString();
         TextView question2=(TextView)findViewById(R.id.question2);
         String question2_body=question2.getText().toString();
         editText = (EditText)findViewById(R.id.answer2);
         String answer2 = editText.getText().toString();
         TextView question3=(TextView)findViewById(R.id.question3);
         String question3_body=question3.getText().toString();
         editText = (EditText)findViewById(R.id.answer3);
         String answer3 = editText.getText().toString();


         adapter.displayAllUsers();

         adapter.handleRegister(userName, password, question, answer, name, email, phone,question2_body,answer2,question3_body,answer3);

         if (ServiceUtil.isNetworkConnected(this)) {

            AnalyticsApplication.getInstance().trackEvent("UX", userName+" Registered","new User");

            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("submitted", true);
            editor.commit();
         }

         Intent mainView = new Intent(this, LoginActivity.class);
         startActivity(mainView);
         Toast.makeText(this, getString(R.string.user_created), Toast.LENGTH_SHORT).show();
      }catch (Exception ex)
      {
         Toast.makeText(getApplicationContext(),ex.toString(),Toast.LENGTH_LONG).show();
      }
   }


   @Override
   public Context getContext()
   {
      return this;
   }

}
