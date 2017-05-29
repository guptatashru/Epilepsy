package com.hp.epilepsy.widget;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hp.epilepsy.R;

/*import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;*/

public class xyz extends Fragment {
Button btnSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.x1, container, false);
        btnSave=(Button)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    Fragment fr= new NewMedication();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();


                transaction.replace(R.id.fragment_place, fr);
                transaction.addToBackStack(null);
                transaction.commit();*/
            }
        });
        // Inflate the layout for this fragment
        return view;

    }
}
