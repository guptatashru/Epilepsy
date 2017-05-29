package com.hp.epilepsy.widget;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.model.NewContactEntity;

import java.text.ParseException;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewContactFragment extends Fragment implements View.OnClickListener {


    NewContactEntity newContactEntity;

    EditText et_name, et_relationship;
    EditText et_email,et_contact_number;
    LinearLayout linear_btn_save, linear_btn_delete;



    DBAdapter dbAdapter ;
    private boolean Update = false;
    private int ContactID;
    private int appointmentContact;
    public NewContactFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        rootView = inflater.inflate(R.layout.fragment_new_contact, container, false);
        initializeViews(rootView);
        return rootView;
    }

    void initializeViews(View rootView) {
        try {
            newContactEntity = new NewContactEntity();
            dbAdapter = new DBAdapter(this.getActivity());

            et_name = (EditText) rootView.findViewById(R.id.et_name);
            et_relationship = (EditText) rootView.findViewById(R.id.et_relationship);
            et_email = (EditText) rootView.findViewById(R.id.et_email);
            et_contact_number = (EditText) rootView.findViewById(R.id.et_contact_number);

            linear_btn_save = (LinearLayout) rootView.findViewById(R.id.linear_btn_save);
            linear_btn_delete = (LinearLayout) rootView.findViewById(R.id.linear_btn_delete);

            linear_btn_save.setOnClickListener(this);
            linear_btn_delete.setOnClickListener(this);


            ContactID = (int) getArguments().getInt("ContactID",-1);
            appointmentContact = (int) getArguments().getInt("AppointmentContacts",-1);


            if (ContactID !=-1){
                GetContactDatafromDB(ContactID);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void GetContactDatafromDB(int ContactID) {

        try {
            Update = true;
            newContactEntity = dbAdapter.getContactbyID(ContactID);

            newContactEntity.getId();

            fillFields(newContactEntity.getContactName(),  newContactEntity.getRelationship(), newContactEntity.getE_mail(), newContactEntity.getContactNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public void fillFields(String contactName, String relationship, String e_mail, String contactNumber) {
        et_name.setText(contactName);
        et_relationship.setText(relationship);
        et_email.setText(e_mail);
        et_contact_number.setText(contactNumber);

    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

                case R.id.linear_btn_save:
                    try {
                        if (Update) {
                            UpdateContact();
                        } else {
                            SaveNewContact();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    break;
                case R.id.linear_btn_delete:
                    if (Update) {
                        UpdateContactToDeleted();
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


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /************************************************************************************************/
    private String validateInputs(String ContactName, String Relationship,String ContactNumber) {
        if (ContactName.length() == 0) {
            return "Contact Name can't be empty";
        } else if (Relationship.length() == 0) {
            return "Relationship  can't be empty";
        } else if (ContactNumber.length() == 0) {
            return "ContactNumber  can't be empty";
        }
        return "";
    }

    public void SaveNewContact() {
        String errorMessage = validateInputs(et_name.getText().toString(), et_relationship.getText().toString(),et_contact_number.getText().toString());
        if (errorMessage.length() == 0) {
            try {
                newContactEntity.setContactName(et_name.getText().toString());
                newContactEntity.setRelationship(et_relationship.getText().toString());
                newContactEntity.setE_mail(et_email.getText().toString());
                newContactEntity.setContactNumber(et_contact_number.getText().toString());
                newContactEntity.setIsDeleted(false);


                long contact_id = dbAdapter.SaveNewContact(newContactEntity);
                Toast.makeText(getActivity(), "Contact " +newContactEntity.getContactName()+" saved successfully", Toast.LENGTH_LONG).show();

                if(appointmentContact==3)
                {
                    Bundle args = new Bundle();
                    Fragment frag = new NewAppointment();
                    String tagString = getString(R.string.NEW_APPOINTMENT_FRAGMENT);
                    args.putSerializable("AppointmentID",-1);
                    args.putInt("where",0);
                    frag.setArguments(args);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame,frag,tagString);
                    String backStateName = frag.getClass().getName();
                    ft.addToBackStack(backStateName);
                    ft.commit();

                }else
                {
                    Fragment fragment= new ContactsList();
                    FragmentManager fragmentManager = getActivity().getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }

            } catch (Exception ex) {
                Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }



    /************************************************************************************/

    public void UpdateContactToDeleted() {

        try {
            NewContactEntity contact = new NewContactEntity();
            contact.setId(ContactID);
            contact.setIsDeleted(true);
            dbAdapter.UpdateContactToDeleted(contact);

            //return to medications list after update...
            Fragment fragment = new ContactsList();
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void UpdateContact() throws ParseException {

        String errorMessage = validateInputs(et_name.getText().toString(), et_relationship.getText().toString(),et_contact_number.getText().toString());

        if (errorMessage.length() == 0) {
            newContactEntity.setContactName(et_name.getText().toString());
            newContactEntity.setRelationship(et_relationship.getText().toString());
            newContactEntity.setE_mail(et_email.getText().toString());
            newContactEntity.setContactNumber(et_contact_number.getText().toString());
            newContactEntity.setIsDeleted(false);


            try {
                long id = dbAdapter.UpdateContact(newContactEntity);

                Fragment fragment = new ContactsList();
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


}
