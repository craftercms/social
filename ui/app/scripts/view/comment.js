(function (S) {
    'use strict';

    var Base = S.view.Base,
        Comment = S.model.Comment,
        U = S.util,
        $ = S.$;

    var CommentView = Base.extend({
        createUI: U.emptyFn,
        events: {
            'click [data-action-like]': 'like',
            'click [data-action-reply]': 'reply',
            'click [data-action-flag]': 'flag'
        },
        initialize: function (config) {
            var newConfig = $.extend(true, {}, CommentView.DEFAULTS, config);
            Base.prototype.initialize.call(this, newConfig);
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
            this.model.reply();
        },
        flag: function (e) {
            e.preventDefault();
            this.model.flag();
        },
        remove: function () {

        }
    });

    CommentView.DEFAULTS = {
        classes: 'crafter-social-comment',
        templates: {
            main: ('%@comment.hbs').fmt(S.Cfg('url.templates'))
        }
    };

    S.define('view.Comment', CommentView);

}) (crafter.social);