(function (S) {
    'use strict';

    var C = S.Constants,
        Model = S.Backbone.Model,
        $ = S.$;

    function POST(url, attributes, data) {
        return SUBMIT(url, attributes, data, 'POST');
    }

    function SUBMIT(url, data, method) {

        var options = {
            type: method || 'POST',
            url: S.url(url, this.toJSON())
        };

        data && (options.data = $.param(data));
        return this.save(null, options);

    }

    var Comment = Model.extend({

        idAttribute: '_id',
        defaults: {
            'body': '',
            'thread': '',
            'children': [],
            'attributes': {},
            'createdDate': Date.now()
        },

        url: function () {
            return S.url(this.isNew() ? 'comments' : 'comments.{_id}', this.toJSON());
        },

        isTrashed: function () {
            return this.get('moderationStatus') === C.get('MODERATION_STATUS_TRASH');
        },

        voteUp: function (params) {
            SUBMIT.call(this, 'comments.{_id}.votes.up', params);
        },
        voteDown: function (params) {
            SUBMIT.call(this, 'comments.{_id}.votes.down', params);
        },
        removeVote: function (params) {
            SUBMIT.call(this, 'comments.{_id}.votes.neutral', params);
        },

        flag: function (params) {
            SUBMIT.call(this, 'comments.{_id}.flags', params);
        },
        unflag: function (params) {
            // TODO
            this.save(null, {
                type: 'DELETE',
                url: S.url('comments.{_id}.flags', this.toJSON())
            });
            POST.call(this, 'comments.{_id}.flags', { likes: this.get('flags') - 1 }, params);
        },
        flaggedBy: function (profileId) {
            var flags = this.attributes.flags,
                l = flags.length, i;
            for (i = 0; i < l; ++i) {
                if (flags[i].userId === profileId) {
                    return true;
                }
            }
            return false;
        },

        trash: function (params) {
            this.moderate(C.get('MODERATION_STATUS_TRASH'), params);
        },

        moderate: function (status, params) {
            SUBMIT.call(this, 'comments.{_id}.moderate', $.extend({}, params || {}, { status: status }), 'PUT');
        },

        reply: function () {

        }

    });

    S.define('model.Comment', Comment);

})(crafter.social);