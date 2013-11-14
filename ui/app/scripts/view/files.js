(function (S) {
    'use strict';

    var Files,
        Base = S.view.Base,
        File = S.model.File,
        U = S.util,
        $ = S.$;

    Files = Base.extend({

        className: [
            Base.prototype.className,
            'crafter-social-files-view'
        ].join(' '),

        listen: function () {
            var collection = this.collection;
            this.listenTo(collection, 'add', this.addOne);
            this.listenTo(collection, 'sync', this.addAll);
        },

        addAll: function () {
            this.$('.cs-files-list:first').html('');
            this.collection.each(this.addOne, this);
        },

        addOne: function (file) {
            var view = new S.view.File({ model: file });
            this.$('.cs-files-list:first').append(view.render().el);
        },

        uploadComplete: function ( data ) {
            $(data.ui).remove();
            var attrs = U.fromJSON(data.e.target.responseText);
            var model = new File(attrs);
            this.addOne(model);
        }

    });

    Files.DEFAULTS = $.extend(true, {}, Base.DEFAULTS, {
        templates: {
            main: function () {
                return S.string.fmt('%@files.hbs', S.Cfg('url.templates'));
            },
            file: function () {
                return S.string.fmt('%@file.hbs', S.Cfg('url.templates'));
            }
        }
    });

    S.define('view.Files', Files);

}) (crafter.social);