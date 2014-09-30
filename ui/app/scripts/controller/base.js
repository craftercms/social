(function (S) {
    'use strict';

    var $ = S.$,
        U = S.util,
        B = S.Backbone;

    var Base = B.Collection.extend({

        model: S.model.Comment,
        isWatched: false,

        create: function (model, options) {

            var data = new FormData();
            var mdl = model.toJSON ? model.toJSON() : model;

            $.each(mdl, function (key, value) {
                data.append(key, key === 'attributes' ? JSON.stringify(value) : value);
            });

            return B.Collection.prototype.create.call(this, model, $.extend({
                data: data,
                contentType: false
            }, options || {}));

        },

        initialize: function ( models, oConfig ) {
            this.guid = U.guid();
            this.extendCfg(this.constructor.DEFAULTS, oConfig);
        },

        extendCfg: function ( oDefaults, oConfig ) {
            if (!this.cfg) { this.cfg = {}; }
            return $.extend(true, this.cfg, oDefaults, oConfig);
        },

        url: function () {
            // TODO set order through class configuration
            return S.url('threads.{target}.comments', this.cfg);
        },

        parse: function ( response ) {
            var comments = response.comments || response;
            this.setIsWatched(response.watched || false);
            return comments;
        },

        getGUID: function () {
            return this.guid;
        },

        setIsWatched: function (isWatched) {
            this.isWatched = isWatched;
            this.trigger(S.Constants.get('EVENT_DISCUSSION_WATCHED', this.cfg), isWatched);
        },
        getIsWatched: function () {
            return this.isWatched;
        }

    });

    Base.DEFAULTS = {
        context: null,
        target: null
        // TODO url: '' or { ... } ?
    };

    S.define('controller.Base', Base);

}) (crafter.social);