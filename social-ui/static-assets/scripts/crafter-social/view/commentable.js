(function ($, S) {

    var ELEMENT_WRAPPER_CLASS = 'element-wrapper';

    var Commentable, // = function () { this.initialize.apply(this, arguments) },
        Dispatch = S.Orchestrator,
        Superclass = S.view.SocialUI,
        C = S.Constants,
        U = S.util,
        privates = {};

    var prototype = {
        events: {
            'click [data-test]': 'hello'
        },
        hello: function () {
            console.log(arguments);
            alert('hello')
        },

        initialize: function (config) {

            var newConfig = $.extend({}, Commentable.DEFAULTS, config);
            Superclass.prototype.initialize.call(this, newConfig);
            this.listenTo(this.collection, 'sync', this.render);

            this.collection.fetch();

        },
        createUI: function () {

            var $elem = this.element(),
                $target = $(this.cfg.target),
                html = U.template(this.getTemplate('main'), {
                    count: ''
                });

            $elem.html(html).insertBefore($target);
            $target.appendTo($elem.find('.%@:first'.fmt(ELEMENT_WRAPPER_CLASS)));

        },
        render: function () {

            var $badge = this.$('.badge'),
                length = this.collection.length;

            if (length === 0) {
                $badge.text('');
            } else {
                $badge.text(length);
            }

            return this;

        },
        showFullView: function () {

        },
        showDiscussionBubble: function () {
            var view = this.getCache('view.popover');
            if (!view) {
                view = new S.view.Popover({
                    target: this.cfg.target,
                    tenant: this.cfg.tenant,
                    popover: {
                        placement: 'left'
                    },
                    collection: this.collection
                });
                this.cache('view.popover', view);
            }
            view.show();
        }
    };

    prototype['%@.%@'.fmt(C.EVENT_GET_LIST, C.EVENT_STATE_SUCCESS)] = function (data) {
        var $elem = this.element();
        $elem.find('.badge').text(data);
    }

    prototype['%@.%@'.fmt(C.EVENT_GET_LIST, C.EVENT_STATE_FAILURE)] = function () {
        // TODO stuff when the request completes (e.g. display error message or state, rollback a comment, etc)
    }

    prototype['%@.%@'.fmt(C.EVENT_GET_LIST, C.EVENT_STATE_LOADING)] = function () {
        // TODO stuff when the request completes (e.g. show ajax loader icon)
    }

    prototype['%@.%@'.fmt(C.EVENT_GET_LIST, C.EVENT_STATE_COMPLETE)] = function () {
        // TODO stuff when the request completes (e.g. remove ajax loader icon)
    }

    Commentable = Superclass.extend(prototype);

    Commentable.DEFAULTS = {
        events: [C.EVENT_GET_LIST],
        templates: {
            main: ('%@commentable.hbs').fmt(C.TEMPLATES_URL)
        }
    };

    // S.util.inherit(Commentable, S.view.SocialUI, prototype);
    S.define('view.Commentable', Commentable);

}) (jQuery, crafter.social);