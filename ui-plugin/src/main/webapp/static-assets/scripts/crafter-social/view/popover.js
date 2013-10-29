(function (S) {
    'use strict';

    if (!S.$.fn.popover) throw new Error('Extended Popover requires bootstrap popover');

    var Popover,
        Superclass = S.view.SocialUI,
        app = S.Orchestrator,
        C = S.Constants,
        $ = S.$;

    var prototype = $.extend({}, $.fn.popover.Constructor.prototype, {
        className: 'crafter-social-view crafter-social-popover popover',
        events: {
            'click [data-action-comment]': 'comment',
            'click button.close': 'hide'
        },
        initialize: function (config) {

            var newConfig = $.extend(true, {}, Popover.DEFAULTS, config);
            Superclass.prototype.initialize.call(this, newConfig);
            this.init('popover', this.cfg.target, this.cfg.popover);

            var collection = this.collection;
            this.listenTo(collection, 'add', this.addOne);
            this.listenTo(collection, 'sync', this.addAll);

            collection.fetch();

        },
        addAll: function () {

            var $el = this.$('.comments:first');

            $el.html('');
            this.collection.each(this.addOne, this);

        },
        addOne: function (comment) {

            var view = new S.view.Comment({ model: comment });
            this.$('.comments:first').append(view.render().element());
            this.replacement()

        },
        replacement: function () {

            var $tip         = this.tip();
            var pos          = this.getPosition();
            var actualWidth  = $tip[0].offsetWidth;
            var actualHeight = $tip[0].offsetHeight;

            var $parent = this.$element.parent();
            var placement = (typeof this.options.placement == 'function')
                    ? this.options.placement.call(this, $tip[0], this.$element[0])
                    : this.options.placement;

            var orgPlacement = placement;
            var docScroll    = document.documentElement.scrollTop || document.body.scrollTop;
            var parentWidth  = this.options.container == 'body' ? window.innerWidth  : $parent.outerWidth();
            var parentHeight = this.options.container == 'body' ? window.innerHeight : $parent.outerHeight();
            var parentLeft   = this.options.container == 'body' ? 0 : $parent.offset().left;

            placement = placement == 'bottom' && pos.top   + pos.height  + actualHeight - docScroll > parentHeight  ? 'top'    :
                        placement == 'top'    && pos.top   - docScroll   - actualHeight < 0                         ? 'bottom' :
                        placement == 'right'  && pos.right + actualWidth > parentWidth                              ? 'left'   :
                        placement == 'left'   && pos.left  - actualWidth < parentLeft                               ? 'right'  :
                        placement;

            $tip.removeClass(orgPlacement)
                .addClass(placement);

            var calculatedOffset = this.getCalculatedOffset(
                placement, pos, actualWidth, actualHeight);

            this.applyPlacement(calculatedOffset, placement);

        },


        editor: function () {
            var editor = this.getCache('editor');
            if (!editor) {

                var $textarea = this.$('textarea'),
                    editorId = this.identify($textarea, 'editorTextarea'),
                    me = this;

                editor = CKEDITOR.inline($textarea.get(0), $.extend({
                    on: {
                        pluginsLoaded: function () {
                            this.addCommand('myGreetingCommand', {
                                exec : function(editor, data) {
                                    me.comment();
                                }
                            });
                            this.keystrokeHandler.keystrokes[CKEDITOR.SHIFT + 13] = 'myGreetingCommand';
                        }
                    }
                }, this.cfg.editor));

                this.cache('editor', editor);
            }
            return editor;
        },
        comment: function () {

            var editor = this.editor(),
                content = editor.getData();

            if (!content) return;

            this.collection.create({
                textContent: JSON.stringify({ content: content }),
                content: content,
                profile: app.getUser(),
                dateAdded: Date.now(),
                tenant: this.cfg.tenant,
                target: this.cfg.target
            });

            editor.setData('');
            editor.focus();

        },

        /*
         * Popover overrides
         */

        getDefaults: function () {
            return Popover.DEFAULTS;
        },
        setContent: function () {

            var $tip    = this.tip();
            var title   = this.getTitle();
            var content = this.getContent();

            $tip.removeClass('fade top bottom left right in hide');

        },
        show: function () {
            $.fn.popover.Constructor.prototype.show.apply(this, arguments);
            this.editor();
        },
        hide: function () {
            var that = this;
            var $tip = this.tip();
            var e    = $.Event('hide.bs.' + this.type);

            function complete() {
                $tip.addClass('hide');
            }

            this.$element.trigger(e);
            if (e.isDefaultPrevented()) return;

            $tip.removeClass('in');

            if ($.support.transition) {
                if ($tip.hasClass('fade')) {
                    $tip.one($.support.transition.end, complete).emulateTransitionEnd(150);
                } else {
                    complete();
                }
            } else {
                setTimeout(complete, 150);
            }

            this.$element.trigger('hidden.bs.' + this.type);

            return this;
        },
        tip: function () {
            return this.element();
        }
    });

    delete(prototype.constructor);
    Popover = Superclass.extend(prototype);
    Popover.DEFAULTS = $.extend({}, {
        // events: [C.EVENT_GET_LIST],
        templates: {
            main: ('%@popover.hbs').fmt(C.TEMPLATES_URL),
            comment: ('%@comment.hbs').fmt(C.TEMPLATES_URL)
        },
        editor: {
            extraPlugins: 'autogrow',
            autoGrow_maxHeight: 800,
            // Remove the Resize plugin as it does not make sense to use it in conjunction with the AutoGrow plugin.
            removePlugins: 'resize'
        },
        popover: $.extend({}, $.fn.popover.Constructor.DEFAULTS, {
            placement: 'bottom',
            trigger: 'manual',
            // container: 'body',
            content: '(not.empty.for.bs.not.to.go.crazy)'
        })
    });

    S.define('view.Popover', Popover)

})(crafter.social);