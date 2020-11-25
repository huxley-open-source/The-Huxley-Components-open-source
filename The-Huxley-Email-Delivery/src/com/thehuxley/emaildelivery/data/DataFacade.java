package com.thehuxley.emaildelivery.data;

import java.util.ArrayList;

import com.thehuxley.emaildelivery.data.dao.EmailDao;
import com.thehuxley.emaildelivery.data.dao.mysql.EmailDaoMySQL;
import com.thehuxley.emaildelivery.data.model.Email;

public final class DataFacade {

	/**
	 * @author Marcio Augusto Guimarães
	 * @author Romero Malaquias
	 * @version 1.0.0
	 * @since huxley-email-delivery 1.0.0
	 */
	private DataFacade() {
	}

	public static ArrayList<Email> getEmailToSend() {
		EmailDao email = new EmailDaoMySQL();
		return email.getEmailToSend();

	}

	public static void updateEmail(long id, String status) {
		EmailDao email = new EmailDaoMySQL();
		email.update(id,status);

	}

}
