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
    var defaultContext = db.socialContext.findOne();

    db.securityActions.insert([
        {
            actionName:"ugc.update",
            contextId:defaultContext._id,
            roles:["SOCIAL_ADMIN","SOCIAL_MODERATOR","OWNER","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"ugc.moderate",
            contextId:defaultContext._id,
            roles:["SOCIAL_ADMIN","SOCIAL_MODERATOR","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"ugc.unflag",
            contextId:defaultContext._id,
            roles:["SOCIAL_ADMIN","SOCIAL_MODERATOR","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"ugc.flag",
            contextId:defaultContext._id,
            roles:["SOCIAL_ADMIN","SOCIAL_MODERATOR","SOCIAL_USER","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"ugc.create",
            contextId:defaultContext._id,
            roles:["SOCIAL_ADMIN","SOCIAL_MODERATOR","SOCIAL_USER","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"ugc.delete",
            contextId:defaultContext._id,
            roles:["SOCIAL_ADMIN","SOCIAL_MODERATOR","OWNER","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"ugc.read",
            contextId:defaultContext._id,
            roles:["ANONYMOUS","SOCIAL_ADMIN","SOCIAL_MODERATOR","SOCIAL_USER","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"system.securityActions.read",
            contextId:defaultContext._id,
            roles:["SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"system.securityActions.update",
            contextId:defaultContext._id,
            roles:["SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"system.socialctx.all",
            contextId:defaultContext._id,
            roles:["SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"system.socialctx.create",
            contextId:defaultContext._id,
            roles:["SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"system.socialctx.addProfile",
            contextId:defaultContext._id,
            roles:["SOCIAL_SUPERADMIN","SOCIAL_ADMIN"]
        },
        {
            actionName:"system.socialctx.removeProfile",
            contextId:defaultContext._id,
            roles:["SOCIAL_SUPERADMIN","SOCIAL_ADMIN"]
        }
    ]);
    db.securityActions.insert([
        {
            actionName:"ugc.update",
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            roles:["SOCIAL_ADMIN","SOCIAL_MODERATOR","OWNER","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"ugc.moderate",
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            roles:["SOCIAL_ADMIN","SOCIAL_MODERATOR","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"ugc.unflag",
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            roles:["SOCIAL_ADMIN","SOCIAL_MODERATOR","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"ugc.flag",
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            roles:["SOCIAL_ADMIN","SOCIAL_MODERATOR","SOCIAL_USER","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"ugc.create",
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            roles:["SOCIAL_ADMIN","SOCIAL_MODERATOR","SOCIAL_USER","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"ugc.delete",
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            roles:["SOCIAL_ADMIN","SOCIAL_MODERATOR","OWNER","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"ugc.read",
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            roles:["ANONYMOUS","SOCIAL_ADMIN","SOCIAL_MODERATOR","SOCIAL_USER","SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"system.securityActions.read",
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            roles:["SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"system.securityActions.update",
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            roles:["SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"system.socialctx.all",
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            roles:["SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"system.socialctx.create",
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            roles:["SOCIAL_SUPERADMIN"]
        },
        {
            actionName:"system.socialctx.addProfile",
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            roles:["SOCIAL_SUPERADMIN","SOCIAL_ADMIN"]
        },
        {
            actionName:"system.socialctx.removeProfile",
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            roles:["SOCIAL_SUPERADMIN","SOCIAL_ADMIN"]
        }
    ]);
}