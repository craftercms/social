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

var secActions=db.socialContext.find();
secActions.forEach(function(context){
db.securityActions.findAndModify(
    {
        query: {
            contextId:context._id,
            actionName: "system.securityActions.update",
            roles: {$nin: ['SOCIAL_ADMIN']}
        },
        update: {$addToSet: {roles: 'SOCIAL_ADMIN'}}
    });
db.securityActions.findAndModify(
    {
        query: {
            contextId:context._id,actionName: "system.securityActions.read", roles: {$nin: ['SOCIAL_ADMIN']}
        },
        update: {
            $addToSet: {roles: 'SOCIAL_ADMIN'}
        }
    });
db.securityActions.findAndModify(
    {
        query: {
            contextId:context._id,actionName: "system.socialctx.all", roles: {$nin: ['SOCIAL_ADMIN']}
        },
        update: {
            $addToSet: {roles: 'SOCIAL_ADMIN'}
        }
    });
});



db.securityActions.findAndModify(
    {
        query: {
            contextId:"TEMPLATE_CONTEXT_ACTIONS",
            actionName: "system.securityActions.update",
            roles: {$nin: ['SOCIAL_ADMIN']}
        },
        update: {$addToSet: {roles: 'SOCIAL_ADMIN'}}
    });
db.securityActions.findAndModify(
    {
        query: {
            contextId:"TEMPLATE_CONTEXT_ACTIONS",actionName: "system.securityActions.read", roles: {$nin: ['SOCIAL_ADMIN']}
        },
        update: {
            $addToSet: {roles: 'SOCIAL_ADMIN'}
        }
    });
db.securityActions.findAndModify(
    {
        query: {
            contextId:"TEMPLATE_CONTEXT_ACTIONS",actionName: "system.socialctx.all", roles: {$nin: ['SOCIAL_ADMIN']}
        },
        update: {
            $addToSet: {roles: 'SOCIAL_ADMIN'}
        }
    });