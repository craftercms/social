(function (S) {
    'use strict';

    var Lightbox,
        Modal = S.view.Modal,
        Discussion = S.view.Discussion,
        U = S.util,
        $ = S.$;

    var LightboxPrototype = $.extend({}, Discussion.prototype, {

        className: [
            Modal.prototype.className,
            Discussion.prototype.className,
            'crafter-social-lightbox-view'
        ].join(' '),

        initialize: function () {
            Modal.prototype.initialize.apply(this, arguments);
            Discussion.prototype.initialize.apply(this, arguments);
        },

        listen: function () {
            Modal.prototype.listen.apply(this, arguments);
            Discussion.prototype.listen.apply(this, arguments);

            var me = this;
            $(this.$el)
                .one('shown.bs.modal', function () {
                    me.initCommentingView();
                });

        },

        createUI: function () {

            Modal.prototype.createUI.apply(this, arguments);

            this.$('.modal-header')
                .prepend('<div class="options-view-container pull-right"></div>')
                .find('.modal-title')
                    .text('Full Thread View')
                    .end()
                .find('.close')
                    .remove();

            this.$('.modal-body')
                .after(this.getTemplate('comment-box'));

            this.$('footer.modal-footer').addClass('reply-box');

            Discussion.prototype.createUI.apply(this, arguments);

        },

        render: function () {

            var $target = $(this.cfg.target);
            var $clone  = $target.clone();
            var include = { 'font-size': 'inherit', 'text-align': 'inherit' };
            var me      = this;

            // styles might get lost based on class declaration and/or
            // the new position in the DOM. Also due to inherited styles
            // from crafter-social-view class.
            $clone.css(this.compileStyles($target, null, { 'margin-bottom': 0 }));
            $clone.find('>*, >*>*, >*>*>*').each(function () { $(this).css(me.compileStyles(this, include)); });
            // remove the classes added by the commentable view
            $clone.removeClass('reveal crafter-social-commentable');
            // TODO Commentable should tell this component which classes should be removed

            var $container = this.$('.modal-body');
            $container.html('');
            $container.append($clone);

            this.addAll();

            return Modal.prototype.render.apply(this, arguments);

        },

        compileStyles: function ( $elem, include, override ) {
            try {
                if ('getMatchedCSSRules' in S.window) {

                    var styles      = {  };
                    var rules       = S.window.getMatchedCSSRules( $elem instanceof $ ? $elem.get(0) : $elem ),
                        strRules    = [],
                        aRules;

                    rules && $.each(rules, function (i, rule) {
                        var cssText = rule.cssText;
                        if (cssText.indexOf('crafter-social') === -1) {

                            var css = cssText.match(/\{(.+?)\}/g)[0]
                                .replace(/\{/g, '')
                                .replace(/\}/g, '') + ';';

                            strRules.push(css);

                        }
                    });

                    aRules = strRules.j().split(';');
                    $.each(aRules, function (i, rule) {
                        if (rule.trim() !== '') {
                            var split = rule.split(':');
                            styles[split[0].trim()] = split[1].trim();
                        }
                    });

                    return $.extend({}, include || {}, styles, override || {});

                } else {

                    // TODO get a fixed set of styles with jQuery?
                    // eg { margin: $elem.css('margin'), padding: $elem.css('padding') ... }
                    /*

                    // http://stackoverflow.com/questions/4781410/jquery-how-to-get-all-styles-css-defined-within-internal-external-document-w

                    var selector = '#jumbotron'.split(',').map(function(subselector){
                        return subselector + ',' + subselector + ' *';
                    }).join(',');

                    var elts = $(selector);

                    var rulesUsed = [];
                    // main part: walking through all declared style rules
                    // and checking, whether it is applied to some element
                    var sheets = document.styleSheets;
                    for(var c = 0; c < sheets.length; c++) {
                        var rules = sheets[c].rules || sheets[c].cssRules;
                        for(var r = 0; r < rules.length; r++) {
                            var selectorText = rules[r].selectorText;
                            var matchedElts = $(selectorText);
                            for (var i = 0; i < elts.length; i++) {
                                if (matchedElts.index(elts[i]) != -1) {
                                    rulesUsed.push(rules[r]);
                                    break;
                                }
                            }
                        }
                    }

                    var style = rulesUsed.map(function(cssRule){
                        var cssText = cssRule.cssText || cssRule.style.cssText.toLowerCase();
                        // some beautifying of css
                        return cssText.replace(/(\{|;)\s+/g, "\$1\n  ").replace(/\A\s+}/, "}");
                        //                 set indent for css here ^
                    }).join('\n');

                    var stylelem = '<style>\n' + style + '\n</style>\n\n';
                    */
                    return {};
                }
            } catch (ex) {
                U.log(ex.message || ex);
                return {};
            }
        }
    });

    delete(LightboxPrototype.constructor);
    Lightbox = Modal.extend(LightboxPrototype);

    /** @see view.Modal & view.Discussion for more */
    Lightbox.DEFAULTS = $.extend(true, {}, Modal.DEFAULTS, Discussion.DEFAULTS, {
        classes: null,
        initCommentingView: false,
        viewOptions: { hidden: ['lightbox.request'] },
        templates: {
            /* jshint -W015 */
            'comment-box': [
                '<div class="modal-comment-box">',
                '<h4 class="comment-box-title">Comments</h4>',
                '<div class="comments crafter-social-comment-thread">',
                '<div class="no-comments">(no comments)</div>',
                '</div>',
                '</div>'
            ].join('')
        }
    });

    Lightbox.DEFAULTS.classes = []
            .concat(Modal.DEFAULTS.classes || [])
            .concat(Discussion.DEFAULTS.classes || []);

    Lightbox.DEFAULTS.classes.push('crafter-social-lightbox-view');

    S.define('view.Lightbox', Lightbox);

}) (crafter.social);