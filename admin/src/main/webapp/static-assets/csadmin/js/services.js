'use strict';

/* Services */
angular.module('moderationDashboard.services', ['ngResource']).
    value('version', '0.1').
    /**
     * Api: util services
     * - ugc: get ugc according the moderation status
     * - moderationAction: get moderation actions from each moderation
     * - defaultTenant: get default tenant and moderation configured in property file
     **/
    factory('Api', function ($resource) {
        var ugcUrl = "/crafter-social/api/2/ugc/moderation/:moderation?tenant=:tenant&page=:page&pageSize=:pageSize",
            moderationUrl = "/crafter-social-admin/static-assets/csadmin/properties/moderation_status_action.json",
            defaultUrl = "/crafter-social-admin/static-assets/csadmin/properties/app_conf.json",
            updateIndividualUrl = "/crafter-social/api/2/ugc/moderation/:moderationid/status.json?moderationStatus=:moderationstatus&tenant=:tenant";

        return {
            Ugc: $resource(ugcUrl),
            moderationAction: $resource(moderationUrl),
            defaultTenant: $resource(defaultUrl),
            updateModeration: $resource(updateIndividualUrl, {}, {
                update: {
                    method: 'POST',
                    responseType: 'application/json'
                }
            })
        };
    });