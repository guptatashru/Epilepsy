package com.hp.epilepsy.widget.model;

import java.io.Serializable;

/**
 * The Class RecordedVideo.
 *
 * @author Said Gamal
 */
public class RecordedVideo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4735771050859627147L;

	/** The creation date. */
	private String creationDate = "";
	
	/** The duration. */
	private String duration = "";
	
	/** The path. */
	private String path = "";
	
	/**
	 * Gets the creation date.
	 *
	 * @return the creation date
	 */
	public String getCreationDate() {
		return creationDate;
	}
	
	/**
	 * Sets the creation date.
	 *
	 * @param creationDate the new creation date
	 */
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	
	/**
	 * Gets the duration.
	 *
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}
	
	/**
	 * Sets the duration.
	 *
	 * @param duration the new duration
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Sets the path.
	 *
	 * @param path the new path
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
