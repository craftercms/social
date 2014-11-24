importPackage(Packages.org.apache.commons.io);
importPackage(java.net);
messenger.log("TASK_START","Init Script For Profile","Init Profile DB");
var url=props.getProperty("crafter.migration.profile.initScript");
log.info("Donwloading File {}",url);
var script=IOUtils.toString(URI.create(url),"UTF-8");
log.debug("Script to exec \n {}",script);
messenger.log("INFO","Downloaded Init Script "+url,"Init Profile DB");
messenger.log("INFO","Executing Script","Init Profile DB");
log.debug("Running script to Destination DB")
destination.getDatabase().eval(script);
log.debug("Script run ok")
messenger.log("TASK_END","Init Script For Profile","Init Profile DB");