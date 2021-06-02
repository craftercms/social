(function (S) {
    'use strict';

    var C = S.Constants,
        Model = S.Backbone.Model,
        $ = S.$;

    function SUBMIT(url, data, method, urlParam, callbacks) {

        if (!callbacks) callbacks = {};

        var options = {
            type: method || 'POST',
            url: S.url(url,$.extend({}, this.toJSON() || {}, urlParam)),
            success: callbacks.success,
            error: callbacks.error
        };

        data && (options.data = $.param(data));
        return this.save(null, options);

    }

    var AttachmentPreview = Model.extend({
        trashAttachment: function (params, callbacks) {
            SUBMIT.call(this, 'comments.{_id}.attachments.{fileId}.delete', {context:params.context}, 'POST', params, callbacks);
        }
    });

    S.define('model.AttachmentPreview', AttachmentPreview);

}) (crafter.social);
