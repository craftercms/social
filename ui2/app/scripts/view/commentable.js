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
        reveal: true,
        timeout: null,
        className: 'crafter-social-commentable',
        revealed: false,
        events: {
            'mouseenter': 'mouseenter'
        },

        initialize: function (config) {
            var me = this;
            this.pageWidth = $(window).width();
            var isMobile = this.pageWidth < 768;

            config.sortOrder && S.Cfg('global.threadSortOrder', config.sortOrder);
            config.sortBy && S.Cfg('global.threadSortBy', config.sortBy);

            this.setElement(config.target);
            Base.prototype.initialize.apply(this, arguments);
            this.delegateActions(this, this.$options);

            this.collection.fetch({
                data: {
                    id: this.cfg.target
                }
            });

            if ((!isMobile && config.discussionView == "view.Inline") || (isMobile && config.mobileExpanded == true)) {
                setTimeout(function () {
                    me.revealDiscussion();
                }, 500);
            }

            setTimeout(function () {
                me.mouseenter();
            }, 500);

        },
        listen: function () {

            var me = this;

            $(S.window).resize(function () {
                me.windowWidthChanged()
            });

            this.listenTo(this.collection, 'sync', this.render);
            this.listenTo(this.collection, C.get('EVENT_DISCUSSION_WATCHED', this.cfg), this.setWatched);

            this.listenTo(Director, C.get('EVENT_AREAS_VISIBILITY_CHANGE'), this.visibilityModeChanged);
            this.listenTo(Director, C.get('EVENT_REVEAL_DISCUSSIONS', this.cfg), this.revealDiscussion);

        },
        createUI: function () {

            var me = this, $elem = this.element();
            $elem.addClass('crafter-social-commentable');

            var $options = $(U.template(this.getTemplate('main'), {count: ''}));
            $options.find('a.action').tooltip();
            //$options.mouseenter(function () { clearTimeout(me.timeout);  });
            //$options.mouseleave(function () { me.timeout = setTimeout(function () { me.mouseleave(); }); });

            $options.css({
                right: 0,
                top: 0
            });
            this.$options = $options;
            this.$el.append($options);

        },
        render: function () {

            var $badge = this.$options.find('.badge'),
                length = this.collection.length;
            var targetId = this.cfg.target;
            this.$options.find('#socialCommentBadge').attr('href', '#' + targetId.substring(1, targetId.length) + '-comments');
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
                    this.$el.is(':visible') && this.mouseleave();
                    break;
            }
        },

        inline: function () {

            var view = this.cache('view.Inline');

            if (view) {
                view.destroy();
                this.stopListening(view);
            }

            view = new S.view.Inline({
                target: this.cfg.target,
                context: this.cfg.context,
                collection: this.collection,
                commentUrl: this.cfg.commentUrl,
                commentThreadName: this.cfg.commentThreadName
            });

            view.render();
            view.show();

            this.cache('view.Inline', view);
            this.listenTo(view, 'view.change.request', this.viewChangeRequest);
            this.setActiveView(view);

        },
        lightbox: function () {
            var view = this.cache('view.Lightbox');
            if (!view) {
                view = new S.view.Lightbox({
                    target: this.cfg.target,
                    context: this.cfg.context,
                    collection: this.collection,
                    commentUrl: this.cfg.commentUrl,
                    commentThreadName: this.cfg.commentThreadName
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
                    collection: this.collection,
                    commentUrl: this.cfg.commentUrl,
                    commentThreadName: this.cfg.commentThreadName
                });
                this.cache('view.Popover', view);
                this.listenTo(view, 'visibility.change', this.popoverVisibilityDidChange);
                this.listenTo(view, 'view.change.request', this.viewChangeRequest);
                view.render();
            }
            view.show();
            this.setActiveView(view);
        },
        setActiveView: function (view) {
            this.activeView = view;
        },

        popoverVisibilityDidChange: function (visible) {
            this.$el[visible ? 'addClass' : 'removeClass']('revealed-by-view');
        },

        revealDiscussion: function (e) {

            var me = this;
            this.isMobile = $(window).width() < 768;

            if (!(e instanceof $.Event) && (typeof arguments[0] === 'object')) {
                var cfg = arguments[0];
                (cfg.view) && (this.cfg.discussionView = cfg.view);
            }

            if (this.isMobile && this.cfg.mobileView) {
                this.viewChangeRequest(this.cfg.mobileView);
            } else {
                this.viewChangeRequest(this.cfg.discussionView, true);
            }

            if (e && ((!this.isMobile && this.cfg.discussionView == "view.Inline") || (this.isMobile && this.cfg.mobileView == "view.Inline"))) {
                $('html, body').animate({
                    scrollTop: $($(e.currentTarget).attr('href')).offset().top
                }, 500);
            }

            $.each(['view.Popover', 'view.Lightbox', 'view.Inline'], function (i, v) {
                var view = me.cache(v);
                (view) && view.render();
            });

        },

        viewChangeRequest: function (requested, updateCfg) {

            var me = this;
            $.each(['view.Popover', 'view.Lightbox', 'view.Inline'], function (i, v) {
                var view = me.cache(v);
                (view) && view.hide();
            });

            /* jshint -W086 */
            switch (requested) {
                case 'view.Popover':
                {
                    this.popover();
                    break;
                }
                case 'view.Lightbox':
                {
                    this.lightbox();
                    break;
                }
                case 'view.Inline':
                {
                    this.inline();
                    break;
                }
            }

            if (updateCfg) {
                this.cfg.discussionView = requested;
            }

        },

        mouseenter: function () {
            var $elem = this.element();
            $elem.addClass(REVEAL_CLASS);

            //if (!this.hide) {
            //
            //    clearTimeout(this.timeout);
            //
            //    var $elem = this.element(),
            //        $options = this.$options;
            //
            //    $elem.addClass(REVEAL_CLASS);
            //
            //    $options.css({
            //        right: 0,
            //        top: 0
            //    });
            //
            //    $options.appendTo($elem).show();
            //
            //    //$options.hide();
            //
            //    this.revealed = true;
            //
            //}

        },
        mouseleave: function () {
            var me = this;
            me.element().removeClass(REVEAL_CLASS);

            //if ( !(this.reveal) ) {
            //
            //    var me = this;
            //    me.timeout = setTimeout(function () {
            //        me.element().removeClass(REVEAL_CLASS);
            //        me.$options.hide().detach();
            //        me.revealed = false;
            //    }, 10);
            //
            //}

        },
        windowWidthChanged: function () {
            var currentWidth = $(window).width(),
                me = this;

            if ((this.pageWidth >= 767 && currentWidth < 767) || (this.pageWidth <= 768 && currentWidth > 768)) {
                $.each(['view.Popover', 'view.Lightbox', 'view.Inline'], function (i, v) {
                    var view = me.cache(v);
                    (view) && view.hide();
                });

                if (((this.pageWidth <= 767 && currentWidth > 768) && this.cfg.discussionView == "view.Inline")
                    || ((this.pageWidth >= 767 && currentWidth < 768) && this.cfg.mobileExpanded == true)) {
                    setTimeout(function () {
                        me.revealDiscussion();
                        me.mouseenter();
                    }, 500);
                }
            }
            this.pageWidth = currentWidth;

            if (me.revealed) {
                me.mouseenter();
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

})(crafter.social);
