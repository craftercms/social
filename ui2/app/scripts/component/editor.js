(function (S) {
    'use strict';

    var CKEDITOR = window.CKEDITOR;

    S.define('Editor', CKEDITOR, 'social.Editor');

    CKEDITOR.basePath = S.resource('libs/ckeditor/');
    CKEDITOR.plugins.basePath = S.resource('libs/ckeditor/');

    CKEDITOR.config.customConfig = S.resource('scripts/component/ckeditor/config.js');

    // TODO ckeditor.js uses the window.CKEDITOR all over the place
    // S.noConflict('CKEDITOR');

}) (crafter.social);