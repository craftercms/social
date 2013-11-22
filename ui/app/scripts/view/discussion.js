(function (S) {
    'use strict';

    var Discussion,
        Base = S.view.Base,
        $ = S.$;

    Discussion = Base.extend({

        className: [
            Base.prototype.className,
            'crafter-social-discussion-view'
        ].join(' '),

        listen: function () {
            var collection = this.collection;
            if (collection) {
                this.listenTo(collection, 'add', this.addOne);
                this.listenTo(collection, 'sync', this.addAll);
            }
        },

        createUI: function () {

            Base.prototype.createUI.call(this);

            var view = new S.util.instance('view.Commenting', $.extend({
                collection: this.collection,
                tenant: this.cfg.tenant,
                target: this.cfg.target
            }, this.cfg.commenting));

            this.$('.reply-box').append(view.render().el);
            this.cache('commentingView', view);

        },

        addAll: function () {
            this.$('.comments:first').html('');
            this.collection.each(this.addOne, this);
        },

        addOne: function (comment) {
            this.$('.no-comments').remove();
            var view = new S.view.Comment({ model: comment });
            this.$('.comments:first').append(view.render().element());
        }

    });

    Discussion.DEFAULTS = $.extend({}, Base.DEFAULTS, {
        commenting: {
            editor: {
                'extraPlugins': 'autogrow',
                'autoGrow_maxHeight': 800,
                // Remove the Resize plugin as it does not make sense to use it in conjunction with the AutoGrow plugin.
                'removePlugins': 'resize'
            }
        }
    });

    S.define('view.Discussion', Discussion);

}) (crafter.social);