# Moderation Dashboard

Manage and moderate comments

***

## Requirements

* Tomcat >= 6
* MongoDB >= 2.4.8
* [Crafter Social](https://github.com/craftercms/social)
* [Crafter Profile](https://github.com/craftercms/profile)

## Installation

### Bundle Installation

Per the requirements, this project is made up of different software components. You may speed up installation by installing the [Crafter Social bundle](http://craftercms.org/downloads) which includes all the necessary software components.

After installing the bundle, you may then replace the wars that come by default with the application by following steps 2, 3, 6 and 7 in the 'Full Installation' section.

### Full Installation

If you're comfortable setting up Tomcat and MongoDB by yourself, please follow

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


## Usage

To see the application running locally, go to: http://localhost:8080/crafter-social-admin which should show the moderation dashboard login screen.

Use username "admin" and password "admin" to login.

## Adding Data

When you first log in to the moderation dashboard, you will not see any comments to moderate/manage; however, you can add these manually by using a REST client and the following service calls:

Step 1:

    Method: GET
    URL: http://localhost:8080/crafter-profile/api/2/auth/app_token.json?username=craftersocial&password=craftersocial

    Returns: AppToken value

***

Step 2:

    Method: GET
    URL: http://localhost:8080/crafter-profile/api/2/auth/ticket.json?appToken=<AppTokenValue>&username=admin&password=admin&tenantName=craftercms

    *Replace <AppTokenValue> with AppToken value from step 1.

    Returns: Ticket value

***

Step 3: Post new content

    Method: POST
    URL: http://localhost:8090/crafter-social/api/2/ugc/create.json?ticket=<TicketValue>&tenant=craftercms

    Request Header:
    Content-Type application/json

    Request Body (sample): 
    {
        "tenant": "craftercms",
        "targetId": "test-123323TI",
        "targetUrl": "http://google.com",
        "targetDescription": "Don Quijote",
        "textContent": "Quieren decir que tenía el sobrenombre de Quijada, o Quesada, que en esto hay alguna diferencia en los autores que deste caso escriben; aunque, por conjeturas verosímiles, se deja entender que se llamaba Quejana. Pero esto importa poco a nuestro cuento; basta que en la narración dél no se salga un punto de la verdad.",
        "subject": "Ingenioso Hidalgo"
    }
   
    *Replace <TicketValue> with Ticket value from step 2.
