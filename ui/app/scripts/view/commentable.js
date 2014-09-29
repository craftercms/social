(function (S) {
    'use strict';

    var REVEAL_CLASS = 'reveal';

    var Commentable,
        Base = S.view.Base,
        C = S.Constants,
        Director = S.getDirector(),
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
                data : { id: this.cfg.target }
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
            this.listenTo(this.collection, C.get('EVENT_DISCUSSION_WATCHED', this.cfg), this.setWatched);

            this.listenTo(Director, C.get('EVENT_AREAS_VISIBILITY_CHANGE'), this.visibilityModeChanged);
            this.listenTo(Director, C.get('EVENT_REVEAL_DISCUSSIONS', this.cfg), this.revealDiscussion);

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

            var isWatched = this.collection.getIsWatched();
            this.setWatched(isWatched);

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
                    this.$el.is(':visible') && this.mouseleave();
                    break;
            }
        },

        inline: function () {
            var view = this.cache('view.Inline');
            if (!view) {
                view = new S.view.Inline({
                    target: this.cfg.target,
                    context: this.cfg.context,
                    collection: this.collection
                });
                this.cache('view.Inline', view);
                this.listenTo(view, 'view.change.request', this.viewChangeRequest);
                view.render();
            }
            view.show();
            this.setActiveView(view);
        },
        lightbox: function () {
            var view = this.cache('view.Lightbox');
            if (!view) {
                view = new S.view.Lightbox({
                    target: this.cfg.target,
                    context: this.cfg.context,
                    collection: this.collection
                });
                this.cache('view.Lightbox', view);
                this.listenTo(view, 'view.change.request', this.viewChangeRequest);
                view.render();
            }
            view.show();
            this.setActiveView(view);
        },
        popover: function () {
            var view = this.cache('view.Popover');
            if (!view) {
                view = new S.view.Popover({
                    target: this.cfg.target,
                    context: this.cfg.context,
                    collection: this.collection
                });
                this.cache('view.Popover', view);
                this.listenTo(view, 'visibility.change', this.popoverVisibilityDidChange);
                this.listenTo(view, 'view.change.request', this.viewChangeRequest);
                view.render();
            }
            view.show();
            this.setActiveView(view);
        },
        setActiveView: function ( view ) {
            this.activeView = view;
        },

        popoverVisibilityDidChange: function ( visible ) {
            this.$el[visible ? 'addClass' : 'removeClass']('revealed-by-view');
        },

        revealDiscussion: function ( e ) {

            if ( !(e instanceof $.Event) && (typeof arguments[0] === 'object') ) {
                var cfg = arguments[0];
                (cfg.view) && (this.cfg.discussionView = cfg.view);
            }

            this.viewChangeRequest(this.cfg.discussionView);

        },
        viewChangeRequest: function ( requested ) {

            var me = this;
            $.each(['view.Popover','view.Lightbox','view.Inline'], function (i, v) {
                var view = me.cache(v);
                (view) && view.hide();
            });

            /* jshint -W086 */
            switch (requested) {
                case 'view.Popover': {
                    this.popover();
                    break;
                }
                case 'view.Lightbox': {
                    this.lightbox();
                    break;
                }
                case 'view.Inline': {
                    this.inline();
                    break;
                }
            }

            this.cfg.discussionView = requested;

        },

        watch: function (/*e*/) {
            var collection = this.collection;
            var watched = collection.getIsWatched();
            // TODO: what's backend like?
            S.request({
                type: 'POST',
                context: this,
                url: S.url((watched ? 'subscriptions.unsubscribe' : 'subscriptions.subscribe'), {
                    target: this.cfg.target
                }),
                success: function () {
                    collection.setIsWatched(!watched);
                },
                error: function () {

                }
            });
        },
        setWatched: function ( isWatched ) {
            var $elem = this.$options.find('[data-action="watch"]');
            $elem.parents('li:first')[ (isWatched === null) ? 'addClass' : 'removeClass' ]('hide');
            $elem.show().css('color', isWatched ? 'green' : '');
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
            main: function () {
                return S.string.fmt('%@commentable.hbs', S.Cfg('url.templates'));
            }
        },
        discussionView: 'view.Popover'
    };

    S.define('view.Commentable', Commentable);

}) (crafter.social);