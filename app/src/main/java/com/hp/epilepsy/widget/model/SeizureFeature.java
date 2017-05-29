package com.hp.epilepsy.widget.model;

import java.io.Serializable;

public class SeizureFeature implements ISpinnerItem, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private boolean userDefined;
	private String name;
	private String description;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	public void setName(String nam) {
		name = nam;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return description;
	}

	public void setDescription(String des) {
		description = des;
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
		this.userDefined=((userDefined == 1)? true : false);
	}
	
	public int getUserDefinedSql()
	{
		return (this.userDefined)? 1 : 0;
	}
}
