(function (S) {
    'use strict';

    var Class,
        Superclass = S.view.barget.Base,
        C = S.Constants,
        $ = S.$;

    Class = Superclass.extend({

        icon: 'share-alt',
        title: 'Share',
        events: {
            'click button' : 'submit',
            'keypress .form-control' : 'keypress'
        },
        initialize: function (config) {
            var newConfig = $.extend({}, Class.DEFAULTS, config);
            Superclass.prototype.initialize.call(this, newConfig);
            this.elemCache();
        },

        elemCache: function () {
            this.$input = this.$('input[type="text"]');
            this.$btn = this.$('button.submit');
        },

        keypress: function (e) {

        },
        submit: function () {
            this.$input.val('');
            this.message({ msg: 'Page shared successfully.', icon: 'ok', callback: function () { this.hide() } });
        }

    });

    Class.DEFAULTS = {
        classes: ['crafter-comments-bar-form', 'crafter-comments-bar-share'],
        templates: {
            main: ('%@share-barget.hbs').fmt(S.Cfg('url.templates'))
        }
    };

    S.define('view.barget.Share', Class, true);

}) (crafter.social);