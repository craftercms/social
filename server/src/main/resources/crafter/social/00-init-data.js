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
/**Create Default Social Context.**/
if (db.socialContext.count() == 0) {
    db.socialContext.insert(
        {
            _id: "f5b143c2-f1c0-4a10-b56e-f485f00d3fe9",
            contextName: "Default"
        }
    );
}
/** Create Default Permissions for Default Context**/
if (db.securityActions.count() == 0) {
    var defaultContext = db.socialContext.findOne();

    db.securityActions.insert([
        {
            actionName: "ugc.update",
            contextId: defaultContext._id,
            roles: ["SOCIAL_ADMIN", "SOCIAL_MODERATOR", "OWNER", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "ugc.moderate",
            contextId: defaultContext._id,
            roles: ["SOCIAL_ADMIN", "SOCIAL_MODERATOR", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "ugc.unflag",
            contextId: defaultContext._id,
            roles: ["SOCIAL_ADMIN", "SOCIAL_MODERATOR", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "ugc.flag",
            contextId: defaultContext._id,
            roles: ["SOCIAL_ADMIN", "SOCIAL_MODERATOR", "SOCIAL_USER", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "ugc.create",
            contextId: defaultContext._id,
            roles: ["SOCIAL_ADMIN", "SOCIAL_MODERATOR", "SOCIAL_USER", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "ugc.delete",
            contextId: defaultContext._id,
            roles: ["SOCIAL_ADMIN", "SOCIAL_MODERATOR", "OWNER", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "ugc.read",
            contextId: defaultContext._id,
            roles: ["ANONYMOUS", "SOCIAL_ADMIN", "SOCIAL_MODERATOR", "SOCIAL_USER", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "system.securityActions.read",
            contextId: defaultContext._id,
            roles: ["SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "system.securityActions.update",
            contextId: defaultContext._id,
            roles: ["SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "system.socialctx.all",
            contextId: defaultContext._id,
            roles: ["SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "system.socialctx.create",
            contextId: defaultContext._id,
            roles: ["SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "system.socialctx.addProfile",
            contextId: defaultContext._id,
            roles: ["SOCIAL_SUPERADMIN", "SOCIAL_ADMIN"]
        },
        {
            actionName: "system.socialctx.removeProfile",
            contextId: defaultContext._id,
            roles: ["SOCIAL_SUPERADMIN", "SOCIAL_ADMIN"]
        }
    ]);
    db.securityActions.insert([
        {
            actionName: "ugc.update",
            contextId: "TEMPLATE_CONTEXT_ACTIONS",
            roles: ["SOCIAL_ADMIN", "SOCIAL_MODERATOR", "OWNER", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "ugc.moderate",
            contextId: "TEMPLATE_CONTEXT_ACTIONS",
            roles: ["SOCIAL_ADMIN", "SOCIAL_MODERATOR", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "ugc.unflag",
            contextId: "TEMPLATE_CONTEXT_ACTIONS",
            roles: ["SOCIAL_ADMIN", "SOCIAL_MODERATOR", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "ugc.flag",
            contextId: "TEMPLATE_CONTEXT_ACTIONS",
            roles: ["SOCIAL_ADMIN", "SOCIAL_MODERATOR", "SOCIAL_USER", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "ugc.create",
            contextId: "TEMPLATE_CONTEXT_ACTIONS",
            roles: ["SOCIAL_ADMIN", "SOCIAL_MODERATOR", "SOCIAL_USER", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "ugc.delete",
            contextId: "TEMPLATE_CONTEXT_ACTIONS",
            roles: ["SOCIAL_ADMIN", "SOCIAL_MODERATOR", "OWNER", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "ugc.read",
            contextId: "TEMPLATE_CONTEXT_ACTIONS",
            roles: ["ANONYMOUS", "SOCIAL_ADMIN", "SOCIAL_MODERATOR", "SOCIAL_USER", "SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "system.securityActions.read",
            contextId: "TEMPLATE_CONTEXT_ACTIONS",
            roles: ["SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "system.securityActions.update",
            contextId: "TEMPLATE_CONTEXT_ACTIONS",
            roles: ["SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "system.socialctx.all",
            contextId: "TEMPLATE_CONTEXT_ACTIONS",
            roles: ["SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "system.socialctx.create",
            contextId: "TEMPLATE_CONTEXT_ACTIONS",
            roles: ["SOCIAL_SUPERADMIN"]
        },
        {
            actionName: "system.socialctx.addProfile",
            contextId: "TEMPLATE_CONTEXT_ACTIONS",
            roles: ["SOCIAL_SUPERADMIN", "SOCIAL_ADMIN"]
        },
        {
            actionName: "system.socialctx.removeProfile",
            contextId: "TEMPLATE_CONTEXT_ACTIONS",
            roles: ["SOCIAL_SUPERADMIN", "SOCIAL_ADMIN"]
        }
    ]);
}
/** Create Initial Context Email Config (also default ! )**/
if (db.preferences.count() == 0) {
    db.preferences.insert(
        [
            {
                _id: "f5b143c2-f1c0-4a10-b56e-f485f00d3fe9",
                email: {
                    host: "localhost",
                    encoding: "UTF-8",
                    port: 25,
                    auth: false,
                    username: "",
                    password: "",
                    tls: false,
                    replyTo: "social@local.loc",
                    from: "social@local.loc",
                    priority: 5,
                    subject: "Latest change on your subscribed threads for Social"
                },
                templates: [
                    {
                        type: "DAILY",
                        template:"Hi ${profile.username}, these are the changes that happened on your subscribed Threads<#list digest as change><h1> ${change[\"_id\"]} </h1><dl><#list change.ugcList as ugc><dt>Subject</dt><dd> ${ugc.subject!\"\"} </dd><dt>Body</dt>  <dd>${ugc.body!\"\"} </dd><dt>Changed by<dt><dd>${ugc.lastModifiedBy.username}<dd></#list></dl></#list>"
                    },
                    {
                        type: "WEEKLY",
                        template:"Hi ${profile.username}, these are the changes that happened on your subscribed Threads<#list digest as change><h1> ${change[\"_id\"]} </h1><dl><#list change.ugcList as ugc><dt>Subject</dt><dd> ${ugc.subject!\"\"} </dd><dt>Body</dt>  <dd>${ugc.body!\"\"} </dd><dt>Changed by<dt><dd>${ugc.lastModifiedBy.username}<dd></#list></dl></#list>"
                    },
                    {
                        type: "INSTANT",
                        template:"Hi ${profile.username}, these are the changes that happened on your subscribed Threads<#list digest as change><h1> ${change[\"_id\"]} </h1><dl><#list change.ugcList as ugc><dt>Subject</dt><dd> ${ugc.subject!\"\"} </dd><dt>Body</dt>  <dd>${ugc.body!\"\"} </dd><dt>Changed by<dt><dd>${ugc.lastModifiedBy.username}<dd></#list></dl></#list>"
                    }
                ]
            },
            {
                _id: "TEMPLATE_CONTEXT_ACTIONS",
                email: {
                    host: "localhost",
                    encoding: "UTF-8",
                    port: 25,
                    auth: false,
                    username: "",
                    password: "",
                    tls: false,
                    replyTo: "social@local.loc",
                    from: "social@local.loc",
                    priority: 5,
                    subject: "Latest change on your subscribed threads for Social"
                },
                templates: [
                    {
                        type: "DAILY",
                        template:"Hi ${profile.username} these are the changes that happened on your subscribed Threads<#list digest as change><h1> ${change[\"_id\"]} </h1><dl><#list change.ugcList as ugc><dt>Subject</dt><dd> ${ugc.subject!\"\"} </dd><dt>Body</dt>  <dd>${ugc.body!\"\"} </dd><dt>Changed by<dt><dd>${ugc.lastModifiedBy.username}<dd></#list></dl></#list>"
                    },
                    {
                        type: "WEEKLY",
                        template:"Hi ${profile.username} these are the changes that happened on your subscribed Threads<#list digest as change><h1> ${change[\"_id\"]} </h1><dl><#list change.ugcList as ugc><dt>Subject</dt><dd> ${ugc.subject!\"\"} </dd><dt>Body</dt>  <dd>${ugc.body!\"\"} </dd><dt>Changed by<dt><dd>${ugc.lastModifiedBy.username}<dd></#list></dl></#list>"
                    },
                    {
                        type: "INSTANT",
                        template:"Hi ${profile.username} these are the changes that happened on your subscribed Threads<#list digest as change><h1> ${change[\"_id\"]} </h1><dl><#list change.ugcList as ugc><dt>Subject</dt><dd> ${ugc.subject!\"\"} </dd><dt>Body</dt>  <dd>${ugc.body!\"\"} </dd><dt>Changed by<dt><dd>${ugc.lastModifiedBy.username}<dd></#list></dl></#list>"
                    }
                ]// End Of Templates
            }
        ] // End Of docs
    );
}