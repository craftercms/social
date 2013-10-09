'use strict';

/* Services */
angular.module('moderationDashboard.services', ['ngResource', 'ngCookies']).
    value('version', '0.1').
    /**
     * Api: util services
     * - ugc: get ugc according the moderation status
     * - moderationAction: get moderation actions from each moderation
     * - defaultTenant: get default tenant and moderation configured in property file
     **/
    factory('Api', function ($resource, $cookies) {
        var TICKET = $cookies.crafterAuthCookie,
            ugcUrl = "/crafter-social/api/2/ugc/moderation/:moderation?ticket=:ticket&tenant=:tenant",
            moderationUrl = "/crafter-social-admin/resources/properties/moderation_status_action.json",
            defaultUrl = "/crafter-social-admin/resources/properties/default_tenant.json",
            updateIndividualUrl = "/crafter-social/api/2/ugc/moderation/:ugcId/status.json?ticket=:ticket&moderationStatus=:modstatus&tenant=:tenant";

        return {
            Ugc: $resource(ugcUrl, { ticket: TICKET} ),
            moderationAction: $resource(moderationUrl),
            defaultTenant: $resource(defaultUrl),
            updateModeration: $resource(updateIndividualUrl, { ticket: TICKET}, {
                update: {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        params: {
                            ugcId: "ugcid",
                            ticket: TICKET,
                            modstatus: "modstatus",
                            tenant: "craftercms"
                        }
                    }
                }
            })
        };
    });
