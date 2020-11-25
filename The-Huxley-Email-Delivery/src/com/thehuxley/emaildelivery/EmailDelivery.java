package com.thehuxley.emaildelivery;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thehuxley.emaildelivery.data.Configurator;
import com.thehuxley.emaildelivery.data.DataFacade;
import com.thehuxley.emaildelivery.data.model.Email;
import com.thehuxley.emaildelivery.email.EmailFacade;
import com.thehuxley.emaildelivery.exception.HuxleyMailException;

public class EmailDelivery {

	private static Logger logger = LoggerFactory.getLogger(EmailDelivery.class);

	public static void main(String[] args) {
		
		EmailFacade emailFacade = EmailFacade.getInstance();
		boolean running = true;

		System.out.print("Huxley Email Delivery 1.0.1");
		logger.info("Huxley Email Delivery 1.0.1");
		
		while (running) {
			
			logger.debug("Checando novos emails...");
			Email email = null;
			try {				
				ArrayList<Email> emails = DataFacade.getEmailToSend();
				for (Email mail : emails) {
					email = mail;
					emailFacade.send(email.getMessage(), "", email.getEmail());
					DataFacade.updateEmail(email.getId(), "SENT");
					Thread.sleep(Long.parseLong(Configurator.getProperty("mail.interval")));
				}
				Thread.sleep(Long.parseLong(Configurator.getProperty("mail.delay")));
			} catch (InterruptedException e) {
				logger.error("Não foi possível botar a thread para ninar.", e);
			} catch (HuxleyMailException e) {
				logger.error("Email não enviado, mantendo o status TOSEND.", e);
				emailFacade.reportError("Erro durante o envio do email id " + email.getId() + " - " + email.getEmail() + ".", e);
				DataFacade.updateEmail(email.getId(), "ERROR");
			}
		}

	}
}
