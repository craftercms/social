(function (S) {
    'use strict';

    var Class,
        Superclass = S.view.barget.Base,
        C = S.Constants,
        $ = S.$;

    // TODO make classes configurable?
    var STAR_EMPTY_CLASS = 'cs-icon-star-empty',
        STAR_CLASS = 'cs-icon-star',
        RATED_CLASS = 'rated';

    Class = Superclass.extend({

        icon: 'star',
        title: 'Rate',
        events: {
            'mouseover i'   : 'highlight',
            'mouseleave i'  : 'dehighlight',
            'click i'       : 'select',
            'click button'  : 'submit'
        },
        initialize: function (config) {
            var newConfig = $.extend({}, Class.DEFAULTS, config);
            Superclass.prototype.initialize.call(this, newConfig);
            this.$('.star-rating').find('i').tooltip();
        },

        highlight: function ( e ) {
            var $me = $(e.target);
            if (!$me.hasClass('rated')) {

                $me.parent().find('i:not(.rated)')
                    .removeClass(STAR_CLASS)
                    .addClass(STAR_EMPTY_CLASS);
                $me.prevAll().add($me)
                    .removeClass(STAR_EMPTY_CLASS)
                    .addClass(STAR_CLASS);
            }
        },
        dehighlight: function ( e ) {
            var $me = $(e.target);
            if (!$me.hasClass('rated')) {
                $me.parent().find('i:not(.rated)').removeClass(STAR_CLASS).addClass(STAR_EMPTY_CLASS);
            }
        },
        select: function ( e ) {
            var $me = $(e.target),
                $iElements = $me.parent().find('i'),
                previouslyRatedFirst = ($me.hasClass('rated') && $me.get(0) === $iElements.get(0) && $iElements.filter('.rated').length === 1),
                classes = '%@ %@'.fmt(STAR_CLASS, RATED_CLASS);

            $iElements
                .removeClass(classes)
                .addClass(STAR_EMPTY_CLASS);

            if (!previouslyRatedFirst) {
                $me.prevAll().add($me)
                    .removeClass(STAR_EMPTY_CLASS)
                    .addClass(classes);
            }
        },
        submit: function () {
            this.message({ msg: 'Page Rated Successfully.', icon: 'ok', callback: function () { this.hide() } });
        }

    });

    Class.DEFAULTS = {
        classes: ['crafter-social-bar-form', 'crafter-social-bar-rate'],
        templates: {
            main: ('%@rate-barget.hbs').fmt(S.Cfg('url.templates'))
        }
    };

    S.define('view.barget.Rate', Class);

}) (crafter.social);