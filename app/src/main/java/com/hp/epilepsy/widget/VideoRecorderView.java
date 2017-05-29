package com.hp.epilepsy.widget;

/**
 * @author Said Gamal
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.hardware.camera.CameraPreview;
import com.hp.epilepsy.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * The Class VideoRecorderView.
 */
@SuppressWarnings("deprecation")
public class VideoRecorderView extends Activity {
	
	/** The Constant TAG. */
	private static final String TAG = VideoRecorderView.class.getSimpleName();
	/**
	 * The Constant BACK_CAMERA_ID.
	 */
	private static final int BACK_CAMERA_ID = 0;
	/** The m preview. */
	private CameraPreview mPreview;
	/** The m camera. */
	private Camera mCamera;
	/** The is recording. */
	private boolean isRecording = false;
	/** The m media recorder. */
	private MediaRecorder mMediaRecorder;
	
    //private File recordedFile;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.video_recorder_layout);


		getCameraInstance();
		if(mCamera != null){
			// Create our Preview view and set it as the content of our activity.
	        mPreview = new CameraPreview(this, mCamera);
	        FrameLayout preview = (FrameLayout)findViewById(R.id.camera_preview);
	        preview.addView(mPreview);
	        //mCamera.setDisplayOrientation(90);
	        if(mCamera != null){
		    	 mCamera.setDisplayOrientation(getDisplayOrientation(this, BACK_CAMERA_ID));
		     }

		ImageButton capture = (ImageButton) findViewById(R.id.captureBtn);
		capture.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				
				recordVideoEvent();
			}

		});
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    if(mCamera != null){
	    	 mCamera.setDisplayOrientation(getDisplayOrientation(this, BACK_CAMERA_ID));
	     }
	}
	
	/**
	 * Gets the display orientation.
	 *
	 * @param activity the activity
	 * @param cameraId the camera id
	 * @return the display orientation
	 */
	private int getDisplayOrientation(Activity activity,
	         int cameraId) {
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     int rotation = activity.getWindowManager().getDefaultDisplay()
	             .getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;

	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     return result;
	 }
	
	/**
	 * A safe way to get an instance of the Camera object.
	 * 
	 * @return the camera instance
	 */
	private void getCameraInstance(){
	    try {
            mCamera = Camera.open(); // attempt to get a Camera instance
            int numCams = Camera.getNumberOfCameras();
    		if(numCams > 0){
    			try{
    				mCamera = Camera.open(0);
    				CamcorderProfile profile = CamcorderProfile
    			            .get(BACK_CAMERA_ID, CamcorderProfile.QUALITY_HIGH);
    				Camera.Parameters parameters = mCamera.getParameters();
    				parameters.setPreviewSize(profile.videoFrameWidth,profile.videoFrameHeight);
    				//parameters.setPreviewSize(720,480);
    				mCamera.setParameters(parameters);
    				mCamera.startPreview();
    			} catch (RuntimeException ex){
    			}
	    }
	    }
	    catch (Exception e){
	    }
	}
	
	/**
	 * Record video event.
	 */
	private void recordVideoEvent(){
		try {
			if (isRecording) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                findViewById(R.id.button_border).setBackgroundColor(getResources().getColor(R.color.blue_border));
                try{
                    // stop recording and release camera

                    mMediaRecorder.stop();
                    releaseMediaRecorder(); // release the MediaRecorder object
                    //save video data to DB
    //	            saveRecordedVideoDataToDB();
                    // show home view
                    showHomePage();
                    Toast.makeText(this, getResources().getString(R.string.vid_captured), Toast.LENGTH_LONG).show();
                }catch(RuntimeException stopException){
                    //handle cleanup here
                    //mCamera.lock();
                    showHomePage();
                    releaseMediaRecorder(); // release the MediaRecorder object
                    stopException.toString();
                }

                // inform the user that recording has stopped
                isRecording = false;
            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                findViewById(R.id.button_border).setBackgroundColor(getResources().getColor(R.color.red_border));

                // initialize video camera
                if (prepareVideoRecorder()) {
                    // Camera is available and unlocked, MediaRecorder is prepared,
                    // now you can start recording
                    mMediaRecorder.start();

                    // inform the user that recording has started
                    isRecording = true;
                } else {
                    // prepare didn't work, release the camera
                    releaseMediaRecorder();
                    // inform user
                }
            }
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */

	private void showHomePage() {
		if(LoginActivity.userName!=null){//logged in
			Intent mainView = new Intent(this, HomeView.class);
			startActivity(mainView);
			
		}else{
			Intent mainView = new Intent(this, RecordVideoActivity.class);
			startActivity(mainView);
		}
		this.finish();
	}
	
	/**
	 * Prepare video recorder.
	 * 
	 * @return true, if successful
	 */
	private boolean prepareVideoRecorder(){

	    //getCameraInstance();
	    mMediaRecorder = new MediaRecorder();

	    // Step 1: Unlock and set camera to MediaRecorder
	    mCamera.stopPreview();
	    mCamera.unlock();
	    mMediaRecorder.setCamera(mCamera);

	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	 
	    // Step 3: Set output format and encoding (for versions prior to API Level 8)
	    	
	    CamcorderProfile profile = CamcorderProfile
	            .get(BACK_CAMERA_ID, CamcorderProfile.QUALITY_HIGH);
	    
	    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

	    mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
	    mMediaRecorder.setVideoFrameRate(profile.videoFrameRate);
	    mMediaRecorder.setVideoSize(profile.videoFrameWidth,
	            profile.videoFrameHeight);

	    mMediaRecorder.setAudioChannels(profile.audioChannels);
	    mMediaRecorder.setAudioEncodingBitRate(profile.audioBitRate);
	    mMediaRecorder.setAudioSamplingRate(profile.audioSampleRate);

	    mMediaRecorder.setAudioEncoder(profile.audioCodec);
	    mMediaRecorder.setVideoEncoder(profile.videoCodec);

        // Step 3.5: Set Max Recording Length
        mMediaRecorder.setMaxDuration(0);
	    
	    // Step 4: Set output file
	    File file = FileUtils.getOutputMediaFile(CameraPreview.MEDIA_TYPE_VIDEO,this);
	    mMediaRecorder.setOutputFile(file.toString());
//	    recordedFile = file;
	    // Step 5: Set the preview output
	    mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
	    //mMediaRecorder.setOrientationHint(90);
	     mMediaRecorder.setOrientationHint(getDisplayOrientation(this, BACK_CAMERA_ID));

        // Step 4.5: Listen for the max recording length stop signal
        mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    recordVideoEvent();

                }
            }
        });


	     // Step 6: Prepare configured MediaRecorder
	    try {
	        mMediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    return true;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
    public void onPause() {
        super.onPause();
	/*	ApplicationBackgroundCheck.getDecrement(getApplicationContext());
		System.out.println("getCurrentValue32"+ApplicationBackgroundCheck.getCurrentValue(getApplicationContext()));*/
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    /**
	 * Release media recorder.
	 */
	private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    /**
	 * Release camera.
	 */
	private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
}
