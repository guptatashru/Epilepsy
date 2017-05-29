package com.hp.epilepsy.widget;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.epilepsy.Manifest;
import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.ContactsListCustomAdapter;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.model.NewContactEntity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsList extends Fragment {
    ListView contacts_Listview;
    ContactsListCustomAdapter adapter;
    Button add_Contact;
    DBAdapter adapterr;
    TextView userMessage;

   public String PhoneNumber=null;
private static final int REQUEST_PHONECALL = 2;
    int[] MedicineID;

    ArrayList<NewContactEntity> contactsList;
    public ContactsList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        rootView = inflater.inflate(R.layout.fragment_contacts_list, container, false);
        initializeViews(rootView);

        return rootView;
    }




void initializeViews(View rootView){
    try {
        contactsList = new ArrayList<>();
        contacts_Listview = (ListView) rootView.findViewById(R.id.contacts_Listview);
        userMessage = (TextView) rootView.findViewById(R.id.Message);
        adapterr = new DBAdapter(this.getActivity());
        adapterr.displayAllUsers();
        GetListOfContacts();


        add_Contact = (Button) rootView.findViewById(R.id.add_Contact);
        add_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Fragment frag = new NewContactFragment();
                String tagString = getString(R.string.CONTACTS_LIST);
                //the below -1 value mean the user need to add new Medication not update exist one
                args.putSerializable("ContactID", -1);
                frag.setArguments(args);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, frag, tagString);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        adapter = new ContactsListCustomAdapter(this.getActivity(),contactsList);
        contacts_Listview.setAdapter(adapter);
        contacts_Listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {

                    //navigate to NewMedication Page with MedicationID
                    // to get all data related with this Medication by it's ID
                    Bundle args = new Bundle();
                    Fragment frag = new NewContactFragment();
                    String tagString = getString(R.string.NEW_CONTACT);
                    args.putInt("ContactID", contactsList.get(position).getId());
                    frag.setArguments(args);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, frag, tagString);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();

                }
            });
    } catch (Exception e) {
        e.printStackTrace();
    }
}



    @TargetApi(Build.VERSION_CODES.M)
    public boolean CheckCameraPermission( Context context) {

        int permissionCheckCamera = ContextCompat.checkSelfPermission(context,Manifest.permission.CALL_PHONE);
        if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONECALL);
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
            case REQUEST_PHONECALL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + PhoneNumber));
                    startActivity(callIntent);
                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONECALL);
                    }
                    // Permission Denied
                    Toast.makeText(getActivity(), "Phone Call Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void GetListOfContacts() {

        contactsList = adapterr.getContactsList();
        if (contactsList.size()==0) {
            userMessage.setVisibility(View.VISIBLE);
        }
    }
}
