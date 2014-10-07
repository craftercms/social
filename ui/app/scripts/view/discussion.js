(function (S) {
    'use strict';

    var Discussion,
        Base = S.view.Base,
        $ = S.$;

    Discussion = Base.extend({

        className: [
            Base.prototype.className,
            'crafter-social-discussion-view'
        ].join(' '),

        listen: function () {
            var collection = this.collection;
            if (collection) {
                this.listenTo(collection, 'add', this.addOne);
                this.listenTo(collection, 'sync', this.addAll);
            }
        },

        createUI: function () {

            Base.prototype.createUI.call(this);

            this.cfg.initCommentingView && this.initCommentingView();

            var $opts = this.$('.options-view-container');
            if ($opts.size()) {

                var options = new S.view.Options($.extend({
                    target: this.cfg.target,
                    context: this.cfg.context,
                    collection: this.collection
                }, this.cfg.viewOptions || {}));

                options.render();
                $opts.append(options.el);

                this.listenTo(options, 'view.change.request', this.changeView);
                this.listenTo(options, 'view.close.request', this.hide || S.util.emptyFn);

            }

        },

        initCommentingView: function () {

            var $replies        = this.$('.reply-box');
            var profile         = S.getDirector().getProfile();
            var isSocialAuthor  = profile.hasRole('SOCIAL_AUTHOR') || profile.hasRole('SOCIAL_SUPERADMIN');

            (!isSocialAuthor) && $replies.hide();

            if ($replies.size() && isSocialAuthor) {

                var view = new S.util.instance('view.Commenting', $.extend({
                    collection: this.collection,
                    context: this.cfg.context,
                    target: this.cfg.target
                }, this.cfg.commenting));

                $replies.append(view.render().el);
                this.cache('commentingView', view);

            }

        },

        changeView: function ( view ) {
            this.trigger('view.change.request', view);
        },

        addAll: function () {
            this.$('.comments:first').html('');
            this.collection.each(this.addOne, this);
        },

        addOne: function (comment) {
            this.$('.no-comments').remove();
            var view = new S.view.Comment({
                model: comment,
                context: this.cfg.context
            });
            this.$('.comments:first').append(view.render().element());
        }

    });

    Discussion.DEFAULTS = $.extend({}, Base.DEFAULTS, {
        initCommentingView: true,
        commenting: {
            editor: {
                'extraPlugins': 'autogrow',
                'autoGrow_maxHeight': 800,
                // Remove the Resize plugin as it does not make sense to use it in conjunction with the AutoGrow plugin.
                'removePlugins': 'resize'
            }
        }
    });

    S.define('view.Discussion', Discussion);

}) (crafter.social);