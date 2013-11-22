package org.craftercms.social.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Subscriptions implements Serializable {
	
	private String frequency;
	private String action;
	private String format;
    private boolean autoWatch;
	private List<String> targets;
	
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
