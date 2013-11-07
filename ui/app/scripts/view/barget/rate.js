(function (S) {
    'use strict';

    var Class,
        Superclass = S.view.barget.Base,
        C = S.Constants,
        $ = S.$;

    // TODO make classes configurable

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
                    .removeClass('glyphicon-star')
                    .addClass('glyphicon-star-empty');
                $me.prevAll().add($me)
                    .removeClass('glyphicon-star-empty')
                    .addClass('glyphicon-star');
            }
        },
        dehighlight: function ( e ) {
            var $me = $(e.target);
            if (!$me.hasClass('rated')) {
                $me.parent().find('i:not(.rated)').removeClass('glyphicon-star').addClass('glyphicon-star-empty');
            }
        },
        select: function ( e ) {
            var $me = $(e.target),
                $iElements = $me.parent().find('i'),
                previouslyRatedFirst = ($me.hasClass('rated') && $me.get(0) === $iElements.get(0) && $iElements.filter('.rated').length === 1);

            $iElements
                .removeClass('glyphicon-star rated')
                .addClass('glyphicon-star-empty');

            if (!previouslyRatedFirst) {
                $me.prevAll().add($me)
                    .removeClass('glyphicon-star-empty')
                    .addClass('rated glyphicon-star');
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