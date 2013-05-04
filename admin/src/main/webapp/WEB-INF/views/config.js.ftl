namespace("crafter.social.config");
crafter.social.config = function () {
	return {
		requestTimeout: ${requestTimeout},
		requestManagement: "${requestManagement}", 
		host: "${host}",
		apiUrl: "${apiUrl}",
		baseUrl: "${host}${apiUrl}",
		ugcUrl: "${host}${apiUrl}${ugcPath}"
	};
}();