var ArrayList = java.util.ArrayList; 
var HashMap = java.util.HashMap;
var SocialCtx="f5b143c2-f1c0-4a10-b56e-f485f00d3fe9";
var insertQ="{"+
    "\"_id\" : {$oid:#},"+
    "\"ancestors\" : [],"+
    "\"targetId\" : #,"+
    "\"contextId\" : #,"+
    "\"subject\" : #,"+
    "\"body\" : #,"+
    "\"createdBy\" : #,"+
    "\"lastModifiedBy\" : #,"+
    "\"createdDate\" : {$date:#},"+
    "\"lastModifiedDate\" : {$date:#},"+
    "\"anonymousFlag\" : #,"+
    "\"attributes\" : {\"targetUrl\":#,\"targetDescription\":#},"+
    "\"attachments\" : [],"+
    "\"moderationStatus\" : #,"+
    "\"votesUp\" : [],"+
    "\"votesDown\" : [],"+
    "\"flags\" : []"+
    "}";
messenger.log("TASK_START","UGC Migration","UGC Migration");
var srcCollection = source.getCollection("uGC")
var dstCollection = destination.getCollection("ugc")
var toMigrateCount=srcCollection.count();
messenger.log("INFO","Migrating "+toMigrateCount+" ugcs ","UGC Migration");
var ugcs=srcCollection.find("{}").as(HashMap);
messenger.resetProgress();
var currentRow=1
    while(ugcs.hasNext()){
        var ugc=utils.toJSObject(ugcs.next());
        log.debug("Migrating UGC {} target {} ",ugc._id,ugc.targetId)
        messenger.setProgress((currentRow)/toMigrateCount);
        if(dstCollection.count("{_id:#}",ugc._id)>0){
             messenger.log("WARNING","UGC "+ugc._id+" already exist in new DB skiping","UGC Migration");
        }else{
            dstCollection.insert(insertQ,ugc._id.toString(),ugc.targetId,SocialCtx,"",ugc.textContent,ugc.profileId,ugc.profileId,ugc.createdDate,
                                 new java.util.Date(),ugc.anonymousFlag,ugc.targetUrl,ugc.targetDescription,ugc.moderationStatus);
         }
      currentRow++;
    }
messenger.log("TASK_END","UGC Migration","UGC Migration");