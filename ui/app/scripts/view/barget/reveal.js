(function (S) {
    'use strict';

    var Class,
        Superclass = S.view.barget.Base,
        C = S.Constants;

    Class = Superclass.extend({

        icons: {
            reveal: 'cs-icon-eye-close',
            hover: 'cs-icon-eye-open'
        },

        icon: 'eye-close',
        title: 'Discussion',
        render: S.util.emptyFn,
        createUI: S.util.emptyFn,
        events: {
            'button .submit' : 'submit',
            'keypress .form-control' : 'keypress'
        },

        listen: function () {
            this.listenTo(S.component.Director, C.get('EVENT_AREAS_VISIBILITY_CHANGE'), this.visibilityModeChanged);
        },

        visibilityModeChanged: function (mode) {
            switch (mode) {
                case C.get('AREA_VISIBILITY_MODE_REVEAL'):
                    this.$trigger
                        .find('i')
                            .removeClass(this.icons.reveal)
                            .addClass(this.icons.hover);
                    break;
                case C.get('AREA_VISIBILITY_MODE_HOVER'):
                    this.$trigger
                        .find('i')
                            .removeClass(this.icons.hover)
                            .addClass(this.icons.reveal);
                    break;
                // case C.get('AREA_VISIBILITY_MODE_HIDE'):
            }
        },

        activate: function ( $trigger ) {

            if (S.component.Director.getAreaVisibilityMode() !== C.get('AREA_VISIBILITY_MODE_REVEAL')) {
                S.component.Director.setAreaVisibilityMode(C.get('AREA_VISIBILITY_MODE_REVEAL'));
            } else {
                S.component.Director.setAreaVisibilityMode(C.get('AREA_VISIBILITY_MODE_HOVER'));
            }

        }

    });

    S.define('view.barget.Reveal', Class, true);

}) (crafter.social);