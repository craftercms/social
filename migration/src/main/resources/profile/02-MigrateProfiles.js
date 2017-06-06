var ArrayList = java.util.ArrayList; 
var HashMap = java.util.HashMap;
messenger.log("TASK_START","Migrating of Profiles","Profiles Migration");
var dstCollection = destination.getCollection("profile")
var srcCollection = source.getCollection("profile")
log.debug("Connected to src and dest profile collections");
var toMigrateCount=srcCollection.count();
messenger.log("INFO","Migrating "+toMigrateCount+" profiles ","Profiles Migration");
profiles=srcCollection.find("{}").as(HashMap);
messenger.resetProgress();
var currentRow=1
while(profiles.hasNext()){
    log.info("Migrating profile # {}",toMigrateCount);
    messenger.setProgress((currentRow)/toMigrateCount);
    var profile = utils.toJSObject(profiles.next());
    log.debug("Migrating {} ",JSON.stringify(profile));
//    messenger.log("INFO","Migrating "+profile.email+" profile ","Profiles Migration");
    if(profile.userName==undefined){
          messenger.log("ERROR","Profile "+profile._id+" does not have username","Profiles Migration");
    }else if(profile.userName.indexOf("admin") !=-1){
        messenger.log("WARNING","Admin and Super admin are not migrated","Profiles Migration");
    }else{
        if(dstCollection.count("{username:\""+profile.userName+"\"}")>0){
             log.debug("Skiping {} already exist in destination source",profile.userName);
             messenger.log("WARNING","Username "+profile.userName+" already exist in new DB skiping","Profiles Migration");
        }else{
            var newProfile={};
            newProfile._id=profile._id;
            newProfile.username=profile.userName;
            newProfile.password=profile.password;
            newProfile.verified=profile.verify;
            newProfile.enabled=profile.active;
            newProfile.createdOn=new java.util.Date(profile.created);
            newProfile.lastModified=new java.util.Date();
            newProfile.email=profile.email;
            newProfile.tenant = "default";
            newProfileStr=utils.toJsonString(newProfile);
            log.debug("Saving {} ",newProfileStr);
            dstCollection.insert(newProfileStr)
        }
        
    }
    log.info("{} profiles to Go",toMigrateCount-currentRow);
    currentRow++;
}
messenger.log("TASK_END","Migrating of Profiles","Profiles Migration");