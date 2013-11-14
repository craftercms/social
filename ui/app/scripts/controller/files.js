(function (S) {
    'use strict';

    // var $ = S.$;
    var Base = S.controller.Base;

    var Files = Base.extend({

        model: S.model.File,

        url: function () {
            return S.url('ugc.{id}.get_attachments', {
                id: this.cfg.comment.get('id'),
                tenant: this.cfg.comment.get('tenant')
            });
        },

        parse: function (response) {
            return response;
        }

    });

    Files.DEFAULTS = {  };

    S.define('controller.Files', Files);

}) (crafter.social);