<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title>Crafter Social Admin - Login</title>
	<link href="resources/img/favicon.ico" rel="Shortcut Icon">
	<link rel="stylesheet" href="resources/css/style.css">
</head>

<body>
  <div id="content">
	  <div id="header">
	    <a class="logo" href="http://craftercms.org" title="Go to Crafter CMS"></a> 
	    <h1 class="mainTitle">Social Admin</h1>
	  </div>
	  <div class="sign-in">
	  	<div class="signin-box">
	  		<h2>
				Sign in
			<strong></strong>
			</h2>
			<form class="login-form" action="crafter-security-login" method="post" accept-charset="UTF-8">
				
				    	<p>
						<label for="j_username">Username:</label>
						<input id="username" name="username" size="20" maxlength="50" type="text"/>
						</p>
						
						<p>
						<label for="j_password">Password:</label>
						<input id="password" name="password" size="20" maxlength="50" type="password"/>
						</p>
						<#if RequestParameters.logout??>
							<!--p class="logout-success">You have been successfully logged out.</p-->
						</#if>
					    <#if RequestParameters.login_error??>
					    	<p class="login-error">*Your credentials were not recognized.</p>
					    </#if>
					    <#if RequestParameters.login_permission_error??>
					    	<p class="login-error">*You do not have permission to authenticate.</p>
					    </#if>
						<p>
						<label for=""></label>
						<button id="login" name="login" class="btn btn-info" type="submit">Sign in</button>
						</p>
					

			</form>
		</div>	
	  </div>
			
  </div>	
  <!--div class="footer">
	&copy; 2005-2013 Crafter CMS. All Rights Reserved.
</div-->
<div style="margin: 0 auto; width: 960px; padding: 10px 0pt; font: 12px 'Adobe Garamond',Georgia,sans-serif;" class="copy-right"> &copy; 2005-2013 Crafter CMS. All Rights Reserved. </div>
</body>
</html>