(function (S) {
    'use strict';

    var C = S.Constants,
        Model = S.Backbone.Model,
        $ = S.$;

    function POST(url, attributes, data) {
        return SUBMIT(url, attributes, data, 'POST');
    }

    function SUBMIT(url, data, method,urlParam) {

        var options = {
            type: method || 'POST',
            url: S.url(url,$.extend({}, this.toJSON() || {}, urlParam) )
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
            var flagId=this.flagId(params.profileId)
            if(flagId) {
                this.get('flags').splice(flagId.index, 1);
                SUBMIT.call(this, 'comments.{_id}.flags.{flagId}', params ,'POST',{flagId:flagId.id});
            }
        },
        flagId: function (profileId) {
            var flags = this.attributes.flags,
                l = flags.length, i;
            for (i = 0; i < l; ++i) {
                if (flags[i].userId === profileId) {
                    return {id:flags[i]._id,index:i}
                }
            }
            return null;
        },
        flaggedBy: function (profileId) {
            if(profileId===undefined){
                return false
            }
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
            SUBMIT.call(this, 'comments.{_id}.moderate', $.extend({}, params || {}, { status: status }), 'POST',{context:params.context});
        },

        reply: function () {

        }

    });

    S.define('model.Comment', Comment);

})(crafter.social);
