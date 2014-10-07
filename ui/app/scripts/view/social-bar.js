(function (S) {
    'use strict';

    var Bar,
        Base = S.view.Base,
        U = S.util,
        $ = S.$;

    Bar = Base.extend({

        events: {
            'click .control-element': 'toggle',
            'click [data-activate-widget]': 'activate',
            'click .social-utilities a': function (e) {
                e.preventDefault();
            }
        },

        initialize: function (config) {
            var newConfig = $.extend({}, Bar.DEFAULTS, config);
            Base.prototype.initialize.call(this, newConfig);
            this.initializeWidgets();
        },
        initializeWidgets: function () {

            var widgetsCfg = this.cfg.widgets,
                $items = this.$('.social-utilities'),
                widgetTpl = this.getTemplate('widget'),
                widgets = {},
                me = this;

            $.each(widgetsCfg, function (i, widgetCfg) {
                // TODO because of the recursive extension (deep copy) of configs, the config-supplied array cant get rid of bargets without setting their index in the widgets array to nullish value
                if (!widgetCfg) { return; }

                var cls = widgetCfg.cls,
                    cfg = $.extend({}, (widgetCfg.cfg || {}), { bar: me }),
                    instance;

                (typeof widgetCfg.cls === 'string') && (cls = U.get(widgetCfg.cls));
                instance = new (cls.extend(cfg))(widgetCfg.instCfg);

                widgets[instance.guid] = instance;

                instance.$trigger = $(U.template(widgetTpl, instance));
                $items.append(instance.$trigger);

                // me.listenTo(instance, 'widget.display.change', this.refreshWidgetTrigger);
            });

            this.widgets = widgets;
        },

        activate: function ( e ) {
            var $elem = $( e.target ),
                id = $elem.data('activateWidget');
            (!id) && (id = $elem.parents('[data-activate-widget]:first').data('activateWidget'));
            $.each(this.widgets, function (guid, widget) { (id !== guid) && widget.hide(); });
            this.trigger('%@.activate'.fmt(id), $elem);
        },

        collapsed: false,
        _collapseData: null,
        toggle: function () {
            if (!this._collapseData) {
                var $ctrl = this.$('.control-element');
                this._collapseData = {
                    $ctrl: $ctrl,
                    width: this.$el.outerWidth(),
                    ctrlWidth: $ctrl.outerWidth()
                };
            }

            var $el = this.$el,
                cfg = this._collapseData;

            $el.animate({ right: this.collapsed ? 0 : (cfg.ctrlWidth - cfg.width) }, 100, function () {  });
            this.collapsed = !this.collapsed;

        }

    });

    /* jshint -W015 */
    Bar.DEFAULTS = {
        templates: {
            main: ('%@social-bar.hbs').fmt(S.Cfg('url.templates')),
            widget: [
                '<li class="subordinate">',
                    '<a data-activate-widget="{{guid}}">',
                        '<i class="crafter-social-icon cs-icon-{{icon}}"></i> <span class="text">{{title}}</span>',
                    '</a>',
                '</li>'
            ].j()
        },
        classes: ['crafter-social-bar'],
        widgets: [
            { cls: 'crafter.social.view.barget.Reveal', cfg: { title: 'Reveal' }, instCfg: {  } },
            { cls: 'crafter.social.view.barget.Disable', cfg: { title: 'Disable' }, instCfg: {  } }/*,
            { cls: 'crafter.social.view.barget.Feedback' },
            { cls: 'crafter.social.view.barget.Share' },
            { cls: 'crafter.social.view.barget.Rate' }*/
        ]
    };

    S.define('view.SocialBar', Bar);

}) (crafter.social);
