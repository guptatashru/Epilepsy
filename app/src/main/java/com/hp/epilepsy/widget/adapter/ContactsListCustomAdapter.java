package com.hp.epilepsy.widget.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.epilepsy.Manifest;
import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.ContactsList;
import com.hp.epilepsy.widget.model.NewContactEntity;
import com.hp.epilepsy.widget.model.NewMedication;

import java.io.File;
import java.util.ArrayList;

public class ContactsListCustomAdapter extends BaseAdapter {
    public Activity context;
    private static final int REQUEST_PHONECALL = 2;
    public LayoutInflater inflater;
    ArrayList<NewContactEntity> contactsList;


    public ContactsListCustomAdapter(Activity context, ArrayList<NewContactEntity> contactsList) {
        super();
        this.context = context;
        this.contactsList = contactsList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return contactsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.contact_list_row_layout, null);

                holder.contactName = (TextView) convertView.findViewById(R.id.contact_name);
                holder.relationship = (TextView) convertView.findViewById(R.id.relationship);
                holder.mobile = (TextView) convertView.findViewById(R.id.mobile);
                holder.mail_address = (TextView) convertView.findViewById(R.id.mail_address);

                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            holder.contactName.setText(contactsList.get(position).getContactName() + "");
            holder.relationship.setText(contactsList.get(position).getRelationship() + "");
            holder.mobile.setText(contactsList.get(position).getContactNumber() + "");
            holder.mail_address.setText(contactsList.get(position).getE_mail() + "");

//            if (contactsList.get(position).getE_mail()!=null && contactsList.get(position).getE_mail().length()>0) {
//                holder.mail_address.setText(contactsList.get(position).getE_mail()+ "");
//            }else {
//                holder.mail_address.setVisibility(View.INVISIBLE);
//            }


            holder.mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ContactsList list = new ContactsList();
                    list.PhoneNumber=holder.mobile.getText().toString();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        boolean hasPermssion = list.CheckCameraPermission(context);
                        if (hasPermssion) {


                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    String phoneNumber = holder.mobile.getText().toString();
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    context.startActivity(callIntent);
                        } else {
                            context.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONECALL);
                        }
                    } else {


                    }




                }
            });

            holder.mail_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent callIntent = new Intent(Intent.ACTION_CALL);
//                    String phoneNumber = holder.mobile.getText().toString();
//                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
//                    context.startActivity(callIntent);
                    sendMail(holder.mail_address.getText().toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }


    public static class ViewHolder {
        TextView contactName;
        TextView relationship;
        TextView mobile;
        TextView mail_address;
    }


    public void sendMail(String mailTo) {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", mailTo, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
