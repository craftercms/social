(function (S) {
    'use strict';

    // var $ = S.$;
    var Base = S.controller.Base;

    var Files = Base.extend({

        model: S.model.File,

        url: function () {
            return S.url('comments.{_id}.attachments', {
                _id: this.cfg.comment.get('_id')
            });
        },

        parse: function (response) {
            return response;
        }

    });

    Files.DEFAULTS = {  };

    S.define('controller.Files', Files);

}) (crafter.social);