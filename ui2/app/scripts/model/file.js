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

        parse: function (modelData, config) {
            try {
                modelData.url = S.url('comments.{_id}.attachments.{fileId}', {
                    _id: modelData.attributes.owner,
                    fileId: modelData.fileId,
                    context: config.context || config.data.context
                });

                modelData.urlPreview = this.getPreviewUrl(modelData);

            } catch (ex) {
                console && console.log('crafter.social.model.File: ', ex);
            }
            return modelData;
        },

        detach: function () {

        },

        open: function () {

        },

        getPreviewUrl: function (modelData) {
            if (modelData.fileName.match(S.Constants.get('SUPPORTED_IMAGE_FORMATS'))) {
                return modelData.url;
            } else if (modelData.fileName.match(S.Constants.get('SUPPORTED_VIDEO_FORMATS'))) {
                return S.Constants.get('POSTER_URL');
            } else {
                return ""
            }
        }

    });

    S.define('model.File', File);

}) (crafter.social);