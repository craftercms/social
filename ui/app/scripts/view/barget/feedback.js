(function (S) {
    'use strict';

    var Class,
        Superclass = S.view.barget.Base,
        C = S.Constants,
        $ = S.$;

    Class = Superclass.extend({

        icon: 'sort',
        title: 'Feedback',
        events: {
            'click button[type="submit"]' : 'submit',
            'click button[type="cancel"]' : 'hide'
        },
        initialize: function (config) {
            var newConfig = $.extend({}, Class.DEFAULTS, config);
            Superclass.prototype.initialize.call(this, newConfig);
        },

        submit: function () {
            this.$('input[type="text"], textarea, select').val('');
            this.$('input[type="radio"]').attr('checked', false);
            this.message({ msg: 'Message sent. Thanks for your feedback.', icon: 'ok', callback: function () { this.hide() } });
        }

    });

    Class.DEFAULTS = {
        classes: ['crafter-social-bar-form', 'crafter-social-bar-feedback'],
        templates: {
            main: ('%@feedback-barget.hbs').fmt(C.TEMPLATES_URL)
        }
    };

    S.define('view.barget.Feedback', Class);

}) (crafter.social);