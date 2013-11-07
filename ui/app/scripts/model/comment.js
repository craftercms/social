(function (S) {
    'use strict';

    var U = S.util,
        SERVICE = S.Cfg('url.service');

    var Comment = S.Backbone.Model.extend({
        idAttribute: 'id',
        url: function () {
            if (this.isNew()) {
                return '%@ugc/create.json'.fmt(SERVICE);
            } else {
                return '%@ugc/like/%@.json'.fmt(SERVICE, this.id);
            }
        },
        defaults: {
            'actions' : [
                {
                    'name' : 'read',
                    'roles' : ['SOCIAL_AUTHOR']
                },
                {
                    'name' : 'update',
                    'roles' : ['SOCIAL_AUTHOR']
                },
                {
                    'name' : 'create',
                    'roles' : ['SOCIAL_AUTHOR']
                },
                {
                    'name' : 'delete',
                    'roles' : ['SOCIAL_AUTHOR']
                },
                {
                    'name' : 'act_on',
                    'roles' : ['SOCIAL_AUTHOR']
                },
                {
                    'name' : 'moderate',
                    'roles' : ['SOCIAL_AUTHOR']
                }
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
            'textContent' : '{ "content": "" }',
            'timesModerated' : 0
        },
        parse: function (data) {
            data.content = U.fromJSON(data.textContent).content;
            return data;
        },
        like: function () {
            this.save({ likeCount: this.get('likeCount') + 1 }, {
                url: S.component.Director.actionURL('ugc.like', { id: this.id }),
                type: 'POST', // TODO REMOVE OR ADJUST
                //data: JSON.stringify({ tenant: this.get('tenant'), reason: 'hello' })
            });
        },
        flag: function () {
            this.save({ flagCount: this.get('flagCount') + 1 }, {
                url: S.component.Director.actionURL('ugc.flag', { id: this.id }),
                type: 'POST' // TODO REMOVE OR ADJUST
            });
        },
        reply: function () {

        }
    });

    Comment.DEFAULTS = {
        tenant: null,
        target: null
    };

    S.define('model.Comment', Comment);

}) (crafter.social);