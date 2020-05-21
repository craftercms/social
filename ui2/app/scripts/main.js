(function (window) {
    'use strict';

    var require = window.require;

    require.config({
        paths: {
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
            bootstrapAffix: '../libs/bootstrap-sass/assets/javascript/bootstrap/affix',
            bootstrapAlert: '../libs/bootstrap-sass/assets/javascript/bootstrap/alert',
            bootstrapButton: '../libs/bootstrap-sass/assets/javascript/bootstrap/button',
            bootstrapCarousel: '../libs/bootstrap-sass/assets/javascript/bootstrap/carousel',
            bootstrapCollapse: '../libs/bootstrap-sass/assets/javascript/bootstrap/collapse',
            bootstrapDropdown: '../libs/bootstrap-sass/assets/javascript/bootstrap/dropdown',
            bootstrapModal: '../libs/bootstrap-sass/assets/javascript/bootstrap/modal',
            bootstrapPopover: '../libs/bootstrap-sass/assets/javascript/bootstrap/popover',
            bootstrapScrollspy: '../libs/bootstrap-sass/assets/javascript/bootstrap/scrollspy',
            bootstrapTab: '../libs/bootstrap-sass/assets/javascript/bootstrap/tab',
            bootstrapTooltip: '../libs/bootstrap-sass/assets/javascript/bootstrap/tooltip',
            bootstrapTransition: '../libs/bootstrap-sass/assets/javascript/bootstrap/transition',
            'blueimp-canvas-to-blob': '../libs/blueimp-canvas-to-blob/js/canvas-to-blob',
            'blueimp-tmpl': '../libs/blueimp-tmpl/js/tmpl',
            requirejs: '../libs/requirejs/require',
            'sass-bootstrap': '../libs/bootstrap-sass/assets/javascript/bootstrap',
            'load-image': '../libs/blueimp-load-image/js/load-image',
            'load-image-ios': '../libs/blueimp-load-image/js/load-image-ios',
            'load-image-orientation': '../libs/blueimp-load-image/js/load-image-orientation',
            'load-image-meta': '../libs/blueimp-load-image/js/load-image-meta',
            'load-image-exif': '../libs/blueimp-load-image/js/load-image-exif',
            'load-image-exif-map': '../libs/blueimp-load-image/js/load-image-exif-map',
            'jquery.postmessage-transport': '../libs/jquery-file-upload/js/cors/jquery.postmessage-transport',
            'jquery.xdr-transport': '../libs/jquery-file-upload/js/cors/jquery.xdr-transport',
            'jquery.ui.widget': '../libs/jquery-file-upload/js/vendor/jquery.ui.widget',
            'jquery.fileupload': '../libs/jquery-file-upload/js/jquery.fileupload',
            'jquery.fileupload-process': '../libs/jquery-file-upload/js/jquery.fileupload-process',
            'jquery.fileupload-validate': '../libs/jquery-file-upload/js/jquery.fileupload-validate',
            'jquery.fileupload-image': '../libs/jquery-file-upload/js/jquery.fileupload-image',
            'jquery.fileupload-audio': '../libs/jquery-file-upload/js/jquery.fileupload-audio',
            'jquery.fileupload-video': '../libs/jquery-file-upload/js/jquery.fileupload-video',
            'jquery.fileupload-ui': '../libs/jquery-file-upload/js/jquery.fileupload-ui',
            'jquery.fileupload-jquery-ui': '../libs/jquery-file-upload/js/jquery.fileupload-jquery-ui',
            'jquery.fileupload-angular': '../libs/jquery-file-upload/js/jquery.fileupload-angular',
            'jquery.iframe-transport': '../libs/jquery-file-upload/js/jquery.iframe-transport'
        },
        shim: {
            'crafter.social': {
                deps: [

                ]
            },
            'crafter.social_api': {
                deps: [
                    'jquery',
                    'backbone',
                    'crafter.social'
                ]
            },
            'component.TemplateEngine': {
                deps: [
                    'crafter.social',
                    'handlebars'
                ]
            },
            'component.Director': {
                deps: [
                    'crafter.social_api'
                ]
            },
            'component.Editor': {
                deps: [
                    'crafter.social_api',
                    'ckeditor',
                    'ckeditor.plugins.autogrow'
                ]
            },
            'model.Comment': {
                deps: [
                    'crafter.social_api'
                ]
            },
            'view.Base': {
                deps: [
                    'crafter.social_api',
                    'component.TemplateEngine'
                ]
            },
            'view.Discussion': {
                deps: [
                    'view.Base'
                ]
            },
            'view.Commenting': {
                deps: [
                    'component.Editor'
                ]
            },
            'view.Comment': {
                deps: [
                    'component.Director',
                    'view.Base'
                ]
            },
            'view.Lightbox': {
                deps: [
                    'view.Discussion',
                    'view.Comment',
                    'view.Commenting',
                    'bootstrapModal'
                ]
            },
            'view.Popover': {
                deps: [
                    'view.Discussion',
                    'view.Comment',
                    'view.Commenting',
                    'bootstrapPopover'
                ]
            },
            'view.Commentable': {
                deps: [
                    'view.Base',
                    'bootstrapTooltip',
                    'view.Popover',
                    'view.Lightbox'
                ]
            },
            'view.SocialBar': {
                deps: [
                    'view.Base',
                    'view.barget.Feedback',
                    'view.barget.Rate',
                    'view.barget.Reveal',
                    'view.barget.Share'
                ]
            },
            'view.barget.Base': {
                deps: [
                    'crafter.social_api',
                    'view.Base'
                ]
            },
            'view.barget.Feedback': {
                deps: [
                    'crafter.social_api',
                    'view.barget.Base'
                ]
            },
            'view.barget.Rate': {
                deps: [
                    'crafter.social_api',
                    'view.barget.Base'
                ]
            },
            'view.barget.Reveal': {
                deps: [
                    'crafter.social_api',
                    'view.barget.Base'
                ]
            },
            'view.barget.Share': {
                deps: [
                    'crafter.social_api',
                    'view.barget.Base'
                ]
            },
            'controller.Base': {
                deps: [
                    'crafter.social_api',
                    'model.Comment'
                ]
            },
            backbone: {
                deps: [
                    'jquery',
                    'underscore'
                ]
            },
            ckeditor: {
                deps: [

                ]
            },
            'ckeditor.plugins.autogrow': {
                deps: [
                    'ckeditor'
                ]
            },
            bootstrapAffix: {
                deps: [
                    'jquery'
                ]
            },
            bootstrapAlert: {
                deps: [
                    'jquery',
                    'bootstrapTransition'
                ]
            },
            bootstrapButton: {
                deps: [
                    'jquery'
                ]
            },
            bootstrapCarousel: {
                deps: [
                    'jquery',
                    'bootstrapTransition'
                ]
            },
            bootstrapCollapse: {
                deps: [
                    'jquery',
                    'bootstrapTransition'
                ]
            },
            bootstrapDropdown: {
                deps: [
                    'jquery'
                ]
            },
            bootstrapModal: {
                deps: [
                    'jquery',
                    'bootstrapTransition'
                ]
            },
            bootstrapPopover: {
                deps: [
                    'jquery',
                    'bootstrapTooltip'
                ]
            },
            bootstrapScrollspy: {
                deps: [
                    'jquery'
                ]
            },
            bootstrapTab: {
                deps: [
                    'jquery',
                    'bootstrapTransition'
                ]
            },
            bootstrapTooltip: {
                deps: [
                    'jquery',
                    'bootstrapTransition'
                ]
            },
            bootstrapTransition: {
                deps: [
                    'jquery'
                ]
            }
        }
    });

    require([ 'component.Director' ], function ( Director ) {

        Director.socialise({
            target: '#jumbotron',
            context: 'xyz'
        });

    });


}) (window);
