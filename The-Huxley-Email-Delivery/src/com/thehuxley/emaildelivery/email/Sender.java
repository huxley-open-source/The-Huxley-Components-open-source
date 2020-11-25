package com.thehuxley.emaildelivery.email;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thehuxley.emaildelivery.data.Configurator;

public class Sender {
	
	private Logger logger = LoggerFactory.getLogger(Sender.class);
	
	private Transport transport;
	private Session session;
	private static Sender instance;
	
	private Sender() {}
	
	public static Sender getInstance() {
		
		if (instance == null) {
			instance = new Sender();
		}
		
		return instance;
	}	
	
	public void send(Message message, Address[] addresses) throws MessagingException {
		
		logger.debug("Enviando a mensagem...");
		
		try {
			transport = session.getTransport(Configurator.getProperty("mail.transport.protocol"));
			transport.connect();			
			transport.sendMessage(message, addresses);
			transport.close();
		} catch (MessagingException e) {
			logger.error("Erro durante o envio da mensagem.", e);
			throw new MessagingException();
		}
		

	}

	public Session getSession() {
		Properties properties = Configurator.getProperties();

		logger.debug("Criando a conexão como servidor smtp.");
		
		session = Session.getDefaultInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Configurator.getProperty("mail.smtp.user"), Configurator.getProperty("mail.smtp.password"));
			}
		});
		
		return session;
	}
}
