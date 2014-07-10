<#import "spring.ftl" as spring/>
<#import "common/components.ftl" as components/>

<!DOCTYPE html>
<html lang="en">
    <head>
        <@components.head "Login"/>
        <!-- Custom styles for this template -->
        <link href="<@spring.url '/resources/css/login.css'/>" rel="stylesheet"/>
    </head>

    <body>
        <div class="container">
            <form class="form-signin" role="form" action="<@spring.url '/crafter-security-login'/>" method="post">
                <img class="logo" src="<@spring.url '/resources/image/logo.png'/>">
                <input name="username" type="text" class="form-control" placeholder="Username" autofocus="autofocus">
                <input name="password" type="password" class="form-control" placeholder="Password">
                <#if RequestParameters.logout??>
                    <div class="alert alert-success">You have successfully logged out</div>
                </#if>
                <#if RequestParameters.login_error??>
                    <div class="alert alert-danger">Your credentials were not recognized</div>
                </#if>
                <#if RequestParameters.login_permission_error??>
                    <div class="alert alert-danger">You do not have permission to access this application</div>
                </#if>
                <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
            </form>
        </div> <!-- /container -->
    </body>
</html>