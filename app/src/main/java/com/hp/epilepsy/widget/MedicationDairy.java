package com.hp.epilepsy.widget;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.adapter.MedicationsListViewCustomAdapter;
import com.hp.epilepsy.widget.model.NewMedication;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */

/**
 * -----------------------------------------------------------------------------
 * Class Name: MedicationDairy
 * Created By:Mahmoud
 * Modified By:Shruti and Nikunj
 * Purpose:to display list of created med
 * -----------------------------------------------------------------------------
 */
public class MedicationDairy extends Fragment {
    ListView medication_Lstview;
    TextView userMessage;
    MedicationsListViewCustomAdapter adapter;
    Button add;
    private int MedicationID;
    DBAdapter adapterr;
    String[] MedicineName;
    String[] StartDate;
    String[] Image1Path;
    String[] Image2Path;
    int[] MedicineID;
    int names = 0;
    int image1 = 0;
    int image2 = 0;
    ArrayList<NewMedication> medications = new ArrayList<>();
    public static final String MEDICATION_ID = "MedicationID";
  //  public static final String  strtext1;





    public MedicationDairy() {
    }

    View rootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_medication_dairy, container, false);

        medication_Lstview = (ListView) rootView.findViewById(R.id.Medication_Listview);
        userMessage = (TextView) rootView.findViewById(R.id.Message);

        adapterr = new DBAdapter(this.getActivity());
        adapterr.displayAllUsers();
        GetListOfMedications();
      //  strtext1 = getArguments().getString("CID");


        add = (Button) rootView.findViewById(R.id.Add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Bundle args = new Bundle();
                    Fragment frag = new com.hp.epilepsy.widget.NewMedication();
                    String tagString = getString(R.string.NEW_MEDICATION_FRAGMENT);
                    //the below -1 value mean the user need to add new Medication not update exist one
                    args.putSerializable(MEDICATION_ID, -1);
                    args.putInt("where", 0);
                    frag.setArguments(args);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    String backStateName = frag.getClass().getName();
                    ft.replace(R.id.content_frame, frag, tagString);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(backStateName);
                    ft.commit();


            }
        });

        adapter = new MedicationsListViewCustomAdapter(this.getActivity(), MedicineName, StartDate, Image1Path, Image2Path, MedicineID, 0);
        medication_Lstview.setAdapter(adapter);
        medication_Lstview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                    Bundle args = new Bundle();
                    Fragment frag = new com.hp.epilepsy.widget.NewMedication();
                    String tagString = getString(R.string.NEW_MEDICATION_FRAGMENT);
                    String backStateName = frag.getClass().getName();
                    args.putSerializable("MedicationID", MedicineID[position]);
                    args.putInt("where", 0);
                    frag.setArguments(args);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, frag, backStateName);
                    ft.addToBackStack(null);
                    ft.commit();


                }//


                //navigate to NewMedication Page with MedicationID
                // to get all data related with this Medication by it's ID

          //  }
        });
        return rootView;
    }

    public void GetListOfMedications() {

        medications = adapterr.getMedicationss();
        if (medications.size() == 0) {

            userMessage.setVisibility(View.VISIBLE);
        }

        //Initialize array's sizes
        for (NewMedication newMedication : medications) {
            names++;
            image1++;
            image2++;
        }

        MedicineName = new String[names];
        StartDate = new String[names];
        MedicineID = new int[names];
        Image1Path = new String[image1];
        Image2Path = new String[image2];

        for (int i = 0; i < medications.size(); i++) {
            NewMedication newMedication = medications.get(i);
            MedicineID[i] = newMedication.getId();
            MedicineName[i] = newMedication.getMedication_name();

            StartDate[i] = DateTimeFormats.reverseDateFormat(newMedication.getStart_date());

            String Image = newMedication.getMedicationImages().get(0);
            Image1Path[i] = Image;
            if (newMedication.getMedicationImages().size() > 1) {
                String Image2 = newMedication.getMedicationImages().get(1);
                Image2Path[i] = Image2;
            }
        }
    }

}
