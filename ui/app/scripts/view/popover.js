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

            Base.prototype.initialize.apply(this, arguments);

            this.init('popover', this.cfg.target, this.cfg.popover);
            this.$tip = this.element();

            this.collection.fetch({
                data : {
                    target: this.cfg.target,
                    tenant: this.cfg.tenant
                }
            });

        },
        addOne: function () {
            Base.prototype.addOne.apply(this, arguments);
            this.replacement();
        },
        listen: function () {
            Base.prototype.listen.apply(this, arguments);

            var $tip = this.tip();

            $tip.on('hidden.bs.popover', function () {
                $tip.hide();
            });

        },

        render: function () {
            Base.prototype.render();
            // TODO see how this works.
            // Are users confortable with this behaviour or is it rather irksome?
            var $bd         = $('body'),
                overflow    = $bd.css('overflow');
            this.$('.crafter-social-comment-thread')
                .mouseenter(function (  ) {
                    $bd.css('overflow', 'hidden');
                }).mouseleave(function (  ) {
                    $bd.css('overflow', overflow);
                });
            return this;
        },

        refresh: function () {
            this.collection.fetch({
                data : {
                    target: this.cfg.target,
                    tenant: this.cfg.tenant
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
            placement: 'auto bottom',
            trigger: 'manual',
            container: 'body',
            content: '(not.empty)'
        })
    });

    S.define('view.Popover', Popover);

})(crafter.social);