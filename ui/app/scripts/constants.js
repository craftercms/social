(function (S) {
    'use strict';

    var _ = {

        MODERATION_STATUS_UNMODERATED       : 'UNMODERATED',
        MODERATION_STATUS_PENDING           : 'PENDING',
        MODERATION_STATUS_APPROVED          : 'APPROVED',
        MODERATION_STATUS_SPAM              : 'SPAM',
        MODERATION_STATUS_TRASH             : 'TRASH',

        EVENT_SOCIAL_CONFIG_CHANGED         : 'crafter.social.configuration.changed',

        EVENT_AREAS_VISIBILITY_CHANGE       : 'crater.social.event.areas.visibility.change',
        EVENT_UNAUTHORISED_RESPONSE         : 'crafter.social.event.unauthorised.response',
        EVENT_USER_AUTHENTICATION_SUCCESS   : 'crafter.social.event.user.authentication.success',
        EVENT_USER_AUTHENTICATION_FAILED    : 'crafter.social.event.user.authentication.failed',

        AREA_VISIBILITY_MODE_REVEAL         : 'area.visibility.mode.reveal',
        AREA_VISIBILITY_MODE_HOVER          : 'area.visibility.mode.hover',
        AREA_VISIBILITY_MODE_HIDE           : 'area.visibility.mode.hide',

        //
        // Dynamic Constants
        //
        EVENT_REVEAL_DISCUSSIONS        : 'crafter.social.reveal.discussions.{tenant}:{target}',
        EVENT_DISCUSSION_WATCHED        : 'crafter.social.discussion.watched.{tenant}:{target}',

        DESTROY: { }

    };

    S.define('Constants', {
        get: function ( key /* , format1, format2 || { format1: 'value1', ... } */ ) {
            if ( arguments.length === 1 ) {
                return _[key];
            }
            var args = Array.prototype.slice.call(arguments, 0);
            Array.prototype.splice.call(args, 0, 1, _[key]);
            return S.string.fmt.apply(S.string, args);
        },
        define: function ( key, value ) {
            if ( typeof key === 'object' ) {
                value = key;
                for ( key in value ) {
                    this.define(key, value[key]);
                }
            } else {
                if ( !(key in _) ) {
                    _[key] = value;
                } else {
                    S.util.log(
                        'Constant %@ is already defined (value: %@). Value not changed.',
                        key, S.Constants.get(key));
                }
            }
            return true;
        }
    }, 'social.Constants');

}) (crafter.social);