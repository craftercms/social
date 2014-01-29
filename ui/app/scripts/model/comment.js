(function (S) {
    'use strict';

    var C       = S.Constants,
        Model   = S.Backbone.Model,
        $       = S.$;

    function POST (url, attributes, data) {
        this.save(attributes, {
            url: S.url(url, this.toJSON()),
            type: 'POST', // TODO REMOVE OR ADJUST
            data: $.param($.extend({ tenant: this.get('tenant') }, data || {}))
        });
    }

    var Comment = Model.extend({

        idAttribute: 'id',
        defaults: {
            // 'actions': ACTIONS,
            'anonymousFlag' : false,
            'childCount' : 0,
            'children' : [],
            'createdDate' : Date.now(),
            'dateAdded' : Date.now(),
            'extraChildCount' : 0,
            'flagCount' : 0,
            'id' : null,
            'lastModifiedDate' : Date.now(),
            'likeCount' : 0,
            'moderationStatus' : 'UNMODERATED',
            'offenceCount' : 0,
            'profile' : {},
            'profileId' : '',
            'targetDescription' : '',
            'targetId' : '',
            'targetUrl' : '',
            'tenant' : '',
            'textContent' : '', // '{ "content": "" }',
            'timesModerated' : 0
        },

        url: function () {
            if (this.isNew()) {
                return S.url('ugc.create');
            } else {
                // TODO ... update?
            }
        },

        fetch: function ( options ) {
            return Model.prototype.fetch.call(this, $.extend({
                url: S.url('ugc.get_ugc', this.toJSON())
            }, options || {}));
        },

        isTrashed: function () {
            return this.get('moderationStatus') === C.get('MODERATION_STATUS_TRASH');
        },

        like: function () {
            POST.call(this, 'ugc.like', { likes: this.get('likes') + 1 });
        },
        unlike: function () {
            POST.call(this, 'ugc.unlike', { likes: this.get('likes') - 1 });
        },

        flag: function ( reason ) {
            POST.call(this, 'ugc.flag', { likes: this.get('flags') + 1 }, { reason: reason });
        },
        unflag: function ( reason ) {
            POST.call(this, 'ugc.unflag', { likes: this.get('flags') - 1 }, { reason: reason });
        },

        dislike: function () {
            POST.call(this, 'ugc.dislike', { likes: this.get('dislikes') + 1 });
        },
        undoDislike: function () {
            POST.call(this, 'ugc.undislike', { likes: this.get('dislikes') - 1 });
        },

        trash: function () {
            this.moderate(C.get('MODERATION_STATUS_TRASH'));
        },

        moderate: function ( status ) {
            POST.call(this, 'ugc.moderation.{id}', { moderationStatus: status }, { moderationStatus: status, ugcId: this.get('id') });
        },

        reply: function () {

        }

    });

    S.define('model.Comment', Comment);

}) (crafter.social);