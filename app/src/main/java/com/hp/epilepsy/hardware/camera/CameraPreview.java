package com.hp.epilepsy.hardware.camera;

/**
 * @author Said Gamal
 */

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * The Class CameraPreview. testing AGM
 */
@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {

    /**
     * The Constant MEDIA_TYPE_VIDEO.
     */
    public static final int MEDIA_TYPE_VIDEO = 200;
    public static final int MEDIA_TYPE_Images = 100;

    /**
     * The Constant APP_DIR.
     */
    public static final String APP_DIR = ".Epilepsy";
    public static final String APP_DIR_Images = "EpilepsyImages";
    /**
     * The tag.
     */
    private static String TAG = "Camera perview";
    public Context ctx;
    /**
     * The m holder.
     */
    private SurfaceHolder mHolder;
    /**
     * The m camera.
     */
    private Camera mCamera;
    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        ctx = context;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        // mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /* (non-Javadoc)
     * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the
        // preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    /* (non-Javadoc)
     * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        try {
            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }
            // stop preview before making changes

            mCamera.stopPreview();

            // set preview size and make any resize, rotate or
            // reformatting changes here
            // start preview with new settings
            //TODO auto focus here
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            mCamera.setParameters(parameters);

            mCamera.setPreviewDisplay(mHolder);
            //mCamera.autoFocus();
            requestLayout();
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /* (non-Javadoc)
     * @see android.view.SurfaceView#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            // We purposely disregard child measurements because act as a
            // wrapper to a SurfaceView that centers the camera preview instead
            // of stretching it.
            final int width = resolveSize(getSuggestedMinimumWidth(),
                    widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(),
                    heightMeasureSpec);
            setMeasuredDimension(width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}