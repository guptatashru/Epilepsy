package com.hp.epilepsy.widget;

/**
 * @author Said Gamal
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.adapter.ImageGalleryAdapter;
import com.hp.epilepsy.widget.model.CapturedImages;
import com.hp.epilepsy.widget.model.SeizureDetails;

import java.util.List;

/**
 * The Class VideoGalleryView.
 */
public class ImageGalleryView extends Fragment implements IStepScreen {

    /** The m view. */
    private Activity mView;



    /** The video files. */
    private List<CapturedImages> ImagesFiles;

    private boolean playVideo;

    private SeizureDetails seizureDetails;

    public ImageGalleryView(){

    }

	@SuppressLint("ValidFragment")
	public ImageGalleryView(Activity mView) {
		this.mView = mView;
		//setPlayVideo(true);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}
	
	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView;
		rootView = inflater.inflate(R.layout.image_gallery_view_layout,
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
			if(mView==null)mView=getActivity();
			if(mView!=null){
            GridView gridView = (GridView) mView.findViewById(R.id.Image_gallery);


            if(gridView!=null){
            ImageGalleryAdapter customGridAdapter;

                //vhange here
            this.ImagesFiles = (List<CapturedImages>) getArguments().getSerializable("ImageFiles");

            customGridAdapter = new ImageGalleryAdapter(mView, R.layout.image_gallery_row_layout, ImagesFiles);

            gridView.setAdapter(customGridAdapter);

            }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public SeizureDetails getSeizureDetails() {
		return seizureDetails;
	}

	public void setSeizureDetails(SeizureDetails seizureDetails) {
		this.seizureDetails = seizureDetails;
	}


}
