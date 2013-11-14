/* jshint -W106 */
(function (S) {
    'use strict';

    var ready   = S.window.crafterSocial_onAppReady,
        params  = [ S.getDirector(), S ];

    if ( S.util.isArray(ready) ) {

        ready.every(function (fn) {
            return fn.apply(null, params);
        });

    } else if (ready) {

        ready.apply(null, params);

    }

}) (crafter.social);