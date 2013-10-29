(function (S) {

    var U = S.util,
        O = S.Orchestrator;

    var Comment = S.Backbone.Model.extend({
        idAttribute: 'id',
        url: function () {
            if (this.isNew()) {
                return '/crafter-social/api/2/ugc/create.json';
            } else {
                return '/crafter-social/api/2/ugc/like/%@.json'.fmt(this.id);
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
            data.content = U.eval(data.textContent).content;
            return data;
        },
        like: function () {
            this.save({ likeCount: this.get('likeCount') + 1 }, {
                url: O.actionURL('ugc.like', { id: this.id }),
                type: 'POST' // TODO REMOVE OR ADJUST
            });
        },
        flag: function () {
            this.save({ flagCount: this.get('flagCount') + 1 }, {
                url: O.actionURL('ugc.flag', { id: this.id }),
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