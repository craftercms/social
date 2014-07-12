/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 /**Create Default Social Tenant.**/
if(db.socialContext.count() ==0){
    db.socialContext.insert(
        {
            _id:"f5b143c2-f1c0-4a10-b56e-f485f00d3fe9",
            contextName:"Default"
        }
    );
}
/** Create Default Permissions for Default Tenant**/
if(db.securityActions.count()==0){
    var defaultTenant = db.socialContext.findOne();

    db.securityActions.insert([
        {
            actionName:"ugc.update",
            tenantId:defaultTenant._id,
            roles:["SOCIAL_SUPER_ADMIN","SOCIAL_ADMIN","SOCIAL_MODERATOR","OWNER"]
        },
        {
            actionName:"ugc.moderate",
            tenantId:defaultTenant._id,
            roles:["SOCIAL_SUPER_ADMIN","SOCIAL_ADMIN","SOCIAL_MODERATOR"]
        },
        {
            actionName:"ugc.unflag",
            tenantId:defaultTenant._id,
            roles:["SOCIAL_SUPER_ADMIN","SOCIAL_ADMIN","SOCIAL_MODERATOR"]
        },
        {
            actionName:"ugc.flag",
            tenantId:defaultTenant._id,
            roles:["SOCIAL_SUPER_ADMIN","SOCIAL_ADMIN","SOCIAL_MODERATOR","SOCIAL_USER"]
        },
        {
            actionName:"ugc.create",
            tenantId:defaultTenant._id,
            roles:["SOCIAL_SUPER_ADMIN","SOCIAL_ADMIN","SOCIAL_MODERATOR","SOCIAL_USER"]
        },
        {
            actionName:"ugc.delete",
            tenantId:defaultTenant._id,
            roles:["SOCIAL_SUPER_ADMIN","SOCIAL_ADMIN","SOCIAL_MODERATOR","OWNER"]
        },
        {
            actionName:"ugc.read",
            tenantId:defaultTenant._id,
            roles:["ANONYMOUS","SOCIAL_SUPER_ADMIN","SOCIAL_ADMIN","SOCIAL_MODERATOR","SOCIAL_USER"]
        },
        {
            actionName:"system.securityActions.read",
            tenantId:defaultTenant._id,
            roles:["SOCIAL_SUPER_ADMIN","SOCIAL_ADMIN,"]
        },
        {
            actionName:"system.securityActions.update",
            tenantId:defaultTenant._id,
            roles:["SOCIAL_SUPER_ADMIN","SOCIAL_ADMIN"]
        },
        {
            actionName:"system.socialctx.all",
            tenantId:defaultTenant._id,
            roles:["SOCIAL_SUPER_ADMIN"]
        },
        {
            actionName:"system.socialctx.create",
            tenantId:defaultTenant._id,
            roles:["SOCIAL_SUPER_ADMIN"]
        },
        {
            actionName:"system.socialctx.addProfile",
            tenantId:defaultTenant._id,
            roles:["SOCIAL_SUPER_ADMIN","SOCIAL_ADMIN"]
        },
        {
            actionName:"system.socialctx.removeProfile",
            tenantId:defaultTenant._id,
            roles:["SOCIAL_SUPER_ADMIN","SOCIAL_ADMIN"]
        }
    ])
}