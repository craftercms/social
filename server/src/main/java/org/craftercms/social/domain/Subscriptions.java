package org.craftercms.social.domain;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;


public class Subscriptions implements Serializable {

    public static final String ATTRIBUTE_FREQUENCY =    "subscriptions_frequency";
    public static final String ATTRIBUTE_ACTION =       "subscriptions_action";
    public static final String ATTRIBUTE_FORMAT =       "subscriptions_format";
    public static final String ATTRIBUTE_AUTO_WATCH =   "subscriptions_autoWatch";
    public static final String ATTRIBUTE_TARGETS =      "subscriptions_targets";
	
	private String frequency;
	private String action;
	private String format;
    private boolean autoWatch;
	private List<String> targets;

    public static Subscriptions getFromAttributes(Map<String, Object> attributes) {
        if (MapUtils.isNotEmpty(attributes)) {
            Subscriptions subscriptions = new Subscriptions();
            subscriptions.setFrequency((String) attributes.get(ATTRIBUTE_FREQUENCY));
            subscriptions.setAction((String) attributes.get(ATTRIBUTE_ACTION));
            subscriptions.setFormat((String) attributes.get(ATTRIBUTE_FORMAT));
            subscriptions.setAutoWatch(Boolean.parseBoolean((String) attributes.get(ATTRIBUTE_AUTO_WATCH)));
            subscriptions.setTargets((List<String>) attributes.get(ATTRIBUTE_TARGETS));

            return subscriptions;
        } else {
            return null;
        }
    }

    public static Map<String, Object> setInAttributes(Subscriptions subscriptions, Map<String, Object> attributes) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }

        attributes.put(ATTRIBUTE_FREQUENCY, subscriptions.getFrequency());
        attributes.put(ATTRIBUTE_ACTION, subscriptions.getAction());
        attributes.put(ATTRIBUTE_FORMAT, subscriptions.getFormat());
        attributes.put(ATTRIBUTE_AUTO_WATCH, Boolean.toString(subscriptions.isAutoWatch()));
        attributes.put(ATTRIBUTE_TARGETS, subscriptions.getTargets());

        return attributes;
    }
	
	public Subscriptions() {
		this.targets = new ArrayList<String>();
	}

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isAutoWatch() {
        return autoWatch;
    }

    public void setAutoWatch(boolean autoWatch) {
        this.autoWatch = autoWatch;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
    }

}
