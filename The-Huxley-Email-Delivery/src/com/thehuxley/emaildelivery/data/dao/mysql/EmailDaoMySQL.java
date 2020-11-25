package com.thehuxley.emaildelivery.data.dao.mysql;

import com.thehuxley.emaildelivery.data.Configurator;
import com.thehuxley.emaildelivery.data.dao.EmailDao;

public class EmailDaoMySQL extends EmailDao{
	
	public EmailDaoMySQL() {
		GET_TO_SEND = "SELECT * FROM email_to_send WHERE STATUS = 'TOSEND' LIMIT " + Configurator.getProperty("mail.limit");
		UPDATE = "UPDATE email_to_send set status = ? WHERE id = ?";
	}

}
