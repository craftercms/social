package org.craftercms.social.notification.harvester.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.craftercms.social.domain.Notification;
import org.craftercms.social.domain.Notification.TransmittedStatus;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.MailException;
import org.craftercms.social.notification.harvester.BaseHarvesterService;
import org.craftercms.social.repositories.NotificationRepository;
import org.craftercms.social.repositories.UGCRepository;
import org.craftercms.social.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Order;

public class EmailNotifierHarvesterServiceImpl extends BaseHarvesterService {
	private static final HashMap<String, Order> DEFAULT_SORT = new HashMap<String, Order>(){
		{
			put("createdDate", Order.DESCENDING);
		}
	};

	private static final String DEFAULT_FREQUENCY = "instant";
	private static final String EMAIL_ACTION = "email";
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

	private Map<String,Order> notificationQuerySort;

	private String frequency;

	private String emailTemplateFtl;

	private String emailSubject;

	private TransmittedStatus transmitedStatus;

	private Map<String, String> actionToDisplay;

	@Autowired
	private NotificationRepository notificationRepository;

    @Autowired
    private UGCRepository ugcRepository;

	@Autowired
	private MailService mailService;

	private String fromAddress;

	private String emailBody;

	private String signatureEmail;

	//private PageManagement pageManagement;

	private int pageSize;

	private Map<String, String> emailParameters;

	public EmailNotifierHarvesterServiceImpl() {
		frequency = DEFAULT_FREQUENCY;
		pageSize = DEFAULT_PAGE_SIZE;
		transmitedStatus = TransmittedStatus.PENDING;

		signatureEmail = DEFAULT_SIGNATURE_EMAIL;

		emailParameters = new HashMap<String, String>();
        actionFilters = new ArrayList<String>();
        notificationQuerySort = DEFAULT_SORT;
	}

	@Override
	public void doHarvestInternal(Map<String, ?> harvesterProperties) {
		List<Notification> notificationList = notificationRepository.findNotificationByFrequencyAndTransmitedStatus(frequency,
                transmitedStatus.toString(), EMAIL_ACTION, getActionFiltersAsStringArray(), notificationQuerySort);

		if (notificationList != null && notificationList.size() > 0) {
			if (log.isDebugEnabled()) {
				log.debug("Email notifier harvester found notifications: " + notificationList.size());
			}
			emailNotifications(filterNotifications(notificationList));

		}
	}

    private List<Notification> filterNotifications(List<Notification> notificationList) {
        List<Notification> result = new ArrayList<Notification>();
        for (Notification notification : notificationList) {
            Object ugcIdObj = notification.getEvent().getUgcId();
            if (ugcIdObj != null) {
                String ugcId = ugcIdObj.toString();
                UGC ugc = ugcRepository.findOne(new ObjectId(ugcId));
                UGC.ModerationStatus status = ugc.getModerationStatus();
                if (status != UGC.ModerationStatus.SPAM && status != UGC.ModerationStatus.TRASH) {
                    result.add(notification);
                }
            }
        }
        return result;
    }

	/**
     * Sets the email template that will be used to email
     *
     * @param //resetEmailTemplate
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

	protected void emailNotifications(List<Notification> notificationList) {
		TransmittedStatus trasmittedStatus;
		for (Notification notification: notificationList) {
			trasmittedStatus = TransmittedStatus.PROCESSING;
			try {
				updateNotification(notification, trasmittedStatus);
				emailNotification(notification);
				trasmittedStatus = TransmittedStatus.PROCESSED;
			} catch(MailException e) {
				log.error("Email notifier harvester error:" + e.getMessage());
				trasmittedStatus = TransmittedStatus.PENDING;
			} finally {
				if (log.isDebugEnabled()) {
					log.debug("Email notifier harvester updating notification " + notification.getId()  +" status: " + trasmittedStatus);
				}
				updateNotification(notification, trasmittedStatus);
			}
		}
	}

	protected void updateNotification(Notification notification, TransmittedStatus transmitedStatus) {
		notification.setTransmitedStatus(transmitedStatus);
		this.notificationRepository.save(notification);
	}

	private void emailNotification(Notification notification) throws MailException {
        Map<String, Object> templateArgs = getNotificationTemplateArguments(notification);
        templateArgs.putAll(getGlobalTemplateArguments(notification));
        sendEmail(templateArgs, notification.getSubscriberEmail());
    }

    protected void sendEmail(Map<String, Object> templateArgs, String subscriberEmail) throws MailException {
        mailService.sendMailTLS(emailSubject, emailBody, this.emailTemplateFtl, templateArgs, subscriberEmail,
                fromAddress);
    }

    protected Map<String, Object> getGlobalTemplateArguments(Notification notification) {
        Map<String, Object> templateArgs = new HashMap<String, Object>();

        templateArgs.put(SIGNATURE_EMAIL, this.signatureEmail);
        templateArgs.put(SUBSCRIBER_USER_NAME, notification.getSubscriberUsername());
        templateArgs.put(SUBSCRIBER_EMAIL, notification.getSubscriberEmail());

        templateArgs.putAll(this.emailParameters);

        return templateArgs;
    }

    protected Map<String, Object> getNotificationTemplateArguments(Notification notification) {
        Map<String, Object> templateArgs = new HashMap<String, Object>();

        templateArgs.put(EVENT_ACTION, notification.getEvent().getAction());
        templateArgs.put(EVENT_TARGET, notification.getEvent().getTarget());
        templateArgs.put(EVENT_USERNAME, notification.getEvent().getProfile().getUserName());
        templateArgs.put(EVENT_USER_EMAIL, notification.getEvent().getProfile().getEmail());
        templateArgs.put(EVENT_DATE, notification.getEvent().getAuditDate());

        return templateArgs;
    }

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Map<String, String> getEmailParameters() {
		return emailParameters;
	}

	public void setEmailParameters(Map<String, String> emailParameters) {
		if (emailParameters!=null) {
			this.emailParameters = emailParameters;
		}
	}

	public Map<String,Order> getNotificationQuerySort() {
		return notificationQuerySort;
	}

	public void setNotificationQuerySort(Map<String,Order> notificationQuerySort) {
		this.notificationQuerySort = notificationQuerySort;
	}

}
