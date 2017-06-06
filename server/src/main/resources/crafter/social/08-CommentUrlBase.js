/*
 * Copyright (C) 2007-2015 Crafter Software Corporation.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
var contexts=db.socialContext.find();
contexts.forEach(function(context) {
    var  result=db.preferences.find({_id:context._id,"preferences.baseUrl":{$exists:true}}).count();
    if(result===0){
        db.preferences.update({_id:context._id},{$set:{"preferences.baseUrl":"http://myDomain.com"}});
    }
});
var  result=db.preferences.find({_id:"TEMPLATE_CONTEXT_ACTIONS","preferences.baseUrl":{$exists:true}}).count();
if(result===0){
    db.preferences.update({_id:"TEMPLATE_CONTEXT_ACTIONS"},{$set:{"preferences.baseUrl":"http://myDomain.com"}});
}