package com.thehuxley.emaildelivery.data.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thehuxley.emaildelivery.data.Connector;
import com.thehuxley.emaildelivery.data.model.Email;
import com.thehuxley.emaildelivery.data.model.ResourcesUtil;

public abstract class EmailDao {
	static Logger logger = LoggerFactory.getLogger(EmailDao.class);
	protected String GET_TO_SEND;
	protected String UPDATE;

	public ArrayList<Email> getEmailToSend() {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Obtendo email ...");
		}

		ArrayList<Email> emails = new ArrayList<Email>();

		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {

			logger.debug(GET_TO_SEND);

			statement = Connector.getConnection()
					.prepareStatement(GET_TO_SEND);
			rs = statement.executeQuery();
			
			while (rs.next()) {
				Email email = new Email();
				email.setId(rs.getLong("id"));
				email.setEmail(rs.getString("email"));
				email.setStatus(rs.getString("status"));
				email.setMessage(rs.getString("message"));
				update(email.getId(), "SENDING");
				emails.add(email);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			ResourcesUtil.closeResultSet(rs);
			ResourcesUtil.closeStatement(statement);
		}

		return emails;

	}

	public void update(long id, String status) {
		PreparedStatement statement = null;
		try {

			logger.debug(UPDATE);
			logger.debug("Atualizando o status do email " + id + " para " + status);

			statement = Connector.getConnection().prepareStatement(UPDATE);
			statement.setString(1, status);
			statement.setLong(2, id);
			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			ResourcesUtil.closeStatement(statement);
		}

	}

}
