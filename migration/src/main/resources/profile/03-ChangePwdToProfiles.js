var HashMap = java.util.HashMap;
messenger.log("TASK_START","Changing Password of Profiles"," Change Passwords");
var dstCollection = destination.getCollection("profile")
var toMigrateCount=dstCollection.count();
messenger.log("INFO","Changing Passwords to "+toMigrateCount+" profiles "," Change Passwords");
profiles=dstCollection.find("{}").as(HashMap);
messenger.resetProgress();
var currentRow=1
while(profiles.hasNext()){
    messenger.setProgress((currentRow)/toMigrateCount);
    var profile = utils.toJSObject(profiles.next());
    log.info("Changing password of profile # {}/{}",profile._id,profile.username);
     if(profile.username.indexOf("admin") !=-1){
        messenger.log("WARNING","Admin and Super admin are not migrated"," Change Passwords");
    }else{
        var fnm=dstCollection.findAndModify("{_id:{$oid:\""+profile._id+"\"}}");
        //TO Change this to match old pwd generation , if does not exist run it but pwd must be change!
        utils.update(fnm,"{$set:{password:\""+utils.hash(profiles.username)+"\"}}").as(HashMap);
    }
    log.info("{} profiles to Go",toMigrateCount-currentRow);
    currentRow++;
}
messenger.log("TASK_END","Changing Password of Profiles"," Change Passwords");