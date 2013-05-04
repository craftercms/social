
function namespace() {
    var a=arguments, o=null, i, j, d;
    for (i=0; i<a.length; i=i+1) {
        d=a[i].split(".");
        o=window;
        for (j=0; j<d.length; j=j+1) {
            o[d[j]]=o[d[j]] || {};
            o=o[d[j]];
        }
    }
    return o;
};


namespace("crafter.social.common");
crafter.social.common = function () {	
	
	function doSendPost(url, postData, successCallback) {
		YAHOO.util.Connect.asyncRequest('POST', url, {
			cache:false,
			success: successCallback,
			failure: function(o) {
				alert('Error: ' + o.statusText);
			}
		}, postData);
	}
	
	return {
		getElem: function(id) {
			return YAHOO.util.Dom.get(id);
		},
		
		sendGet: function(url, successCallback) {
			YAHOO.util.Connect.asyncRequest('GET', url, {
				cache:false,
				success: function(o) {
					successCallback(o.responseText);
				},
				failure: function(o) {
					alert('Error: ' + o.statusText);
				}
			});
		},
		
		sendPost: function(url, postData, successCallback) {
			YAHOO.util.Connect.setDefaultPostHeader(true);
			doSendPost(url, postData, successCallback);
		},
		
		sendPostText: function(url, postData, successCallback) {
			YAHOO.util.Connect.setDefaultPostHeader(false);
			YAHOO.util.Connect.initHeader("Content-Type", "text/plain", false);
			doSendPost(url, postData, successCallback);
		},
		
		sendPostJson: function (url, postData, successCallback) {
			YAHOO.util.Connect.setDefaultPostHeader(false);
			YAHOO.util.Connect.initHeader("Content-Type", "application/json", false);
			doSendPost(url, postData, successCallback);
		},
		
		trim: function (string) {
			return YAHOO.lang.trim(string);
		},
		
		getJsonStr: function (obj) {
			return YAHOO.lang.JSON.stringify(obj);
		},
		
		getJson: function(str) {
			return YAHOO.lang.JSON.parse(str);
		},
		
		sendGetJson: function(url, successCallback) {
			crafter.social.common.sendGet(url, function(responseText) {
				var jsonObj;
				
				jsonObj = crafter.social.common.getJson(responseText);
				successCallback(jsonObj);
			});
		},
	};
}();






