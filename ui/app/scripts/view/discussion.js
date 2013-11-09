(function (S) {
    'use strict';

    var Discussion,
        Base = S.view.Base,
        $ = S.$,
        SPACE = ' ';

    Discussion = Base.extend({
        initialize: function (config) {

            /* jshint -W014 */
            (config.classes) &&
                (config.classes = (S.util.isArray(config.classes)
                    ? config.classes
                    : config.classes.split(SPACE)).concat(Discussion.DEFAULTS.classes));

            var newConfig = $.extend(true, {}, Discussion.DEFAULTS, config);
            Base.prototype.initialize.call(this, newConfig);
        },
        listen: function () {
            var collection = this.collection;
            this.listenTo(collection, 'add', this.addOne);
            this.listenTo(collection, 'sync', this.addAll);
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

    Discussion.DEFAULTS = {
        classes: 'crafter-social-discussion-view',
        templates: {
            main: ('%@discussion.hbs').fmt(S.Cfg('url.templates'))
        },
        commenting: {
            editor: {
                'extraPlugins': 'autogrow',
                'autoGrow_maxHeight': 800,
                // Remove the Resize plugin as it does not make sense to use it in conjunction with the AutoGrow plugin.
                'removePlugins': 'resize'
            }
        }
    };

    S.define('view.Discussion', Discussion);

}) (crafter.social);