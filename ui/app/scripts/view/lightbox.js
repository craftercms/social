(function (S) {
    'use strict';

    // TODO Scope bootstrap stuff under social and clear $.fn.modal ? - same for popover view
    if (!S.$.fn.modal) {
        throw new Error('Lightbox requires bootstrap modal');
    }

    var Lightbox,
        Base = S.view.Discussion,
        $ = S.$;

    var prototype = $.extend({}, $.fn.modal.Constructor.prototype, {
        initialize: function (config) {

            var newConfig = $.extend(true, {}, Lightbox.DEFAULTS, config);
            Base.prototype.initialize.call(this, newConfig);

            this.initModal();

        },
        listen: function () {

            Base.prototype.listen.call(this);

            this.$el.on('shown.bs.modal', function () {
                $('body, html').css('overflow', 'hidden');
            });

            this.$el.on('hidden.bs.modal', function () {
                $('body, html').css('overflow', '');
            });

        },

        render: function () {

            var clone = $(this.cfg.target).clone(),
                $container = this.$('.target-container');

            // remove the classes added by the commentable view
            // TODO Commentable should tell this component which classes should be removed
            clone.removeClass('reveal crafter-social-commentable');

            $container.html('');
            $container.append(clone);

            this.$('.modal-title').text('Full Thread View');
            (this.cfg.modal.show) && this.show();

            this.collection.fetch({
                data : {
                    target: this.cfg.target,
                    tenant: this.cfg.tenant
                }
            });

            return this;

        },

        initModal: function () {

            var $this   = $(this.el);
            var data    = $this.data('bs.modal');

            ( !data ) && ( $this.data('bs.modal', this) );
            this.options   = this.cfg.modal;
            this.$element  = $this;
            this.$backdrop = this.isShown = null;

            ( this.options.remote ) && this.$element.load(this.options.remote);

            // TODO required?
            // if (typeof option == 'string') data[option](_relatedTarget)
            // else if (options.show) data.show(_relatedTarget)

        }
    });

    // Constructor casused some trouble with Backbone extends
    delete(prototype.constructor);
    Lightbox = Base.extend(prototype);

    Lightbox.DEFAULTS = $.extend({}, {
        /** @see view.Discussion for more */
        classes: ['crafter-social-lightbox-view', 'modal', 'fade'],
        templates: {
            main: ('%@lightbox.hbs').fmt(S.Cfg('url.templates'))
        },
        modal: $.extend({}, $.fn.modal.Constructor.DEFAULTS, {
            backdrop: true,
            keyboard: true,
            show: false,
            remote: false
        })
    });

    S.define('view.Lightbox', Lightbox);

}) (crafter.social);