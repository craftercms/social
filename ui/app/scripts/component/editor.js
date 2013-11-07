(function (S) {
    'use strict';

    var CKEDITOR = window.CKEDITOR;

    S.define('Editor', CKEDITOR, 'social.Editor');

    CKEDITOR.basePath = S.url('libs/ckeditor/');

    CKEDITOR.config.customConfig = S.url('scripts/component/ckeditor/config.js');

}) (crafter.social);