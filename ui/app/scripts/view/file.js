(function (S) {
    'use strict';

    var FileView                ,
        Base    = S.view.Base   ,
        U       = S.util,
        $       = S.$           ;

    FileView = Base.extend({

        createUI: null,

        className: [
            Base.prototype.className,
            'crafter-comments-file-view'
        ].join(' '),

        open: function () {

        },

        detach: function () {

        },

        render: function () {

            var tpl = this.getTemplate('main');
            this.$el.html(U.template(tpl, this.model.toJSON()));

            return this;

        }

    });

    FileView.DEFAULTS = $.extend(true, {}, Base.DEFAULTS, {
        templates: {
            main: function () {
                return S.string.fmt('%@file.hbs', S.Cfg('url.templates'));
            }
        }
    });

    S.define('view.File', FileView);

}) (crafter.social);