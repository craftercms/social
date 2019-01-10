/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
    if(db.securityActions.find({"contextId":context._id,
                                "actionName": "system.notification.changeTemplate"}).count()<=0) {
        db.securityActions.insert(
            {
                "actionName": "system.notification.changeTemplate",
                "contextId": context._id,
                "roles": [
                    "SOCIAL_ADMIN",
                    "SOCIAL_SUPERADMIN"
                ]
            }
        );
    }
});

if(db.securityActions.find({"contextId":"TEMPLATE_CONTEXT_ACTIONS",
        "actionName": "system.notification.changeTemplate"}).count()<=0) {
    db.securityActions.insert(
        {
            "actionName": "system.notification.changeTemplate",
            "contextId": "TEMPLATE_CONTEXT_ACTIONS",
            "roles": [
                "SOCIAL_ADMIN",
                "SOCIAL_SUPERADMIN"
            ]
        }
    );
}
