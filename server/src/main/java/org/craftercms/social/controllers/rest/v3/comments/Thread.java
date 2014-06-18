package org.craftercms.social.controllers.rest.v3.comments;

import java.util.ArrayList;
import java.util.List;

import org.craftercms.social.domain.UGC;

/**
 * Created by cortiz on 6/13/14.
 */
public class Thread {

    private long total;
    private int pageSize;
    private int pageNumber;
    private List<? extends UGC> comments;

    public Thread() {
        comments = new ArrayList<>();
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(final long total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<? extends UGC> getComments() {
        return comments;
    }

    public void setComments(final List<? extends UGC> comments) {
        if (comments == null) {
            this.comments = new ArrayList<>(0);
        } else {
            this.comments = comments;
        }
    }
}
