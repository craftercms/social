/* jshint -W106 */
(function (S) {
    'use strict';

    var ready   = S.window.crafterSocial_onAppReady,
        params  = [ S.get(S.Cfg('director')) || S.get(S.Cfg('director'), window), S ];

    if ( S.util.isArray(ready) ) {

        ready.every(function (fn) {
            return fn.apply(null, params);
        });

    } else if (ready) {

        ready.apply(null, params);

    }

}) (crafter.social);