<#import "spring.ftl" as spring/>
<#import "common/components.ftl" as components />

<!DOCTYPE html>
<html lang="en" ng-app="CrafterAdminConsole">
    <head>
        <@components.head "Crafter Social Admin Console"/>
        <link href="<@spring.url '/resources/css/main.css'/>" rel="stylesheet">
    </head>

    <body>
        <@components.navBar/>

        <div class="container-fluid">
            <div class="row">
            <@components.navSidebar/>

                <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                    <div ng-view></div>
                </div>
            </div>
        </div>

        <@components.scripts/>
    </body>
</html>