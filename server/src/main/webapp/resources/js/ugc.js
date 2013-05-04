(function( $ ) {
	$.ajaxSetup({
        error: function(jqXHR, exception) {
            if (jqXHR.status === 0) {
                alert('Not connect.\n Verify Network.');
            } else if (jqXHR.status == 403) {
                alert('Requested not allowed. Try to get a Multipass');
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
					options = $this.data('ugc');

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
						'css-class' : 'ugc',
						'ticket' 	:'default',
						'pageSize' : 10,
						'childrenPageSize' : 10 
					}, pOptions);

					$(this).data('ugc', settings);
					
					$this.append("Loading...");
					
					$.view().context({
						formattedDate: function( timestamp ) {
							return new Date(timestamp).format(settings.dateFormat);
						},
						anonymousIfNull: function( text ) {
							return (text)?text:"Anonymous";
						}
					});
					
					$.get(settings.resourceUrl + '/templates/templates_1.0.html', function (data, textStatus, jqXHR) {
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
					options = $this.data('ugc');
				
				// Namespacing FTW
				$(window).unbind('.ugc');
				$this.removeData('ugc');
				$this.empty();
				
				if (options && options.ellapseTimer) {
					window.clearInterval(options.ellapseTimer);
					options.ellapseTimer = null;
				}
			});
		},

		loadUGC : function ( ) {
			return this.each(function () {
				var $this = $(this),
					options = $this.data('ugc');

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
					    	util.updateEllapsedTimeText(aData.hierarchyList.list);
					    	aData.settings=options;
					    	util.renderUGC(aData, options, $this);
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
	
	var util = {
		renderUGC : function (data, options, container) {
			if (options.templatesLoaded) {

				container.html($.render( data, 'ugcListTmpl')).link(data);

				container.on( "click", "#attachmentOneBtn", function(event) {
					var url = options.restUrl + '/get_attachment/' + event.target.name+"?ticket=" + options.ticket + "&tenant=" + options.tenant;
					location.href = url;
						
				});
				
				var $ugcDiv = $(' > div.ugc-list', container),
					$actionsDiv = $(' > div.actions ', container),
					$addUGCBtn = $('a', $actionsDiv);
				
				$addUGCBtn.click(function (event) {
					util.showTextUGCDialog('', options, $actionsDiv, $ugcDiv);
				});
				
				$('div.ugc', $ugcDiv).each(function () {
					util.wireUpUGC.apply( this, [ options ]);
				});
				
		    	util.scheduleTimeUpdates(options, data.hierarchyList.list);
			} else {
				setTimeout(function() {util.renderUGC(data, options, container);} , 200);
			}
		},
		
		wireUpUGC : function (options) {
			var $this = $(this),
				$actions = $('> div > div.user-ugc > div.footer > div.actions', $this),
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

		createHiddenInput :function(inputId, inputValue) {
			var inputField = document.createElement("input");
 			inputField.type = "hidden";
 			inputField.name = inputId;
 			inputField.id = inputId;
 			inputField.value = inputValue;
 			return inputField;
		},

		addTextAttachmentsUGC : function (body, parentId, options, appendTo, attachments, formContent) {
			$("#attachments-list").append(util.createHiddenInput("textContent",body));
 			$("#attachments-list").append(util.createHiddenInput("target",options.target));
 			if(parentId){
				$("#attachments-list").append(util.createHiddenInput("parentId",parentId));
			 }
 			var url = $("#fileuploadForm").attr("action");
 			if(url.indexOf("?ticket=") === -1)
 				url = url +"?ticket=" + options.ticket;
 			if(url.indexOf("&tenant=") === -1)
 			    url = url + "&tenant=" + options.tenant;
 			$("#fileuploadForm").attr("action",url);
			var iframeStr = "<IFRAME id='hidden_upload' name='hidden_upload' src='' style='width:0;height:0;border:0px solid #fff'></IFRAME>";

			$("#fileuploadForm").append(iframeStr);
			$('#hidden_upload').load(function(e){
				var myIFrame = document.getElementById("hidden_upload");
				if (myIFrame.contentWindow.document.childNodes[0].childNodes.length == 0) {
					alert('Social server response unexpected');
					$.error('Social server response unexpected');
					return;
				}
				var idUgc = myIFrame.contentWindow.document.childNodes[0].children[0].textContent;
			    var url = options.restUrl + '/ugc/get_ugc/' + idUgc + '.' + options.outputType;;
				var data = {ticket : options.ticket, tenant : options.tenant};
			    $.ajax({
				    url: url,
				    data: data,
				    dataType : options.outputType,
				    cache: false,
				    type: 'GET',
				    success: function(aData, textStatus, jqXHR){
				    	if (aData.UGC) {
				    		util.updateEllapsedTimeText([aData.UGC]);
				    		util.observableAddUGC.apply(appendTo, [aData.UGC]);
				    		util.wireUpUGC.apply( $('#ugc_'+aData.UGC.id, appendTo), [options]);
				    	}
				    	formContent.remove();
				    }
				});
   			});
			$("#fileuploadForm").submit();
			return false;
		},
		
		addTextUGC : function (body, parentId, options, appendTo, attachments, formContent) {
			if ( options ) {
				if (attachments ==null || attachments.length == 0) {
					if (body != null && body.length > 0) {
						var url = options.restUrl + '/ugc/create' + '.' + options.outputType;
						var data = {'target' : options.target, 'textContent' : body, ticket : options.ticket, 'tenant' : options.tenant};
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
						    	if (aData.UGC) {
						    		util.updateEllapsedTimeText([aData.UGC]);
						    		util.observableAddUGC.apply(appendTo, [aData.UGC]);
						    		util.wireUpUGC.apply( $('#ugc_'+aData.UGC.id, appendTo), [options]);
						    	}
						    	formContent.remove();
						    }

						});
					}
				} else {
					util.addTextAttachmentsUGC(body, parentId, options, appendTo, attachments, formContent);
					
				}
			}
		},
		
		showTextUGCDialog : function (parentId, options, appendTo, appendUGCTo) {
			var data = { 'parentId' : parentId },
				html = $.render( data, 'addTextUGCTmpl' ),
				$d = $('<form id="fileuploadForm" action="api/2/ugc/create" method="POST" enctype="multipart/form-data" target="hidden_upload">', {}).html(html),
				$addUGC = $('> div.add-ugc', $d),
				$body = $('> div > textarea', $addUGC);
				$attachments = $('> div.attachments-list', $addUGC);
			
			$('> div.actions > a.add-btn', $addUGC).click(function (event) {
				util.addTextUGC($body.val(), parentId, options,  appendUGCTo, $("#attachments-list").children(),$d);
				return false;
			});

			$('> div.actions > a.add-attachment', $addUGC).click(function (event) {
				var parentFileContent = $('<div></div>');
				var newFileContent = $('<div style="display:none;"></div>');
				var inputFile = document.createElement("input");
	 			inputFile.type = "file";
	 			inputFile.name = "attachments";
	 			inputFile.id = "attachments";
	 			inputFile.onchange = function(e) {
	 				var fileContent = $(this.parentElement.parentElement);
	 				removeFile = $('<a class="MultiFile-remove" href="#">x</a>'), 
	 				fileSpan = $('<span class="MultiFile-title" title="'+this.value+'" id="'+this.value+' name="'+this.value+'">'+this.value+'</span>');
         			$("#attachments-list").append(
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
				    	if (aData.UGC) {
				    		util.observableUpdateUGCProps.apply(ugcDiv, [aData.UGC]);
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
				    data:{ticket : options.ticket, 'tenant' : options.tenant},
				    cache: false,
				    type: 'POST',
				    success: function(aData, textStatus, jqXHR){
				    	if (aData.UGC) {
				    		util.observableUpdateUGCProps.apply(ugcDiv, [aData.UGC]);
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
				children = $.observable(old.children?old.children:old.hierarchyList.list);
				//children = $.observable(old.children?old.children:old.hierarchyList.list);
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
		}

	};

	$.fn.ugc = function(method) {
		// Method calling logic
		if ( methods[method] ) {
			return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
		} else if ( typeof method === 'object' || ! method ) {
			return methods.init.apply( this, arguments );
		} else {
			$.error( 'Method ' +  method + ' does not exist on jQuery.ugc' );
		}    
	};
})( jQuery );
