package com.hp.epilepsy.widget;

/**
 * @author Said Gamal
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.VideoGalleryAdapter;
import com.hp.epilepsy.widget.model.RecordedVideo;
import com.hp.epilepsy.widget.model.SeizureDetails;

import java.util.List;

/**
 * The Class VideoGalleryView.
 */
public class VideoGalleryView extends Fragment implements IStepScreen {

    /** The m view. */
    private Activity mView;
    
    /** The video files. */
    private List<RecordedVideo> videoFiles;
    
    private boolean playVideo;
    
    private SeizureDetails seizureDetails;
    
    public VideoGalleryView(){
    	
    }

	@SuppressLint("ValidFragment")
	public VideoGalleryView(Activity mView) {		
		this.mView = mView;
		setPlayVideo(true);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean("playVideo", isPlayVideo());
		outState.putSerializable("seizureDetails", getSeizureDetails());
	}
	
	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView;
		rootView = inflater.inflate(R.layout.video_gallery_view_layout,
				container, false);
		return rootView;
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
		try {
			if(mView==null)
				mView=getActivity();


			if(mView!=null){

            GridView gridView = (GridView) mView.findViewById(R.id.video_gallery);

            if(gridView!=null){

            VideoGalleryAdapter customGridAdapter;
            this.videoFiles = (List<RecordedVideo>) getArguments().getSerializable("videoFiles");
				int countOfVideos=this.videoFiles.size();
				if(countOfVideos==0)
				{
					Toast.makeText(getActivity(),"No Videos Found",Toast.LENGTH_LONG).show();

				}
            Bundle args = getArguments();
            setSeizureDetails((SeizureDetails) args.getSerializable("seizureDetails"));
            if(getSeizureDetails()!=null)setPlayVideo(false);
            if(savedState!=null){

                setPlayVideo(savedState.getBoolean("playVideo"));
                setSeizureDetails((SeizureDetails) savedState.getSerializable("seizureDetails"));
            }

            customGridAdapter = new VideoGalleryAdapter(mView, R.layout.video_gallery_row_layout, videoFiles);
            customGridAdapter.setPlayVideo(isPlayVideo());
            customGridAdapter.setSeizureDetails(getSeizureDetails());
            gridView.setAdapter(customGridAdapter);
            }


            }
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
}
