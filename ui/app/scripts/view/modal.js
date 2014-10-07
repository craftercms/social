(function (S) {
    'use strict';

    // TODO Scope bootstrap stuff under social and clear $.fn.modal ? - same for popover view
    if (!S.$.fn.modal) {
        throw new Error('Modal requires bootstrap modal');
    }

    var Modal,
        Base = S.view.Base,
        U = S.util,
        $ = S.$;

    var prototype = $.extend({}, $.fn.modal.Constructor.prototype, {

        className: [
            Base.prototype.className,
            'crafter-social-modal-view',
            // TODO get rid of twitter default classes
            'fade'
        ].join(' '),

        initialize: function (  ) {
            Base.prototype.initialize.apply(this, arguments);
            this.initModal();
        },

        listen: function () {

            Base.prototype.listen.call(this);

            var me = this;

            this.$el.on('shown.bs.modal', function () {
                me.trigger('shown');
            });

            this.$el.on('hidden.bs.modal', function () {
                me.trigger('hidden');
            });

            this.listenTo(this, 'shown', function () {
                $('body, html').css('overflow', 'hidden');
            });

            this.listenTo(this, 'hidden', function () {
                $('body, html').css('overflow', '');
            });

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

        },

        backdrop: function () {
            $.fn.modal.Constructor.prototype.backdrop.apply(this, arguments);
            this.$backdrop && this.$backdrop
                .removeClass('modal-backdrop')
                .addClass('crafter-social-modal-view-backdrop');
        },

        render: function () {
            (this.cfg.modal.show) && this.show();
            return this;
        },

        set: function ( section, content ) {

            if ( arguments.length === 1 ) {
                var me = this, oSets = section;
                $.each(oSets, function (section, content) {
                    me.set(section, content);
                });
            }

            var $selection = this.$({
                'header': '.modal-header',
                'title': '.modal-title',
                'body': '.modal-body',
                'footer': '.modal-footer'
            }[section]);

            if ( (typeof content === 'object') && (('nodeType' in content) || (content instanceof $)) ) {
                $selection.html('').append(content);
            } else if (U.isHTML(content)){
                $selection.html(content);
            } else {
                $selection.text(content);
            }

        },

        destroy: function () {
            this.trigger('hidden');
            this.$el.remove();
            this.$backdrop && this.$backdrop.remove();
        }

    });

    // Constructor caused some trouble with Backbone extends
    delete(prototype.constructor);
    Modal = Base.extend(prototype);

    Modal.DEFAULTS = $.extend({}, {
        templates: {
            main: ('%@modal.hbs').fmt(S.Cfg('url.templates'))
        },
        modal: $.extend({}, $.fn.modal.Constructor.DEFAULTS, {
            backdrop: true,
            keyboard: true,
            show: false,
            remote: false
        })
    });

    S.define('view.Modal', Modal);

}) (crafter.social);