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
var contexts=db.socialContext.find();
contexts.forEach(function(context) {
    var  result=db.preferences.find({_id:context._id,"preferences.setupAutoWatch":{$exists:true}}).count();
    if(result===0){
        db.preferences.update({_id:context._id},{$set:{"preferences.setupAutoWatch":false}});
    }
    var  result2=db.preferences.find({_id:context._id,"preferences.defaultFrequency":{$exists:true}}).count();
    if(result2===0){
        db.preferences.update({_id:context._id},{$set:{"preferences.defaultFrequency":"INSTANT"}});
    }
});
var  result=db.preferences.find({_id:"TEMPLATE_CONTEXT_ACTIONS","preferences.setupAutoWatch":{$exists:true}}).count();
if(result===0){
    db.preferences.update({_id:"TEMPLATE_CONTEXT_ACTIONS"},{$set:{"preferences.setupAutoWatch":false}});
}

var  result3=db.preferences.find({_id:"TEMPLATE_CONTEXT_ACTIONS","preferences.defaultFrequency":{$exists:true}}).count();
if(result3===0){
    db.preferences.update({_id:"TEMPLATE_CONTEXT_ACTIONS"},{$set:{"preferences.defaultFrequency":"INSTANT"}});
}


