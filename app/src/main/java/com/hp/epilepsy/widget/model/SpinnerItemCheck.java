package com.hp.epilepsy.widget.model;

import java.io.Serializable;

public class SpinnerItemCheck implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ISpinnerItem item;
	
	private boolean isChecked;
	public ISpinnerItem getItem() {
		return item;
	}
	public void setItem(ISpinnerItem item) {
		this.item = item;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
}
