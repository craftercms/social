(function( $ ) {
	$.ajaxSetup({
        error: function(jqXHR, exception) {
            if (jqXHR.status === 0) {
                alert('Not connect.\n Verify Network.');
            } else if (jqXHR.status == 401) {
            	alert('Service Permission not allowed');
            } else if (jqXHR.status == 403) {
               jqXHR.status = -1;
               console.error('Invalid ticket. Sign in again: ' + jqXHR.statusText);
               window.location = "login";
            }  else if (jqXHR.status == 404) {
            	jqXHR.status = -1; //permission not available
                console.error('Invalid ticket. Sign in again: ' + jqXHR.statusText);
                window.location = "login";
            } else if (jqXHR.status == 500) {
                alert('Internal Server Error.');
            } else if (jqXHR.status == 400) {
                jqXHR.status = -1;
                console.error('Invalid ticket. Sign in again: ' + jqXHR.statusText);
             } else if (exception === 'parsererror') {
                alert('Requested JSON parse failed.');
            } else if (exception === 'timeout') {
                alert('Time out error.');
            } else if (exception === 'abort') {
                alert('Ajax request aborted.');
            } else {
                alert('Uncaught Error.\n' + jqXHR.responseText);
                
            }
        }
    });
	var methods = {
		init : function( pOptions ) {
			return this.each(function() {
				var $this = $(this),
					options = $this.data('ugc_blog_post');

				// If the plugin hasn't been initialized yet
				if ( ! options ) {
					/*
					 * Do more setup stuff here
					 */
					var settings = $.extend({
						'restUrl' : '/crafter-social/api/2',
						'resourceUrl' : '/crafter-social/resources',
						'outputType' : 'json',
						'target' : 'http://www.google.com',
						'targetJQObj' : $this,
						'date-format' : 'ddd mmm dd yyyy HH:MM:ss', 
						'css-class' : 'ugc_blog_post',
						'ticket' 	:'default',
						'pageSize' : 10,
						'parentId' : '',
						'childrenPageSize' : 10 
					}, pOptions);

					$(this).data('ugc_blog_post', settings);
					
					$this.append("Loading...");
					
					$.view().context({
						formattedDate: function( timestamp ) {
							return new Date(timestamp).format(settings.dateFormat);
						},
						getSmallContent: function(content) {
							if (content.length > 1024) {
								content = content.substr(0, 1024);
							}
							return content;
							
						},
						anonymousIfNull: function( text ) {
							return (text)?text:"Anonymous";
						}

					});
					
					$.get(settings.resourceUrl + '/templates/templates_blog_post.html', function (data, textStatus, jqXHR) {
						var $data = $(data);
						
						$data.each(function (){
							if (this.id && this.type === "text/x-jquery-tmpl") {
								$.template(this.id, this.innerHTML);
							}
						});

						settings.templatesLoaded = true;
					});
				}
			});

		},
		
		destroy : function( ) {
			return this.each(function(){
				var $this = $(this),
					options = $this.data('ugc_blog_post');
				
				// Namespacing FTW
				$(window).unbind('.ugc_blog_post');
				$this.removeData('ugc_blog_post');
				$this.empty();
				
				if (options && options.ellapseTimer) {
					window.clearInterval(options.ellapseTimer);
					options.ellapseTimer = null;
				}
			});
		},

		loadUGCBlogPost : function ( ) {
			return this.each(function () {
				var $this = $(this),
					options = $this.data('ugc_blog_post');

				if ( options ) {
					var url = options.restUrl + '/ugc/target.' + options.outputType; 
					var data = {ticket : options.ticket, 'target' : options.target, 'tenant' : options.tenant};
					$.ajax({
					    url: url,
					    data: data,
					    dataType : options.outputType,
					    contentTypeString:"application/json;charset=UTF-8",
					    cache: false,
					    type: 'GET',
					    success: function(aData, textStatus, jqXHR){
					    	$this.empty();
					    	util.updateEllapsedTimeText(aData.list);
					    	aData.settings=options;
					    	util.renderUGCBlogPost(aData, options, $this);
					    }
					});
				}
			});
		}
		
	};
	
	var second=1000;
	var minute=second*60;
	var hour=minute*60;
	var day=hour*24;
	var month=day*30;
	var year=day*365;
	
	var ellapseUpdateInterval = 5000;
	var profileData = {
						token:"",
						ticket:""
						};
	
	var util = {
		renderUGCBlogPost : function (data, options, container) {
			if (options.templatesLoaded) {
				for (i = 0; i<data.list.length;i++) {
					var content = data.list[i].textContent;
					if (content.substr(0,1) == '{') {
						jsonObj = $.parseJSON(content);
						
						data.list[i].textContent = jsonObj;	
					} 
				}
				try {
					container.html($.render( data, 'ugcListTmpl')).link(data);
					//util.wireAuths(container, options);
					util.wireAuths($('> div > ul.page-actions', container),options);
				} catch(e) {

					console.error('There was an error loading the template: ' + e);
				}
				container.options = options;
				container.on( "click", "#detailPostBtn", function(event) {
					options = container.options;

					if ( options ) {
						options.parentId = event.target.name;
						var url = options.restUrl + '/ugc/get_ugc/' + event.target.name+ "." +options.outputType; 
						var data = {ticket : options.ticket, 'tenant' : options.tenant, 'ticket' : options.ticket};
						$.ajax({
						    url: url,
						    data: data,
						    dataType : options.outputType,
						    contentTypeString:"application/json;charset=UTF-8",
						    cache: false,
						    type: 'GET',
						    success: function(aData, textStatus, jqXHR){
						    	container.empty();
						    	$("body").attr("class", "perm");
						    	util.updateEllapsedTimeText(aData.children);
						    	aData.settings=options;
						    	util.renderUGCBlogPostDetail(aData, options, container);
						    }
						});
					}
						
				});
				util.scheduleTimeUpdates(options, data.list);
			} else {
				setTimeout(function() {util.renderUGCBlogPost(data, options, container);} , 200);
			}
		},

		renderUGCBlogPostDetail : function (data, options, container) {
			if (options.templatesLoaded) {
				var content = data.textContent;
				if (content.substr(0,1) == '{') {
					jsonObj = $.parseJSON(content);
					data.textContent = jsonObj;
				} 
				try {
					container.html($.render( data, 'ugcDetailTmpl')).link(data);
					//util.wireAuths(container, options);
					util.wireAuths($('> header > div > ul.page-actions', container), options);
				} catch(e) {
					console.error('There was an error loading the template: ' + e);
				}
				container.options = options;
				
				var $ugcDiv = $(' > div > div.ugc-width-medium > form > div.ugc-widget > div.ugc-comment-stream > div.ugc-stream-content', container);

				container.on( "click", "#attachmentOneBtn", function(event) {
					var url = options.restUrl + '/get_attachment/' + event.target.name+"?ticket=" + options.ticket + "&tenant=" + options.tenant;
					location.href = url;
						
				});
				
				util.assignPermissions('CREATE',$(' > div > div.ugc-width-medium  > form > div.ugc-widget > div.ugc-editor', container), data.id, options);
				
				//var $postAttachmentButton = $(' > div > form > div.ugc-width-medium > div.ugc-widget > div.ugc-editor > div.fyre-editor-toolbar > div.goog-toolbar > div.fyre-attachment-button > div.post-attach-btn',container);
				var $form = $(' > div > div.ugc-width-medium > form',container);

				$(' > div > div.ugc-width-medium > form > div.ugc-widget > div.ugc-editor > div.fyre-editor-toolbar > div.goog-toolbar > div.fyre-attachment-button > div.post-attach-btn', container).click(function (event) {
					//<input type="file" multiple accept='image/*|audio/*|video/*' > //http://stackoverflow.com/questions/1561847/html-how-to-limit-file-upload-to-be-only-images
					var parentFileContent = $('<div></div>');
					var newFileContent = $('<div style="display:none;"></div>');
					var inputFile = document.createElement("input");
		 			inputFile.type = "file";
		 			inputFile.name = "attachments";
		 			inputFile.id = "attachments";
		 			inputFile.onchange = function(e) {

		 				var fileContent = $(this.parentElement.parentElement);
		 				var fileName = this.value.replace("C:\\fakepath\\","");
		 				removeFile = $('<a class="MultiFile-remove" href="#">x</a>'), 

		 				file2 = e.originalTarget.files[0];
		 				if (file2.type.startsWith("image")) {
	        				oFReader = new FileReader();
	        				oFReader.readAsDataURL(file2);
	        				oFReader.onload = function (oFREvent) {
					            imagePreview = $('<img style="max-height:100px;max-width:100px" class="image-preview" src="'+oFREvent.target.result+'"></img>');
					            $("#attachments-list").append(
			         				fileContent.append(removeFile, " ", imagePreview));
					        };
					    } else {
							fileSpan = $('<span class="MultiFile-title" title="'+fileName+'" id="'+fileName+' name="'+fileName+'">'+fileName+'</span>');

		 				
		         			$("#attachments-list").append(
		         				fileContent.append(removeFile, " ", fileSpan)
		         				);
		         		}
	         			
						removeFile.click(function(){
							var parent = $(this.parentElement);
							parent.remove();
						});
		 			}
		 			$("#attachments-list").append(
		 				parentFileContent.append(newFileContent.append(inputFile))
		 				);
		 			inputFile.click();
		 			return false;
				});

				
				$('article.ugc-comment-article', $ugcDiv).each(function () {
					util.wireUpUGC.apply( this, [ options ]);
				});

				container.on( "click", "#post-comment", function(event) {
					var body = $("#textContentField").val();
					//$("#textContentField")[0].value = "";
					options.container = $ugcDiv;
					options.attachments = $("#attachments-list");
					options.removeParent = false;
					util.addTextUGC(body, container.options.parentId, options,  $ugcDiv,$("#attachments-list"),$form, false);
						
				});
				
				$('#hidden_upload').load(function(e){
					$("#textContentField")[0].value = "";
					var myIFrame = document.getElementById("hidden_upload");
					if (myIFrame.contentWindow.document.childNodes[0].childNodes.length == 0) {
						console.error('Social server response unexpected');
						window.location = "login"; 
						return;
					}
					var newJson= $.parseJSON(myIFrame.contentWindow.document.childNodes[0].textContent);
					var idUgc = null;
					if (newJson) {
						idUgc = newJson.id;
					} else {
						idUgc = myIFrame.contentWindow.document.childNodes[0].children[0].textContent;
					}
				    var url = options.restUrl + '/ugc/get_ugc/' + idUgc + '.' + options.outputType;
				    
					var data = {ticket : options.ticket, tenant : options.tenant};
				    $.ajax({
					    url: url,
					    data: data,
					    dataType : options.outputType,
					    cache: false,
					    type: 'GET',
					    success: function(aData, textStatus, jqXHR){
					    	
					    	if (aData) {
					    		
					    		util.updateEllapsedTimeText([aData]);
							    util.observableAddUGC.apply(options.container, [aData]);
							    util.wireUpUGC.apply( $('#ugc-message-'+aData.id, options.container), [options]);
					    	}
					    	//removeExtraInputs();
					    	while (options.attachments.children().length>0) {
					    		$attachObj = $(options.attachments.children()[0]);
					    		$attachObj.remove();
					    	}
					    	while ($("#attachments-list").children().length>0) {
					    		$attachObj = $($("#attachments-list").children()[0]);
					    		$attachObj.remove();
					    	}
					    	if (options.removeParent) {
					    		options.parentContainer.remove();
					    	}
							
					    	
					    }
					});
					
	   			});
				
				util.scheduleTimeUpdates(options, data.children);
				
			} else {
				setTimeout(function() {util.renderUGCBlogPostDetail(data, options, container);} , 200);
			}
		},

		addTextUGC : function (body, parentId, options, appendTo,attachments, container, removeContainer) {
			if ( options ) {
				if (attachments ==null || attachments.children().length == 0) {	
					if (body != null && body.length > 0) {
						
						var dataTextContent = body;
						var url = options.restUrl + '/ugc/create' + '.' + options.outputType + util.getActionsParams(options.actions);
						var data = {'target' : options.target, 'textContent' : dataTextContent, ticket : options.ticket, 'tenant' : options.tenant};
						 if(parentId){
							 data.parentId=parentId;
						 }
						$.ajax({
						    url: url,
						    data: data,
						    dataType : options.outputType,
						    cache: false,
						    type: 'POST',
						    success: function(aData, textStatus, jqXHR){
						    	$("#textContentField")[0].value = "";
						    	if (aData) {
						    		util.updateEllapsedTimeText([aData]);
						    		util.observableAddUGC.apply(appendTo, [aData]);
						    		util.wireUpUGC.apply( $('#ugc-message-'+aData.id, appendTo), [options]);
						    	}
						    	if (container !=null && removeContainer) {
						    		container.remove();
						    	} 
						    }

						});
					}
				} else {
					util.addTextAttachmentsUGC(body, parentId, options, appendTo, attachments);
					
				}
			}
		},
		
		createHiddenInput :function(inputId, inputValue) {
			var inputField = document.createElement("input");
 			inputField.type = "hidden";
 			inputField.name = inputId;
 			inputField.id = inputId;
 			inputField.value = inputValue;
 			return inputField;
		},

		addTextAttachmentsUGC : function (body, parentId, options, appendTo, attachments) {
			$("#attachments-list").append(util.createHiddenInput("textContent",body));
 			$("#attachments-list").append(util.createHiddenInput("target",options.target));
 			var url = $("#fileuploadForm").attr("action");
 			if(url.indexOf("?ticket=") === -1) {
 				url = url +"?ticket=" + options.ticket;
 			}
            if(url.indexOf("&tenant=") === -1) {
                url = url +"&tenant=" + options.tenant;
            }
 			url += "&target=" + options.target + "&textContent=" + body;
 			if(parentId){
				$("#attachments-list").append(util.createHiddenInput("parentId",parentId));
				url += "&parentId=" + parentId;
			 }
 			
 			
 			$("#fileuploadForm").attr("action",url);
			$("#fileuploadForm").submit();
			return false;
		},

		wireAuths :  function(container, options) {
			signin = $('> li > a.signin', container);
			signout = $('> li > a.signout', container);
			console = $('> li > a.blogconsole', container);

			if (options.isAuthenticate) {
				signout[0].style.display = "block";
				signin[0].style.display = "none";
				console[0].style.display = "block";
				
				util.isValidTicket(function(result) {
					if (!result) {
						signout[0].style.display = "none";
						signin[0].style.display = "block";
						console[0].style.display = "none";
					} else {
						util.checkCreatePermissions(options, function(result) {
								if (!result) {
									console[0].style.display = "none";
								} 
						});
						util.assignPermissions('CREATE',$('> div > ul.page-actions > li > a.blogconsole', container), data.id, options);
					}
					});
			} else {
				signout[0].style.display = "none";
				signin[0].style.display = "block";
				console[0].style.display = "none";
			}
		},
		
		wireUpUGC : function (options) {
			// ID Starts with ugc-message-
			
			thisId = this.id ? this.id : this[0].id;
			var $this = $(this),
			    $actions = $(' div.ugc-comment-wrapper > footer.ugc-comment-footer > div.ugc-listen-actions-' + thisId.substring(12),$this),
				$like = $('> a.like', $actions),
				$reply = $('> a.reply', $actions),
				$flag = $('> a.flag', $actions);
			
			$like.click(function (event) {
				util.likeUGC($this.attr('ugc-id'), options, $this);
			});
			util.assignPermissions('ACT_ON',$like, $this.attr('ugc-id'), options);

			$reply.click(function (event) {
				
				util.showTextUGCDialog($this.attr('ugc-id'), options, $this, $this);
			});
			
			util.assignPermissions('CREATE',$reply, $this.attr('ugc-id'), options);
			
			$flag.click(function (event) {
				util.flagUGC($this.attr('ugc-id'), options, $this);
			});
			util.assignPermissions('ACT_ON',$flag, $this.attr('ugc-id'), options);
		},

		showTextUGCDialog : function (parentId, options, appendTo, appendUGCTo) {
			if (options.isReplying) {
				return;
			}
			options.isReplying = true;
			var data = { 'parentId' : parentId },
				html = $.render( data, 'addTextUGCTmpl' ),

				$d = $('<div>', {}).html(html),
				$addUGC = $('> div.add-ugc', $d),
				$body = $('> div > textarea', $addUGC);
				$attachments = $(' > div.ugc-editor > div.attachments-list',$addUGC);
				
			$('> div.fyre-editor-toolbar > div.goog-toolbar > div.goog-inline-block > div.post-reply-btn', $addUGC).click(function (event) {
				options.isReplying = false;
				options.container = appendUGCTo;
				options.attachments = $attachments;
				options.removeParent = true;
				options.parentContainer = $d;
				util.addTextUGC($body.val(), parentId, options,  appendUGCTo, $attachments,$d, true);
				
				return false;
			});
			
			$('> div.fyre-editor-toolbar > div.goog-toolbar > div.goog-inline-block > div.post-attach-btn', $addUGC).click(function (event) {
				var parentFileContent = $('<div></div>');
				var newFileContent = $('<div style="display:none;"></div>');
				var inputFile = document.createElement("input");
	 			inputFile.type = "file";
	 			inputFile.name = "attachments";
	 			inputFile.id = "attachments";
	 			inputFile.onchange = function(e) {
	 				var fileContent = $(this.parentElement.parentElement);
	 				var fileName = this.value.replace("C:\\fakepath\\","");
	 				removeFile = $('<a class="MultiFile-remove" href="#">x</a>'), 
	 				fileSpan = $('<span class="MultiFile-title" title="'+fileName+'" id="'+fileName+' name="'+fileName+'">'+fileName+'</span>');
	 				$attachments.append(
	 						fileContent.append(removeFile, " ", fileSpan)
	 						);
         			removeFile.click(function(){
						var parent = $(this.parentElement);
						parent.remove();
					});
	 			}
	 			$("#attachments-list").append(
	 				parentFileContent.append(newFileContent.append(inputFile))
	 				);
	 			inputFile.click();
	 			return false;
					
			});

			$('> div.fyre-editor-toolbar > div.goog-toolbar > div.goog-inline-block > div.cancel-btn', $addUGC).click(function (event) {
				options.isReplying = false;
				$d.remove();
				return false;
			});	
			
			appendTo.append($d);
		},
		
		likeUGC : function (ugcId, options, ugcDiv) {
			if ( options ) {
				var url = options.restUrl + '/ugc/like/' +ugcId + '.' + options.outputType; 
				$.ajax({
				    url: url,
				    dataType : options.outputType,
				    data:{ticket : options.ticket, tenant : options.tenant},
				    cache: false,
				    type: 'POST',
				    success: function(aData, textStatus, jqXHR){
				    	if (aData) {
				    		util.observableUpdateUGCProps.apply(ugcDiv, [aData]);
				    	}
				    }
				});
			}
		},
		
		flagUGC : function (ugcId, options, ugcDiv) {
			if ( options ) {
				var url = options.restUrl + '/ugc/dislike/'+ ugcId + '.' + options.outputType; 
				
				$.ajax({
					url: url,
					dataType : options.outputType,
					data:{ticket : options.ticket, tenant : options.tenant},
					cache: false,
					type: 'POST',
					success: function(aData, textStatus, jqXHR){
						if (aData) {
							util.observableUpdateUGCProps.apply(ugcDiv, [aData]);
						}
					}
				});
			}
		},
		
		isValidTicket: function (callback) {
			if ( options ) {
				$.ajax({
				    url: "is_authenticated.json",
				    dataType : options.outputType,
				    data:{},
				    cache: false,
				    type: 'GET',
				    success: function(aData, textStatus, jqXHR){
				    	if (aData) {
				    		callback(aData);
				    	}
				    }
				});
			}
		},
		
				
		observableUpdateUGCProps : function (data) {
			var oOld = $.observable($.view((this.length)?this[0]:this).data);
			
			for (var key in data) {
				var value = data[key];
				if ($.isArray(value)) {
					// TODO: insert/remove array elements
				} else {
					oOld.setProperty(key, value);
				}
			}
		},
		
		observableAddUGC : function (data) {
			var old = $.view((this.length)?this[0]:this).data,
				children = $.observable(old.children?old.children:old.children);

			children.insert( 0, data );

		},
		
		updateEllapsedTimeText : function (list) {
			var now = new Date().getTime();
			
			for (var key in list) {
				var ugc = list[key];
				var millis = now - (new Date(ugc.dateAdded).getTime());
				
				var years = Math.floor(millis / year); 
				millis -= years * year;
				var months = Math.floor(millis / month);
				millis -= months * month;
				var days=Math.floor(millis / day);
				millis -= days * day;
				var hours = Math.floor(millis / hour);
				millis -= hours * hour;
				var mins = Math.floor(millis / minute);
				millis -= mins * minute;
				var secs = Math.floor(millis / second);
				
				var text = '';
				
				if      (years ) text = years  + ' year'  + ((years > 1)?'s':'');
				else if (months) text = months + ' month' + ((months> 1)?'s':'');
				else if (days  ) text = days   + ' day'   + ((days  > 1)?'s':'');
				else if (hours ) text = hours  + ' hour'  + ((hours > 1)?'s':'');
				else if (mins  ) text = mins   + ' minute'+ ((mins  > 1)?'s':'');
				else             text = secs   + ' second'+ ((secs  > 1)?'s':'');
				
				text = text + ' ago';
				
				$.observable(ugc).setProperty('ellapsedTime', text);
				
				if (ugc.children && ugc.children.length) {
					util.updateEllapsedTimeText(ugc.children);
				}
			}
		},
	  	
	  	getActionsParams: function(actions) {
			var actionsParams = "";
			var currentAction;
			var actionName = "";
			var param = "";
			for (var i = 0;i<actions.length;i++) {
				currentAction = actions[i];
				actionName = currentAction.name.toLowerCase()
				for (var j=0;j<currentAction.roles.length;j++) {
					param = "action_" + actionName + "=" + currentAction.roles[j];
					if (actionsParams === "") {
						actionsParams = "?" + param;
					} else {
						actionsParams = actionsParams + "&" + param;
					}
				}
			}
			return actionsParams;

		},
  


		scheduleTimeUpdates : function (options, data) {
			if (options.ellapseTimer) {
				window.clearInterval(options.ellapseTimer);
				options.ellapseTimer = null;
			}
			
			var t=window.setInterval(function () {util.updateEllapsedTimeText(data)}, ellapseUpdateInterval);
			options.ellapseTimer = t;
		},
		
		assignPermissions: function(action, domObj, parentId, options){
			util.getPermissions(action, parentId, options, function(result){
				if(result){
					domObj.addClass('allowed');
				}else{
					domObj.addClass('notAllowed');
				}
			});
			
		},
		
		checkCreatePermissions: function( options, callback ) {
			var url = options.restUrl + '/permission/create.' + options.outputType ; 
			var data = {ticket : options.ticket, tenant : options.tenant};
			
			$.ajax({
			    url: url,
			    data: data,
			    dataType : options.outputType,
			    contentTypeString:"application/json;charset=UTF-8",
			    cache: false,
			    async: false,
			    type: 'GET',
			    success: function(aData, textStatus, jqXHR){
			    	callback(aData);
			    }
			});
		},
		
		getPermissions: function( action, ugcId, options, callback ) {
			var url = options.restUrl + '/permission/' + ugcId + '/' + action + ".json"; 
			var data = {ticket : options.ticket, tenant : options.tenant};
			
			$.ajax({
			    url: url,
			    data: data,
			    dataType : options.outputType,
			    contentTypeString:"application/json;charset=UTF-8",
			    cache: false,
			    async: false,
			    type: 'GET',
			    success: function(aData, textStatus, jqXHR){
			    	callback(aData);
			    }
			});
		}

	};

	$.fn.ugc_blog_post = function(method) {
		// Method calling logic
		if ( methods[method] ) {
			return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
		} else if ( typeof method === 'object' || ! method ) {
			return methods.init.apply( this, arguments );
		} else {
			$.error( 'Method ' +  method + ' does not exist on jQuery.ugc_blog_post' );
		}    
	};
})( jQuery );