(function (S) {
    'use strict';

    var Base = S.view.Base,
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

            this.$el.html(U.template(
                this.getTemplate('main'), this.model.toJSON()));

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