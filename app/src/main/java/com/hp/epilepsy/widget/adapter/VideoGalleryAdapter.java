package com.hp.epilepsy.widget.adapter;

/**
 * @author Said Gamal
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.model.IVideoSetter;
import com.hp.epilepsy.widget.model.RecordedVideo;
import com.hp.epilepsy.widget.model.SeizureDetails;

import java.io.File;
import java.util.List;

/**
 * The Class VideoGalleryAdapter.
 */
public class VideoGalleryAdapter extends ArrayAdapter<RecordedVideo> {

    TextView lastModified;
    TextView duration;
   // TextView videoCount;
    /**
     * The layout resource id.
     */
    private int layoutResourceId;
    /**
     * The m view.
     */
    private Activity mView;

    /**
     * The ctlr.
     */
    private MediaController ctlr;
    private boolean playVideo;
//    int videoCountOfFiles;
    private Bitmap holderBitmap;

    private SeizureDetails seizureDetails;

    public VideoGalleryAdapter(Activity context, int layoutResourceId,
                               List<RecordedVideo> videos) {
        super(context, layoutResourceId, videos);
        this.layoutResourceId = layoutResourceId;
        this.mView = context;
  //      this.videoCountOfFiles=videos.size();
        this.holderBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.record_video);
    }


    /*
     * (non-Javadoc)
     *
     * @see android.widget.ArrTeayAdapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;


        try {
            final RecordedVideo recordedVideo = getItem(position);
            row = convertView;
            RecordHolder holder = null;



            if (row == null) {
                LayoutInflater inflater = mView.getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new RecordHolder();
                holder.imageItem = (ImageView) row.findViewById(R.id.thumbImage);
                lastModified = (TextView) row.findViewById(R.id.last_modified);
               // videoCount=(TextView)row.findViewById(R.id.videoCount);
                duration = (TextView) row.findViewById(R.id.duration);
                row.setTag(holder);
            } else {


                holder = (RecordHolder) row.getTag();
            }


            if (!holder.isLoaded) {
                System.out.println("not loaded");
                VideoPreviewAsyncTask mVideoPreviewAsyncTask = new VideoPreviewAsyncTask(holder);
                mVideoPreviewAsyncTask.execute(recordedVideo.getPath());
                holder.imageItem.setImageBitmap(holderBitmap);
            }

            holder.imageItem.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    if (isPlayVideo()) {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mView);
                        alertDialogBuilder.setTitle("Play or delete the video");
                        alertDialogBuilder
                                .setMessage("select play to show the video or delete to delete the video!")
                                .setCancelable(true)
                                .setPositiveButton("Play Video", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        showMediaPlayer(recordedVideo.getPath());
                                    }
                                })
                                .setNegativeButton("Delete Video", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        /////////////////////////////////////////
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mView);
                                        alertDialogBuilder.setTitle("This video will be deleted.");
                                        alertDialogBuilder
                                                .setMessage("Click ok to confirm or cancel.")
                                                .setCancelable(true)
                                                .setIcon(R.drawable.alert)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        File f = new File(recordedVideo.getPath());
                                                        f.delete();
                                                        Toast.makeText(mView, "Video Deleted Successfully.", Toast.LENGTH_LONG).show();
                                                        mView.onBackPressed();
                                                    }
                                                })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                        /////////////////////////////////////////////////////////////////////

                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        openSeizureDetails(recordedVideo);
                    }
                }
            });

            lastModified.setText(recordedVideo.getCreationDate());

            duration.setText(recordedVideo.getDuration());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return row;
    }



    protected void openSeizureDetails(RecordedVideo recordedVideo) {
        getSeizureDetails().setRecordedVideo(recordedVideo);
        FragmentManager frgManager = mView.getFragmentManager();
        Fragment mFragment = frgManager.findFragmentByTag(getContext().getResources().getString(R.string.record_siezure_detail_frag_tag));
        if (mFragment != null) {
            ((IVideoSetter) mFragment).setVideo(recordedVideo);
        }
        mView.onBackPressed();
    }

    private void showMediaPlayer(String filePath) {
        try {
            final Dialog mplayerDialog = new Dialog(mView);
            mplayerDialog.setCancelable(true);
            mplayerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mplayerDialog.setContentView(R.layout.video_player_layout);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayVideo() {
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
    }

    /**
     * The Class RecordHolder.
     */
    static class RecordHolder {

        /**
         * The image item.
         */
        ImageView imageItem;
        boolean isLoaded;

    }

    public class VideoPreviewAsyncTask extends AsyncTask<String, Void, Bitmap> {

        RecordHolder mHolder;

        VideoPreviewAsyncTask(RecordHolder holder) {
            mHolder = holder;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bm = ThumbnailUtils
                    .createVideoThumbnail(params[0],
                            MediaStore.Images.Thumbnails.MINI_KIND);
            return bm;
        }

        protected void onPostExecute(Bitmap mThumbnail) {
            mHolder.imageItem.setImageBitmap(mThumbnail);
            mHolder.isLoaded = true;
        }

    }
}