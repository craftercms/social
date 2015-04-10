<#import "/spring.ftl" as spring />

<#macro head title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>${title}</title>

<!-- Favicon -->
<link rel="shortcut icon" href="<@spring.url '/resources/image/favicon.ico'/>">

<!-- Bootstrap -->
<link href="<@spring.url '/resources/css/bootstrap.min.css'/>" rel="stylesheet">
<link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
<script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</#macro>

<#macro navBar>
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#/">Crafter Social Admin Console</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav navbar-right">
                <li>
                    <a href="<@spring.url '/crafter-security-logout'/>">Log Out</a>
                </li>
                <li>
                    <span class="navbar-text">
                        Signed in as: <a href="#/profile/${loggedInUser.id}">${loggedInUser.username}</a>
                    </span>
                </li>
            </ul>
        </div>
    </div>
</div>
</#macro>

<#macro navSidebar>
<div class="col-sm-3 col-md-2 sidebar">
    <ul class="nav nav-sidebar">
        <li><a href="#/moderation-dashboard">Moderation Dashboard</a></li>
        <li><a href="#/contexts">Contexts</a></li>
        <li><a href="#/security-actions">Security Actions</a></li>
        <li><a href="#/notification-preferences">Notification Preferences</a></li>
        <li><a href="#/search-profiles">Search Profiles</a></li>
    </ul>
</div>
</#macro>

<#macro scripts>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.16/angular.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.16/angular-route.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script src="//cdn.ckeditor.com/4.4.7/full/ckeditor.js"></script>
<script src="<@spring.url '/resources/js/bootstrap.min.js'/>"></script>
<script src="<@spring.url '/resources/js/ui-bootstrap-tpls-0.11.0.min.js'/>"></script>
<script src="<@spring.url '/resources/js/bootstrap-growl.min.js'/>"></script>
<script src="<@spring.url '/resources/js/jquery.cookie.js'/>"></script>
<script src="<@spring.url '/resources/js/ng-ckeditor.min.js'/>"></script>

<script src="<@spring.url '/resources/js/app.js'/>"></script>
<script type="text/javascript">
    var contextPath = "${requestContext.contextPath}";
    var socialAppUrl = "${socialAppUrl}";
    var socialRestBaseUrl = socialAppUrl + "/api/3";
</script>
</#macro>