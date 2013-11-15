(function (S) {
    'use strict';

    var REVEAL_CLASS = 'reveal';

    var Commentable,
        Base = S.view.Base,
        C = S.Constants,
        U = S.util,
        $ = S.$;

    var setTimeout = S.window.setTimeout,
        clearTimeout = S.window.clearTimeout;

    Commentable = Base.extend({

        hide: false,
        reveal: false,
        timeout: null,
        className: 'crafter-social-commentable',
        revealed: false,
        events: {
            'mouseenter' : 'mouseenter',
            'mouseleave' : 'mouseleave'
        },

        initialize: function (config) {

            this.setElement(config.target);
            Base.prototype.initialize.apply(this, arguments);
            this.delegateActions(this, this.$options);

            this.collection.fetch({
                data : {
                    target: this.cfg.target,
                    tenant: this.cfg.tenant
                }
            });

        },
        listen: function () {

            var me = this;
            $(S.window).resize(function () {
                if ( me.revealed ) {
                    me.mouseenter();
                }
            });

            this.listenTo(this.collection, 'sync', this.render);
            this.listenTo(S.getDirector(), C.get('EVENT_AREAS_VISIBILITY_CHANGE'), this.visibilityModeChanged);
        },
        createUI: function () {

            var me = this, $elem = this.element();
            $elem.addClass('crafter-social-commentable');

            var $options = $(U.template(this.getTemplate('main'), { count: '' }));
            $options.find('a.action').tooltip();
            $options.mouseenter(function () { clearTimeout(me.timeout);  });
            $options.mouseleave(function () { me.timeout = setTimeout(function () { me.mouseleave(); }); });

            this.$options = $options;

        },
        render: function () {

            var $badge = this.$options.find('.badge'),
                length = this.collection.length;

            if (length === 0) {
                $badge.text('');
            } else {
                $badge.text(length);
            }

            return this;

        },

        /* jshint -W015 */
        visibilityModeChanged: function (mode) {
            switch (mode) {
                case C.get('AREA_VISIBILITY_MODE_REVEAL'):
                    this.hide = false;
                    this.reveal = true;
                    this.$el.is(':visible') && this.mouseenter();
                    break;
                case C.get('AREA_VISIBILITY_MODE_HOVER'):
                    this.hide = false;
                    this.reveal = false;
                    this.$el.is(':visible') && this.mouseleave();
                    break;
                case C.get('AREA_VISIBILITY_MODE_HIDE'):
                    this.hide = true;
                    this.reveal = false;
                    break;
            }
        },

        showFullView: function () {
            var view = this.cache('view.lightbox');
            if (!view) {
                view = new S.view.Lightbox({
                    target: this.cfg.target,
                    tenant: this.cfg.tenant,
                    collection: this.collection
                });
                this.cache('view.lightbox', view);
                view.render();
            }
            view.show();
        },

        showDiscussionBubble: function () {
            var view = this.cache('view.popover');
            if (!view) {
                view = new S.view.Popover({
                    target: this.cfg.target,
                    tenant: this.cfg.tenant,
                    collection: this.collection
                });
                this.cache('view.popover', view);
                view.render();
            }
            view.show();
        },

        watch: function (e) {
            // TODO: what's backend like?
            S.request({
                url: S.url('notifications.add', {
                    target: this.cfg.target,
                    title: '',
                    url: ''
                }),
                success: function () {
                    $(e.target).css('color', 'green');
                    console.log(arguments);
                },
                error: function () {
                    console.log(arguments);
                }
            });
        },

        mouseenter: function (  ) {

            if (!this.hide) {

                clearTimeout(this.timeout);

                var $elem = this.element(),
                    $options = this.$options;

                $elem.addClass(REVEAL_CLASS);

                var offset = $elem.offset(),
                    width = $elem.outerWidth();

                $options.appendTo('body').show();
                var optsWidth = $options.outerWidth();
                $options.hide();

                $options.css({
                    left: (offset.left + width - optsWidth),
                    top: offset.top
                }).show();

                this.revealed = true;

            }

        },
        mouseleave: function (  ) {

            if ( !(this.reveal) ) {

                var me = this;
                me.timeout = setTimeout(function () {
                    me.element().removeClass(REVEAL_CLASS);
                    me.$options.hide().detach();
                    me.revealed = false;
                }, 10);

            }

        }

    });

    Commentable.DEFAULTS = {
        templates: {
            main: ('%@commentable.hbs').fmt(S.Cfg('url.templates'))
        }
    };

    S.define('view.Commentable', Commentable);

}) (crafter.social);