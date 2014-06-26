(function (S) {
    'use strict';
    /* jshint -W015 */
    /* jshint -W086 */

    var window      = S.window;
    var Handlebars  = window.Handlebars;
    var U           = S.util;

    S.define('TemplateEngine', Handlebars, 'social.TemplateEngine');

    // Handlebars Helpers

    Handlebars.registerHelper('html', function( html ) {
        return new Handlebars.SafeString(html);
    });

    Handlebars.registerHelper('eq', function( value, compare, options ) {
        if ( value === compare ) {
            return options.fn(this);
        } else {
            return options.inverse(this);
        }
    });

    Handlebars.registerHelper('gt', function( value, compare, options ) {
        if ( value > compare ) {
            return options.fn(this);
        } else {
            return options.inverse(this);
        }
    });

    Handlebars.registerHelper('lt', function( value, compare, options ) {
        if ( value < compare ) {
            return options.fn(this);
        } else {
            return options.inverse(this);
        }
    });

    Handlebars.registerHelper('mif', function( value, type, compare, options ) {
        if (U.isFunction(value)) {
            value = value.call(this);
        }
        if (U.isFunction(compare)) {
            compare = compare.call(this);
        }
        switch (type) {
            case 'has':
            case 'contains': {
                // TODO currently only supports arrays
                if ( U.contains(value, compare) ) {
                    return options.fn(this);
                } else {
                    return options.inverse(this);
                }
            }
            case '||':
            case 'or': {
                if ( value || compare ) {
                    return options.fn(this);
                } else {
                    return options.inverse(this);
                }
            }
            case 'gt':
            case '>': {
                if ( value > compare ) {
                    return options.fn(this);
                } else {
                    return options.inverse(this);
                }
            }
            case 'lt':
            case '<': {
                if ( value < compare ) {
                    return options.fn(this);
                } else {
                    return options.inverse(this);
                }
            }
            case '&&':
            case 'and': {
                if ( value && compare ) {
                    return options.fn(this);
                } else {
                    return options.inverse(this);
                }
            }
        }
    });

    Handlebars.registerHelper('munless', function( value, type, compare ) {
        if (U.isFunction(value)) {
            value = value.call(this);
        }
        if (U.isFunction(compare)) {
            compare = compare.call(this);
        }
        // TODO
    });

    Handlebars.registerHelper('ternary', function( condition, question, colon ) {
        if (U.isFunction(condition)) {
            condition = condition.call(this);
        }
        return new Handlebars.SafeString( condition ? question : colon );
    });

    Handlebars.registerHelper('multiplicity', function( number, plural, singular, zero ) {
        var string = (number === 0 ? zero : (number > 1) ? plural : singular).fmt(number);
        return new Handlebars.SafeString( string );
    });

    Handlebars.registerHelper('minusOne', function( number ) {
        return new Handlebars.SafeString( --number );
    });

    Handlebars.registerHelper('round', function( value ) {
        return new Handlebars.SafeString(Math.round(value));
    });

    Handlebars.registerHelper('divide', function( x, y ) {
        return new Handlebars.SafeString(Math.round(x / y));
    });

    Handlebars.registerHelper('date', function( millis ) {

        var date = new Date(millis);
        var format;

        var str = (format || '{day} {date}, {month} {year}').fmt({
            month: 'months.%@'.fmt(date.getMonth()).loc(),
            day: 'days.%@'.fmt(date.getDay()).loc(),
            date: date.getDate(),
            year: date.getFullYear()
        });

        return new Handlebars.SafeString(str);

    });

    Handlebars.registerHelper('formatFileSize', function ( bytes ) {
        if (typeof bytes !== 'number') {
            return '';
        }
        if (bytes >= 1000000000) {
            return (bytes / 1000000000).toFixed(2) + ' GB';
        }
        if (bytes >= 1000000) {
            return (bytes / 1000000).toFixed(2) + ' MB';
        }
        return (bytes / 1000).toFixed(2) + ' KB';
    });

    Handlebars.registerHelper('log', function(  ) {
        console && console.log && console.log(this);
    });

    S.noConflict('Handlebars');

}) (crafter.social);