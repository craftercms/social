(function (S) {
    'use strict';

    var Commenting,
        Base = S.view.Base,
        app = S.component.Director,
        CKEDITOR = S.Editor,
        C = S.Constants,
        $ = S.$;

    Commenting = Base.extend({
        events: {
            'click [data-action-comment]': 'comment'
        },
        initialize: function (config) {
            var newConfig = $.extend(true, {}, Commenting.DEFAULTS, config);
            Base.prototype.initialize.call(this, newConfig);
        },
        editor: function () {
            var editor = this.cache('editor');
            if (!editor) {

                var $textarea = this.$('textarea'),
                    me = this;

                editor = CKEDITOR.inline($textarea.get(0), $.extend({
                    on: {
                        pluginsLoaded: function () {
                            this.addCommand('myGreetingCommand', {
                                exec : function( /* editor, data */ ) {
                                    me.comment();
                                }
                            });
                            this.keystrokeHandler.keystrokes[CKEDITOR.SHIFT + 13] = 'myGreetingCommand';
                        }
                    }
                }, this.cfg.editor));

                this.cache('editor', editor);
            }
            return editor;
        },
        comment: function () {

            var editor = this.editor(),
                content = editor.getData();

            if (!content) { return; }

            this.collection.create({
                textContent: JSON.stringify({ content: content }),
                content: content,
                profile: app.getUser(),
                dateAdded: Date.now(),
                tenant: this.cfg.tenant,
                target: this.cfg.target
            });

            editor.setData('');
            editor.focus();

        },
        render: function () {

            this.editor();

            return this;

        }
    });

    Commenting.DEFAULTS = $.extend({}, {
        classes: ['crafter-social-commenting-view'],
        templates: {
            main: ('%@commenting.hbs').fmt(C.TEMPLATES_URL)
        },
        editor: {
            'extraPlugins': 'autogrow',
            'autoGrow_maxHeight': 800,
            // Remove the Resize plugin as it does not make sense to use it in conjunction with the AutoGrow plugin.
            'removePlugins': 'resize'
        }
    });

    S.define('view.Commenting', Commenting);

}) (crafter.social);