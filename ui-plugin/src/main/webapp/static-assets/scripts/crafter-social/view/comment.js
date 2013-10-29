(function (S) {

    var SocialUI = S.view.SocialUI,
        app = S.Orchestrator,
        C = S.Constants,
        U = S.util,
        $ = S.$;

    var CommentView = SocialUI.extend({
        createUI: U.emptyFn,
        className: 'crafter-social-view crafter-social-comment',
        events: {
            'click [data-action-like]': 'like',
            'click [data-action-reply]': 'reply',
            'click [data-action-flag]': 'flag'
        },
        initialize: function (config) {
            var newConfig = $.extend(true, {}, CommentView.DEFAULTS, config);
            SocialUI.prototype.initialize.call(this, newConfig);
        },
        listen: function () {
            this.listenTo(this.model, 'change', this.render);
            this.listenTo(this.model, 'destroy', this.remove);
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
        templates: {
            main: ('%@comment.hbs').fmt(C.TEMPLATES_URL)
        }
    }

    S.define('view.Comment', CommentView);

}) (crafter.social);