package com.hp.epilepsy.widget.model;

import java.io.Serializable;

public class SeizureDuration implements ISpinnerItem, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7793516696156120314L;
	private long id;
	private String name;
	private String description;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
