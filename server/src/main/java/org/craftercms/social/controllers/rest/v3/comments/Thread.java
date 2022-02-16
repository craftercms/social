/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.controllers.rest.v3.comments;

import java.util.ArrayList;
import java.util.List;

import org.craftercms.social.domain.UGC;

/**
 *
 */

public class Thread {

    private long total;
    private int pageSize;
    private int pageNumber;
    private boolean watched;
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

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(final boolean watched) {
        this.watched = watched;
    }
}
