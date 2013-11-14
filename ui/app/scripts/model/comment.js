(function (S) {
    'use strict';

    var $ = S.$,
        DEFAULT_ROLES = [
            'SOCIAL_AUTHOR',
            'SOCIAL_ADMIN',
            'SOCIAL_MODERATOR',
            'SOCIAL_USER'
        ];

    var Comment = S.Backbone.Model.extend({
        idAttribute: 'id',
        url: function () {
            if (this.isNew()) {
                return S.url('ugc.create');
            } else {
                // TODO ... update?
            }
        },
        defaults: {
            'actions' : [
                { 'name' : 'read'       , 'roles' : DEFAULT_ROLES },
                { 'name' : 'update'     , 'roles' : DEFAULT_ROLES },
                { 'name' : 'create'     , 'roles' : DEFAULT_ROLES },
                { 'name' : 'delete'     , 'roles' : DEFAULT_ROLES },
                { 'name' : 'act_on'     , 'roles' : DEFAULT_ROLES },
                { 'name' : 'moderate'   , 'roles' : DEFAULT_ROLES }
            ],
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
//        parse: function (data) {
//            data.content = U.fromJSON(data.textContent).content;
//            return data;
//        },
        like: function () {
            this.save({ likeCount: this.get('likeCount') + 1 }, {
                url: S.url('ugc.like', { id: this.get('id') }),
                type: 'POST', // TODO REMOVE OR ADJUST
                data: $.param({ tenant: this.get('tenant') })
            });
        },
        flag: function ( reason ) {
            this.save({ flagCount: this.get('flagCount') + 1 }, {
                url: S.url('ugc.flag', { id: this.get('id') }),
                type: 'POST', // TODO REMOVE OR ADJUST
                data: $.param({ tenant: this.get('tenant'), reason: reason })
            });
        },
        reply: function () {

        }
    });

    S.define('model.Comment', Comment);

}) (crafter.social);