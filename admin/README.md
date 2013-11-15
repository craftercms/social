# Moderation Dashboard

Manage and moderate comments

***

## Requirements

* Tomcat >= 6
* MongoDB >= 2.4.8
* [Crafter Social](https://github.com/craftercms/social)
* [Crafter Profile](https://github.com/craftercms/profile)

## Installation

1) Create a root folder for your project:

    mkdir ~/moderation-dashboard

2) Create a folder in which to store all the source code:

    cd ~/moderation-dashboard
    mkdir src

3) Clone the source code from their repos : [Crafter Social](https://github.com/craftercms/social) & [Crafter Profile](https://github.com/craftercms/profile)

    cd src
    git clone https://github.com/craftercms/social.git
    git clone https://github.com/craftercms/profile.git

4) Create a folder for your deployed app:

    mkdir ~/moderation-dashboard/deploy


5) Put your uncompressed Tomcat and MongoDB folders in the deploy folder

    mv tomcat-x.x ~/moderation-dashboard/deploy/tomcat
    mv mongodb-x.x ~/moderation-dashboard/deploy/mongodb

6) Build wars for:

6.1) Profile client
    
    cd ~/moderation-dashboard/src/profile/client
    mvn clean install

6.2) Profile crafter security provider
    
    cd ~/moderation-dashboard/src/profile/crafter-security-provider
    mvn clean install   

6.3) Profile server

    cd ~/moderation-dashboard/src/profile/server
    mvn clean package

6.4) Social server

    cd ~/moderation-dashboard/src/social/server
    mvn clean package

6.5) Moderation dashboard

    cd ~/moderation-dashboard/src/social/admin
    mvn clean package

7) Move war files to Tomcat's webapps folder:

    cd ~/moderation-dashboard
    mv src/profile/server/target/crafter-profile.war deploy/tomcat/webapps/crafter-profile.war
    mv src/social/server/target/crafter-social.war deploy/tomcat/webapps/crafter-social.war
    mv src/social/admin/target/crafter-social-admin.war deploy/tomcat/webapps/crafter-social-admin.war

8) Edit the shared.loader property in catalina.properties:

    cd ~/moderation-dashboard/deploy/tomcat/conf
    vim catalina.properties

Change the property to:

    shared.loader=${catalina.base}/shared/classes,${catalina.base}/shared/lib/*.jar

9) Add folders (profile, security, social) with properties files inside of ~/moderation-dashboard/deploy/tomcat/shared/classes/crafter 

Note*: To ask Tomcat for more memory than the value provided by default, create a setenv.sh file in: ~/moderation-dashboard/deploy/tomcat/bin and add the following line: 

    export JAVA_OPTS="-Xms1024m -Xmx10246m -XX:NewSize=256m -XX:MaxNewSize=356m -XX:PermSize=256m -XX:MaxPermSize=356m"
