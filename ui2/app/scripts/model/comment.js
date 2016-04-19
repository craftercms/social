(function (S) {
    'use strict';

    var C = S.Constants,
        Model = S.Backbone.Model,
        $ = S.$;

    function POST(url, attributes, data) {
        return SUBMIT(url, attributes, data, 'POST');
    }

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

    var Comment = Model.extend({

        idAttribute: '_id',
        defaults: {
            'ancestors': [],
            'body': '',
            'thread': '',
            'children': [],
            'attributes': {},
            'createdDate': Date.now(),
            'attachments': []
        },
        hasChildren: function () {
            return this.get('children').length > 0;
        },

        isReply: function () {
            return this.get('ancestors').length > 0;
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
                   SUBMIT.call(this,
                       'comments.{_id}.moderate',
                       $.extend({}, params || {}, { status: status }),
                       'POST', {
                           context: params.context
                       });
          },

        update: function (params, callbacks) {
                   SUBMIT.call(this, 'comments.{_id}.update', {
                       id: '',
                       body: this.get('body')
                   }, 'POST', {
                       context: params.context
                   }, callbacks);
          },

        reply: function (params, callbacks) {
            var replyingTo = this.attributes;
            var ancestors = replyingTo.ancestors.slice();
            ancestors.push(replyingTo);
            var data = {
                    body: params.body,
                    parent: replyingTo._id, 
                    dateAdded: Date.now(),
                    thread: replyingTo.targetId, // this is the targetId
                    context: params.context,
                    targetId: replyingTo.targetId,
                    attributes: {
                        'commentUrl': replyingTo.attributes.commentUrl,
                        'commentThreadName': replyingTo.attributes.commentThreadName
                }
            };
            this.collection.create(data, callbacks);
        },
        fetch: function (options) {
                options = options || {};
                options.cache = false;
                //return S.Backbone.Collection.prototype.fetch.call(this, options);
            }

    });

    S.define('model.Comment', Comment);

})(crafter.social);
