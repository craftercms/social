(function (S) {
    'use strict';

    var Base    = S.view.Base,
        Comment = S.model.Comment,
        U       = S.util,
        $       = S.$;

    var Director = S.getDirector();

    var CommentView = Base.extend({

        className: [
            Base.prototype.className,
            'crafter-social-comment'
        ].join(' '),

        createUI: U.emptyFn,

        events: {
            'click [data-action-reply]': 'reply',
            'click [data-action-flag]': 'flag'
        },

        listen: function () {
            if (this.model) {
                this.listenTo(this.model, 'sync', this.render);
                this.listenTo(this.model, 'change', this.render);
                this.listenTo(this.model, 'destroy', this.remove);
                this.listenTo(this.model, 'remove', this.remove);
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
                avatarUrl: function(){
                    return S.url('profile.avatar',{id: model.user?model.user.id:profile.id,
                                                    context: me.cfg.context})
                }
            });

            this.$el.html(U.template(
                this.getTemplate('main'), model));

            var $children   = this.$('.comment-children:first');

            this.$("#changeAvatar").hover( null,function(){
                if(model.createdBy === profile.id){
                    me.$("#changeAvatar").addClass("hidden");
                    me.$("#currentAvatar").removeClass("hidden");
                }
            });

            this.$("#currentAvatar").hover( function(){
                if(model.createdBy === profile.id){
                    me.$("#currentAvatar").addClass("hidden");
                    me.$("#changeAvatar").removeClass("hidden");
                    }
            },null);


            model.children.every(function ( child ) {

                var m = new Comment(child),
                    v = new CommentView($.extend({}, me.cfg, { model: m }));

                $children.append(v.render().el);

                return true;

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
                    'click .btn-primary': function () {
                       // Create a formdata object and add the files
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
                                modal.hide();
                            },
                            error: function () {
                                modal.$('.modal-body')
                                .prepend('<div class="alert alert-danger">Unable to Upload profile image</div>');
                            }
                        });
                    }
                }
            });

            modal.set({
                'title': 'Upload Profile Image',
                'body': '<div class="form-group"><input id="avatarFileupload" type="file" name="avatar" value="Choose new profile picture"><br/><div id="progress"><div class="bar" style="width: 0%;"></div></div>     </div>',
                'footer': '<button class="btn btn-primary">Upload</button>'
            });

            modal.render();


        },
        removeVote: function () {
            var params = this.getRequestParams();
            this.model.removeVote(params);
        },

        reply: function (e) {
            e.preventDefault();
//            var Commenting  = S.get('view.Commenting');
//            var commenting  = new Commenting(this.cfg.commenting);
        },
        flag: function (e) {
            e.preventDefault();

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
                'title': isFlagged ? 'Discard Comment Flag' : 'Flag Comment',
                'body': (isFlagged ? '<p>You previously flagged this comment. This form allows you to retreat your flag.</p>' : '') + '<div class="form-group"><label>Reason</label><textarea class="form-control"></textarea></div>',
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

            var fetchOptions = { data: { context: this.cfg.context } };

            modal.$el.on('hidden.bs.modal', function () {
                me.model.fetch(fetchOptions);
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
                        me.model.fetch(fetchOptions);
                    });

                });

            } else {

                view.$el.html([
                    '<div class="alert alert-warning">',
                    'This comment is flagged. File attachments are disabled.',
                    '</div>'
                ].join(''));

            }

            modal.set('title', 'File Attachments');
            modal.set('body', view.el);
            modal.set('footer', '<button class="btn btn-default" data-dismiss="modal">Close</button>');

            modal.render();

            files.fetch(fetchOptions);

        },

        remove: function () {
            this.trigger('remove', this.model);
            this.$el.remove();
        }

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
