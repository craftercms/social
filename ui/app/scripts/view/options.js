(function (S) {
    'use strict';

    var Options,
        Base = S.view.Base,
        $ = S.$;

    var backdrop = '.dropdown-backdrop';
    var toggle   = '[data-toggle="dropdown"]';

    Options = Base.extend({

        tagName: 'ul',
        className: 'list-inline list-unstyled crafter-social-view-options-view',

        initialize: function () {
            Base.prototype.initialize.apply(this, arguments);

            var me = this;
            $.each(this.cfg.hidden, function (i, o) {
                me.$(S.string.fmt('[data-option="%@"]', o)).hide();
            });

            this.$('[data-toggle]').dropdown();

        },

        refresh: function () {
            this.collection.fetch({
                data : {
                    target: this.cfg.target,
                    tenant: this.cfg.tenant
                }
            });
        },

        changeView: function (e, view) {
            this.closeDropdown();
            this.trigger('view.change.request', view);
        },

        close: function () {
            this.closeDropdown();
            this.trigger('view.close.request');
        },

        closeDropdown: function () {
            this.$(backdrop).remove();
            this.$(toggle).removeClass('open').parents('li.open').removeClass('open');
            // TODO trigger bootstrap events?
            // this.$('[data-toggle]').dropdown('toggle');
        }

    });

    Options.DEFAULTS = {
        hidden: [],
        templates: {
            main: function () {
                return S.string.fmt('%@options.hbs', S.Cfg('url.templates'));
            }
        }
    };

    S.define('view.Options', Options);

}) (crafter.social);