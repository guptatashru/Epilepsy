/**
 * 
 */
package com.hp.epilepsy.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.model.ISpinnerItem;
import com.hp.epilepsy.widget.model.SeizureDetails;

import java.io.File;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mustamus
 * 
 */
@SuppressLint("SimpleDateFormat")
public class SeizureDetailsFragment extends Fragment {

	private static final int deleteID=918841148;
	private SeizureDetails seizureDetails;
	private String[] weekDays = { "SATURDAY", "SUNDAY", "MONDAY", "TUESDAY",
			"WEDNESDAY", "THURSDAY", "FRIDAY" };

	private static String listObjectItems(List<ISpinnerItem> mitems) {

		StringBuffer buffer = new StringBuffer();
		String prefix = "";
		for (ISpinnerItem iSpinnerItem : mitems) {
			buffer.append(prefix);
			prefix = "\n";
			buffer.append(iSpinnerItem.getName());
		}


		return buffer.toString();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onPause()
	{
		try {
			super.onPause();
		} catch (Exception e) {
			e.toString();
		}
	}

	@Override
	public void onStop() {
		try {
			super.onStop();
		} catch (Exception e) {
			e.toString();
		}
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        try {
			super.onSaveInstanceState(outState);
		} catch (Exception e) {
			e.toString();
		}

    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_seizure_details,
				container, false);

		try {
			setUpDetailsInfo(rootView);
		} catch (Exception e) {
			e.toString();
		}

		return rootView;
	}

	private void setUpDetailsInfo(View view) throws ParseException
	{
		try {
			seizureDetails = (SeizureDetails) getArguments()
                    .getSerializable("seizureDetails");

			ImageView imageView = (ImageView) view.findViewById(
                    R.id.sdvf_thumbImage);


			TextView textView = (TextView) view.findViewById(R.id.sdvf_type);
			textView.setText(seizureDetails.getSeizureType().getName());

			Date now=new SimpleDateFormat("yyyy-MM-dd").parse(seizureDetails.getDate());
			SimpleDateFormat	simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
			textView = (TextView) view.findViewById(R.id.sdvf_day_of_week);
			textView.setText(simpleDateformat.format(now).toString());


			Format formatter = new SimpleDateFormat("MMM dd");
			String s=formatter.format(now);

			textView = (TextView) view.findViewById(R.id.sdvf_day_month);
			textView.setText(formatter.format(now).toString());

			textView = (TextView) view.findViewById(R.id.sdvf_duration);
			textView.setText(seizureDetails.getSeizureDuration().getName());

			textView = (TextView) view.findViewById(R.id.sdvf_time);
			textView.setText(seizureDetails.getTime());


			textView = (TextView) view.findViewById(R.id.sdvf_trigger);
			textView.setText(listObjectItems(new ArrayList<ISpinnerItem>(seizureDetails.getSeizureTriggers())));



			if(seizureDetails.getDesc()==null||seizureDetails.getDesc().isEmpty())
            {
                textView = (TextView) view.findViewById(R.id.sdvf_desc_lbl);
                textView.setVisibility(View.INVISIBLE);
            }
            else {
                textView = (TextView) view.findViewById(R.id.sdvf_desc);
                textView.setText(seizureDetails.getDesc());
            }

			textView = (TextView) view.findViewById(
                    R.id.sdvf_emergency_medication_given);
			textView.setText((seizureDetails.isEmegencyMedicationGiven())?"Yes":"No");
			textView = (TextView) view.findViewById(R.id.sdvf_ambulance_called);
			textView.setText((seizureDetails.isAmbulanceCalled()?"Yes":"No"));
			textView = (TextView) view.findViewById(R.id.sdvf_pre_feature);
			textView.setText(listObjectItems(new ArrayList<ISpinnerItem>(seizureDetails.getSeizurePreFeatures())));
			textView = (TextView) view.findViewById(R.id.sdvf_post_feature);
			textView.setText(listObjectItems(new ArrayList<ISpinnerItem>(seizureDetails.getSeizurePostFeatures())));
			textView = (TextView) view.findViewById(R.id.sdvf_feature);
			textView.setText(listObjectItems(new ArrayList<ISpinnerItem>(seizureDetails.getSeizureFeatures())));
			if (seizureDetails.getRecordedVideo() != null
                    && !seizureDetails.getRecordedVideo().getPath().equals("")) {
                Bitmap bm = ThumbnailUtils.createVideoThumbnail(seizureDetails
                        .getRecordedVideo().getPath(),
                        MediaStore.Images.Thumbnails.MINI_KIND);
                imageView.setImageBitmap(bm);
                imageView.setOnClickListener(new OnClickListener() {

                    public void onClick(View arg0) {

                        showMediaPlayer(seizureDetails.getRecordedVideo().getPath());
                    }

                });
            } else {
                imageView.setVisibility(View.GONE);
            }


		} catch (ParseException e) {
			e.printStackTrace();
		}


	}

	private void showMediaPlayer(String filePath) {
		try {
			final Dialog mplayerDialog = new Dialog(getActivity());
			mplayerDialog.setCancelable(true);
			mplayerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			// dialog.getWindow().getDecorView()
			mplayerDialog.setContentView(R.layout.video_player_layout);
			// changePasswordDialog.setTitle(activity.getResources().getText(
			// R.string.changeMyPassword));

			// get the Refferences of views
			getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
			File clip = new File(filePath);
			// start playing video
			if (clip.exists()) {
                VideoView video = (VideoView) mplayerDialog
                        .findViewById(R.id.video);
                video.setVideoPath(clip.getAbsolutePath());

                MediaController ctlr = new MediaController(getActivity());
                ctlr.setMediaPlayer(video);
                video.setMediaController(ctlr);
                video.requestFocus();
                video.start();
                video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        // Do whatever u need to do here
                        mplayerDialog.dismiss();
                    }
                });
            }

			final ImageButton cancel = (ImageButton) mplayerDialog
                    .findViewById(R.id.deleteBtn);
			// Set On ClickListener
			cancel.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    mplayerDialog.dismiss();
                }
            });
			mplayerDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		MenuItem item=menu.add(Menu.NONE, deleteID, Menu.NONE, "");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item.setIcon(R.drawable.recycle_bin);

	}


	private void deleteEvent() {

		try {

			DBAdapter dbAdapter=new DBAdapter(getActivity());
			dbAdapter.removeSeizure(seizureDetails.getId());
		} catch (Exception e) {
			Toast.makeText(this.getActivity(), getString(R.string.delete_event_error), Toast.LENGTH_LONG).show();
		}
		Toast.makeText(this.getActivity(), getString(R.string.event_deleted), Toast.LENGTH_LONG).show();

		Fragment fragment = new CalendarFragmentV2();
		FragmentManager frgManager = getFragmentManager();
		frgManager.beginTransaction().replace(R.id.content_frame, fragment)
		.commit();

	}

	private void confirmDeleteEvent() {
		try {
			AlertDialog.Builder dialogBuilder= new AlertDialog.Builder(this.getActivity());

			dialogBuilder.setTitle("Confirm Delete");
			dialogBuilder.setMessage("Are you sure you wish to remove this Event?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    deleteEvent();

                }});
			dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    dialog.cancel();

                }});

			AlertDialog dialog=dialogBuilder.create();
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case deleteID:
			confirmDeleteEvent();
			return true;    
		default:
			return super.onOptionsItemSelected(item);
		}
	}




}
