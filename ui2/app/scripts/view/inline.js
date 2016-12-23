(function (S) {
    'use strict';

    var Inline,
        Discussion = S.view.Discussion,
        $ = S.$;

    Inline = Discussion.extend({

        className: [Discussion.prototype.className, 'crafter-social-inline-view', 'panel-group'].join(' '),

        render: function () {
            Discussion.prototype.render.apply(this, arguments);

            var me      = this;
            var panel   = this.$(me.cmpID('panel'));
            var toggler = this.$(this.cmpID('toggler'));
            var targetId = me.cfg.target;
            this.$(this.cmpID('toggler')).attr('name',targetId.substring(1,targetId.length)+'-comments');
            this.$(this.cmpID('toggler')).attr('id',targetId.substring(1,targetId.length)+'-comments');
            return this;
        },

        show: function () {

            this.$el.insertAfter(this.cfg.target);
            this.addAll();

            var view = this.cache('commentingView');
            view && view.editor();

        },

        hide: function () {

            var view = this.cache('commentingView');
            view && view.editor('destroy');

            this.$el.detach();

        },

        destroy: function () {
            var view = this.cache('commentingView');
            view && view.editor('destroy');
            this.$el.remove();
        }

    });

    Inline.DEFAULTS = $.extend({}, Discussion.DEFAULTS, {
        viewOptions: {
            hidden: ['inline.request']
        },
        templates: {
            /* jshint -W015 */
           main: ('%@inline.hbs').fmt(S.Cfg('url.templates'))
        }
    });

    S.define('view.Inline', Inline);

}) (crafter.social);
