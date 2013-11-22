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

            // TODO evaluate if this should stay. Might not be great for security.
            app.trigger('social.view.Commenting.beforecreate', data);

            var collection = this.collection;
            collection.create(data, {
                error: function (model) {

                    // Put back the comment that didn't get posted.
                    // Attach any text that could have been written while request completed.
                    editor.setData('<div>' + model.get('textContent') + '</div>' + editor.getData());
                    // Put the cursor position to the end of the editor, where it is likely that it will be
                    var range = editor.createRange();
                    range.moveToPosition(range.root, CKEDITOR.POSITION_BEFORE_END);
                    editor.getSelection().selectRanges([ range ]);
                    // Remove the un-posted comment from the controller
                    collection.remove(model);

                }
            });

            editor.setData('');
            // this.$('button.reply').focus();

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