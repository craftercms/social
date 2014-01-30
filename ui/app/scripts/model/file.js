(function (S) {
    'use strict';

    var File = S.Backbone.Model.extend({
        
        idAttribute: 'attachmentId',

        defaults: {
            'attachmentId'  : '5282ac830364dabc3c4634c5',
            'contentType'   : 'image/jpeg',
            'filename'      : 'acmeimages.jpg',
            'url'           : '/crafter-social/api/2/get_attachment/5282ac830364dabc3c4634c5?tenant=craftercms'
        },

        url: function () {
            if (this.isNew()) {
                return S.url('ugc.file');
            } else {
                // TODO ... update?
            }
        },

        parse: function (model) {
            model.url = S.string.fmt(S.Cfg('url.files'), model);
            return model;
        },

        detach: function () {

        },

        open: function () {

        }

    });

    S.define('model.File', File);

}) (crafter.social);