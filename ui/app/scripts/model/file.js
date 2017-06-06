(function (S) {
    'use strict';

    var File = S.Backbone.Model.extend({
        
        idAttribute: 'attachmentId',

        downloadUrl: function () {
            return '/';
        },

        url: function () {
            if (this.isNew()) {
                return S.url('comments.{_id}.attachments');
            } else {
                // TODO ... update?
            }
        },

        parse: function (modelData) {
            try {
                modelData.url = S.url('comments.{_id}.attachments.{fileId}', {
                    _id: modelData.attributes.owner,
                    fileId: modelData.fileId,
                    context: 'f5b143c2-f1c0-4a10-b56e-f485f00d3fe9'
                });
            } catch (ex) {
                console && console.log('crafter.social.model.File: ', ex);
            }
            return modelData;
        },

        detach: function () {

        },

        open: function () {

        }

    });

    S.define('model.File', File);

}) (crafter.social);