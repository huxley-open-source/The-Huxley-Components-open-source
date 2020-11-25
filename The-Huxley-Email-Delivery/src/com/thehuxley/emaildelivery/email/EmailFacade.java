package com.thehuxley.emaildelivery.email;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thehuxley.emaildelivery.data.Configurator;
import com.thehuxley.emaildelivery.exception.HuxleyMailException;

public class EmailFacade {
	
	private Logger logger = LoggerFactory.getLogger(EmailFacade.class);

	private static EmailFacade instance;
	private Sender sender;
	
	private EmailFacade() {
		sender = Sender.getInstance();		
	}
	
	public static EmailFacade getInstance() {
		
		if (instance == null) {
			instance = new EmailFacade();			
		}
		
		return instance;
	}
	
	public void send(String message, String subject, String address) throws HuxleyMailException {
		String[] addresses = {address};
		send(message, subject, addresses);
	}
	
	public void send(String message, String subject, String[] addresses) throws HuxleyMailException {
		
		Message mimeMessage = new MimeMessage(sender.getSession());
		Address[] addressess = new Address[addresses.length];
		
		try {
			
			for (int i = 0; i < addresses.length; i++) {
				logger.info("Enviando o email '" + Configurator.getProperty("mail.subject.prefix") + " " + subject + "' para '" + addresses[i] + "'.");
				addressess[i] = new InternetAddress(addresses[i]);
			}
			
			mimeMessage.setContent(message, "text/html");
			mimeMessage.setSubject(Configurator.getProperty("mail.subject.prefix") + " " + subject);
			
			sender.send(mimeMessage, addressess);
		} catch (MessagingException e) {
			logger.error("Erro para montar a mensagem.", e);
			throw new HuxleyMailException();
		}
				
		
	}

	public void reportError(String errorMessage, HuxleyMailException e) {
		try {
			send(Configurator.getProperty("mail.report.message") + " \n" + errorMessage + " \n" + e, Configurator.getProperty("mail.report.subject"), Configurator.getProperty("mail.report.address"));
		} catch (HuxleyMailException huxleyMailException) {
			logger.error("Erro durante o envio do report para o administrador.", huxleyMailException);
		}
		
	}
}
