<%@page import="org.craftercms.profile.httpclient.ProfileRestClientImpl"%>
<%@page import="org.craftercms.profile.httpclient.ProfileRestClient"%>
<%@page import="org.craftercms.social.util.support.CrafterProfile"%>
<%@page import="org.springframework.web.context.ContextLoader"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%
WebApplicationContext webAppContext = ContextLoader.getCurrentWebApplicationContext();
CrafterProfile crafterProfileService = webAppContext.getBean("crafterProfileService", CrafterProfile.class);

ProfileRestClient crafterProfileRestClient = webAppContext.getBean("crafterProfile", ProfileRestClientImpl.class);

String ticket = crafterProfileRestClient.getTicket(crafterProfileService.getAppToken(), "vjalilov", "vagif");

%> 
<html>
	<head>
		<title>Sample UGC page</title>
		<link rel="stylesheet" href="http://localhost:28080/crafter-social/resources/css/combined-minified.css" type="text/css" media="screen"/>
	</head>
    <body>

		<div id="ugc_div"></div>
	
		<script type="text/javascript" src="http://localhost:28080/crafter-social/resources/js/jquery-1.7.1.js"></script>
		<script type="text/javascript" src="http://localhost:28080/crafter-social/resources/js/jquery.form.js"></script>
		<script type="text/javascript" src="http://localhost:28080/crafter-social/resources/js/date.format.js"></script>
		<script type="text/javascript" src="http://localhost:28080/crafter-social/resources/js/jsrender.js"></script>
		<script type="text/javascript" src="http://localhost:28080/crafter-social/resources/js/jquery.observable.js"></script>
		<script type="text/javascript" src="http://localhost:28080/crafter-social/resources/js/jquery.views.js"></script>
		<script type="text/javascript" src="http://localhost:28080/crafter-social/resources/js/ugc.js"></script>
		
		<script type="text/javascript">
		jQuery(document).ready(function(){
			jQuery('#ugc_div').ugc({
				clientId : 'CrafterCMS',
				restUrl : 'http://localhost:28080/crafter-social/api/1',
				resourceUrl : 'http://localhost:28080/crafter-social/resources', 
				target : 'http://www.craftercms.org',
				ticket 	:'<%=ticket%>',
				tenant : 'craftercms'
			}).ugc('loadUGC');
		});
		</script>
	</body>
</html>