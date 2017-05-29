package com.hp.epilepsy.widget.adapter;

/**
 * @author Said Gamal
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.MyEmergencyMedicationsFragment;
import com.hp.epilepsy.widget.NewMedication;
import com.hp.epilepsy.widget.model.CapturedImages;
import com.hp.epilepsy.widget.model.IimagesSetter;
import com.hp.epilepsy.widget.model.SeizureDetails;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class VideoGalleryAdapter.
 */
public class ImageGalleryAdapter extends ArrayAdapter<CapturedImages> {

	public int numofcolums;
	List<String> paths = new ArrayList<String>();
	List<String> unselected=new ArrayList<String>();
	int Clickindex = 0;
	RecordHolder holder = null;
	/**
	 * The layout resource id.
	 */
	private int layoutResourceId;
	/** The m view. */
	private Activity mView;
	/** The ctlr. */
	private MediaController ctlr;
	private boolean playVideo;
	private Bitmap holderBitmap;
	private SeizureDetails seizureDetails;

	/**
	 * Instantiates a new video gallery adapter.
	 *
	 * @param context
	 *            the context
	 * @param layoutResourceId
	 *            the layout resource id
	 * @param images
	 *            the videos
	 */
	public ImageGalleryAdapter(Activity context, int layoutResourceId, List<CapturedImages> images) {
		super(context, layoutResourceId, images);
		this.layoutResourceId = layoutResourceId;
		this.mView = context;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, final View convertView, ViewGroup parent) {
		final CapturedImages Cap_Images = getItem(position);
		View row = convertView;

		if (row == null) {
			LayoutInflater inflater = mView.getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new RecordHolder();
			holder.imageItem = (ImageView) row.findViewById(R.id.imagecaptured);
			holder.checked = (ImageView) row.findViewById(R.id.checked);
			paths.clear();

			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}

		if(!holder.isLoaded)
		{
			VideoPreviewAsyncTask mVideoPreviewAsyncTask= new VideoPreviewAsyncTask(holder);
			mVideoPreviewAsyncTask.execute(Cap_Images.getPath());
		}

		Button accept=(Button) mView.findViewById(R.id.accept);
		accept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		//	int asd=paths.size();
//
			/*	if(unselected.size()>0) {
					paths.removeAll(unselected);
				}*/

				GetSelectedImages(paths);
			}
		});



		/*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					ImageView checked=(ImageView) mView.findViewById(R.id.checked);
					checked.setVisibility(View.VISIBLE);
				}
			});*/




		holder.imageItem.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {


				ImageView img = (ImageView) arg0;
				img.setVisibility(View.VISIBLE);
				int paddingPixel = 5;
				float density = mView.getResources().getDisplayMetrics().density;
				int paddingDp = (int) (paddingPixel * density);

				if (paths.contains(Cap_Images.getPath().toString())) {

					//unselected.add(Cap_Images.getPath().toString());
					paths.remove(Cap_Images.getPath().toString());

					img.setPadding(0, 0, 0, 0);
				} else {
					img.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
					paths.add(Cap_Images.getPath().toString());
				}
				//numofcolums++;
				//holder.checked.setVisibility(arg0.);
			}
		});



	/*	holder.savee.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				//holder.checked.setVisibility(View.VISIBLE);


			}
		});*/

		TextView lastModified = (TextView) row.findViewById(R.id.imagename);
	    lastModified.setText(Cap_Images.getCreationDate());
		return row;
	}


	protected void GetSelectedImages(List<String> path) {
		// TODO Auto-generated method stub

		FragmentManager frgManager;
		frgManager = mView.getFragmentManager();
		Fragment mFragment=frgManager.findFragmentByTag(mView.getResources().getString(R.string.NEW_MEDICATION_FRAGMENT));
		Fragment eFragment=frgManager.findFragmentByTag(mView.getResources().getString(R.string.MY_EMERGENCY_MEDICATION));
				if(mFragment!=null && mFragment instanceof NewMedication)
				{
					((IimagesSetter)mFragment).setImage(path,numofcolums);
				}else if(eFragment!=null && eFragment instanceof MyEmergencyMedicationsFragment) {

					((IimagesSetter)eFragment).setImage(path,numofcolums);
				}
					//For some reason calling frgManager.popBackStack() will go to the first screen. But this works
					mView.onBackPressed();
	}

	/**
	 * Show media player.
	 *
	 * @param filePath
	 *            the file path
	 */
	private void showMediaPlayer(String filePath) {
		final Dialog mplayerDialog = new Dialog(mView);
		mplayerDialog.setCancelable(true);
		mplayerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// dialog.getWindow().getDecorView()
		// .setBackgroundResource(android.R.color.transparent);
		mplayerDialog.setContentView(R.layout.video_player_layout);
		// changePasswordDialog.setTitle(activity.getResources().getText(
		// R.string.changeMyPassword));

		// get the Refferences of views
		mView.getWindow().setFormat(PixelFormat.TRANSLUCENT);
		File clip = new File(filePath);
		// start playing video
		if (clip.exists()) {
			VideoView video = (VideoView) mplayerDialog
					.findViewById(R.id.video);
			video.setVideoPath(clip.getAbsolutePath());

			ctlr = new MediaController(mView);
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
				// TODO Auto-generated method stub
				mplayerDialog.dismiss();
			}
		});
		mplayerDialog.show();
	}

	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

/*	public boolean isPlayVideo() {
		return playVideo;
	}

	public void setPlayVideo(boolean playVideo) {
		this.playVideo = playVideo;
	}

	public SeizureDetails getSeizureDetails() {
		return seizureDetails;
	}

	public void setSeizureDetails(SeizureDetails seizureDetails) {
		this.seizureDetails = seizureDetails;
	}*/

	/**
	 * The Class RecordHolder.
	 */
	static class RecordHolder {

		/** The image item. */
		ImageView imageItem;
		ImageView checked;
		Button savee;
		boolean isLoaded;

	}

	public class VideoPreviewAsyncTask extends AsyncTask<String, Void, Bitmap>
	{

		RecordHolder mHolder;
		VideoPreviewAsyncTask( RecordHolder holder)
		{
			mHolder=holder;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			/*Bitmap bm = ThumbnailUtils
					.createVideoThumbnail(params[0],
							MediaStore.Images.Thumbnails.MICRO_KIND);


			Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(params[0]), 220, 220);
			return resized;*/
			Bitmap bm = null;
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(params[0], options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, 220, 220);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bm = BitmapFactory.decodeFile(params[0], options);
			return bm;
		}

		protected void onPostExecute(Bitmap mThumbnail)
		{
			mHolder.imageItem.setImageBitmap(mThumbnail);
			mHolder.isLoaded=true;
		}

	}
}