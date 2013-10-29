(function (S) {
    'use strict';

    var $ = S.$,
        C = S.Constants,
        U = S.util,
        E = S.EventProvider,
        B = S.Backbone,
        O = S.Orchestrator;

    var SocialController = B.Collection.extend({
        model: S.model.Comment,
        initialize: function (oConfig) {
            this.guid = U.guid();
            this.extendCfg(SocialController.DEFAULTS, oConfig);
        },
        extendCfg: function (oDefaults, oConfig) {
            if (!this.cfg) this.cfg = {};
            return $.extend(true, this.cfg, oDefaults, oConfig);
        },
        url: function () {
            return O.actionURL('ugc.target');
        },
        parse: function (response) {
            return response.list;
        },
        getGUID: function () {
            return this.guid;
        }
    });

    SocialController.DEFAULTS = {
        tenant: null,
        target: null
    };

    S.define('controller.SocialController', SocialController);

}) (crafter.social);