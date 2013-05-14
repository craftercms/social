(function( $ ) {
	$.ajaxSetup({
        error: function(jqXHR, exception) {
            if (jqXHR.status === 0) {
                alert('Not connect.\n Verify Network.');
            } else if (jqXHR.status == 401) {
            	jqXHR.status = -1;
                alert('Service not allowed');
            } else if (jqXHR.status == 403) {
            	jqXHR.status = -1;
                console.error('Invalid ticket. Sign in again: ' + jqXHR.statusText);
                window.location = "../login";
            }  else if (jqXHR.status == 404) {
                alert('Requested page not found.');
            } else if (jqXHR.status == 500) {
                alert('Internal Server Error.');
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
					options = $this.data('ugc_blog_console');

				// If the plugin hasn't been initialized yet
				if ( ! options ) {
					/*
						Do more setup stuff here
					*/
					var settings = $.extend({
						'restUrl' : '/crafter-social/api/2',
						'resourceUrl' : '/crafter-social/resources',
						'outputType' : 'json',
						'target' : 'http://www.google.com',
						'targetJQObj' : $this,
						'date-format' : 'ddd mmm dd yyyy HH:MM:ss', 
						'css-class' : 'ugc_blog_console',
						'ticket' 	:'default',
						'pageSize' : 10,
						'editorConfig': {},
						'parentId' : '',
						'templatesLoaded' : false,
						'childrenPageSize' : 10 
					}, pOptions);

					$(this).data('ugc_blog_console', settings);
					
					$this.append("Loading...");
					
					$.view().context({
						formattedDate: function( timestamp ) {
							return new Date(timestamp).format(settings.dateFormat);
						},
						getValueFromTextContent: function(jsonString,attribute) {
							var newJson= $.parseJSON(jsonString);
							if (attribute == "title") {
								return newJson.title;
							} else if (attribute == "content") {
								return newJson.content;
							}
							
						},
						anonymousIfNull: function( text ) {
							return (text)?text:"Anonymous";
						}
					});
					
					$.get(settings.resourceUrl + '/templates/templates_blog_console.html', function (data, textStatus, jqXHR) {
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
					options = $this.data('ugc_blog_console');
				
				// Namespacing FTW
				$(window).unbind('.ugc_blog_console');
				$this.removeData('ugc_blog_console');
				$this.empty();
				
				if (options && options.ellapseTimer) {
					window.clearInterval(options.ellapseTimer);
					options.ellapseTimer = null;
				}
			});
		},

		loadUGCBlogConsole : function ( ) {
			return this.each(function () {
				var $this = $(this),
					options = $this.data('ugc_blog_console');

				if ( options ) {
					util.loadRenderEntries(options, $this, true);
					
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
		renderUGCBlogConsole : function (data, options, container) {
			if (options.templatesLoaded) {
				for (i = 0; i<data.list.length;i++) {
					var content = data.list[i].textContent;
					if (typeof content=="string" && content.substr(0,1) == '{') {
						jsonObj = $.parseJSON(content);
						data.list[i].textContent = jsonObj;	
					}	
					data.list[i].msgs = util.countChildren(data.list[i]);
					
				}
				data.username = options.username;
				options.data = data;
				container.html($.render( data, 'ugcListTmpl')).link(data);
				container.options = options;
				util.unbindConsoleEvents(container);
				$saveButton = $('> div.addUGCBlogPublish > div.top > div.pad > nav > ul.main-nav > li.save > a.saveEntry', container);
				$editButton = $('> div.addUGCBlogPublish > div.top > div.pad > nav > ul.main-nav > li.edit > a.editEntry', container);
				$updateButton = $('> div.addUGCBlogPublish > div.top > div.pad > nav > ul.main-nav > li.update > a.updateEntry', container);
				$deleteButton = $('> div.addUGCBlogPublish > div.top > div.pad > nav > ul.main-nav > li.delete > a.deleteEntry', container);
				
				$saveParent = $('> div.addUGCBlogPublish > div.top > div.pad > nav > ul.main-nav > li.save', container);
				$editParent = $('> div.addUGCBlogPublish > div.top > div.pad > nav > ul.main-nav > li.edit', container);
				$updateParent = $('> div.addUGCBlogPublish > div.top > div.pad > nav > ul.main-nav > li.update', container);
				$deleteParent = $('> div.addUGCBlogPublish > div.top > div.pad > nav > ul.main-nav > li.delete', container);     
				
				util.checkCreatePermissions(options, function(result) {
					options.isCreated = result;
					if (!result) {
						newButton = $('#newEntry', container); 
						newButton[0].style.display = "none";
					}
				});
				
				container.on( "click", "#newEntry", function(event) {
					$saveParent[0].style.display = "block";
					$editParent[0].style.display = "none";
					$updateParent[0].style.display = "none";
					$deleteParent[0].style.display = "none";
					
					options = container.options;
					var data = {};
					data.textContent = {"title": "","content":""};
					data.id = "";
					util.renderUGCBlogEntryPublishing(options, container, data,true);
				});
				
				container.on( "click", "#detailEntryBtn", function(event) {
					ugcId = event.target.name;

					$saveParent[0].style.display = "none";
					$editParent[0].style.display = "block";
					$updateParent[0].style.display = "none";
					$deleteParent[0].style.display = "block";   
					

					var data = {};
					options = container.options;
					
					for(i = 0; i < options.data.list.length; i++) {
						currentUGC = options.data.list[i];
						if (currentUGC.id == ugcId) {
							data = currentUGC;
							break;
						}
					}    
					
					
					util.getPermissions("DELETE", data.id, options, function(result){
					  if(!result) {
						 $deleteParent[0].style.display = "none";
					  } 
					}); 
					util.getPermissions("UPDATE", data.id, options, function(result){
					  if(!result) {
						 $editParent[0].style.display = "none";
					  } 
					});
					util.renderUGCBlogEntryPublishing(options, container, data, false);
					
				});

				$editButton.click(function (event) {   
					$titleInput = $('> div.addUGCBlogPublish > div.box > p > input.blog-title', container);
					util.enableEditors(true, $titleInput[0]);     
					$editParent[0].style.display = "none";
					$updateParent[0].style.display = "block";
					});   
					
                $updateButton.click(function (event) {
						container.bloglist = this.bloglist;
						container.publishDiv = this.publishDiv;
						var contentData = tinyMCE.get('textContentField').getContent();
						var $title = $('> div.ugc-list > div.box > p > input.blog-title', container);

						if(contentData==null || contentData == "" || $title.val()==null || $title.val()=="") {
							var $errorDiv = $('> div.ugc-list > div.box > div.errormsg', container);						
							$errorDiv[0].textContent = "*Title and Content are required values";
							$errorDiv[0].style.display = "block";
							return;
						}

						var isNew = false;
						ugcId = $saveButton[0].name;
						util.addNewEntry(contentData, "", $title.val(), "", options,  container, false, ugcId);	
						return false;

					});  
					
			   $deleteButton.click(function (event) {
					var url = options.restUrl + '/ugc/delete.' + options.outputType + "?ugcIds=" + ugcId; 
					var data = {ticket : options.ticket, 'tenant' : options.tenant};
					$.ajax({
					    url: url,
					    data: data,
					    dataType : options.outputType,
					    cache: false,
					    type: 'POST',
					    success: function(aData, textStatus, jqXHR){
					    	util.loadRenderEntries(options, container,true);
					    }
					});

				});	

				$saveButton.click(function (event) {
					container.bloglist = this.bloglist;
					container.publishDiv = this.publishDiv;
					var contentData = tinyMCE.get('textContentField').getContent();
					var $title = $('> div.ugc-list > div.box > p > input.blog-title', container);

					if(contentData==null || contentData == "" || $title.val()==null || $title.val()=="") {
						var $errorDiv = $('> div.ugc-list > div.box > div.errormsg', container);						
						$errorDiv[0].textContent = "*Title and Content are required values";
						$errorDiv[0].style.display = "block";
						return;
					}

					var isNew = false;
					var ugcId = "";
					if (event.target.name=="") {
						isNew = true;
					} else {
						ugcId = event.target.name;
					}
					
					util.addNewEntry(contentData, "", $title.val(), "", options,  container, isNew, ugcId);	
					return false;
					
				});

				util.scheduleTimeUpdates(options, data.list);
			} else {
				setTimeout(function() {util.renderUGCBlogConsole(data, options, container);} , 200);
			}
		},

		unbindConsoleEvents: function(container) {
			if (("#newEntry",container).data('events') && ("#newEntry",container).data('events').click &&
				("#newEntry",container).data('events').click.length>0) {
				("#newEntry",container).unbind("click")

			}
			if (("#deleteEntry",container).data('events') && ("#deleteEntry",container).data('events').click &&
				("#deleteEntry",container).data('events').click.length>0) {
				("#deleteEntry",container).unbind("click")
			}
			$saveButton = $('> div.addUGCBlogPublish > div.top > div.pad > nav > ul.main-nav > li.save > a.saveEntry', container);
			if ($saveButton.data('events') && $saveButton.data('events').click &&
					$saveButton.data('events').click.length>0) {
				$saveButton.unbind("click");
			}
			$editButton = $('> div.addUGCBlogPublish > div.top > div.pad > nav > ul.main-nav > li.edit > a.editEntry', container);
			if ($editButton.data('events') && $editButton.data('events').click &&
					$editButton.data('events').click.length>0) {
				$editButton.unbind("click");
			}
			$updateButton = $('> div.addUGCBlogPublish > div.top > div.pad > nav > ul.main-nav > li.update > a.updateEntry', container);
			if ($updateButton.data('events') && $updateButton.data('events').click &&
					$updateButton.data('events').click.length>0) {
				$updateButton.unbind("click");
			}
			
			$deleteButton = $('> div.addUGCBlogPublish > div.top > div.pad > nav > ul.main-nav > li.delete > a.deleteEntry', container);
			if ($deleteButton.data('events') && $deleteButton.data('events').click &&
					$deleteButton.data('events').click.length>0) {
				$deleteButton.unbind("click");
			}
			
			
		},

		renderUGCBlogEntryPublishing : function (options, container, data, isNew) {
			if (options.templatesLoaded) {
				$bloglist = $('> div.bloglist', container),
				$titleInput = $('> div.addUGCBlogPublish > div.box > p > input.blog-title', container);
				$publishDiv = $('> div.addUGCBlogPublish', container);
				$bloglist[0].style.display = "none";
				$publishDiv[0].style.display = "block";
				$saveButton = $('> div.addUGCBlogPublish > div.top > div.pad > nav > ul.main-nav > li.save > a.saveEntry', container);
				$saveButton[0].name = data.id;
				$saveButton.bloglist = $bloglist;
				$saveButton.publishDiv = $publishDiv;

				var $errorDiv = $('> div.ugc-list > div.box > div.errormsg', container);						
				$errorDiv[0].textContent = "";
				$errorDiv[0].style.display = "none";

				var editor = tinymce.get('textContentField'); 
				var oInstance = tinyMCE.getInstanceById("textContentField");
				if (oInstance) {
					if (oInstance.isHidden()) tinyMCE.remove(oInstance);
						tinyMCE.execCommand('mceRemoveControl', true, "textContentField");
				}

				tinyMCE.init({
					oninit : function(e) {
						tinyMCE.activeEditor.setContent(data.textContent.content);
						$titleInput[0].value = data.textContent.title;  
    					util.enableEditors(isNew, $titleInput[0]);
						
					},
					mode : "exact",
					elements : "textContentField",
					theme : "advanced",
					cleanup : true,
				    language:"en",
				    theme_advanced_buttons1 : "bold,italic,underline,|,formatselect,fontselect,fontsizeselect, |,cut,copy,paste,pastetext,pasteword,|,search,replace,|,undo,redo,|,link,unlink,image,cleanup,code,|,forecolor,backcolor",
				    theme_advanced_buttons2 : "",
					theme_advanced_buttons3 : "",
					 theme_advanced_layout_manager: "SimpleLayout",
				    theme_advanced_toolbar_location : "top",
				    theme_advanced_toolbar_align : "center",
				    theme_advanced_statusbar_location : "bottom",
				    theme_advanced_resizing : true,
				    plugins : 'autoresize',
	  				autoresize_min_height: "500px",
	  				autoresize_max_height: "500px",  
					width: "100%",
					height: "450px"
					
				});  

				container.on( "click", "#close", function(event) {
					$bloglist[0].style.display = "block";
					$publishDiv[0].style.display = "none";
				});


			} else {
				setTimeout(function() {util.renderUGCBlogEntryPublishing(options, container);} , 200);
			}
		},  
		
		enableEditors: function(enable, title) {
		   	if (!enable) {             
				 $titleInput[0].readOnly=true;      
				 $titleInput[0].disabled=true; 
				 tinyMCE.activeEditor.getBody().setAttribute('contenteditable', false);
			} else {
				$titleInput[0].readOnly=false; 
				$titleInput[0].disabled=false;  
				tinyMCE.activeEditor.getBody().setAttribute('contenteditable', true);
			}
		},

		addNewEntry : function (body, imageData, titleData, parentId, options, appendTo, isNew, ugcId) {

					if (body != null && body.length > 0) {
						dataTextContent = JSON.stringify({
			    			title: titleData,
			    			image: imageData,
			    			content: body
			    		});
			    		var serviceName = isNew?'/ugc/create' :'/ugc/update';
						var url = options.restUrl + serviceName + '.' + options.outputType + (isNew?util.getActionsParams(options.actions):"");
						var data = {'target' : options.target, 'textContent' : dataTextContent, ticket : options.ticket, 'tenant' : options.tenant};
						if (!isNew) {
							data.ugcId = ugcId;
						}
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
								if (appendTo.bloglist!=null && appendTo.bloglist!=undefined)
									appendTo.bloglist[0].style.display = "block";
								if (appendTo.publishDiv!=null && appendTo.publishDiv!=undefined)
									appendTo.publishDiv[0].style.display = "none";
						    	if (aData) {
						    		util.loadRenderEntries(options,appendTo,true);
						    	}
						    	
						    }

						});
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

		loadRenderEntries : function (options, thisContainer, loadEntries) {
			if (loadEntries) {
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
				    	util.manageRenderBlogConsole(aData, options, thisContainer);
				    }
				});
			} else {
				util.manageRenderBlogConsole(options.aData, options, thisContainer);
			}
		},

		manageRenderBlogConsole : function(aData, options, container) {
			container.empty();
	    	util.updateEllapsedTimeText(aData.list);
	    	options.aData = aData;
	    	aData.settings=options;
	    	util.renderUGCBlogConsole(aData, options, container);
		},

		addTextUGC : function (body, parentId, options, appendTo) {
			if ( options ) {
					
					if (body != null && body.length > 0) {
						var dataTextContent = body;
						var url = options.restUrl + '/ugc/create' + '.' + options.outputType;
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
						    	if (aData) {
						    		util.updateEllapsedTimeText([aData]);
						    		util.observableAddUGC.apply(appendTo, [aData]);
						    		util.wireUpUGC.apply( $('#ugc-message-'+aData.id, appendTo), [options]);
						    	}
						
						    }

						});
					}
				
			}
		},
		
		wireUpUGC : function (options) {
			thisId = this.id ? this.id : this[0].id;
			var $this = $(this),
			    $actions = $(' div.ugc-comment-wrapper > footer.ugc-comment-footer > div.ugc-listen-actions-' + thisId.substring(12),$this),
				$like = $('> a.like', $actions),
				$reply = $('> a.reply', $actions),
				$flag = $('> a.flag', $actions);
			
			$like.click(function (event) {
				util.likeUGC($this.attr('ugc-id'), options, $this);
			});			

			$reply.click(function (event) {
				util.showTextUGCDialog($this.attr('ugc-id'), options, $this, $this);
			});			

			$flag.click(function (event) {
				util.flagUGC($this.attr('ugc-id'), options, $this);
			});
		},

		showTextUGCDialog : function (parentId, options, appendTo, appendUGCTo) {
			var data = { 'parentId' : parentId },
				html = $.render( data, 'addTextUGCTmpl' ),
				$d = $('<div>', {}).html(html),
				$addUGC = $('> div.add-ugc', $d),
				$body = $('> div > textarea', $addUGC);
			
			$('> div.actions > a.post-comment-btn', $addUGC).click(function (event) {
				$d.remove();
				util.addTextUGC($body.val(), parentId, options,  appendUGCTo);
				return false;
			});

			$('> div.actions > a.cancel-btn', $addUGC).click(function (event) {
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
				    data:{ticket : options.ticket, 'tenant' : options.tenant},
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
				children = $.observable(old.children?old.children:old.UGC.children);
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

		scheduleTimeUpdates : function (options, data) {
			if (options.ellapseTimer) {
				window.clearInterval(options.ellapseTimer);
				options.ellapseTimer = null;
			}
			
			var t=window.setInterval(function () {util.updateEllapsedTimeText(data)}, ellapseUpdateInterval);
			options.ellapseTimer = t;
		},

		countChildren: function(ugc) {
			var currentUgc = null;
			var count = 0;
			for (var i =0; i < ugc.children.length; i ++) {
				currentUgc = ugc.children[i];
				count ++;
				if (currentUgc.children.length > 0) {
					count+=util.countChildren(currentUgc);
				}
			}
			return count;
		}

	};

	$.fn.ugc_blog_console = function(method) {
		// Method calling logic
		if ( methods[method] ) {
			return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
		} else if ( typeof method === 'object' || ! method ) {
			return methods.init.apply( this, arguments );
		} else {
			$.error( 'Method ' +  method + ' does not exist on jQuery.ugc_blog_console' );
		}    
	};
})( jQuery );