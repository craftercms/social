(function (S) {
    'use strict';

    var $ = S.$,
        U = S.util,
        B = S.Backbone;

    var Base = B.Collection.extend({

        model: S.model.Comment,

        initialize: function ( oConfig ) {
            this.guid = U.guid();
            this.extendCfg(this.constructor.DEFAULTS, oConfig);
        },

        extendCfg: function ( oDefaults, oConfig ) {
            if (!this.cfg) { this.cfg = {}; }
            return $.extend(true, this.cfg, oDefaults, oConfig);
        },

        url: function () {
            return S.url('ugc.target');
        },

        parse: function ( response ) {
            return response.list || response;
        },

        getGUID: function () {
            return this.guid;
        }

    });

    Base.DEFAULTS = {
        tenant: null,
        target: null
    };

    S.define('controller.Base', Base);

}) (crafter.social);