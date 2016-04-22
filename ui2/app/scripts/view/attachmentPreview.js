(function (S) {
    'use strict';

    var AttachmentPreview                ,
        Base    = S.view.Base   ,
        U       = S.util,
        $       = S.$           ;

    AttachmentPreview = Base.extend({

        createUI: U.emptyFn,

        className: [
            Base.prototype.className,
            'crafter-social-file-view'
        ].join(' '),

        initialize: function () {
            Base.prototype.initialize.apply(this, arguments);
        },

        render: function () {
            var model = $.extend(this.model.toJSON(), {
                title: function () {
                    return model.fileName;
                },
                src: function () {
                    var src = '';
                    if (model.fileName.match(S.Constants.get('SUPPORTED_IMAGE_FORMATS'))) {
                        src = model.url;
                    } else if (model.fileName.match(S.Constants.get('SUPPORTED_VIDEO_FORMATS'))) {
                        src = S.Constants.get('POSTER_URL');
                    }
                    return src;
                }
            });

            this.$el.html(U.template(
                this.getTemplate('main'), model));
            return this;
        },

        trashAttachment: function (event) {
            event.stopPropagation();
            var self = this;
            var model = this.model;
            var Modal = S.get('view.Modal');
            var modal  = new Modal({
                modal: { 
                    show: true, 
                    keyboard: false, 
                    backdrop: 'static'
                },
                events: {
                    'click .btn-danger': function () {
                        self.model.trashAttachment(self.getRequestParams({
                            fileId: model.get('fileId'),
                            _id: model.get('commentId')
                        }), {
                            success: function(data) {
                                console.log('Success ', data);
                                self.$el.remove();
                            },
                            error: function(data) {
                                console.log('Error ', data);
                            }
                        });
                    }
                }
            });

            var fileObj = event.target;
            
            modal.set('title', 'Delete Attachment');
            modal.set('body', '<span>Are you sure you want to delete the attachment <strong>'+ self.getAttachmentName(model.get('fileName')) +'</strong>?</span>')
            modal.set('footer', '<button class="btn btn-danger">Delete</button><button class="btn btn-default" data-dismiss="modal">Cancel</button>');

            modal.render();
        },

        getAttachmentName: function (name) {
            var parts = name.split('.');
            var attachmentName = (parts.length > 1)? parts[0]: name.fileName;
            return attachmentName;
        },

        getAttachmentType: function (name) {
            var parts = name.split('.');
            var attachmentType = (parts.length > 1)? parts[1]: '';
            return attachmentT,ype;  
        },

        getRequestParams: function (extraParams) {
            return $.extend({
                context: this.cfg.context
            }, extraParams || {});
        },

        viewAttachment: function (event) {
            var self = this;
            var model = this.model;
            var Modal = S.get('view.Modal');
            var modal  = new Modal({
                modal: { show: true, keyboard: false, backdrop: 'static' }
            });

            var fileObj = event.target;

            if (model.get('fileName').match(S.Constants.get('SUPPORTED_VIDEO_FORMATS'))) {
                var attachmentType = self.getAttachmentType(model.get('fileName'));
                modal.set('body', '<video controls autoplay class="img-file-full"><source src="'+ model.get('url') +'" type="video/'+ attachmentType +'" ></source></video>')
            } else {
                modal.set('body', '<img class="img-file-full" src="'+ model.get('url') +'" alt="'+ model.get('fileName') +'" title="'+ model.get('fileName') +'" />')    
            }

            modal.set('footer', '<button class="btn btn-default" data-dismiss="modal">Close</button>');

            modal.render();
        },
    });

    AttachmentPreview.DEFAULTS = $.extend(true, {}, Base.DEFAULTS, {
        templates: {
            main: ('%@attachment-preview.hbs').fmt(S.Cfg('url.templates'))
        },
    });

    S.define('view.AttachmentPreview', AttachmentPreview);

}) (crafter.social);