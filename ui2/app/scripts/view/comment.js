(function (S) {
    'use strict';

    var Base    = S.view.Base,
        Comment = S.model.Comment,
        U       = S.util,
        $       = S.$;

    var Director = S.getDirector();
    var acceptTermsOnComment = typeof S.Cfg('comments.acceptTerms') !== 'undefined' ? S.Cfg('comments.acceptTerms') : true;

    var CommentView = Base.extend({

        className: [
            Base.prototype.className,
            'crafter-social-comment'
        ].join(' '),

        createUI: U.emptyFn,

        events: {
            'click [data-action-reply]': 'reply',
            'click [data-action-flag]': 'flag',
            'click [data-action-terms]': 'acceptTerms'
        },

        initialize: function (config) {
            Base.prototype.initialize.apply(this, arguments);
            this.model.collection = this.model.collection || config.collection;
        },

        listen: function () {
            if (this.model) {
                this.listenTo(this.model, 'sync', this.render);
                this.listenTo(this.model, 'destroy', this.remove);
                this.listenTo(this.model, 'remove', this.remove);
                this.listenTo(this.model, 'change', this.render);
            }
        },
        submissionDetails: function () {
            var submissionLabel = 'commenting.submissionLabel'.loc(),
                agreeTermsLabel = 'commenting.agreeTermsLabel'.loc(),
                agreeTermsLinkText = 'commenting.agreeTermsLinkText'.loc(),
                agreeTermsLink = 'commenting.agreeTermsLink'.loc(),
                submissionContainer = $(this.el).find('.reply-controls');

            submissionContainer.find('.submission-label').text(submissionLabel);
            submissionContainer.find('.attachments-tip').text('commenting.attachmentsTip'.loc());
            submissionContainer.find('.agree-terms-label').append(agreeTermsLabel);
            submissionContainer.find('.terms-link').append(agreeTermsLinkText);
            submissionContainer.find('.terms-link').attr('href', agreeTermsLink);

        },
        acceptTerms: function () {
            var postBtn = $(this.el).find('.reply-controls .btn.btn-primary'),
                checkBtn = $(this.el).find('.reply-controls input[type=checkbox]');

            if(checkBtn.is(':checked')){
                postBtn.removeClass('disabled');
            }else{
                postBtn.addClass('disabled');
            }
        },
        render: function () {

            var me          = this;
            var profile     = Director.getProfile();
            var model       = $.extend(this.model.toJSON(), {
                sessionUserId: profile.id,
                isTrashed: function () {
                    return me.model.isTrashed();
                },

                // TODO move to constants
                isAdmin: function () {
                    return profile.hasRole('SOCIAL_ADMIN');
                },
                isModerator: function () {
                    return profile.hasRole('SOCIAL_MODERATOR');
                },
                isOwner: function(){
                    return model.createdBy === profile.id
                },
                isLogged: function(){
                    return profile!==undefined && profile.id !==undefined;
                },
                avatarUrl: function(){
                    var ts = me.model.ts;
                    if(model.reloadAvatar !== undefined){
                        ts= model.reloadAvatar;
                    }
                    return S.url('profile.avatar',{
                        id: model.user? model.user.id: profile.id,
                        context: me.cfg.context,
                        ts:ts
                    });
                },
                showTermsAcceptance: (acceptTermsOnComment === true)
            });

            this.$el.html(U.template(
                this.getTemplate('main'), model));

            var $children   = this.$('.comment-children:first');

            this.$('.change-avatar').mouseleave(function () {
                if (model.createdBy === profile.id) {
                    me.$('.change-avatar').addClass('hidden');
                    me.$('.current-avatar').removeClass('hidden');
                }
            });

            this.$('.current-avatar').mouseover(function () {
                if (model.createdBy === profile.id) {
                    me.$('.current-avatar').addClass('hidden');
                    me.$('.change-avatar').removeClass('hidden');
                    }
            });

            var $attachments = this.$('.comment-attachments:first');

            model.children.forEach(function ( child ) {

                var m = new Comment(child),
                    v = new CommentView($.extend({}, me.cfg, { model: m }));

                $children.append(v.render().el);
            });

            model.attachments.forEach(function (attachment) {
                var File = S.get('model.File');
                var file = new File();
                file = file.parse(attachment, me.cfg);

                var tempModel = $.extend(file, {
                    commentId: model._id,
                    showOverlay: model.isOwner(),
                });

                var attachmentModel = new S.model.AttachmentPreview(tempModel);
                var attachmentView = new S.view.AttachmentPreview({
                    model: attachmentModel,
                    context: me.cfg.context
                });

                $attachments.append(attachmentView.render().el);
            });

            return this;
        },

        voteUp: function (e) {
            e.preventDefault();
            var params = this.getRequestParams();
            this.model.voteUp(params);
        },
        voteDown: function () {
            var params = this.getRequestParams();
            this.model.voteDown(params);
        },
        uploadAvatar: function(){
            var me = this;
            var files=[];
            var modal = S.util.instance('view.Modal', {
                modal: { show: true },
                events: {
                    'change input[type=file]': function(event){
                        files = event.target.files;
                    },
                    'click .btn-primary': function (e) {
                       // Create a formdata object and add the files
                        $(e.target).addClass('disabled');
                        var data = new FormData();
                        $.each(files, function(key, value)
                        {
                            data.append(key, value);
                        });

                        S.request({
                            type: 'POST',
                            cache: false,
                            contentType: false,
                            processData: false,
                            data:data,
                            url: S.url('profile.avatar', {
                                _id: Director.getProfile().id,
                                 context: me.cfg.context
                            }),
                            success: function () {
                                $(e.target).removeClass('disabled');
                                modal.hide();
                                me.model.collection.each(function(ugc){
                                    if(ugc.get("user").id===Director.getProfile().id){
                                        ugc.set("reloadAvatar",new Date().getTime());
                                    }
                                });
                            },
                            error: function () {
                                $(e.target).removeClass('disabled');
                                modal.$('.modal-body')
                                .prepend('<div class="alert alert-danger">Unable to Upload profile image</div>');
                            }
                        });
                    }
                }
            });

            modal.set({
                'title': 'Upload Profile Image',
                //---------------  modification to add picture guidelines -----------//
                'body': '<div class="form-group"><p>If you choose to upload a photo, select a current business relevant photo of yourself (recommended dimensions are 100x100 pixels).</p><br/><input id="avatarFileupload" type="file" name="avatar" value="Choose new profile picture"><br/><div id="progress"><div class="bar" style="width: 0%;"></div></div>     </div>',
                //---------------  modification to add picture guidelines -----------//
                'footer': '<button class="btn btn-primary">Upload</button>'
            });

            modal.render();


        },
        removeVote: function () {
            var params = this.getRequestParams();
            this.model.removeVote(params);
        },

        edit: function (e) {

                var $body = this.$('.comment-data').addClass('editing');
                var $editor = $body.find('.editor');

                $editor.html($body.find('.content-wrapper').html());
                $editor.attr('contenteditable','true');
                var editor = CKEDITOR.inline($editor.get(0), {
                    startupFocus: true,
                    toolbar:'Basic'
                });

                this.cache('editor', editor);
        },
        cancelEdit: function (e) {
            if (this.cache('editor')) {
                this.cache('editor').destroy();
                this.cache('editor', S.Constants.get('DESTROY'));
                this.$('.comment-data').removeClass('editing')
                    .find('.editor').html('');
            }
        },
        doEdit: function (e) {
           var editor;
            if ((editor = this.cache('editor'))) {

                var me = this;
                var body = editor.getData();
                if (!body) { return; }

                // Can't use backbone's save due to backend param expectations
                // this.model.save({ 'body': body });
                this.model.set('body', body);
                this.model.update(this.getRequestParams(), {
                    success: function () {
                        editor.destroy();
                        me.cache('editor', S.Constants.get('DESTROY'));
                    }
                });

            }
        },

        reply: function (e) {
            e.preventDefault();
            e.stopPropagation();
            var $body = this.$('.comment-body-reply:first').addClass('replying');
            var $editor = $body.find('.editor');
            $editor.html($body.find('.content-wrapper').html());
            $editor.attr('contenteditable', 'true');
            var editor = CKEDITOR.inline($editor.get(0), {
                startupFocus: true,
                toolbar: 'Basic'
            });
            this.cache('editor', editor);
            this.submissionDetails();
        },
        cancelReply: function (e) {
            if (this.cache('editor')) {
                this.cache('editor').destroy();
                this.cache('editor', S.Constants.get('DESTROY'));
                this.$('.comment-body-reply').removeClass('replying')
                    .find('.editor').html('');
            }
        },
        doReply: function (e) {
            var editor;
            var checkBtn = $(this.el).find('.reply-controls input[type=checkbox]');
            var acceptTermsInvalid = (acceptTermsOnComment === true) ? !checkBtn.is(':checked') : false;
            
            if ((editor = this.cache('editor')) || acceptTermsInvalid) {
                var me = this;
                var body = editor.getData();
                if (body) {
                    this.model.reply(this.getRequestParams({body: body}), {
                        success: function() {
                            editor.destroy();
                            me.cache('editor', S.Constants.get('DESTROY'));
                            // fetch again the collection, with the updated replies
                            me.collection.fetch({
                                data : {
                                    target: me.cfg.target,
                                    context: me.cfg.context
                                }
                            });
                        },
                        error: function(model) {
                            // Put back the comment that didn't get posted.
                            // Attach any text that could have been written while request completed.
                            editor.setData('<div>' + model.get('body') + '</div>' + editor.getData());
                            // Put the cursor position to the end of the editor, where it is likely that it will be
                            var range = editor.createRange();
                            range.moveToPosition(range.root, CKEDITOR.POSITION_BEFORE_END);
                            editor.getSelection().selectRanges([ range ]);
                            // Remove the un-posted comment from the controller
                            collection.remove(model);
                        }
                    });
                }
            }
        },
        flag: function (e) {
            e.preventDefault();
            e.stopPropagation();

            var me = this;
            var isFlagged = this.model.flaggedBy(Director.getProfile().get('id'));

            var modal = S.util.instance('view.Modal', {
                modal: { show: true },
                events: {
                    'click .btn-primary': function () {
                        this.$('.alert').remove();
                        var reason = this.$('textarea').val().trim();
                        if ( reason !== '' ) {
                            me.model[isFlagged ? 'unflag' : 'flag'](me.getRequestParams({
                                reason: reason,
                                profileId:Director.getProfile().get('id')
                            }));
                            // TODO destroy after successful flagging
                            this.destroy();
                        } else {
                            this.$('.modal-body')
                                .prepend('<div class="alert alert-danger">Please provide the reason.</div>')
                                .find('textarea')
                                    .focus();
                        }
                    }
                }
            });

            modal.set({
                'title': isFlagged ? 'Discard  Report' : 'Report Comment',
                'body': (isFlagged ? '<p>You previously reported this comment. This form allows you to retreat your report.</p>' : '') + '<div class="form-group"><label>Reason</label><textarea class="form-control"></textarea></div>',
                'footer': '<button class="btn btn-primary">Submit</button>'
            });

            modal.render();
        },
        unflag: function ( reason ) {
            this.model.unflag(reason);
        },

        trash: function () {
            if (!this.model.isTrashed()) {
                this.model.trash(this.getRequestParams());
                this.$el.remove();
            }
        },

        _dragOverFn: function (e) {
            e.preventDefault();
        },

        getRequestParams: function (extraParams) {
            return $.extend({
                context: this.cfg.context
            }, extraParams || {});
        },

        files: function () {
            var me    = this;
            var model = this.model;

            var files   = S.util.instance('controller.Files', null, {
                comment: model
            });

            var view    = S.util.instance('view.Files', {
                collection: files
            });

            view.render();

            var Modal  = S.get('view.Modal');
            var modal  = new Modal({
                modal: { show: true, keyboard: false, backdrop: 'static' }
            });

            var fetchOptions = {
                data: { context: me.cfg.context }
            };

            modal.$el.on('hidden.bs.modal', function () {
                modal.uploader && modal.uploader.fileupload('destroy');
                modal.destroy();
            });

            if (true || this.model.get('flags').length === 0) {

                var URL = S.url('comments.{_id}.attachments', model.toJSON(), fetchOptions.data);

                modal.$el.on('shown.bs.modal', function () {

                    // Initialize the jQuery File Upload widget:
                    (modal.uploader = view.$('#fileupload')).fileupload({
                        autoUpload: true,
                        dataType: 'json',
                        // dropZone: view.$el,
                        singleFileUploads: true,
                        url: URL,
                        xhrFields: { withCredentials: true },
                        paramName: 'attachment',
                        uploadPostKey: 'attachment',
                        formData: fetchOptions.data,
                        getFilesFromResponse: function (data) {
                            return data.files || [];
                        }
                    }).attr('action', URL).bind('fileuploadfinished', function (/* e, data */) {
                        me.collection.fetch();
                    });

                });

            } else {

                view.$el.html([
                    '<div class="alert alert-warning">',
                    'This comment is flagged. File attachments are disabled.',
                    '</div>'
                ].join(''));

            }

            modal.set('title', 'Upload Photo(s)');
            modal.set('body', view.el);
            modal.set('footer', '<button class="btn btn-default" data-dismiss="modal">Close</button>');

            modal.render();

            files.fetch(fetchOptions);

        },

        remove: function () {
            this.trigger('remove', this.model);
            this.$el.remove();
        },
    });

    CommentView.DEFAULTS = $.extend(true, {}, Base.DEFAULTS, {
        templates: {
            main: ('%@comment.hbs').fmt(S.Cfg('url.templates'))
        },
        commenting: {
            templates: {
                main: function () {
                    return ('%@commenting.hbs').fmt(S.Cfg('url.templates'));
                }
            },
            editor: {
                'extraPlugins': '',
                toolbar: 'Full',
                height: 800
            }
        }
    });

    S.define('view.Comment', CommentView);

}) (crafter.social);
