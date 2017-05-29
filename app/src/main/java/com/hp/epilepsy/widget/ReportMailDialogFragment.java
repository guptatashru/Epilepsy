package com.hp.epilepsy.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.AlarmHelper;
import com.hp.epilepsy.utils.DateTimeFormats;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by elmalah on 5/3/2016.
 */
public class ReportMailDialogFragment extends DialogFragment {


View rootView;
String path;
String from;
String To;
    String startdatee;
    String enddatee;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
         rootView=inflater.inflate(R.layout.report_mail_dialog, null);

         path=getArguments().getString("PATH");
         from=getArguments().getString("FromDate");
         To=getArguments().getString("ToDate");

        Date Startdate= DateTimeFormats.convertStringToDate(from);
        Date enddate= DateTimeFormats.convertStringToDate(To);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");


         startdatee = dateFormat.format(Startdate);
         enddatee = dateFormat.format(enddate);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rootView)

                // Add action buttons
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        sendMail(path, startdatee,enddatee);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ReportMailDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }


    public void sendMail(String path,String startDate,String endDate) {

        Calendar c=Calendar.getInstance();

        Date DateTime = c.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        String date = dateFormat.format(DateTime);
        String time = timeFormat.format(DateTime);


        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[] { ((EditText)rootView.findViewById(R.id.mail_et)).getText().toString()});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Seizures and Missed Medications Report");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                "Seizures and missed medications report from "+startDate+" to "+ endDate+"."+"\n" +
                        "for the user "+LoginActivity.userName+"\n" +
                        "The date the screenshot was taken is "+date+ "\t and the time was "+time+"\n");
        emailIntent.setType("image/png");
        Uri myUri = Uri.parse("file://" + path);
        emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        startActivity(emailIntent);
    }
}
