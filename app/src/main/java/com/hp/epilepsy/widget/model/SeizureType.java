package com.hp.epilepsy.widget.model;

import java.io.Serializable;

/**
 * The Class DrawerMenuItem.
 * 
 * @author Said Gamal
 */
public class SeizureType implements ISpinnerItem, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8286001565641588813L;
	private long id;
	private boolean userDefined;
	private String name;
	private String description;
	

	public SeizureType() {

	}

	public SeizureType(String name) {
		setName(name);
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString()
	{
		return name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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
