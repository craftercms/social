(function (S) {
    'use strict';

    var Class,
        Superclass = S.view.barget.Base,
        C = S.Constants;

    Class = Superclass.extend({

        icons: {
            reveal: 'cs-icon-eye-open',
            hover: 'cs-icon-hand-up'
        },

        icon: 'eye-open',
        title: 'Discussion',
        render: S.util.emptyFn,
        createUI: S.util.emptyFn,
        events: {
            'button .submit' : 'submit',
            'keypress .form-control' : 'keypress'
        },

        listen: function () {
            this.listenTo(S.getDirector(), C.get('EVENT_AREAS_VISIBILITY_CHANGE'), this.visibilityModeChanged);
        },

        visibilityModeChanged: function (mode) {
            switch (mode) {

                case C.get('AREA_VISIBILITY_MODE_REVEAL'):
                    this.$trigger
                        .find('.text:first')
                            .text('Hover')
                            .end()
                        .find('i')
                            .removeClass(this.icons.reveal)
                            .addClass(this.icons.hover);
                    break;
                case C.get('AREA_VISIBILITY_MODE_HIDE'):
                case C.get('AREA_VISIBILITY_MODE_HOVER'):
                    this.$trigger
                        .find('.text:first')
                            .text('Reveal')
                            .end()
                        .find('i')
                            .removeClass(this.icons.hover)
                            .addClass(this.icons.reveal);
                    break;
            }
        },

        activate: function ( $trigger ) {

            if (S.getDirector().getAreaVisibilityMode() !== C.get('AREA_VISIBILITY_MODE_REVEAL')) {
                S.getDirector().setAreaVisibilityMode(C.get('AREA_VISIBILITY_MODE_REVEAL'));
            } else {
                S.getDirector().setAreaVisibilityMode(C.get('AREA_VISIBILITY_MODE_HOVER'));
            }

        }

    });

    S.define('view.barget.Reveal', Class, true);

}) (crafter.social);