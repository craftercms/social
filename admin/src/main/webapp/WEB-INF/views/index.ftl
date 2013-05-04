<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="en">
<head>
	<title>Content Moderation</title>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	
	<link rel="stylesheet" type="text/css" href="resources/js/libs/yui/fonts/fonts-min.css">
	<link rel="stylesheet" type="text/css" href="resources/js/libs/yui/resize/assets/skins/sam/resize.css">
	<link rel="stylesheet" type="text/css" href="resources/js/libs/yui/layout/assets/skins/sam/layout.css">
	<link rel="stylesheet" type="text/css" href="resources/js/libs/yui/button/assets/skins/sam/button.css">
	<link rel="stylesheet" type="text/css" href="resources/js/libs/yui/datatable/assets/skins/sam/datatable.css">
	<link rel="stylesheet" type="text/css" href="resources/js/libs/yui/container/assets/skins/sam/container.css">
	<link rel="stylesheet" type="text/css" href="resources/js/libs/yui/paginator/assets/skins/sam/paginator.css">
	
	<script type="text/javascript" src="resources/js/libs/yui/yahoo-dom-event/yahoo-dom-event.js"></script>	
	<script type="text/javascript" src="resources/js/libs/yui/element/element-min.js"></script>
	<script type="text/javascript" src="resources/js/libs/yui/json/json-min.js"></script>	
	<script type="text/javascript" src="resources/js/libs/yui/resize/resize-min.js"></script>
	<script type="text/javascript" src="resources/js/libs/yui/layout/layout-min.js"></script>
	<script type="text/javascript" src="resources/js/libs/yui/button/button-min.js"></script>
	<script type="text/javascript" src="resources/js/libs/yui/dragdrop/dragdrop-min.js"></script>
	<script type="text/javascript" src="resources/js/libs/yui/container/container-min.js"></script>
	<script type="text/javascript" src="resources/js/libs/yui/datatable/datatable-min.js"></script>
	<script type="text/javascript" src="resources/js/libs/yui/animation/animation-min.js"></script>
	<script type="text/javascript" src="resources/js/libs/yui/paginator/paginator-min.js"></script>
	<script type="text/javascript" src="resources/js/libs/yui/connection/connection-min.js"></script>
	<script type="text/javascript" src="resources/js/libs/yui/datasource/datasource-min.js"></script>
	<script type="text/javascript" src="resources/js/libs/yui/event-delegate/event-delegate-min.js"></script>
	
	<link rel="stylesheet" type="text/css" href="resources/css/style.css">
	
	<script type="text/javascript" src="resources/js/common.js"></script>	
	<script type="text/javascript" src="js/crafter.social.config.js"></script>
	<script type="text/javascript" src="resources/js/index.js"></script>
    <script type="text/javascript" src="resources/js/moderation.js"></script>	
</head>
 
<body class="yui-skin-sam">
<div id="index-center">
	<div id="index-list"></div>
</div>
<div id="index-footer">
	&nbsp;Crafter CMS
</div>

<script type="text/javascript">
YAHOO.util.Event.onDOMReady(crafter.social.index.loadLayout);
YAHOO.util.Event.onDOMReady(crafter.social.index.showModeration);
</script>
</body>
</html>