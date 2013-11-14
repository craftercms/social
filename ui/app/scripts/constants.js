(function (S) {
    'use strict';

    var _ = {

        EVENT_SOCIAL_CONFIG_CHANGED: 'crafter.social.configuration.changed',

        EVENT_AREAS_VISIBILITY_CHANGE: 'crater.social.event.areas.visibility.change',
        EVENT_UNAUTHORISED_RESPONSE: 'crafter.social.event.unauthorised.response',
        EVENT_USER_AUTHENTICATION_SUCCESS: 'crafter.social.event.user.authentication.success',
        EVENT_USER_AUTHENTICATION_FAILED: 'crafter.social.event.user.authentication.failed',

        AREA_VISIBILITY_MODE_REVEAL: 'area.visibility.mode.reveal',
        AREA_VISIBILITY_MODE_HOVER: 'area.visibility.mode.hover',
        AREA_VISIBILITY_MODE_HIDE: 'area.visibility.mode.hide'

    };

    S.define('Constants', {
        get: function ( key ) { return _[key]; },
        define: function ( key, value ) {
            if ( !(key in _) ) {
                _[key] = value;
                return true;
            } else {
                S.util.log(
                    'Constant %@ is already defined (value: %@). Value not changed.',
                    key, S.Constants.get(key));
                return true;
            }
        }
    }, 'social.Constants');

}) (crafter.social);