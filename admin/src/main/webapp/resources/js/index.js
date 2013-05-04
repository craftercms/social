
namespace("crafter.social.index");
crafter.social.index = function () {
	var layout;
	
	return {
		loadHTMLinLayoutUnit: function (htmlURL, unitPosition, onceLoadedCallback) {
			crafter.social.common.sendGet(htmlURL, function(responseText) {
				var unit;
				
				unit = layout.getUnitByPosition(unitPosition);
				unit.set("body", responseText, false);
				
				if (onceLoadedCallback) {
					onceLoadedCallback();
				}
			});
		},
		
		loadLayout: function () {
			layout = new YAHOO.widget.Layout({
				units: [
		            {position: 'center', body: 'index-center', scroll: true},
					{position: 'bottom', body: 'index-footer', height: "20"}
		        ]
			});
			layout.render();
		},
		
		showModeration: function() {
			crafter.social.moderation.loadList(crafter.social.config.ugcUrl + "moderationstatus/PENDING.json");
		}
	};
}();


