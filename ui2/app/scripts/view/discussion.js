(function (S) {
    'use strict';

    var Discussion,
        Base = S.view.Base,
        Director = S.getDirector(),
        C = S.Constants,
        $ = S.$;

    Discussion = Base.extend({

        className: [
            Base.prototype.className,
            'crafter-social-discussion-view'
        ].join(' '),


      
        listen: function () {
            var collection = this.collection;
            if (collection) {
                this.listenTo(collection, 'add', this.addOne);
                this.listenTo(collection, 'sync', this.addAll);
            }
        },

        createUI: function () {

            Base.prototype.createUI.call(this);

            this.cfg.initCommentingView && this.initCommentingView();

            var $opts = this.$('.options-view-container');
            if ($opts.size()) {

                var options = new S.view.Options($.extend({
                    target: this.cfg.target,
                    context: this.cfg.context,
                    collection: this.collection
                }, this.cfg.viewOptions || {}));

                options.render();
                $opts.append(options.el);

                this.listenTo(options, 'view.change.request', this.changeView);
                this.listenTo(options, 'view.close.request', this.hide || S.util.emptyFn);

            }

        },

        initCommentingView: function () {

            var $replies        = this.$('.reply-box');
            var profile         = S.getDirector().getProfile();
            var isSocialAuthor  = profile.hasRole('SOCIAL_AUTHOR',this.cfg.context) || profile.hasRole('SOCIAL_SUPERADMIN',this.cfg.context)  || profile.hasRole('SOCIAL_MODERATOR',this.cfg.context) || profile.hasRole('SOCIAL_ADMIN',this.cfg.context);;

            (!isSocialAuthor) && $replies.hide();

            if ($replies.size() && isSocialAuthor) {

                var view = new S.util.instance('view.Commenting', $.extend({
                    collection: this.collection,
                    context: this.cfg.context,
                    target: this.cfg.target,
                    commentUrl:this.cfg.commentUrl,
                    commentThreadName:this.cfg.commentThreadName,
                    model: {},
                }, this.cfg.commenting));

                $replies.append(view.render().el);
                this.cache('commentingView', view);
            }
        },

        changeView: function ( view ) {
            this.trigger('view.change.request', view);
        },

        addAll: function () {
            var profile         = S.getDirector().getProfile();
            var labelComment = '';
            this.$('.comments:first').html('');

            if(this.collection.length===0){
                labelComment = 'discussion.comment'.loc();
                $(this.el).find('.comments:empty').append('<span class="sui-log-event">'+ labelComment+ '</span>');
                var emptyDiscussion=$(this.el).find('.sui-log-event');
            }else if(true && !(profile.hasRole('SOCIAL_USER',this.cfg.context) || profile.hasRole('SOCIAL_ADMIN',this.cfg.context) || profile.hasRole('SOCIAL_MODERATOR',this.cfg.context))){
                if( $(this.el).find('.sui-comment-nonempty').length <=0) {
                    labelComment = 'discussion.login-comment'.loc();
                    $(this.el).find('.comments:last').after('<span class="sui-comment-nonempty">' + labelComment + '</span>');
                    var emptyDiscussion = $(this.el).find('.sui-comment-nonempty');
                }
            }

            $(emptyDiscussion).on('click',function(){
                Director.trigger(C.get('EVENT_SOCIAL_NOCOMMENT'));
            });
            this.collection.each(this.addOne, this);
        },

        addOne: function (comment) {
            if(!comment.isTrashed()) {
                var ts=new Date().getTime();
                comment.ts=ts;
                this.$('.no-comments').remove();
                if (!comment.isReply()) { // replies are handled separately
                    var view = new S.view.Comment({
                        model: comment,
                        context: this.cfg.context,
                        collection: this.collection
                    });
                    this.$('.comments:first').append(view.render().element());
                }
            }
        }

    });

    Discussion.DEFAULTS = $.extend({}, Base.DEFAULTS, {
        initCommentingView: true,
        commenting: {
            editor: {
                'extraPlugins': 'autogrow',
                'autoGrow_maxHeight': 800,
                // Remove the Resize plugin as it does not make sense to use it in conjunction with the AutoGrow plugin.
                'removePlugins': 'resize'
            }
        }
    });

    S.define('view.Discussion', Discussion);

}) (crafter.social);
