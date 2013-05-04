
namespace("crafter.social.moderation");
crafter.social.moderation = function() {
	var HTML_VIEW_MORE_FORM = 
		"<div id='moderation-detail-container'>" +
			"<table>" +
				"<tr>" +
					"<td class='form-label'>Id:</td>" +
					"<td><div id='moderation-detail-id'></div></td>" +
				"</tr>" +
				"<tr>" +
					"<td class='form-label'>ParentId:</td>" +
					"<td><div id='moderation-detail-parentId'></div></td>" +
				"</tr>" +
				"<tr>" +
					"<td class='form-label'>Target:</td>" +
					"<td><div id='moderation-detail-target'></div></td>" +
				"</tr>" +
				"<tr>" +
					"<td class='form-label'>Author:</td>" +
					"<td><div id='moderation-detail-profileId'></div></td>" +
				"</tr>" +
				"<tr>" +
					"<td class='form-label'>Content:</td>" +
					"<td><div class='text-content-detail' id='moderation-detail-content'></div></td>" +
				"</tr>" +
				"<tr>" +
					"<td class='form-label'>Attachments:</td>" +
					"<td><div id='moderation-detail-attachment'></div></td>" +
				"</tr>" +
				"<tr>" +
					"<td class='form-label'>Times moderated:</td>" +
					"<td><div id='moderation-detail-timesModerated'></div></td>" +
				"</tr>" +
				"<tr>" +
					"<td class='form-label'>Like count:</td>" +
					"<td><div id='moderation-detail-likesCount'></div></td>" +
				"</tr>" +
				"<tr>" +
					"<td class='form-label'>Dislike count:</td>" +
					"<td><div id='moderation-detail-offenceCount'></div></td>" +
				"</tr>" +
				"<tr>" +
					"<td class='form-label'>Flag count:</td>" +
					"<td><div id='moderation-detail-flagCount'></div></td>" +
				"</tr>" +
				"<tr>" +
					"<td class='form-label'>Submitted on:</td>" +
					"<td><div id='moderation-detail-submittedOn'></div></td>" +
				"</tr>" +
			"</table>" +
		"<div>";
	var moderationDataTable = null;
	
	
	function approveOrReject(row, status, confirmMsg) {
		var confirmed;
		
		confirmed = true;
		
		if (confirmMsg != null) {
			confirmed = confirm(confirmMsg);
		}
		
		if (confirmed) {
			var ugc;
			
			ugc = moderationDataTable.getRecord(row).getData();
			
			crafter.social.common.sendPostText(crafter.social.config.ugcUrl + ugc.id + "/moderationstatus", status, function() {
				moderationDataTable.deleteRow(row.id);
			});
		}
	}
	
	function approveOrRejectSeveral(status, confirmMsg) {
		var row;
		
		row = moderationDataTable.getFirstTrEl();
		
		if (row) {
			var rowsToProcess;
			
			rowsToProcess = new Array();
			
			do {
				
				if (row.children[0].children[0].children[0].checked) {
					rowsToProcess.push(row);
				}
				
				row = moderationDataTable.getNextTrEl(row);			
			} while (row != null);
			
			if (rowsToProcess.length > 0 && confirm(confirmMsg)) {
				
				for (var i = 0; i < rowsToProcess.length; ++i) {
					approveOrReject(rowsToProcess[i], status);
				}
				
				crafter.social.common.getElem("crafter.social.moderation-checkall").checked = false;
			}
		}
	}
	
	function loadTextThread(container, ugcId) {
		crafter.social.common.sendGetJson(crafter.social.config.ugcUrl + "thread/" + ugcId + ".json", function(jsonObj) {
			var html;
			var i;
			
			html = "<div class='thread-title'>Thread</div>";
			i = 0;
			jsonObj = jsonObj.UGC;
			
			while (jsonObj && jsonObj.id != ugcId) {
				html += "<div class='thread-item' style='margin-left:" + ((i+1) * 10) + "px;'>";
					html += "<div  class='text-thread'>" + jsonObj.textContent + "</div>";
					html += "<label class='form-label'>Author:</label>&nbsp;" + jsonObj.profileId;
					html += "&nbsp;&nbsp;&nbsp;&nbsp;<label class='form-label'>Submitted on:</label>&nbsp;" +  new Date(jsonObj.dateAdded);
				html += "</div>";
				jsonObj = jsonObj.children[0];
				i++;
			}
			
			container.innerHTML = html;
			container.style.display = 'block';
		});
	}
	
	function showAuditDialog(title, formBody, formId, alignmentObj) {
		var dialog;
		
		dialog = new YAHOO.widget.Dialog(formId, {
			context: [alignmentObj, "tl", "br"],
			width: "420px",
			height: "240px",
			visible: false,
			underlay: "shadow",
			close: false,
			zIndex: 65000,
			buttons: [
			   {text: "Close", isDefault: true, handler: function() {this.destroy();} }
			]
		});
		
		formBody = "<div class='audit-container'>" + formBody + "</div";
		
		dialog.setHeader(title);
		dialog.setBody(formBody);
		dialog.render(document.body);
		dialog.show();
	}
	
	return {
		viewMore: function(row) {
			var dialog;
			var ugc;
			var attachmentId;
			
			dialog = new YAHOO.widget.Dialog("moderationDetail", {
				visible: false,
				underlay: "shadow",
				close: false,
				buttons: [
				   {text: "Close", isDefault: true, handler: function() {this.destroy();} }
				]
			});
			
			dialog.setHeader("Detail");
			dialog.setBody(HTML_VIEW_MORE_FORM);
			dialog.render(document.body);
			
			ugc = moderationDataTable.getRecord(row).getData();
			crafter.social.common.getElem("moderation-detail-id").innerHTML = ugc.id;
			
			if (ugc.parentId) {
				crafter.social.common.getElem("moderation-detail-parentId").innerHTML = ugc.parentId;
			}
			
			crafter.social.common.getElem("moderation-detail-target").innerHTML = "<textarea rows=1 cols=80 readonly=true>" + ugc.targetId + "</textarea>";
			crafter.social.common.getElem("moderation-detail-profileId").innerHTML = ugc.profileId;
			
			if (ugc.textContent) {
				crafter.social.common.getElem("moderation-detail-content").innerHTML = ugc.textContent;
			}
			
			attachmentId = ugc.attachmentId;
			
			if (attachmentId && attachmentId.length > 0) {
				var html;
				
				html = "";
				
				for (var i = 0; i < attachmentId.length; ++i) {
					html += "<img src='resources/img/attachment.png' title='Show attachment " + (i+1) + "' onclick='crafter.social.moderation.showAttachement(\"" + attachmentId[i] + "\");'>&nbsp;&nbsp;";
				}
				
				crafter.social.common.getElem("moderation-detail-attachment").innerHTML = html;						
			}
			
			crafter.social.common.getElem("moderation-detail-timesModerated").innerHTML = ugc.timesModerated;
			
			if (ugc.offenceCount > 0) {
				crafter.social.common.getElem("moderation-detail-offenceCount").innerHTML = "<a href='#' onclick=\"crafter.social.moderation.showDislikes(this, '" + ugc.id + "');\">" + ugc.offenceCount + "</a>";
				
			} else {
				crafter.social.common.getElem("moderation-detail-offenceCount").innerHTML = "0";
			}
			
			if (ugc.likeCount > 0) {
				crafter.social.common.getElem("moderation-detail-likesCount").innerHTML = "<a href='#' onclick=\"crafter.social.moderation.showLikes(this, '" + ugc.id + "');\">" + ugc.likeCount + "</a>";
				
			} else {
				crafter.social.common.getElem("moderation-detail-likesCount").innerHTML = "0";
			}
			
			if (ugc.flagCount > 0) {
				crafter.social.common.getElem("moderation-detail-flagCount").innerHTML = "<a href='#' onclick=\"crafter.social.moderation.showFlags(this, '" + ugc.id + "');\">" + ugc.flagCount + "</a>";
				
			} else {
				crafter.social.common.getElem("moderation-detail-flagCount").innerHTML =	"0";
			}
			
			crafter.social.common.getElem("moderation-detail-submittedOn").innerHTML =  new Date(ugc.dateAdded);
			
			dialog.center();
			dialog.show();
		},
		
		showAttachement: function(attachmentId) {
			window.open(crafter.social.config.baseUrl + "ugc_attachment/" + attachmentId, 'attachment');
		},
		
		toogleAllSelection: function(status) {
			var row;
			
			row = moderationDataTable.getFirstTrEl();
			
			if (row) {
				
				do {
					var checkBox;
					
					checkBox = row.children[0].children[0].children[0];
					checkBox.checked = status;
					
					row = moderationDataTable.getNextTrEl(row);			
				} while (row != null);
			}
		},
		
		rejectOne: function(row) {
			approveOrReject(row, "REJECTED", "Do you want to reject this?");
		},
		
		approveOne: function(row) {
			approveOrReject(row, "APPROVED", "Do you want to approve this?");
		},
		
		rejectSeveral: function() {
			approveOrRejectSeveral("REJECTED", "Do you want to reject these?");
		},
		
		approveSeveral: function() {
			approveOrRejectSeveral("APPROVED", "Do you want to approve these?");
		},
		
		
		toogleContentView: function(obj, ugcId) {
			
			if (obj.getAttribute("class") == "text-content-max") {
				obj.parentNode.firstChild.style.display = 'none';
				obj.setAttribute("class", "text-content-min");
				
			} else {
				var threadContainer;
				
				obj.setAttribute("class", "text-content-max");
				
				threadContainer = obj.parentNode.firstChild;
				
				if (threadContainer.innerHTML == "<br>") {
					loadTextThread(threadContainer, ugcId);
					
				} else {
					threadContainer.style.display = 'block';
				}
			}
		},
		
		showLikes: function(obj, ugcId) {
			crafter.social.common.sendGetJson(crafter.social.config.baseUrl + "audit/" + ugcId + "/LIKE.json", function(auditList) {
				var html;
				
				auditList = auditList.UGCAuditList;
				html = "";
				
				for (var i = 0; i < auditList.length; ++i) {
					html += "<div class='audit-item'>" +  auditList[i].profileId + " <span class='form-label'>liked on</span> " +  new Date(auditList[i].dateCreated) + "</div>";
				}
				
				showAuditDialog("Likes", html, "moderationLikes", obj);
			});
		},
		
		showDislikes: function(obj, ugcId) {
			crafter.social.common.sendGetJson(crafter.social.config.baseUrl + "audit/" + ugcId + "/DISLIKE.json", function(auditList) {
				var html;
				
				auditList = auditList.UGCAuditList;
				html = "";
				
				for (var i = 0; i < auditList.length; ++i) {
					html += "<div class='audit-item'>" +  auditList[i].profileId + " <span class='form-label'>disliked on</span> " +  new Date(auditList[i].dateCreated) + "</div>";
				}
				
				showAuditDialog("Dislikes", html, "moderationDislikes", obj);
			});
		},
		
		showFlags: function(obj, ugcId) {
			crafter.social.common.sendGetJson(crafter.social.config.baseUrl + "audit/" + ugcId + "/FLAG.json", function(auditList) {
				var html;
				
				auditList = auditList.UGCAuditList;
				html = "";
				
				for (var i = 0; i < auditList.length; ++i) {
					html += "<div class='audit-item'>";
						html +=  auditList[i].profileId + " <span class='form-label'>flagged on</span> " +  new Date(auditList[i].dateCreated);
						html += "<br/><span class='form-label'>Reason: </span>" + auditList[i].reason;
					html += "</div>";
				}
				
				showAuditDialog("Flags", html, "moderationFlags", obj);
			});
		},
		
		loadList: function(restCall) {
			var jsonDS;
			var columnDefs;
			var html;
			
			function contentFormatter (elCell, oRecord, oColumn, oData) {
				var html;
				var attachmentId;
				var targetId;
				var targetNew;
				
				html = "";
				html += "<div class='thread-container' onclick='crafter.social.moderation.toogleContentView(this.parentNode.childNodes[1],\"" + oRecord.getData().id + "\");'><br></div>";
				html += "<div class='text-content-min' onclick='crafter.social.moderation.toogleContentView(this,\"" + oRecord.getData().id + "\");'>" + oRecord.getData().textContent + "</div>";
				attachmentId = oRecord.getData().attachmentId;
				html += "<label class='form-label'>Attachments:</label>&nbsp;";
					
				for (var i = 0; i < attachmentId.length; ++i) {
					html += "<img src='resources/img/attachment.png' title='Show attachment " + (i+1) + "' onclick='crafter.social.moderation.showAttachement(\"" + attachmentId[i] + "\");'>&nbsp;&nbsp;";
				}
					
				html += "<br/>";
				html += "<img class='audit-img' src='resources/img/like.png' title='Likes'>&nbsp;";
				
				if (oRecord.getData().likeCount > 0) {
					html += "<a href='#' onclick=\"crafter.social.moderation.showLikes(this, '" + oRecord.getData().id + "');\">" + oRecord.getData().likeCount + "</a>";
					
				} else {
					html +=	"0";
				}
				
				html += "&nbsp;&nbsp;<img class='audit-img' src='resources/img/dislike.png' title='Dislikes'>&nbsp;";
				
				if (oRecord.getData().offenceCount > 0) 
					html += "<a href='#' onclick=\"crafter.social.moderation.showDislikes(this, '" + oRecord.getData().id + "');\">" + oRecord.getData().offenceCount + "</a>";
				
				else {
					html +=	"0";
				}
				
				html += "&nbsp;&nbsp;<img class='audit-img' src='resources/img/flagred.png' title='Flags'>&nbsp;";
				
				if (oRecord.getData().flagCount > 0) {
					html += "<a href='#' onclick=\"crafter.social.moderation.showFlags(this, '" + oRecord.getData().id + "');\">" + oRecord.getData().flagCount + "</a>";
					
				} else {
					html +=	"0";
				}
				
				html += "<br/><label class='form-label'>Author:</label>&nbsp;" + oRecord.getData().profileId;
				html += "&nbsp;&nbsp;&nbsp;&nbsp;<label class='form-label'>Submitted on:</label>&nbsp;" +  new Date(oRecord.getData().dateAdded);
				
				targetId = oRecord.getData().targetId;
				targetNew = targetId;
				
				if (targetId.length >= 150) {
					targetNew = targetId.substr(0, 130) + " ... " + targetId.substr(targetId.length - 15);
				} 
				
				html += "<br/><label class='form-label'>Target:</label>&nbsp;<span title='" + targetId + "'>" + targetNew + "</span>";
				
				elCell.innerHTML = html;
			}
			
			function actionsFormatter (elCell, oRecord, oColumn, oData) {		
				elCell.innerHTML = 
					"<img class='toolbar-btn' src='resources/img/approve.png' title='Approve' onclick='crafter.social.moderation.approveOne(this.parentElement.parentElement.parentElement);'>" +
					"<img class='toolbar-btn' src='resources/img/reject.png' title='Reject' onclick='crafter.social.moderation.rejectOne(this.parentElement.parentElement.parentElement);'>" +
					"<img class='toolbar-btn' src='resources/img/viewmore.png' title='View more' onclick='crafter.social.moderation.viewMore(this.parentElement.parentElement.parentElement);'>";
			}
			
			jsonDS = new YAHOO.util.XHRDataSource(restCall);
			jsonDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
			jsonDS.connTimeout = crafter.social.config.requestTimeout;
			jsonDS.connXhrMode = crafter.social.config.requestManagement;
			jsonDS.responseSchema = {
			    resultsList : "UGCList",
			    fields: [
			      {key: "id" },
			      {key: "parentId" },
			      {key: "textContent"},
			      {key: "targetId"},
			      {key: "attachmentId"},
			      {key: "moderationStatus"},
			      {key: "timesModerated"},
			      {key: "likeCount"},
			      {key: "offenceCount"},
			      {key: "flagCount"},
			      {key: "profileId"},
			      {key: "dateAdded"}
			    ]
			};

			html = "";
			html += "<input id='crafter.social.moderation-checkall' type='checkbox' onclick='crafter.social.moderation.toogleAllSelection(this.checked)'/>";
			html += "<img class='toolbar-btn' src='resources/img/approve.png'  title='Approve' onclick='crafter.social.moderation.approveSeveral();'>";
			html += "<img class='toolbar-btn' src='resources/img/reject.png' title='Reject' onclick='crafter.social.moderation.rejectSeveral();'>";	
			
			columnDefs = [
			    {label: html, formatter:"checkbox", width: 65},
			    {label:"Actions", formatter:actionsFormatter, width: 35},
			    {key:"textContent", label:"Content", formatter:contentFormatter}			    
			];
			
			if (moderationDataTable) {
				moderationDataTable.destroy();
				moderationDataTable = null;
			}
			
			moderationDataTable = new YAHOO.widget.DataTable("index-list", columnDefs, jsonDS, {
				paginator : new YAHOO.widget.Paginator({
					rowsPerPage: 3
				})
			});
		}
	};
}();
