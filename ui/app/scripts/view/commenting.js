(function (S) {
    'use strict';

    var Commenting,
        Base = S.view.Base,
        app = S.getDirector(),
        CKEDITOR = S.Editor,
        $ = S.$;

    Commenting = Base.extend({

        className: [
            Base.prototype.className,
            'crafter-social-commenting-view'
        ].join(' '),

        events: {
            'click [data-action-comment]': 'comment'
        },

        editor: function () {
            var editor = this.cache('editor');
            if (!editor) {

                var $textarea = this.$('textarea'),
                    me = this;

                editor = CKEDITOR[this.cfg.editorMode]($textarea.get(0), $.extend({
                    on: {
                        pluginsLoaded: function () {
                            this.addCommand('_enterpressed_', {
                                exec : function( /* editor, data */ ) {
                                    me.comment();
                                }
                            });
                            this.keystrokeHandler.keystrokes[CKEDITOR.SHIFT + 13] = '_enterpressed_';
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

            var data = {
                profile: app.getProfile().toJSON(),
                textContent: content,
                dateAdded: Date.now(),
                tenant: this.cfg.tenant,
                targetId: this.cfg.target
            };

            console.log(data);

            // TODO temporary, remove.
            (S.window.tempGetCustomAttributes) && (S.window.tempGetCustomAttributes(data));

            this.collection.create(data);

            editor.setData('');
            editor.focus();

        },

        render: function () {
            this.editor();
            return this;
        }

    });

    Commenting.DEFAULTS = $.extend({}, Base.DEFAULTS, {
        templates: {
            main: function () {
                return ('%@commenting.hbs').fmt(S.Cfg('url.templates'));
            }
        },
        editorMode: 'inline',
        /* jshint -W106 */
        // http://docs.ckeditor.com/#!/api/CKEDITOR.config
        editor: {
            'extraPlugins': 'autogrow',
            'autoGrow_maxHeight': 800,
            // Remove the Resize plugin as it does not make sense to use it in conjunction with the AutoGrow plugin.
            'removePlugins': 'resize',
            customConfig: '',
            toolbar_Basic: [
                [
                    'Bold',
                    'Italic',
                    'Underline', '-',
                    'NumberedList',
                    'BulletedList', '-',
                    'Link', '-',
                    'Image'
                ]
            ],
            toolbar: 'Basic'
        }
    });

    S.define('view.Commenting', Commenting);

}) (crafter.social);