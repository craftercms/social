(function (S) {
    'use strict';

    var Widget,
        Base = S.view.Base,
        U = S.util,
        $ = S.$,
        SPACE = ' ';

    Widget = Base.extend({

        icon: '',
        title: '',
        shown: false,
        attributes: {
            'style': 'display: none;'
        },
        events: {
            'click [data-dismiss]' : 'hide',
            'click button.submit' : 'submit',
            'keypress .form-control' : 'keypress'
        },

        initialize: function (config) {

            (config.classes) &&
                (config.classes = (S.util.isArray(config.classes)
                    ? config.classes
                    : config.classes.split(SPACE)).concat(Widget.DEFAULTS.classes));

            Base.prototype.initialize.call(this, $.extend(true, {}, Widget.DEFAULTS, config));

            var bar = this.bar,
                guid = this.guid;

            this.listenTo(bar, '%@.activate'.fmt(guid), this.activate);

            this.render();

        },

        render: function () {
            this.$el.appendTo(this.bar.el);
        },

        activate: function ( $trigger ) {
            this[this.shown ? 'hide' : 'show']();
        },
        message: function (cfg) {
            var me = this;
            this.$el.prepend(U.template(this.getTemplate('message'), cfg));
            setTimeout(function () {
                me.$('.alert').remove();
                cfg.callback && cfg.callback.call(me, cfg);
            }, 3000);
        },
        toggle: function () {
            this.$el.toggle('fast');
            this.shown = !this.shown;
        },
        show: function () {
            this.$el.show('fast');
            this.shown = true;
        },
        hide: function () {
            this.$el.hide('fast');
            this.shown = false;
        }

    });

    Widget.DEFAULTS = {
        classes: ['crafter-social-bar-widget'],
        templates: {
            message: '<div class="alert alert-success"><i class="glyphicon glyphicon-{{icon}}"></i> {{msg}}</div>'
        }
    };

    S.define('view.barget.Base', Widget);

}) (crafter.social);