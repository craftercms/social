(function (S) {
    'use strict';

    var Options,
        Base = S.view.Base,
        $ = S.$;

    var backdrop = '.dropdown-backdrop';
    var toggle   = '[data-toggle="dropdown"]';
    var Cfg = S.Cfg;

    Options = Base.extend({

        tagName: 'ul',
        className: 'list-inline list-unstyled crafter-social-view-options-view',

        initialize: function () {
            Base.prototype.initialize.apply(this, arguments);

            var me = this,
                collection = this.collection,
                watched = collection.getIsWatched();

            this.cfg.hidden.push('options.watch');

            $.each(this.cfg.hidden, function (i, o) {
                me.$(S.string.fmt('[data-option="%@"]', o)).hide();
            });

            this.$('[data-toggle]').dropdown();

            this.setWatched(watched);

        },
        sortBy:function(e,value,order){

            Cfg('global.threadSortBy', value);
            Cfg('global.threadSortOrder', order);

            this.refresh();

        },
        refresh: function () {
            this.collection.fetch({
                data: {
                    target: this.cfg.target,
                    context: this.cfg.context,
                    sortBy: Cfg('global.threadSortBy'),
                    sortOrder: Cfg('global.threadSortOrder')
                }
            });
        },

        watch: function() {
            var me = this,
                collection = this.collection,
                watched = collection.getIsWatched();
            // TODO: what's backend like?
            S.request({
                type: 'POST',
                context: this,
                data: {frequency: 'INSTANT'},
                url: S.url((watched ? 'threads.{_id}.unsubscribe' : 'threads.{_id}.subscribe'), {
                    _id: this.cfg.target,
                    context: this.cfg.context
                }),
                success: function () {
                    collection.setIsWatched(!watched);
                    me.setWatched(!watched);
                },
                error: function () {

                }
            });
        },

        setWatched: function (isWatched) {
            var $elem = $(this.el).find('[data-action="watch"]');
            $elem.parents('li:first')[(isWatched === null) ? 'addClass' : 'removeClass']('hide');
            //$elem.find('i').toggleClass("fa-bell-slash", "fa-bell")

            if(isWatched){
                $elem.find('i').removeClass("fa-bell-slash").addClass("fa-bell");
                $elem.find('.text').text('Disable Notifications');
            }else{
                $elem.find('i').removeClass("fa-bell").addClass("fa-bell-slash");
                $elem.find('.text').text('Enable Notifications');
            }
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
