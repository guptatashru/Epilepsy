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
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.DateTimeFormats;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.adapter.MedicationsListViewCustomAdapter;
import com.hp.epilepsy.widget.model.EmergencyMedicationEntity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyMedicationsList extends Fragment {
    ListView emergency_Medication_Listview;
    TextView userMessage;
    MedicationsListViewCustomAdapter adapter;
    Button add_Emergency_Medication;
    DBAdapter adapterr;
    String[] MedicineName;
    String[] StartDate;
    String[] Image1Path;
    String[] Image2Path;
    boolean IsThereEMedicationExist=false;
    int[] MedicineID;
    int names = 0;
    int image1 = 0;
    int image2 = 0;

    public EmergencyMedicationsList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = null;
        rootView = inflater.inflate(R.layout.fragment_emergency_medications_list, container, false);
       initializeViews(rootView);
        return rootView;
    }
void initializeViews(View rootView){
    try {
        emergency_Medication_Listview = (ListView) rootView.findViewById(R.id.emergency_Medication_Listview);
        userMessage = (TextView) rootView.findViewById(R.id.Message);

        adapterr = new DBAdapter(this.getActivity());
        //adapterr.displayAllUsers();
        GetListOfEmergencyMedications();


        add_Emergency_Medication = (Button) rootView.findViewById(R.id.add_Emergency_Medication);
        add_Emergency_Medication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!IsThereEMedicationExist) {
                    Bundle args = new Bundle();
                    Fragment frag = new MyEmergencyMedicationsFragment();
                    String tagString = getString(R.string.MY_EMERGENCY_MEDICATION);
                    //the below -1 value mean the user need to add new Medication not update exist one
                    args.putSerializable("MedicationID", -1);
                    args.putInt("where", 0);
                    frag.setArguments(args);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, frag, tagString);
                    String backStateName = frag.getClass().getName();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(backStateName);
                    ft.commit();
                }else
                {
                    Toast.makeText(getActivity(),"you already have an emergency medication, only one emergency medication allowed",Toast.LENGTH_LONG).show();
                }
            }
        });

        adapter = new MedicationsListViewCustomAdapter(this.getActivity(), MedicineName, StartDate, Image1Path, Image2Path, MedicineID,1);
        emergency_Medication_Listview.setAdapter(adapter);
        emergency_Medication_Listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {

                    //navigate to NewMedication Page with MedicationID
                    // to get all data related with this Medication by it's ID
                    Bundle args = new Bundle();
                    Fragment frag = new MyEmergencyMedicationsFragment();
                    String tagString = getString(R.string.MY_EMERGENCY_MEDICATION);
                    args.putSerializable("MedicationID", MedicineID[position]);
                    args.putInt("where", 0);
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
    public void GetListOfEmergencyMedications() {
        try {
            ArrayList<EmergencyMedicationEntity> emergencyMedications = new ArrayList<EmergencyMedicationEntity>();
            emergencyMedications = adapterr.getEmergencyMedications();
            if (emergencyMedications.size()==0) {
                userMessage.setVisibility(View.VISIBLE);
            }else
            {
                IsThereEMedicationExist=true;
            }

            //Initialize array's sizes
            for (EmergencyMedicationEntity emergencyMedicationEntity : emergencyMedications) {
                names++;
                image1++;
                image2++;
            }

            MedicineName = new String[names];
            StartDate = new String[names];
            MedicineID = new int[names];
            Image1Path = new String[image1];
            Image2Path = new String[image2];

            for (int i = 0; i < emergencyMedications.size(); i++) {
                EmergencyMedicationEntity emergencyMedicationEntity = emergencyMedications.get(i);
                MedicineID[i] = emergencyMedicationEntity.getId();
                MedicineName[i] = emergencyMedicationEntity.getEmergencyMedicationName();
                StartDate[i] = DateTimeFormats.reverseDateFormat(emergencyMedicationEntity.getExpiryDate());
                String Image = emergencyMedicationEntity.getMedicationImages().get(0);
                Image1Path[i] = Image;
                if (emergencyMedicationEntity.getMedicationImages().size() > 1) {
                    String Image2 = emergencyMedicationEntity.getMedicationImages().get(1);
                    Image2Path[i] = Image2;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
