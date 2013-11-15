(function (S) {
    'use strict';

    var Base = S.view.Base,
        Comment = S.model.Comment,
        U = S.util,
        $ = S.$;

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
                this.listenTo(this.model, 'change', this.render);
                this.listenTo(this.model, 'destroy', this.remove);
            }
        },
        render: function () {

            var me          = this;
            var model       = this.model.toJSON();

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
        reply: function (e) {
            e.preventDefault();

//            var Commenting  = S.get('view.Commenting');
//            var commenting  = new Commenting(this.cfg.commenting);

        },
        flag: function (e) {
            e.preventDefault();

            var me = this;

            var modal = S.util.instance('view.Modal', {
                modal: { show: true },
                events: {
                    'click .btn-primary': function () {
                        var reason = this.$('textarea').val().trim();
                        (reason !== '') && me.model.flag(reason);
                        // TODO destroy after successful flagging
                        this.destroy();
                    }
                }
            });

            modal.set({
                'title': 'Flag Comment',
                'body': '<div class="form-group"><label>Reason</label><textarea class="form-control"></textarea></div>',
                'footer': '<button class="btn btn-primary">Submit</button>'
            });

            modal.render();

        },

        files: function () {

            var model = this.model;

            var files   = S.util.instance('controller.Files', {
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
            });

            modal.set('title', 'File Attachments');
            modal.set('body', view.el);
            modal.set('footer', '<button class="btn btn-default" data-dismiss="modal">Close</button>');

            modal.render();

            files.fetch();

        },

        remove: function () {

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