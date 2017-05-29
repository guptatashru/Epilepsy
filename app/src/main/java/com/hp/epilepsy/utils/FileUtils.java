package com.hp.epilepsy.utils;

/**
 * @author Said Gamal
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import com.hp.epilepsy.hardware.camera.CameraPreview;
import com.hp.epilepsy.widget.LoginActivity;
import com.hp.epilepsy.widget.adapter.DBAdapter;
import com.hp.epilepsy.widget.model.CapturedImages;
import com.hp.epilepsy.widget.model.RecordedVideo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class FileUtils.
 */
@SuppressLint("SimpleDateFormat")
public class FileUtils {

	/** The Constant EPILEPSY_VIDEO_FILE_PATTERN. */
	private static final String EPILEPSY_VIDEO_FILE_PATTERN = "([^\\s]+(\\.(?i)(/temp*|temp))$)";

	private static final String EVENTS_APP_DIR = ".EpilepsyEvents";

	public static final int APPOINTMENT_TYPE=1;
	public static final int SIEZURE_TYPE=2;
	public static final int MEDICATION_TYPE=3;
	public static final int EMERGENCY_MEDICATION_TYPE=4;
	public static final int PRESCRIPTION_RENEWAL_TYPE=6;



	public static File CreateCSVFile(String fileName) {
		try {
			if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                return null;
            }
			File mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory(), EVENTS_APP_DIR);

			if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(EVENTS_APP_DIR, "failed to create directory");
                    return null;
                }
            }

			File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + fileName + ".csv");
			return mediaFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static File getOutputMediaFile(int type, Activity context) {
		try {
			// To be safe, you check that the SDCard is mounted
			if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                return null;
            }

			String lastLoginUser;
			DBAdapter adapter = new DBAdapter(context);

			lastLoginUser = LoginActivity.userName == null ? adapter
                    .getLastLoginUser() : LoginActivity.userName;

			lastLoginUser = lastLoginUser + InstallationIdentity.id(context);
			File mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory(),
                    CameraPreview.APP_DIR
                            + (lastLoginUser != null ? File.separator
                            + lastLoginUser : ""));
			// This location works best if you want the created images to be shared
			// between applications and persist after your app has been uninstalled.

			// Create the storage directory if it does not exist
			if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(CameraPreview.APP_DIR, "failed to create directory");
                    return null;
                }
            }

			// Create a media file name
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
			File mediaFile;
			if (type == CameraPreview.MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + timeStamp + ".temp");
            } else {
                return null;
            }
			return mediaFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	///preview all vidio for all gallaryies
	public static List<RecordedVideo> getRecordedVideoFiles(String userName, Context context) {
		try {
			File videosDir = new File(Environment.getExternalStorageDirectory(),
                    CameraPreview.APP_DIR + File.separator + userName + InstallationIdentity.id(context));
			File[] dirs = videosDir.listFiles();
			List<RecordedVideo> fls = new ArrayList<RecordedVideo>();
			try {
                for (File file : dirs) {
                    if (!file.isDirectory()) {
                        if (isEpilepsyVideoFile(file.getAbsolutePath())) {
                            RecordedVideo video = extractRecordedVideoData(file);
                            fls.add(video);
                        }
                    }
                }
            } catch (Exception e) {
                e.toString();
            }
			return fls;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}



	public static List<CapturedImages> getCapturedImagesFiles(String userName,Context context) {
		File ImagesDir = new File(Environment.getExternalStorageDirectory(),
				CameraPreview.APP_DIR_Images + File.separator + userName + InstallationIdentity.id(context));
		File[] dirs = ImagesDir.listFiles();
		List<CapturedImages> fls = new ArrayList<CapturedImages>();
		try {
			for (File file : dirs) {
				if (!file.isDirectory()) {

					CapturedImages image =extractCaptureImageData(file);
					fls.add(image);

				}
			}
		} catch (Exception e) {
			e.toString();
		}
		return fls;
	}


	/*public static List<CapturedImages> getCapturedImagesFiles(String userName, Context context) {
		try {
			List<CapturedImages> fls=null;
			File ImagesDir = new File(Environment.getExternalStorageDirectory(),
                    CameraPreview.APP_DIR_Images + File.separator + userName + InstallationIdentity.id(context));
			File[] dirs = ImagesDir.listFiles();
			if(dirs!=null) {
				fls = new ArrayList<CapturedImages>();
			try {
				for (File file : dirs) {
					if (!file.isDirectory()) {

						CapturedImages image = extractCaptureImageData(file);
						fls.add(image);

					}
				}
            } catch (Exception e) {
				e.toString();
			}
			}
			return fls;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}*/


	public static RecordedVideo extractRecordedVideoData(File file) {
		try {
			RecordedVideo video = new RecordedVideo();
			try {

                video.setPath(file.getAbsolutePath());
                Date lastModDate = new Date(file.lastModified());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM. dd,yyyy");
                String lastModified = dateFormat.format(lastModDate);
                video.setCreationDate(lastModified);
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(file.getAbsolutePath());
                long timeInmillisec = 0L;
                String time = retriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                if (time != null) {
                    timeInmillisec = Long.parseLong(time);
                }
                long duration = timeInmillisec / 1000;
                long hours = duration / 3600;
                long minutes = (duration - hours * 3600) / 60;
                long seconds = duration - (hours * 3600 + minutes * 60);
                video.setDuration((hours < 10 ? "0" + hours : hours) + ":"
						+ (minutes < 10 ? "0" + minutes : minutes) + ":"
						+ (seconds < 10 ? "0" + seconds : seconds));
            } catch (Exception e) {
                e.toString();
            }
			return video;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public static CapturedImages extractCaptureImageData(File file) {
		CapturedImages image = new CapturedImages();
		try {

			image.setPath(file.getAbsolutePath());
			Date lastModDate = new Date(file.lastModified());
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM. dd,yyyy");
			String lastModified = dateFormat.format(lastModDate);
			image.setCreationDate(lastModified);

		} catch (Exception e) {
			e.toString();
		}
		return image;
	}


/*
	public static CapturedImages extractCaptureImageData(File file) {
		try {
			CapturedImages image = new CapturedImages();
			try {

                image.setPath(file.getAbsolutePath());
                Date lastModDate = new Date(file.lastModified());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM. dd,yyyy");
                String lastModified = dateFormat.format(lastModDate);
                image.setCreationDate(lastModified);

            } catch (Exception e) {
                e.toString();
            }
			return image;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
*/

	public static boolean isEpilepsyVideoFile(final String path) {
		try {
			Pattern pattern = Pattern.compile(EPILEPSY_VIDEO_FILE_PATTERN);
			Matcher matcher = pattern.matcher(path);
			return matcher.matches();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
