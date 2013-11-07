(function (window) {
    'use strict';

    var require = window.require;

    require.config({
        paths: {

            // crafter: '?',
            'crafter.social': 'app',
            'crafter.social_api': 'api',

            'component.TemplateEngine': 'component/template-engine',
            'component.Director': 'component/director',
            'component.Editor': 'component/editor',

            'model.Comment': 'model/comment',

            'controller.Base': 'controller/base',

            'view.Base': 'view/base',
            'view.Discussion': 'view/discussion',
            'view.Commenting': 'view/commenting',
            'view.Comment': 'view/comment',
            'view.Lightbox': 'view/lightbox',
            'view.Popover': 'view/popover',
            'view.Commentable': 'view/commentable',

            'view.barget.Base': 'view/barget/base',
            'view.barget.Feedback': 'view/barget/feedback',
            'view.barget.Share': 'view/barget/share',
            'view.barget.Reveal': 'view/barget/reveal',
            'view.barget.Rate': 'view/barget/rate',
            'view.SocialBar': 'view/social-bar',

            jquery: '../libs/jquery/jquery',
            backbone: '../libs/backbone/backbone',
            underscore: '../libs/underscore/underscore',
            ckeditor: '../libs/ckeditor/ckeditor',
            'ckeditor.plugins.autogrow': 'component/ckeditor/plugins/autogrow/plugin',
            handlebars: '../libs/handlebars/handlebars',
            bootstrapAffix: '../libs/sass-bootstrap/js/affix',
            bootstrapAlert: '../libs/sass-bootstrap/js/alert',
            bootstrapButton: '../libs/sass-bootstrap/js/button',
            bootstrapCarousel: '../libs/sass-bootstrap/js/carousel',
            bootstrapCollapse: '../libs/sass-bootstrap/js/collapse',
            bootstrapDropdown: '../libs/sass-bootstrap/js/dropdown',
            bootstrapModal: '../libs/sass-bootstrap/js/modal',
            bootstrapPopover: '../libs/sass-bootstrap/js/popover',
            bootstrapScrollspy: '../libs/sass-bootstrap/js/scrollspy',
            bootstrapTab: '../libs/sass-bootstrap/js/tab',
            bootstrapTooltip: '../libs/sass-bootstrap/js/tooltip',
            bootstrapTransition: '../libs/sass-bootstrap/js/transition'

        },
        shim: {

            // 'crafter':: { deps: [] },
            'crafter.social': { deps: [] },
            'crafter.social_api': {
                deps: [ 'jquery', 'backbone', 'crafter.social' ]
            },

            'component.TemplateEngine': {
                deps: [ 'crafter.social', 'handlebars' ]
            },
            'component.Director': {
                deps: [ 'crafter.social_api' ]
            },
            'component.Editor': {
                deps: [ 'crafter.social_api', 'ckeditor', 'ckeditor.plugins.autogrow' ]
            },

            'model.Comment': {
                deps: [ 'crafter.social_api' ]
            },

            'view.Base': {
                deps: [ 'crafter.social_api', 'component.TemplateEngine' ]
            },
            'view.Discussion': {
                deps: [ 'view.Base' ]
            },
            'view.Commenting': {
                deps: [ 'component.Editor' ]
            },
            'view.Comment': {
                deps: [ 'component.Director', 'view.Base' ]
            },
            'view.Lightbox': {
                deps: [ 'view.Discussion', 'view.Comment', 'view.Commenting', 'bootstrapModal' ]
            },
            'view.Popover': {
                deps: [ 'view.Discussion', 'view.Comment', 'view.Commenting', 'bootstrapPopover' ]
            },
            'view.Commentable': {
                deps: [ 'view.Base', 'bootstrapTooltip', 'view.Popover', 'view.Lightbox' ]
            },
            'view.SocialBar': {
                deps: [ 'view.Base', 'view.barget.Feedback', 'view.barget.Rate', 'view.barget.Reveal', 'view.barget.Share' ]
            },
            'view.barget.Base': {
                deps: [ 'crafter.social_api', 'view.Base' ]
            },
            'view.barget.Feedback': {
                deps: [ 'crafter.social_api', 'view.barget.Base' ]
            },
            'view.barget.Rate': {
                deps: [ 'crafter.social_api', 'view.barget.Base' ]
            },
            'view.barget.Reveal': {
                deps: [ 'crafter.social_api', 'view.barget.Base' ]
            },
            'view.barget.Share': {
                deps: [ 'crafter.social_api', 'view.barget.Base' ]
            },

            'controller.Base': {
                deps: [ 'crafter.social_api', 'model.Comment' ]
            },

            /*
             * libs
             */

            backbone: {
                deps: [ 'jquery', 'underscore' ]
            },
            ckeditor: {
                deps: []
            },
            'ckeditor.plugins.autogrow': {
                deps: [ 'ckeditor' ]
            },
            bootstrapAffix: {
                deps: [ 'jquery' ]
            },
            bootstrapAlert: {
                deps: [ 'jquery', 'bootstrapTransition' ]
            },
            bootstrapButton: {
                deps: [ 'jquery' ]
            },
            bootstrapCarousel: {
                deps: [ 'jquery', 'bootstrapTransition' ]
            },
            bootstrapCollapse: {
                deps: [ 'jquery', 'bootstrapTransition' ]
            },
            bootstrapDropdown: {
                deps: [ 'jquery' ]
            },
            bootstrapModal:{
                deps: [ 'jquery', 'bootstrapTransition' ]
            },
            bootstrapPopover: {
                deps: [ 'jquery', 'bootstrapTooltip' ]
            },
            bootstrapScrollspy: {
                deps: [ 'jquery' ]
            },
            bootstrapTab: {
                deps: [ 'jquery', 'bootstrapTransition' ]
            },
            bootstrapTooltip: {
                deps: [ 'jquery', 'bootstrapTransition' ]
            },
            bootstrapTransition: {
                deps: [ 'jquery' ]
            }

        }
    });

    require([ 'component.Director' ], function ( Director ) {

        Director.socialise({
            target: '#jumbotron',
            tenant: 'xyz'
        });

    });


}) (window);