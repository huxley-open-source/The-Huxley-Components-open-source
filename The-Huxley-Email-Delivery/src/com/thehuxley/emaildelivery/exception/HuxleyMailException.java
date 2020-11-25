package com.thehuxley.emaildelivery.exception;

import javax.mail.MessagingException;

public class HuxleyMailException extends MessagingException{
	
	private static final long serialVersionUID = -5099319329376784344L;

	public HuxleyMailException() {
		super();
	}
	
	public HuxleyMailException(String arg0) {		
		super(arg0);
	}
	
	public HuxleyMailException(String arg0, Exception arg1) {
		super(arg0, arg1);
	}
	
	@Override
	public String getMessage() {
		return super.getMessage();
	}
	
	
}
