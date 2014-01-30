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
            'click [data-action-like]': 'like',
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
                isTrashed: function () {
                    return me.model.isTrashed();
                },

                // TODO move to constants
                isAdmin: function () {
                    return profile.hasRole('SOCIAL_ADMIN');
                },
                isModerator: function () {
                    return profile.hasRole('SOCIAL_MODERATOR');
                }
            });

            this.$el.html(U.template(
                this.getTemplate('main'), model));

            var $children   = this.$('.comment-children:first');
            model.children.every(function ( child ) {

                var m = new Comment(child),
                    v = new CommentView($.extend({}, me.cfg, { model: m }));

                $children.append(v.render().el);

                return true;

            });

            return this;
        },

        like: function (e) {
            e.preventDefault();
            this.model.like();
        },
        unlike: function () {
            this.model.unlike();
        },

        dislike: function () {
            this.model.dislike();
        },
        undoDislike: function () {
            this.model.undoDislike();
        },

        reply: function (e) {
            e.preventDefault();

//            var Commenting  = S.get('view.Commenting');
//            var commenting  = new Commenting(this.cfg.commenting);

        },

        flag: function (e) {
            e.preventDefault();

            var me = this;
            // TODO fixed type once backed fixes (flaged)
            var isFlagged = this.model.get('userInfo').flaged;

            var modal = S.util.instance('view.Modal', {
                modal: { show: true },
                events: {
                    'click .btn-primary': function () {
                        this.$('.alert').remove();
                        var reason = this.$('textarea').val().trim();
                        if ( reason !== '' ) {
                            me.model[isFlagged ? 'unflag' : 'flag'](reason);
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
                'body': '<div class="form-group"><label>Reason</label><textarea class="form-control"></textarea></div>',
                'footer': '<button class="btn btn-primary">Submit</button>'
            });

            modal.render();


        },
        unflag: function ( reason ) {
            this.model.unflag(reason);
        },

        trash: function () {
            if (!this.model.isTrashed()) {
                this.model.trash();
            }
        },

        files: function () {

            var me    = this;
            var model = this.model;

            var files   = S.util.instance('controller.Files', null, {
                tenant: this.cfg.tenant,
                comment: model
            });

            var view    = S.util.instance('view.Files', {
                collection: files
            });

            view.render();

            var Modal  = S.get('view.Modal');
            var modal  = new Modal({
                modal: { show: true }
            });

            if (this.model.get('flags') === 0) {

                var Dropbox = S.get('component.Dropbox');
                var db = new Dropbox({

                    element: view.el,
                    display: view.$('.cs-uploads-list'),

                    uploadPostKey: 'attachment',
                    target: S.url('ugc.{id}.add_attachment', model.toJSON()),
                    formData: { tenant: model.get('tenant') },

                    template: view.getTemplate('file'),
                    templateUploadError: [
                        /* jshint -W015 */
                        '<div class="alert alert-danger {{theme.fileError}}">',
                            '<span>An error has occurred trying to upload <i>"{{file.name}}"</i>.</span>', ' ',
                            // TODO dismissing the message keeps the file UI container, remove it?
                            '<a onclick="this.parentNode.parentNode.removeChild(this.parentNode)">Dismiss Message</a>',
                        '</div>'
                    ].j(),

                    theme: {
                        fileDisplay: 'crafter-social-view crafter-social-file-view'
                    }

                });

                db.on(Dropbox.UPLOAD_SUCCESS_EVENT, function (data) {
                    view.uploadComplete(data);
                    me.model.fetch();
                });

                db.on(Dropbox.UPLOAD_ERROR_EVENT, function (data) {

                    var modal;
                    var XHR = data.XHR;

                    if (XHR.status === 502) {

                        modal = new S.view.Modal({
                            events: { 'click [data-dismiss]': 'destroy' },
                            modal: { show: true }
                        }).render();

                        modal.set({
                            title: 'Error',
                            body: 'The file you submitted surpasses the maximum file size.',
                            footer: '<button data-dismiss class="btn btn-default">Close</button>'
                        });

                    } else if (XHR.status === 422) {

                        modal = new S.view.Modal({
                            events: { 'click [data-dismiss]': 'destroy' },
                            modal: { show: true }
                        }).render();

                        modal.set({
                            title: 'Error',
                            body: 'A virus has been detected on the file. File was not uploaded.',
                            footer: '<button data-dismiss class="btn btn-default">Close</button>'
                        });

                    } else if (XHR.status === 401) {

                        Director.trigger(S.Constants.get('EVENT_UNAUTHORISED_RESPONSE'));

                    }

                });

            }

            modal.set('title', 'File Attachments');
            modal.set('body', view.el);
            modal.set('footer', '<button class="btn btn-default" data-dismiss="modal">Close</button>');

            modal.render();

            if (this.model.get('flags') > 0) {
                modal.$('.modal-body').prepend([
                    '<div class="alert alert-warning">',
                        'This comment is flagged. File attachments are disabled.',
                    '</div>'
                ].join(''));
            }

            files.fetch();

        },

        remove: function () {
            this.$el.remove();
            this.trigger('remove', this.model);
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