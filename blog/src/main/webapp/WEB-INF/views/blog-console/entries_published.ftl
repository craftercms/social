<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>Crafter Social Blog</title>
<link href="../resources/image/favicon.ico" rel="Shortcut Icon">
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css"/>
<script type="text/javascript" src="/crafter-social/resources/js/jquery-1.7.1.js"></script>
		<script type="text/javascript" src="/crafter-social/resources/js/jquery.form.js"></script>
		<script type="text/javascript" src="/crafter-social/resources/js/date.format.js"></script>
		<script type="text/javascript" src="/crafter-social/resources/js/jsrender.js"></script>
		<script type="text/javascript" src="/crafter-social/resources/js/jquery.observable.js"></script>
		<script type="text/javascript" src="/crafter-social/resources/js/jquery.views.js"></script>
		<script type="text/javascript" src="/crafter-social/resources/js/ugc_blog_post.js"></script>
		<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
		<link rel="stylesheet" href="/crafter-social/resources/css/style.css" type="text/css" media="screen"/>

</head>
<body>
	<div id="page" class="container">
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
				 jQuery('#ugc_div').ugc_blog_post({
				 	clientId : 'CrafterCMS',
					restUrl : '/crafter-social/api/2',
					resourceUrl : '/crafter-social/resources', 
					target : data1.blogListForm.target,
					username: data1.blogListForm.username,
					actions: data1.blogListForm.actions,
					ticket 	: data1.blogListForm.ticket
					}).ugc_blog_post('loadUGCBlogPost');
			});
		});
		</script>
</body>
</html> 