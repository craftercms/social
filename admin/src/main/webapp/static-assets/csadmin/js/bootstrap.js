jQuery(document).ready(function($){

    'use strict'
    
    var PROPERTIES_PATH = '/crafter-social-admin/static-assets/csadmin/properties/',
        APP_CONFIG = '/crafter-social/api/2/config/config.json',
        appConfigDeferred = $.getJSON(APP_CONFIG),
        sectionsConfigDeferred = $.getJSON(PROPERTIES_PATH + "moderation_status_action.json");

    // Load the configuration files before bootstrapping our angular app
    $.when(appConfigDeferred, sectionsConfigDeferred)
        .done( function (appConfigResult, sectionsConfigResult) {

            var appConfig = appConfigResult[0],
                sectionsConfig = sectionsConfigResult[0],
                section,
                defaultSection;

            // Find the section selected by default
            for (var i = 0; i < sectionsConfig.length; i++) {
                section = sectionsConfig[i];
                if (section.moderation && section.moderation.default) {
                    defaultSection = section.moderation.value.toUpperCase();
                    break;
                }
            }

            // If none of the sections have the 'default' attribute, then we'll set the first section to
            // be the default section.
            if (!defaultSection) {
                defaultSection = sectionsConfig[0].moderation.value.toUpperCase();
            }

            // Make the environment configuration available to our angular app
            angular.module('moderationDashboard')
                .constant('ENV', {
                    config: appConfig,
                    sections: sectionsConfig,
                    defaultSection: defaultSection
                });

            angular.bootstrap($('#moderation-dashboard'), ['moderationDashboard']);
        })
        .fail( function () {
            throw new Error("Unable to retrieve configuration files");
        });
});