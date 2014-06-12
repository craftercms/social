<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>Crafter Blog Console</title>
<link href="resources/image/favicon.ico" rel="Shortcut Icon">
<!--link rel="stylesheet" href="resources/css/main.css"-->
<link rel="stylesheet" href="../resources/css/profile.css">
<script type="text/javascript" src="/crafter-social/resources/js/jquery-1.7.1.js"></script>
<script type="text/javascript" src="/crafter-social/resources/js/jquery.form.js"></script>
<script type="text/javascript" src="/crafter-social/resources/js/date.format.js"></script>
<script type="text/javascript" src="/crafter-social/resources/js/jsrender.js"></script>
<script type="text/javascript" src="/crafter-social/resources/js/jquery.observable.js"></script>
<script type="text/javascript" src="/crafter-social/resources/js/jquery.views.js"></script>
<script type="text/javascript" src="/crafter-social/resources/js/ugc_blog_console.js"></script>
<script type="text/javascript" src="/crafter-social/resources/js/tiny_mce.js"></script>

</head>
<body>
<div id="content">
	<div id="header">
    <a class="logo" href="blog_entries" title="Go to Crafter CMS"></a> 
    <ul class="page-actions">
      <li><a href="../crafter-security-logout" value="Logout" id="logout" name="logout">Sign out</a></li>
    </ul>
   
  </div>

	<div id="ugc_div"></div>
</div>

<script type="text/javascript">
		jQuery(document).ready(function(){
			var configData = {};
			var configManager = {
				    getConfig: function (token) {
				        var url = 'get_config.json';
				        var data = {}; 
				        return $.ajax({
								    url: url,
								    data: data,
								    dataType : 'json',
								    contentTypeString:"application/json;charset=UTF-8",
								    cache: false,
								    type: 'GET',

								    success: function(aData, textStatus, jqXHR){
								    	configData = aData;
								    },
								    error: function(jqXHR, textStatus, errorThrown) {
								    	$.error('Could not load tenant: ' + textStatus + ' - ' + errorThrown);
								    }
								});
				    }
				    
				};
			configManager.getConfig("history").done(function(data1){
				 jQuery('#ugc_div').ugc_blog_console({
					clientId : 'CrafterCMS',
					restUrl : '/crafter-comments/api/2',
					resourceUrl : '/crafter-comments/resources',
					target : data1.blogListForm.target,
					tenant : data1.blogListForm.tenant,
					actions: data1.blogListForm.actions,
					ticket 	: data1.blogListForm.ticket
					}).ugc_blog_console('loadUGCBlogConsole');
			});

			
		});
</script>
<div style="margin: 0 auto; width: 960px; padding: 10px 0pt; font: 12px 'Adobe Garamond',Georgia,sans-serif;" class="copy-right"> &copy; 2005-2013 Crafter CMS. All Rights Reserved. </div>
</body>
</html> 