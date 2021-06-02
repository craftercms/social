/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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

package org.craftercms.social.security;

/**
 * Created by cortiz on 6/26/14.
 */
public final class SecurityActionNames {

    public static final String UGC_UPDATE = "ugc.update";
    public static final String UGC_MODERATE = "ugc.moderate";
    public static final String UGC_UNFLAG = "ugc.unflag";
    public static final String UGC_VOTING = "ugc.voting";
    public static final String UGC_FLAG = "ugc.flag";
    public static final String UGC_CREATE = "ugc.create";
    public static final String UGC_DELETE = "ugc.delete";
    public static final String UGC_READ = "ugc.read";
    public static final String SYSTEM_GET_ALL_CONTEXTS = "system.socialctx.all";
    public static final String SYSTEM_CREATE_CONTEXT = "system.socialctx.create";
    public static final String SYSTEM_ADD_PROFILE_CONTEXT = "system.socialctx.addProfile";
    public static final String SYSTEM_REMOVE_PROFILE_CONTEXT = "system.socialctx.removeProfile";
    public static final String TEMPLATE_CONTEXT_ACTIONS = "TEMPLATE_CONTEXT_ACTIONS";
    public static final String ROLE_SOCIAL_SUPERADMIN="SOCIAL_SUPERADMIN";
    public static final String ROLE_OWNER="OWNER";
    public static final String ROLE_SOCIAL_ADMIN = "SOCIAL_ADMIN";
    public static final String CHANGE_NOTIFICATION_TEMPLATE = "system.notification.changeTemplate";
    public static final String SYSTEM_READ_ACTIONS="system.securityActions.read";
    public static final String SYSTEM_UPDATE_ACTIONS="system.securityActions.update";
    private SecurityActionNames(){}


}