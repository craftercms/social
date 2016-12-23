importPackage(java.util);
var ArrayList = java.util.ArrayList; 
var HashMap = java.util.HashMap;
var template={
        "name" : "NAME",
        "metadata" : {
            "label" : "LABEL",
            "type" : "TEXT",
            "displayOrder" : -666
        },
        "permissions" : [ 
            {
                "application" : "*",
                "allowedActions" : [ 
                    "*"
                ]
            }
        ]
    }
messenger.log("TASK_START","Migrating Schema","Schema Migration");
var dstCollection = destination.getCollection("tenant")
var srcCollection = source.getCollection("tenant")
log.debug("Connected to src and dest tenant collections");
var srcAttrs=srcCollection.find("{tenantName:'craftercms'}").projection("{schema:1,_id:0}").as(HashMap)
if(srcAttrs!=null){
    messenger.log("INFO","Getting Src Schema","Schema Migration");
    while(srcAttrs.hasNext()){
        var attributes=srcAttrs.next().get("schema").get("attributes");
        for each (var attribute in attributes.toArray()){
            var name=attribute.get("name");
            if(name=="first-name"){
                messenger.log("WARNING","Attribute first-name is now called firstName","Schema Migration");
            }else if(name=="last-name"){
                messenger.log("WARNING","Attribute last-name is now called lastName","Schema Migration");
            }else{
                var newAttribute=JSON.parse(JSON.stringify(template));
                newAttribute.name=new String(name);
                newAttribute.metadata.displayOrder=attribute.get("order");
                log.debug("Attribute to migrate "+JSON.stringify(newAttribute));
                utils.update(dstCollection.findAndModify("{name:'default'}"),"{$addToSet:{attributeDefinitions:"+JSON.stringify(newAttribute)+"}}").as(HashMap)                                    
                messenger.log("INFO","Migrating "+name+" Attribute","Schema Migration");
            }
        }
    }
}else{
    messenger.log("WARNING","No Schema Found","Schema Migration");    
}
messenger.log("TASK_END","Migrating Schema","Schema Migration");