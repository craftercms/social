package org.craftercms.social.services;

import java.util.Map;

import org.craftercms.social.exceptions.MailException;

/**
 * Implements the mailing service, using the information provides in the parameters values
 * @author Alvaro Gonzalez
 *
 */
public interface MailService {

	/**
	 * Send a mail using the configured email settings with the arguments passed
	 * The template will be used for the mail content if it exists, otherwise the text will be
	 * @param subject The subject of the email
	 * 
	 * @param text The email body
	 * 
	 * @param template The email template body
	 * 
	 * @param templateArgs values used to exchange the template values 
	 *  
	 * @param toAddress target email account
	 * 
	 * @param fromAddress a valid email account used to send emails
	 * 
	 * @throws MessagingException If an error occurred when the email is sent
	 */
    void sendMailTLS(String subject, String text, String template, Map<String, Object> templateArgs,
                     String toAddress, String fromAddress) throws MailException;

}
