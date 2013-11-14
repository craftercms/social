(function (S) {
    'use strict';

    var window      = S.window;
    var Handlebars  = window.Handlebars;

    S.define('TemplateEngine', Handlebars, 'social.TemplateEngine');

    // Handlebars Helpers

    Handlebars.registerHelper('html', function( html ) {
        return new Handlebars.SafeString(html);
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

}) (crafter.social);