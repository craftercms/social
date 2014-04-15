package org.craftercms.social.exceptions;

public class MailException extends SocialException {

    private static final long serialVersionUID = 7551235092424723532L;

    public MailException(String msg, Throwable e) {
        super(msg, e);
    }

    public MailException(String msg) {
        super(msg);
    }
}