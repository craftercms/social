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
contexts.forEach(function(context){
   var  result=db.preferences.find({_id:context._id,"preferences.hiddenUgcStatus":{$exists:true}}).count();
    if(result===0){
     db.preferences.update({_id:context._id},{$set:{"preferences.hiddenUgcStatus":"SPAM,TRASH"}})
    }

    var  result=db.preferences.find({_id:context._id,"preferences.moderateByMailEnable":{$exists:true}}).count();
    if(result===0){
        db.preferences.update({_id:context._id},{$set:{"preferences.moderateByMailEnable":"false"}})
    }

    var  result=db.preferences.find({_id:context._id,"preferences.moderateByMailRole":{$exists:true}}).count();
    if(result===0){
        db.preferences.update({_id:context._id},{$set:{"preferences.moderateByMailRole":"SOCIAL_APPROVER"}})
    }

    var  result=db.preferences.find({_id:context._id,"preferences.moderateByMailSubject":{$exists:true}}).count();
    if(result===0){
        db.preferences.update({_id:context._id},{$set:{"preferences.moderateByMailSubject":"A new Comment" +
        " needs to be approved"}});
    }

    var  result=db.preferences.find({_id:context._id,"templates.type":"APPROVEREMAIL"}).count();
    if(result===0){
        db.preferences.update({_id:context._id},{$addToSet:{"templates":{
            type:"APPROVEREMAIL",
            template:"<html><head> <style> .footNote{ font-size:10px; font-weight: bold; } </style></head><body><p>Hi ${profile.attributes.displayName!\"Approver\"}</p> <p> User ${ugc.user.attributes.displayName!\"A user\"} has created a comment at ${ugc.createdDate?string(\"MM/dd/yyyy hh:mm.z\")} in ${ugc.targetId} with content:<br/> ${ugc.body} </p> <#assign link=baseUrl+ \"/api/3/comments/extension/approve/\"+ugc.id+\"/\"+verificationToken.id+\"?context=\"+ugc.contextId/> <p> To approve this comment please go to the following link: <a href=\"${link}\">${link}</a> <p> <p class=\"footNote\">This will expire in 24 hours, after that time the comment has to be approved using the Social Admin Console</p> </body></html>"
        }}});
    }


    var  result=db.preferences.find({_id:context._id,"templates.type":"APPROVER_RESULT_TEMPLATE"}).count();
    if(result===0){
        db.preferences.update({_id:context._id},{$addToSet:{"templates":{
            type:"APPROVER_RESULT_TEMPLATE",
            template:"<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\"><html><head> <meta charset='utf-8'> <meta http-equiv='X-UA-Compatible' content='IE=edge'> <title> </title> <link href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css\" rel=\"stylesheet\" type=\"text/css\"> <link href=\"http://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css\"> <link href=\"favicon.png\" rel=\"icon\" type=\"image/png\"><!-- HTML5 Shim andRespond.js IE8 support of HTML5 elements and media queries --><!-- WARNING: Respond.js doesn't work if you view the page via file:// --><!--[if lt IE 9]> <scriptsrc='https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js'></script> <scriptsrc='https://oss.maxcdn.com/respond/1.4.2/respond.min.js'></script> <![endif]--> <style> body { padding-top: 50px; } </style></head><body> <div class=\"container\"> <#if expireToken> <div class=\"panel panel-danger\"> <div class=\"panel-heading\"> <h3 class=\"panel-title\">The Token ${token} is no longer valid!</h3> </div> <div class=\"panel-body\"> <p>The given token is no longer valid, the current state of the ugc is <p class=\"center-block text-center\"> <#if ugc.moderationStatus==\"APPROVED\"> <span class=\"label label-success\">Approved and visible</span> <#else> <span class=\"label label-warning\">Pending moderation and hidden</span> <p>Please use the Social Admin Console to approve this comment</p> </#if> </p> </p> </div> <div class=\"panel-footer \"> <small> ${.now?string(\"yyyy\")} Crafter Social &copy;</small> </div> </div> <#elseif alreadyApprove> <div class=\"panel panel-warning\"> <div class=\"panel-heading\"> <h3 class=\"panel-title\">The comment was already approved by (${approver.attributes.displayName!\"Approver\"})</h3> </div> <div class=\"panel-body\"> <h3>Comment Body:</h3> <p> ${ugc.body}</p> <h3>Comment Information:</h3> <table class=\"table\"> <tr> <td>Comment Thread</td> <td>${ugc.attributes.threadUrl!ugc.targetId}</td> </tr> <tr> <td>Created By</td> <td>${ugc.user.attributes.displayName!\"A User\"}</td> </tr> <tr> <td>Created On:</td> <td>${ugc.createdDate?string(\"MM/dd/yyyy hh:MM z\")}</td> </tr> </table> </div> <div class=\"panel-footer \"> <small> ${.now?string(\"yyyy\")} Crafter Social &copy;</small> </div> </div> <#else> <div class=\"panel panel-success\"> <div class=\"panel-heading\"> <h3 class=\"panel-title\">The comment has been approved by you (${profile.attributes.displayName!\"\"})</h3> </div> <div class=\"panel-body\"> <h3>Comment Body:</h3> <p> ${ugc.body}</p> <h3>Comment Information:</h3> <table class=\"table\"> <tr> <td>Comment Thread</td> <td>${ugc.attributes.threadUrl!ugc.targetId}</td> </tr> <tr> <td>Created By</td> <td>${ugc.user.attributes.displayName}</td> </tr> <tr> <td>Created On:</td> <td>${ugc.createdDate?string(\"MM/dd/yyyy hh:MM z\")}</td> </tr> </table> </div> <div class=\"panel-footer \"> <small> ${.now?string(\"yyyy\")} Crafter Social &copy;</small> </div> </div> </#if> </div><!-- /.container --></body></html>"
        }}});
    }
});

var  result=db.preferences.find({_id:"TEMPLATE_CONTEXT_ACTIONS","preferences.hiddenUgcStatus":{$exists:true}}).count();
if(result===0){
    db.preferences.update({_id:"TEMPLATE_CONTEXT_ACTIONS"},{$set:{"preferences.hiddenUgcStatus":"SPAM,TRASH"}})
}
var  result=db.preferences.find({_id:"TEMPLATE_CONTEXT_ACTIONS","preferences.moderateByMailEnable":{$exists:true}}).count();
if(result===0){
    db.preferences.update({_id:"TEMPLATE_CONTEXT_ACTIONS"},{$set:{"preferences.moderateByMailEnable":"false"}})
}


var  result=db.preferences.find({_id:"TEMPLATE_CONTEXT_ACTIONS","preferences.moderateByMailRole":{$exists:true}}).count();
if(result===0){
    db.preferences.update({_id:"TEMPLATE_CONTEXT_ACTIONS"},{$set:{"preferences.moderateByMailRole":"SOCIAL_APPROVER"}})
}


var  result=db.preferences.find({_id:"TEMPLATE_CONTEXT_ACTIONS","preferences.moderateByMailSubject":{$exists:true}}).count();
if(result===0){
    db.preferences.update({_id:"TEMPLATE_CONTEXT_ACTIONS"},{$set:{"preferences.moderateByMailSubject":"A new Comment" +
    " needs to be approved"}})
}
var  result=db.preferences.find({_id:"TEMPLATE_CONTEXT_ACTIONS","templates.type":"APPROVEREMAIL"}).count();

if(result===0) {
    db.preferences.update({_id: "TEMPLATE_CONTEXT_ACTIONS"}, {
        $addToSet: {
            "templates": {
                type: "APPROVEREMAIL",
                template:"<html><head> <style> .footNote{ font-size:10px; font-weight: bold; } </style></head><body><p>Hi ${profile.attributes.displayName!\"Approver\"}</p> <p> User ${ugc.user.attributes.displayName!\"A user\"} has created a comment at ${ugc.createdDate?string(\"mm/dd/yyyy hh:MM.z\")} in ${ugc.targetId} with content:<br/> ${ugc.body} </p> <#assign link=baseUrl+ \"/api/3/comments/extension/approve/\"+ugc.id+\"/\"+verificationToken.id+\"?context=\"+ugc.contextId/> <p> To approve this comment please go to the following link <a href=\"${link}\">${link}</a> <p> <p class=\"footNote\">This will expire in 24 hours, after that time the comment has to be approved using the Social Admin Console</p> </body></html>"
            }
        }
    });
}

if(result===0) {
    db.preferences.update({_id: "TEMPLATE_CONTEXT_ACTIONS"}, {
        $addToSet: {
            "templates": {
                type: "APPROVER_RESULT_TEMPLATE",
                template:"<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\"><html><head> <meta charset='utf-8'>" +
                " <meta http-equiv='X-UA-Compatible' content='IE=edge'> <title> </title> <link" +
                " href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css\" rel=\"stylesheet\" type=\"text/css\"> <link href=\"http://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css\"> <link href=\"favicon.png\" rel=\"icon\" type=\"image/png\"><!-- HTML5 Shim andRespond.js IE8 support of HTML5 elements and media queries --><!-- WARNING: Respond.js doesn't work if you view thepage via file:// --><!--[if lt IE 9]> <scriptsrc='https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js'></script> <scriptsrc='https://oss.maxcdn.com/respond/1.4.2/respond.min.js'></script> <![endif]--> <style> body { padding-top: 50px; } </style></head><body> <div class=\"container\"> <#if expireToken> <div class=\"panel panel-danger\"> <div class=\"panel-heading\"> <h3 class=\"panel-title\">The token ${token} is no longer valid!</h3> </div> <div class=\"panel-body\"> <p>The given token is no longer valid, the current state of the ugc is <p class=\"center-block text-center\"> <#if ugc.moderationStatus==\"APPROVED\"> <span class=\"label label-success\">Approved and visible</span> <#else> <span class=\"label label-warning\">Pending moderation and hidden</span> <p>Please use the Social Admin Console to approve this comment</p> </#if> </p> </p> </div> <div class=\"panel-footer \"> <small> ${.now?string(\"yyyy\")} Crafter Social &copy;</small> </div> </div> <#elseif alreadyApprove> <div class=\"panel panel-warning\"> <div class=\"panel-heading\"> <h3 class=\"panel-title\">The comment was already approved by (${approver.attributes.displayName!\"Approver\"})</h3> </div> <div class=\"panel-body\"> <h3>Comment Body:</h3> <p> ${ugc.body}</p> <h3>Comment Information:</h3> <table class=\"table\"> <tr> <td>Comment Thread</td> <td>${ugc.attributes.threadUrl!ugc.targetId}</td> </tr> <tr> <td>Created By</td> <td>${ugc.user.attributes.displayName!\"A User\"}</td> </tr> <tr> <td>Created On:</td> <td>${ugc.createdDate?string(\"MM/dd/yyyy hh:mm z\")}</td> </tr> </table> </div> <div class=\"panel-footer \"> <small> ${.now?string(\"yyyy\")} Crafter Social &copy;</small> </div> </div> <#else> <div class=\"panel panel-success\"> <div class=\"panel-heading\"> <h3 class=\"panel-title\">The comment has been approved by you (${profile.attributes.displayName!\"\"})</h3> </div> <div class=\"panel-body\"> <h3>Comment Body:</h3> <p> ${ugc.body}</p> <h3>Comment Information:</h3> <table class=\"table\"> <tr> <td>Comment Thread</td> <td>${ugc.attributes.threadUrl!ugc.targetId}</td> </tr> <tr> <td>Created By</td> <td>${ugc.user.attributes.displayName}</td> </tr> <tr> <td>Created On:</td> <td>${ugc.createdDate?string(\"MM/dd/yyyy hh:MM z\")}</td> </tr> </table> </div> <div class=\"panel-footer \"> <small> ${.now?string(\"yyyy\")} Crafter Social &copy;</small> </div> </div> </#if> </div><!-- /.container --></body></html>"
            }
        }
    });
}