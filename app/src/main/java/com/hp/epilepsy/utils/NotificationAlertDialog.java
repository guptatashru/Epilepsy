package com.hp.epilepsy.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.CalendarFragmentV2;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationAlertDialog extends DialogFragment {

    public NotificationAlertDialog() {
        // Required empty public constructor
    }

    public static NotificationAlertDialog newInstance(String title,String Message,String Type,int flag) {
        NotificationAlertDialog frag = new NotificationAlertDialog();
        Bundle args = new Bundle();
        args.putString("Title", title);
        args.putString("Message", Message);
        args.putString("Type", Type);
        args.putInt("Flag",flag);
        frag.setArguments(args);
        return frag;
    }

    /**
     * -----------------------------------------------------------------------------
     * Class Name: onCreateDialog
     * Created By:mahmoud
     * Modified By:Nikunj & Shruti
     * Purpose: Notification dialog
     * -----------------------------------------------------------------------------
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String Type = getArguments().getString("Type");
        String Title = getArguments().getString("Title");
        String Message = getArguments().getString("Message");
        final int Flag=getArguments().getInt("Flag");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.icon60)
                .setTitle(Title)

                .setMessage(Message)
                .setPositiveButton("Dismiss",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(Flag==1)
                                {

                                }
                                else
                                {
                                    CalendarFragmentV2 fragment = new CalendarFragmentV2();
                                    FragmentManager frgManager = getFragmentManager();
                                    frgManager.beginTransaction().replace(R.id.content_frame, fragment, getString(R.string.calendar_frag_tag))
                                            .addToBackStack(null).commit();
                                }

                            }
                        }
                )
                .create();
    }

}
