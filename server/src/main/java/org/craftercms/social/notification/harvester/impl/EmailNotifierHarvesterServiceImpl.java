package org.craftercms.social.notification.harvester.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.craftercms.social.domain.Notification;
import org.craftercms.social.domain.Notification.TransmittedStatus;
import org.craftercms.social.domain.UGCAudit.AuditAction;
import org.craftercms.social.exceptions.MailException;
import org.craftercms.social.notification.harvester.BaseHarvesterService;
import org.craftercms.social.repositories.NotificationRepository;
import org.craftercms.social.services.MailService;
import org.craftercms.social.util.PageManagement;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailNotifierHarvesterServiceImpl extends BaseHarvesterService {
	
	private static final String DEFAULT_FREQUENCY = "instant";
	private static final String DEFAULT_ACTION = "email";
	private static final String DEFAULT_SIGNATURE_EMAIL = "Crafter Team";
	
	//Email arguments that could be used by the freemarker template
	private static final String SIGNATURE_EMAIL = "signatureEmail";
	private static final String SUBSCRIBER_USER_NAME = "subscriberUsername";
	private static final String SUBSCRIBER_EMAIL = "subscriberEmail";
	private static final String EVENT_ACTION = "eventAction";
	private static final String EVENT_TARGET = "eventTarget";
	private static final String EVENT_USERNAME = "eventUsername";
	private static final String EVENT_USER_EMAIL = "eventUserEmail";
	private static final String EVENT_DATE = "eventDate";
	
	
	private String frequency;
	
	private String emailTemplateFtl;
	
	private String emailSubject;
	
	private TransmittedStatus transmitedStatus;
	
	private Map<String, String> actionToDisplay;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private MailService mailService;

	private String fromAddress;

	private String emailBody;
	
	private String signatureEmail;
	
	private PageManagement pageManagement;
	
	private int pageSize;
	
	public EmailNotifierHarvesterServiceImpl() {
		frequency = DEFAULT_FREQUENCY;
		this.pageSize = DEFAULT_PAGE_SIZE;
		transmitedStatus = TransmittedStatus.PENDING;
		actionToDisplay = initActionsToDisplay();
		signatureEmail = DEFAULT_SIGNATURE_EMAIL;
		pageManagement = new PageManagement();
	}
	
	private Map<String, String> initActionsToDisplay() {
		Map<String, String> actionToDisplay = new HashMap<String, String>();
		actionToDisplay.put(AuditAction.LIKE.toString(), "liked");
		actionToDisplay.put(AuditAction.DISLIKE.toString(), "disliked");
		actionToDisplay.put(AuditAction.FLAG.toString(), "flaged");
		actionToDisplay.put(AuditAction.CREATE.toString(), "created");
		actionToDisplay.put(AuditAction.UPDATE.toString(), "updated");
		actionToDisplay.put(AuditAction.DELETE.toString(), "deleted");
		actionToDisplay.put(AuditAction.MODERATE.toString(), "moderated");
		return actionToDisplay;
	}

	@Override
	public void doHarvestInternal(Map<String, ?> harvesterProperties) {
		List<Notification> notificationList;
		
		boolean isDone = initPageManagement();
		while(!isDone) {
			notificationList = notificationRepository.findNotificationByFrequencyAndTransmitedStatus(frequency, transmitedStatus.toString(), DEFAULT_ACTION, pageManagement.getStart(), pageManagement.getEnd());
			
			if (notificationList != null && notificationList.size() > 0) {
				if (log.isDebugEnabled()) {
					log.debug("Email Notifier Harvester found notifications: " + notificationList.size());
				}
				emailNotifications(notificationList);
				
				isDone = updatePageManagement();
			} else {
				isDone = true;
			}
		}
	}
	
	/**
     * Sets the email template that will be used to email
     * 
     * @param resetEmailTemplate
     */
    public void setEmailTemplateFtl(String emailTemplateFtl) {
    	this.emailTemplateFtl = emailTemplateFtl;
    }

    /**
     * Sets the email subject to be sent whenever an email is sent.
     * 
     * @param emailSubject Subject of the email
     */
    public void setEmailSubject(String emailSubject) {
    	this.emailSubject = emailSubject;
    }
    
    /**
     * Sets the email body to be sent whenever a template is not specified
     * 
     * @param emailText Body text of the email
     */
    public void setEmailText(String emailText) {
    	this.emailBody = emailText;
    }

    /**
     * Sets the mail account will be used to email
     * 
     * @param mailFrom Email account used to send emails
     */
    public void setMailFrom(String mailFrom) {
    	if (mailFrom != null && mailFrom.equals("")) {
    		this.fromAddress = mailFrom;
    	}
    }
	

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	
	public Map<String, String> getActionToDisplay() {
		return actionToDisplay;
	}

	public void setActionToDisplay(Map<String, String> actionToDisplay) {
		this.actionToDisplay = actionToDisplay;
	}

	public String getSignatureEmail() {
		return signatureEmail;
	}

	public void setSignatureEmail(String signatureEmail) {
		this.signatureEmail = signatureEmail;
	}
	
	private boolean initPageManagement() {
		boolean isDone = false;
		
		pageManagement.setStart(0);
		pageManagement.setPageSize(this.pageSize);
		long total = this.notificationRepository.countPendingsByFrequency(this.frequency);
		pageManagement.setTotal(total);
		if (total <= 0) {
			isDone = true;
		}
		
		return isDone;
	}
	
	private boolean updatePageManagement() {
		boolean isDone = false;
		if (pageManagement.isLastPage()) {
			isDone = true;
		} else {
			
			pageManagement.next();
		}
		return isDone;
	}

	private void emailNotifications(List<Notification> notificationList) {
		TransmittedStatus trasmittedStatus;
		for (Notification notification: notificationList) {
			trasmittedStatus = TransmittedStatus.PROCESSING;
			try {
				updateNotification(notification, trasmittedStatus);
				emailNotification(notification);
				trasmittedStatus = TransmittedStatus.PROCESSED;
			} catch(MailException e) {
				log.error("Email Notifier Harvester error:" + e.getMessage());
				trasmittedStatus = TransmittedStatus.PENDING;
			} finally {
				if (log.isDebugEnabled()) {
					log.debug("Email Notifier Harvester updating notification" + notification.getId()  +" status: " + trasmittedStatus);
				}
				updateNotification(notification, trasmittedStatus);
			}
		}
	}
	
	private void updateNotification(Notification notification, TransmittedStatus transmitedStatus) {
		notification.setTransmitedStatus(transmitedStatus);
		this.notificationRepository.save(notification);
	}

	private void emailNotification(Notification notification) throws MailException {
		
		Map<String, Object> templateArgs = getTemplateArguments(notification);
		mailService.sendMailTLS(emailSubject, emailBody, this.emailTemplateFtl, templateArgs, notification.getSubscriberEmail(),
	            fromAddress);
	}
	
	private Map<String, Object> getTemplateArguments(Notification notification) {
		Map<String, Object> templateArgs = new HashMap<String, Object>();
//		templateArgs.put(EVENT_USER_EMAIL,notification.getEvent().getProfile().getUserName());
//		templateArgs.put(EVENT_ACTION,this.actionToDisplay.get(notification.getEvent().getAction().toString()));
//		templateArgs.put(EVENT_TARGET,notification.getEvent().getTarget());
		
		templateArgs.put(SIGNATURE_EMAIL,this.signatureEmail);
		templateArgs.put(SUBSCRIBER_USER_NAME,notification.getSubscriberUsername());
		templateArgs.put(SUBSCRIBER_EMAIL,notification.getSubscriberEmail());
		
		
		templateArgs.put(EVENT_ACTION,notification.getEvent().getAction());
		templateArgs.put(EVENT_TARGET,notification.getEvent().getTarget());
		templateArgs.put(EVENT_USERNAME,notification.getEvent().getProfile().getUserName());
		templateArgs.put(EVENT_USER_EMAIL,notification.getEvent().getProfile().getEmail());
		templateArgs.put(EVENT_DATE,notification.getEvent().getAuditDate());
		
		
		
//		private static final String SIGNATURE_EMAIL = "signatureEmail";
//		private static final String SUBSCRIBER_USER_NAME = "subscriberUsername";
//		private static final String SUBSCRIBER_EMAIL = "subscriberEmail";
//		private static final String EVENT_ACTION = "eventAction";
//		private static final String EVENT_TARGET = "eventTarget";
//		private static final String EVENT_USERNAME = "eventUsername";
//		private static final String EVENT_USER_EMAIL = "eventUserEmail";
//		private static final String EVENT_DATE = "eventDate";
		return templateArgs;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
