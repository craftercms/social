package org.craftercms.social.notification.harvester.impl;

import org.bson.types.ObjectId;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.social.domain.Notification;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.MailException;
import org.craftercms.social.repositories.UGCRepository;
import org.craftercms.social.util.support.CrafterProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Order;

import java.util.*;

public class DigestEmailNotifierHarvesterServiceImpl extends EmailNotifierHarvesterServiceImpl {

    private static final int DEFAULT_MAX_NOTIF_PER_EMAIL = 20;

    private static final String FIELD_SUBSCRIBER_ID = "subscriberId";
    private static final String FIELD_EVENT_AUDIT_DATE = "event.auditDate";

    //Email arguments that could be used by the freemarker template
    private static final String NOTIFICATIONS = "notifications";
    private static final String NOTIFICATION = "notification";
    private static final String PAGE_SIZE = "pageSize";
    private static final String CURRENT_PAGE = "currentPage";
    private static final String SUBSCRIBER_ATTRIBUTES = "subscriberAttributes";

    private static final List<String> DEFAULT_PROFILE_ATTRIBUTES = Arrays.asList();
    private static final String EVENT_PROFILE_ATTRIBUTES = "eventProfileAttributes";
    private static final String ANONYMOUS = "anonymous";

    private List<Notification> currentProfileNotifications;
    private ObjectId currentProfileId;
    private String currentProfileEmail;
    private int currentPage;

    private int maxNotificationsPerEmail;
    private List<String> profileAttributes;

    private CrafterProfileService crafterProfileService;
    private UGCRepository ugcRepository;

    public DigestEmailNotifierHarvesterServiceImpl() {
        maxNotificationsPerEmail = DEFAULT_MAX_NOTIF_PER_EMAIL;
        profileAttributes = DEFAULT_PROFILE_ATTRIBUTES;
        LinkedHashMap<String, Order> sorting = new LinkedHashMap<String, Order>();
        sorting.put(FIELD_SUBSCRIBER_ID, Order.ASCENDING);
        sorting.put(FIELD_EVENT_AUDIT_DATE, Order.DESCENDING);
        setNotificationQuerySort(sorting);
    }

    @Override
    public void doHarvest(Map<String, ?> params) {
        start();
        super.doHarvest(params);
        end();
    }

    @Override
    protected void emailNotifications(List<Notification> notificationList) {
        for (Notification notification : notificationList) {
            ObjectId subscriberId = notification.getSubscriberId();
            if (subscriberId != null) {
                if (needToFlush(subscriberId)) {
                    processNotifications(currentProfileNotifications);
                    currentProfileNotifications.clear();
                    if (profileChanged(subscriberId)) {
                        currentPage = 0;
                    }
                }
                currentProfileId = notification.getSubscriberId();
                currentProfileEmail = notification.getSubscriberEmail();
                currentProfileNotifications.add(notification);
            }
        }
    }

    private boolean profileChanged(ObjectId subscriberId) {
        return !subscriberId.equals(currentProfileId);
    }

    private boolean maxNotificationsReached() {
        return currentProfileNotifications.size() == maxNotificationsPerEmail;
    }

    private boolean needToFlush(ObjectId subscriberId) {
        return maxNotificationsReached()
                || (profileChanged(subscriberId) && currentProfileNotifications.size() > 0);
    }

    private void updateNotifications(List<Notification> notificationList, Notification.TransmittedStatus status) {
        for (Notification notification : notificationList) {
            updateNotification(notification, status);
        }
    }

    private void processNotifications(List<Notification> notificationList) {
        Notification.TransmittedStatus transmittedStatus = Notification.TransmittedStatus.PROCESSING;
        try {
            updateNotifications(notificationList, transmittedStatus);
            sendEmail(notificationList);
            transmittedStatus = Notification.TransmittedStatus.PROCESSED;
        } catch (Exception e) {
            log.error("Digest Email Notifier Harvester error:" + e.getMessage());
            transmittedStatus = Notification.TransmittedStatus.PENDING;
        } finally {
            updateNotifications(notificationList, transmittedStatus);
            currentPage++;
        }
    }

    private void sendEmail(List<Notification> notificationList) throws MailException {
        Map<String, Object> templateArgs = new HashMap<String, Object>();
        templateArgs.putAll(getGlobalTemplateArguments(notificationList.get(0)));

        List<Map<String, Object>> notificationArgs = new ArrayList<Map<String, Object>>();
        for (Notification notification : notificationList) {
            notificationArgs.add(getNotificationTemplateArguments(notification));
        }

        templateArgs.put(NOTIFICATIONS, notificationArgs);
        sendEmail(templateArgs, currentProfileEmail);
    }

    @Override
    protected Map<String, Object> getGlobalTemplateArguments(Notification notification) {
        Map<String, Object> arguments = super.getGlobalTemplateArguments(notification);

        Map<String, Object> subscriberAttributes = retrieveProfileAttributes(notification.getSubscriberId().toString());

        arguments.put(SUBSCRIBER_ATTRIBUTES, subscriberAttributes);

        arguments.put(PAGE_SIZE, maxNotificationsPerEmail);
        arguments.put(CURRENT_PAGE, currentPage);
        return arguments;
    }

    private Map<String, Object> retrieveProfileAttributes(String profileId) {
        Profile profileWithAttributes = crafterProfileService.getProfile(profileId, getProfileAttributes());
        Map<String, Object> attributes = profileWithAttributes.getAttributes();
        if (attributes == null) {
            return new HashMap<String, Object>();
        }
        return attributes;
    }

    @Override
    protected Map<String, Object> getNotificationTemplateArguments(Notification notification) {
        Map<String, Object> templateArgs = super.getNotificationTemplateArguments(notification);
        templateArgs.put(NOTIFICATION, notification);

        String ugcId = notification.getEvent().getUgcId().toString();
        boolean anonymous = false;
        UGC ugc = ugcRepository.findOne(new ObjectId(ugcId));
        if (ugc == null) {
            log.warn("Could not find ugc with id'" + ugcId + "'");
        } else {
            anonymous = ugc.isAnonymousFlag();
        }

        templateArgs.put(ANONYMOUS, anonymous);
        org.craftercms.social.domain.Profile notificationProfile = notification.getEvent().getProfile();
        Map<String, Object> eventProfileAttributes = retrieveProfileAttributes(notificationProfile.getId().toString());
        templateArgs.put(EVENT_PROFILE_ATTRIBUTES, eventProfileAttributes);


        return templateArgs;
    }

    private void end() {
        if (currentProfileNotifications.size() > 0) {
            processNotifications(currentProfileNotifications);
        }
    }

    private void start() {
        currentProfileEmail = null;
        currentProfileNotifications = new ArrayList<Notification>();
        currentProfileId = null;
        currentPage = 0;
    }

    public int getMaxNotificationsPerEmail() {
        return maxNotificationsPerEmail;
    }

    public void setMaxNotificationsPerEmail(int maxNotificationsPerEmail) {
        if (maxNotificationsPerEmail <= 0) {
            this.maxNotificationsPerEmail = DEFAULT_MAX_NOTIF_PER_EMAIL;
        } else {
            this.maxNotificationsPerEmail = maxNotificationsPerEmail;
        }
    }

    @Autowired
    public void setCrafterProfileService(CrafterProfileService crafterProfileService) {
        this.crafterProfileService = crafterProfileService;
    }

    public CrafterProfileService getCrafterProfileService() {
        return crafterProfileService;
    }

    public UGCRepository getUgcRepository() {
        return ugcRepository;
    }

    @Autowired
    public void setUgcRepository(UGCRepository ugcRepository) {
        this.ugcRepository = ugcRepository;
    }

    public List<String> getProfileAttributes() {
        return profileAttributes;
    }

    public void setProfileAttributes(List<String> profileAttributes) {
        if (profileAttributes != null) {
            this.profileAttributes = profileAttributes;
        }
    }
}
