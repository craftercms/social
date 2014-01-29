(function (S) {
    'use strict';

    var $ = S.$,
        U = S.util,
        B = S.Backbone;

    var Base = B.Collection.extend({

        model: S.model.Comment,
        isWatched: false,

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
            return S.url('ugc.target');
        },

        parse: function ( response ) {
            if (response.list.length) {
                this.setIsWatched(
                    response.list[0].userInfo.watched);
            } else {
                this.setIsWatched(null);
            }
            return response.list || response;
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
        tenant: null,
        target: null,
        // TODO url: '' or { ... } ?
    };

    S.define('controller.Base', Base);

}) (crafter.social);