(function (S) {
    'use strict';

    var Class,
        Superclass = S.view.barget.Base,
        C = S.Constants;

    Class = Superclass.extend({

        icons: {
            enabled: 'cs-icon-remove-sign',
            disabled: 'cs-icon-ok-circle'
        },

        icon: 'remove-sign',
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
                case C.get('AREA_VISIBILITY_MODE_HOVER'):
                    this.$trigger
                        .find('.text:first')
                            .text('Disable')
                            .end()
                        .find('i')
                            .removeClass(this.icons.disabled)
                            .addClass(this.icons.enabled);
                    break;
                case C.get('AREA_VISIBILITY_MODE_HIDE'):
                    this.$trigger
                        .find('.text:first')
                            .text('Enable')
                            .end()
                        .find('i')
                            .removeClass(this.icons.enabled)
                            .addClass(this.icons.disabled);
                    break;

            }
        },

        activate: function ( $trigger ) {

            if (S.getDirector().getAreaVisibilityMode() !== C.get('AREA_VISIBILITY_MODE_HIDE')) {
                S.getDirector().setAreaVisibilityMode(C.get('AREA_VISIBILITY_MODE_HIDE'));
            } else {
                S.getDirector().setAreaVisibilityMode(C.get('AREA_VISIBILITY_MODE_HOVER'));
            }

        }

    });

    S.define('view.barget.Disable', Class, true);

}) (crafter.social);