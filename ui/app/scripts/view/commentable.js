(function ($, S) {
    'use strict';

    var REVEAL_CLASS = 'reveal';

    var Commentable,
        Superclass = S.view.Base,
        C = S.Constants,
        U = S.util;

    var setTimeout = S.window.setTimeout,
        clearTimeout = S.window.clearTimeout;

    Commentable = Superclass.extend({

        hide: false,
        reveal: false,
        timeout: null,
        className: 'crafter-social-commentable',
        events: {
            'mouseenter' : 'mouseenter',
            'mouseleave' : 'mouseleave'
        },

        initialize: function (config) {

            this.setElement(config.target);

            var newConfig = $.extend({}, Commentable.DEFAULTS, config);
            Superclass.prototype.initialize.call(this, newConfig);

            this.initializeActionDelegation(this, this.$options);

            this.collection.fetch({
                data : {
                    target: this.cfg.target,
                    tenant: this.cfg.tenant
                }
            });

        },
        listen: function () {
            this.listenTo(this.collection, 'sync', this.render);
            this.listenTo(S.component.Director, C.EVENT_AREAS_VISIBILITY_CHANGE, this.visibilityModeChanged);
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
                case C.AREA_VISIBILITY_MODE_REVEAL:
                    this.hide = false;
                    this.reveal = true;
                    this.mouseenter();
                    break;
                case C.AREA_VISIBILITY_MODE_HOVER:
                    this.hide = false;
                    this.reveal = false;
                    this.mouseleave();
                    break;
                case C.AREA_VISIBILITY_MODE_HIDE:
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

        mouseenter: function (  ) {

            if (!this.hide) {

                clearTimeout(this.timeout);

                var $elem = this.element(),
                    $options = this.$options,
                    offset = $elem.offset(),
                    width = $elem.outerWidth();

                $elem.addClass(REVEAL_CLASS);

                $options.appendTo('body').show();
                var optsWidth = $options.outerWidth();
                $options.hide();

                $options.css({
                    left: (offset.left + width - optsWidth),
                    top: offset.top
                }).show();

            }

        },
        mouseleave: function (  ) {

            if ( !(this.reveal) ) {

                var me = this;
                me.timeout = setTimeout(function () {
                    me.element().removeClass(REVEAL_CLASS);
                    me.$options.hide().detach();
                }, 10);

            }

        }
    });

    Commentable.DEFAULTS = {
        templates: {
            main: ('%@commentable.hbs').fmt(C.TEMPLATES_URL)
        }
    };

    S.define('view.Commentable', Commentable);

}) (jQuery, crafter.social);