
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
					options = $this.data('ugc_blog_publish');

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
						'css-class' : 'ugc_blog_publish',
						'ticket' 	:'default',
						'pageSize' : 10,
						'childrenPageSize' : 10
					}, pOptions);

					$(this).data('ugc_blog_publish', settings);
					
					$.view().context({
						formattedDate: function( timestamp ) {
							return new Date(timestamp).format(settings.dateFormat);
						},

						anonymousIfNull: function( text ) {
							return (text)?text:"Anonymous";
						}
					});
					
					$.get(settings.resourceUrl + '/templates/templates_blog_publish.html', function (data, textStatus, jqXHR) {
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
					options = $this.data('ugc_blog_publish');
				
				// Namespacing FTW
				$(window).unbind('.ugc_blog_publish');
				$this.removeData('ugc_blog_publish');
				$this.empty();
				
				if (options && options.ellapseTimer) {
					window.clearInterval(options.ellapseTimer);
					options.ellapseTimer = null;
				}
			});
		},

		
		loadUGCBlog : function ( ) {
			return this.each(function () {
				aData = {};
				var $this = $(this),
					options = $this.data('ugc_blog_publish');
					util.renderUGCBlog(aData, options, $this);

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
		renderUGCBlog : function (data, options, container) {
			if (options.templatesLoaded) {
				container.html($.render( data, 'ugcBlogTmpl')).link(data);

				var $actionsDiv = $(' > div.actions ', container),
				$addBlogBtn = $('a', $actionsDiv);
				
				$addBlogBtn.click(function (event) {
					util.showTextUGCDialog('', options, $actionsDiv);
				});

			} else {
				setTimeout(function() {util.renderUGCBlog(data, options, container);} , 200);
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
		
		addTextUGC : function (body, imageData, titleData, parentId, options, appendTo, attachments) {
			$("#title").attr('disabled', 'disabled');
    		$("#textContentField").attr('disabled', 'disabled');
    		dataTextContent = JSON.stringify({
    			title: titleData,
    			image: imageData,
    			content: body
    		});
			//dataTextContent = '{"title" : "'  + title +'","image" : "' + image +'","content" : "'+ body + '"}';
			$("#attachments-list").append(util.createHiddenInput("textContent",dataTextContent));
			$("#attachments-list").append(util.createHiddenInput("target",options.target));
 			if(parentId){
				$("#attachments-list").append(util.createHiddenInput("parentId",parentId));
			 }
 			var url = $("#fileuploadForm").attr("action");
 			url = url +"?ticket=" + options.ticket + "&tenant=" + options.tenant;
 			$("#fileuploadForm").attr("action",url);
			var iframeStr = "<IFRAME id='hidden_upload' name='hidden_upload' src='' style='width:0;height:0;border:0px solid #fff'></IFRAME>";

			$("#fileuploadForm").append(iframeStr);
			$('#hidden_upload').load(function(e){
				var myIFrame = document.getElementById("hidden_upload");
				if (myIFrame.contentWindow.document.childNodes[0].childNodes.length == 0) {
					alert('Social server response unexpected');
					$.error('Social server response unexpected');
				}
				tinyMCE.execCommand('mceFocus', false, 'textContentField');
    			tinyMCE.execCommand('mceRemoveControl', false, 'textContentField');
				$("#fileuploadForm").remove();

   			});
			$("#fileuploadForm").submit();
			return false;
		},
		
		showTextUGCDialog : function (parentId, options, appendTo, appendUGCTo) {
			
			var data = { 'parentId' : parentId },
				html = $.render( data, 'addUGCBlogTmpl' ),
				$d = $('<form id="fileuploadForm" action="api/2/ugc/create" method="POST" enctype="multipart/form-data" target="hidden_upload">', {}).html(html),
				$addUGC = $('> div.add-ugc-blog', $d),
				$body = $('> div > div.fields-line > textarea', $addUGC),
				$image = $('> div > div.fields-line > input.blog-image', $addUGC),
				$title = $('> div > div.fields-line > input.blog-title', $addUGC),
				$attachments = $('> div > div.attachments-list', $addUGC);
			
			$('> div.actions > a.add-btn', $addUGC).click(function (event) {
				var contentData = tinyMCE.get('textContentField').getContent();

				//var contentFy = JSON.stringify(content);
				var contentDataEscape = util.escape(contentData);
				// contentData = contentData.replace("\n","\\n");
				// contentData = contentData.replace("\r","");
				util.addTextUGC(contentDataEscape, $image.val(), $title.val(), parentId, options,  appendUGCTo);
				return false;
			});

			$('> div.actions > a.cancel-btn', $addUGC).click(function (event) {
				tinyMCE.execCommand('mceFocus', false, 'textContentField');
    			tinyMCE.execCommand('mceRemoveControl', false, 'textContentField');
				$d.remove();
				return false;
			});
			//tinyMCE.execCommand('mceAddControl', false, 'textContentField');
			appendTo.append($d);
			tinyMCE.execCommand('mceAddControl', false, 'textContentField');
		},
		observableAddUGC : function (data) {
			var old = $.view((this.length)?this[0]:this).data,
				children = $.observable(old.children?old.children:old.hierarchyList.list);
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

		escape :function(str) {
		 return str.replace(/[\n]/g, '')
	      .replace(/[\r]/g, '');
	      
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

	$.fn.ugc_blog_publish = function(method) {
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
	