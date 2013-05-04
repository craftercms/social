<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${!ajaxRequest}">
<html>
<head>
	<title>fileupload | mvc-showcase</title>
	<link href="<c:url value="/resources/form.css" />" rel="stylesheet"  type="text/css" />		
	<script type="text/javascript" src="<c:url value="/resources/js/jquery-1.7.1.js" />"></script>
	<script type="text/javascript" src="<c:url value="/resources/js/jquery.form.js" />"></script>	
</head>
<body>
</c:if>
	<div id="fileuploadContent">
		<h2>File Upload</h2>
		<p>
			See the <code>org.springframework.samples.mvc.fileupload</code> package for the @Controller code	
		</p>
		<form id="fileuploadForm" action="api/1/ugc/create?ticket=a2VvTWF6ZzQwNzZPM2RpUnIwSWg4UT09OjZIN2tNbDlzeldTOTdxMjlVMXF0K3c9PQ" method="POST" enctype="multipart/form-data" class="cleanform">
			<div class="header">
		  		<h2>Form</h2>
		  		<c:if test="${not empty message}">
					<div id="message" class="success">${message}</div>	  		
		  		</c:if>
			</div>
			<h1>Files:</h1>
			<!--input id="attachments" type="file" name="attachments" /-->
			<div id="attachments-name"></div>
			
			<input type="hidden" name="target" value="http://www.craftercms.org" />
			<input type="hidden" name="textContent" value="contenttesting" />
			<!-- <input type="hidden" name="ticket" value="M2RMdlJjSEdSMlVjeVBHVFd5cXVCQT09OkU0d25DL2lvYXBGSmVRcjFLR2xFakE9PQ" /> -->
			<p><button type="submit">Upload</button></p>
			<p><button id="uploadAnother">Upload Another</button></p>				
			<div id="insertAnother" style="display:none;"></div>
		</form>
		<script type="text/javascript">
			$(document).ready(function() {
				$("#fileuploadForm").ajaxForm({ success: function(html) {
						$("#fileuploadContent").replaceWith(html);
					}
				});
				// $("#uploadAnother").click(event) { 
		 	// 		$('<label for="file">File</label>
		 	// 		<input id="attachments" type="file" name="attachments" />').insertAfter($("#insertAnother"));
				// 	return false;
		 	// 	};
		 		//$("#uploadAnother").click(function() {
		 		$("#uploadAnother").live("click", function() {
		 			//$('<input id="attachments" type="file" name="attachments"/>').appendTo($("#insertAnother"));
		 			var t = document.createElement("input");
		 			t.type = "file";
		 			t.name = "attachments";
		 			t.id = "attachments";
		 			// t.change(function(e) {
		 			// 	alert("CHANGED");
		 			// });
		 			t.onchange = function(e) {
		 				var fileName = document.createElement("p");
		 				$("#attachments-name").append(fileName);
		 				var text = this.value + "<br/>";
		 				fileName.outerHTML = text;
		 				
		 			}

		 			t.click();
		 			
		 			$("#insertAnother").append(t);
		 			// var fileName = document.createElement("h1");
		 			// var text = t.value;
		 			// fileName.textContent(text);
		 			// $("#attachments-name").append(fileName);
					return false;
				});
			});
		</script>
	</div>
<c:if test="${!ajaxRequest}">
</body>
</html>
</c:if>