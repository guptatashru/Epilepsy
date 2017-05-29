package com.hp.epilepsy.widget.model;

import java.io.Serializable;

public class SeizureTrigger implements ISpinnerItem, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String trigger;
	private String description;
	private int id;
	private boolean userDefined;
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return trigger;
	}

	public void setName(String triggerName) {
		trigger = triggerName;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isUserDefined() {
		return userDefined;
	}

	public void setUserDefined(int userDefined) 
	{
		this.userDefined = (userDefined == 1) ? true : false;
	}
	
	public int getUserDefinedSql()
	{
		return (this.userDefined)? 1 : 0;
	}
	


}
