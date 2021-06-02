(function (S) {
    'use strict';


    if (!S.$.fn.popover) {
        throw new Error('Popover requires bootstrap popover');
    }

    var Popover,
        Base = S.view.Discussion,
        $ = S.$;

    var prototype = $.extend({}, $.fn.popover.Constructor.prototype, {

        className: [
            Base.prototype.className,
            'crafter-social-popover',
            // TODO get rid of twitter default classes
            'popover',
            'fade'
        ].join(' '),

        events: { 'click button.close': 'hide' },

        initialize: function () {

            this.visible = false;

            Base.prototype.initialize.apply(this, arguments);

            this.init('popover', this.cfg.target, this.cfg.popover);
            this.$tip = this.element();

            this.addAll();

        },
        addOne: function () {
            Base.prototype.addOne.apply(this, arguments);
            // TODO: placement is not working well
            // this.replacement();
        },
        listen: function () {
            Base.prototype.listen.apply(this, arguments);

            var me = this;
            $(this.cfg.target)
                .on('hidden.bs.popover', function () {
                    me.trigger('visibility.change', false);
                    me.visible = false;
                })
                .on('shown.bs.popover', function () {

                    var view = me.cache('commentingView');
                    view && view.editor();

                    me.trigger('visibility.change', true);
                    me.visible = true;
                });

        },

        render: function () {

            Base.prototype.render();
            // TODO see how this works.
            // Are users comfortable with this behaviour or is it rather irksome?
            var $elem = this.$('.crafter-social-comment-thread');
            $elem.on('mousewheel DOMMouseScroll', function (e) {
                var top     = this.scrollTop;
                var delta   = (e.originalEvent.detail < 0) || (e.originalEvent.wheelDelta > 0) ? 1 : -1;

                if (delta < 0 && ($elem.outerHeight() + this.scrollTop) === this.scrollHeight) {
                    e.preventDefault();
                } else if (delta >= 0 && top === 0) {
                    e.preventDefault();
                }

            });

            return this;
        },

        refresh: function () {
            this.collection.fetch({
                data : {
                    target: this.cfg.target,
                    context: this.cfg.context
                }
            });
        },

        replacement: function () {

            this.show();

            /* TODO create fully auto placement

            var $tip         = this.tip();
            var pos          = this.getPosition();
            var actualWidth  = $tip[0].offsetWidth;
            var actualHeight = $tip[0].offsetHeight;

            var $parent = this.$element.parent();
            var placement = (typeof this.options.placement === 'function')
                ? this.options.placement.call(this, $tip[0], this.$element[0])
                : this.options.placement;

            var orgPlacement = placement;
            var docScroll    = document.documentElement.scrollTop || document.body.scrollTop;
            var parentWidth  = this.options.container === 'body' ? window.innerWidth  : $parent.outerWidth();
            var parentHeight = this.options.container === 'body' ? window.innerHeight : $parent.outerHeight();
            var parentLeft   = this.options.container === 'body' ? 0 : $parent.offset().left;

            var points = {
                bottom: (pos.top + pos.height + actualHeight - docScroll),
                top: (pos.top - docScroll - actualHeight),
                right: (pos.right + actualWidth),
                left: (pos.left - actualWidth)
            };

            var best = null, bvalue = null;
            $.each(points, function (key, value) {
                if ((!best) || (bvalue < value)) {
                    best = key;
                    bvalue = value;
                }
            });

            console.log('best position: ', best);

            placement =
                (placement === 'bottom') && ((pos.top + pos.height + actualHeight - docScroll) > parentHeight)
                    ? 'top'
                    : (placement === 'top')  && ((pos.top - docScroll - actualHeight) < 0)
                        ? 'bottom'
                        : (placement === 'right') && ((pos.right + actualWidth) > parentWidth)
                            ? 'left'
                            : (placement === 'left') && ((pos.left - actualWidth) < parentLeft)
                                ? 'right'
                                : placement;

            switch (placement) {
                case 'bottom' : {
                    placement = '';
                }
                case 'top' : {
                    placement = '';
                }
                case 'right' : {
                    placement = '';
                }
                case 'left' : {
                    placement = '';
                }
            }

            $tip.removeClass(orgPlacement)
                .addClass(placement);

            var calculatedOffset = this.getCalculatedOffset(
                placement, pos, actualWidth, actualHeight);

            this.applyPlacement(calculatedOffset, placement);

            */

        },

        /*
         * Popover overrides
         */

        hide: function () {

            var view = this.cache('commentingView');
            view && view.editor('destroy');

            var e = $.Event('hide.bs.' + this.type);

            this.$tip.removeClass('in');
            if (this.hoverState !== 'in') {
                this.$tip.detach();
            }

            this.$element.trigger(e);
            this.$element.trigger('hidden.bs.' + this.type);

            return this;

        },

        getDefaults: function () {
            return Popover.DEFAULTS;
        },
        getOptions: function (options) {
            options = $.extend({}, this.getDefaults(), options, this.$element.data());

            if (options.delay && typeof options.delay === 'number') {
                options.delay = {
                    show: options.delay,
                    hide: options.delay
                };
            }

            return options;
        },
        setContent: function () {
            this.tip().removeClass('fade top bottom left right in hide');
        },
        tip: function () {
            return this.element();
        }

    });

    // Constructor cases some trouble with Backbone extends
    // backbone sort of extends the constructor
    delete(prototype.constructor);
    Popover = Base.extend(prototype);

    Popover.DEFAULTS = $.extend({}, Base.DEFAULTS, {
        templates: {
            main: ('%@popover.hbs').fmt(S.Cfg('url.templates')),
            comment: ('%@comment.hbs').fmt(S.Cfg('url.templates'))
        },
        popover: $.extend({}, $.fn.popover.Constructor.DEFAULTS, {
            // TODO auto placement is not working well
            placement: 'bottom',
            trigger: 'manual',
            container: 'body',
            content: '(not.empty)'
        }),
        viewOptions: {
            hidden: ['close', 'popover.request']
        }
    });

    S.define('view.Popover', Popover);

})(crafter.social);