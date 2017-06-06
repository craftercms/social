var HashMap = java.util.HashMap;
messenger.log("TASK_START","Migrating Attributes"," Migration of Core Attributes");
var dstCollection = destination.getCollection("profile")
var srcCollection = source.getCollection("profile")
var toMigrateCount=srcCollection.count();
messenger.log("INFO","Migrating Attributes of "+toMigrateCount+" profiles "," Migration of Core Attributes");
profiles=srcCollection.find("{}").as(HashMap);
messenger.resetProgress();
var currentRow=1
while(profiles.hasNext()){
    messenger.setProgress((currentRow)/toMigrateCount);
    var profile = utils.toJSObject(profiles.next());
    log.info("Migrating Attributes of {} username {}",profile._id,profile.userName);
     if(profile.userName.indexOf("admin") !=-1){
        messenger.log("WARNING","Admin and Super admin are not migrated"," Migration of Core Attributes");
    }else{
        log.debug("Migrating ")
        if(profile.attributes.get("displayName")==null){
              messenger.log("WARNING","Profile "+profile._id+" is missing displayName Attr"," Migration of Core Attributes");
            continue;
        }
        if(profile.attributes.get("first-name")==null){
              messenger.log("WARNING","Profile "+profile._id+" is missing first-name Attr"," Migration of Core Attributes");
            continue;
        }
        if(profile.attributes.get("last-name")==null){
              messenger.log("WARNING","Profile "+profile._id+" is missing last-name Attr"," Migration of Core Attributes");
            continue;
        }
    
        var socialAttrs=[]
        socialAttrs[0]={};
        socialAttrs[0].name="Default";
        socialAttrs[0].id="f5b143c2-f1c0-4a10-b56e-f485f00d3fe9";
        socialAttrs[0].roles=["SOCIAL_USER"];
        
    
        var fnm=dstCollection.findAndModify("{_id:{$oid:\""+profile._id+"\"}}");
        var attributes={};
        var query="{$set:{attributes:{firstName:#,lastName:#,displayName:#,socialContexts:#}}}"
        utils.update(fnm,query,profile.attributes.get("first-name"),
                               profile.attributes.get("last-name"),
                               profile.attributes.get("displayName"),
                               socialAttrs).as(HashMap);

    }
    log.info("{} profiles to Go",toMigrateCount-currentRow);
    currentRow++;
}
messenger.log("TASK_END","Migrating Attributes"," Migration of Core Attributes");