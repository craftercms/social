<html>
	<head>
		<title>Sample Publish UGC page</title>
	</head>
    <body>

		<div id="ugc_div"></div>
	
		<script type="text/javascript" src="resources/js/jquery-1.7.1.js"></script>
		<script type="text/javascript" src="resources/js/jquery.form.js"></script>
		<script type="text/javascript" src="resources/js/date.format.js"></script>
		<script type="text/javascript" src="resources/js/jsrender.js"></script>
		<script type="text/javascript" src="resources/js/jquery.observable.js"></script>
		<script type="text/javascript" src="resources/js/jquery.views.js"></script>
		<script type="text/javascript" src="resources/js/ugc_blog_publish.js"></script>
		<script type="text/javascript" src="resources/js/tiny_mce_dev.js"></script>
		<link rel="stylesheet" href="resources/css/combined-minified.css" type="text/css" media="screen"/>
		
		<script type="text/javascript">
		
		jQuery(document).ready(function(){
			tinyMCE.init({
				mode : "none",
				entity_encoding : "raw",
	            editor_selector : "mcesimple",
				theme : "simple"
			});

  
			var tenantManager = {
				    getTenant: function (token) {
				        var url = '/crafter-profile/api/2/tenant/get/craftercms.json'; 
				        var data = {'appToken' : token};
						return $.ajax({
								    url: url,
								    data: data,
								    dataType : 'json',
								    contentTypeString:"application/json;charset=UTF-8",
								    cache: false,
								    type: 'GET',
								    success: function(aData, textStatus, jqXHR){
								    	tenantId = aData.id;
								    },
								    error: function(jqXHR, textStatus, errorThrown) {
								    	$.error('Could not load tenant: ' + textStatus + ' - ' + errorThrown);
								    }
								});
				    },
				    getParameterByName: function(name) {
	    				var match = RegExp('[?&]' + name + '=([^&]*)')
	                    	.exec(window.location.search);
	    				return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
					}
				};
			
			var profilesManager = {
			    getToken: function (id) {
			        var url = '/crafter-profile/api/2/auth/app_token.json'; 
					var data = {'username' : 'craftersocial', 'password' : 'craftersocial'};
					return $.ajax({
							    url: url,
							    data: data,
							    dataType : 'json',
							    contentTypeString:"application/json;charset=UTF-8",
							    cache: false,
							    type: 'GET'
							});
			    },
			    getParameterByName: function(name) {
    				var match = RegExp('[?&]' + name + '=([^&]*)')
                    	.exec(window.location.search);
    				return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
				}
			};
			profilesManager.getToken("history").done(function(data1){
				 
				 var userName = RegExp('[?&]' + 'username' + '=([^&]*)').exec(window.location.search);
				userName= userName && decodeURIComponent(userName[1].replace(/\+/g, ' '));
			    
				var password = RegExp('[?&]' + 'password' + '=([^&]*)').exec(window.location.search);
				password= password && decodeURIComponent(password[1].replace(/\+/g, ' '));
				tenantManager.getTenant(data1).done(function(data2) {
				   
					if (userName !=null && password!=null) {
						var url = '/crafter-profile/api/2/auth/ticket.json'; 
						var data = {'appToken' : data1,'username' : userName, 'password' : password,'tenantId':data2.id};
						$.ajax({
								    url: url,
								    data: data,
								    dataType : 'json',
								    contentTypeString:"application/json;charset=UTF-8",
								    cache: false,
								    type: 'GET',
								    success: function(aData, responseStatus, object){
								    	jQuery('#ugc_div').ugc_blog_publish({
											clientId : 'CrafterCMS',
											restUrl : 'api/2',
											resourceUrl : 'resources', 
											target : 'http://www.craftercms.org',
											ticket 	:aData
											}).ugc_blog_publish('loadUGCBlog');
								    	//profileData.ticket = aData;
								    },
								    error: function(jqXHR, textStatus, errorThrown) {
								    	$.error('Could not load ugc blog: ' + textStatus + ' - ' + errorThrown);
								    }
								});
					} else {
						$.error('Could not load ugc blog: ' + textStatus + ' - ' + errorThrown);
					}
				});
			});

			
		
			
		});
		</script>
	</body>
</html>