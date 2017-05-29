package com.hp.epilepsy.widget.model;

public class SMS {
	private String phone;
	private String message;
	
	public SMS(String phone,String message){
		setPhone(phone);
		setMessage(message);
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
