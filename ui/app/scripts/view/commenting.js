(function (S) {
    'use strict';

    var Commenting,
        Base = S.view.Base,
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

        editor: function ( option ) {

            var editor = this.cache('editor');

            if ( option === 'destroy' ) {

                if ( editor ) {
                    editor.destroy();
                    this.cache('editor', S.Constants.get('DESTROY'));
                }

                return;

            }

            if (!editor) {

                var me = this;
                editor = me.cache('editor');

                if (!editor) {

                    var mode        = me.cfg.editorMode;
                    var $container  = me.$('.textarea:first');
                    var $textarea   = $container.find('textarea:first');

                    $container.addClass('editor-mode-' + mode);

                    editor = CKEDITOR[mode]($textarea.get(0), $.extend({
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
                    }, me.cfg.editor));

                    me.cache('editor', editor);

                }

            }

            return editor;

        },

        comment: function () {

            var editor = this.editor(),
                content = editor.getData();

            if (!content) { return; }

            var data = {
                body: content,
                dateAdded: Date.now(),
                thread: this.cfg.target,
                context: this.cfg.context,
                attributes: {}
            };

            var collection = this.collection;
            collection.create(data, {
                error: function (model) {

                    // Put back the comment that didn't get posted.
                    // Attach any text that could have been written while request completed.
                    editor.setData('<div>' + model.get('body') + '</div>' + editor.getData());
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

//        editorMode: 'inline',
        editorMode: 'replace',
//        editorMode: 'appendTo',

        /**
         * @see http://docs.ckeditor.com/#!/api/CKEDITOR.config
         *//* jshint -W106 */
        editor: {
            autoGrow_bottomSpace: 0,
            autoGrow_maxHeight: 800,
            autoGrow_minHeight: 0,
            autoGrow_onStartup: false,
            autoParagraph: true,
            autoUpdateElement: true,
            baseFloatZIndex: 10000,
            baseHref: '',
            basicEntities: true,
            blockedKeystrokes: [
                CKEDITOR.CTRL + 66 /*B*/,
                CKEDITOR.CTRL + 73 /*I*/,
                CKEDITOR.CTRL + 85 /*U*/
            ],
            bodyClass: '',
            bodyId: '',
            browserContextMenuOnCtrl: true,
            colorButton_backStyle: {
                element : 'span',
                styles : { 'background-color' : '#(color)' }
            },
            colorButton_colors: '000,800000,8B4513,2F4F4F,008080,000080,4B0082,696969,B22222,A52A2A,DAA520,006400,40E0D0,0000CD,800080,808080,F00,FF8C00,FFD700,008000,0FF,00F,EE82EE,A9A9A9,FFA07A,FFA500,FFFF00,00FF00,AFEEEE,ADD8E6,DDA0DD,D3D3D3,FFF0F5,FAEBD7,FFFFE0,F0FFF0,F0FFFF,F0F8FF,E6E6FA,FFF',
            colorButton_enableMore: true,
            colorButton_foreStyle: {
                element : 'span',
                styles : { 'color' : '#(color)' }
            },
            contentsCss: S.Cfg('url.base') + 'styles/editor-content.css',
            contentsLangDirection: 'ui',
            contentsLanguage: 'en',
            corePlugins: '',
            coreStyles_bold: { element : 'strong', overrides : 'b' },
            coreStyles_italic: { element : 'em', overrides : 'i' },
            coreStyles_strike: { element : 'strike' },
            coreStyles_subscript: { element : 'sub' },
            coreStyles_superscript: { element : 'sup' },
            coreStyles_underline: { element : 'u' },
            customConfig: '',
            defaultLanguage: 'en',
            devtools_styles: [
                '#cke_tooltip { padding: 5px; border: 2px solid #333; background: #ffffff }',
                '#cke_tooltip h2 { font-size: 1.1em; border-bottom: 1px solid; margin: 0; padding: 1px; }',
                '#cke_tooltip ul { padding: 0pt; list-style-type: none; }',
            ].join(''),
            dialog_backgroundCoverColor: 'white',
            dialog_backgroundCoverOpacity: 0.5,
            dialog_buttonsOrder: 'OS',
            dialog_magnetDistance: 20,
            dialog_startupFocusTab: false,
            disableNativeSpellChecker: true,
            disableNativeTableHandles: true,
            disableObjectResizing: false,
            disableReadonlyStyling: false,
            docType: '<!DOCTYPE html>',
            editingBlock: true,
            emailProtection: '',
            enableTabKeyTools: true,
            enterMode: CKEDITOR.ENTER_P,
            entities: true,
            entities_additional: '#39',
            entities_greek: true,
            entities_latin: true,
            entities_processNumerical: false,
            extraPlugins: '',
            filebrowserBrowseUrl: '',
            filebrowserFlashBrowseUrl: '',
            filebrowserFlashUploadUrl: '',
            filebrowserImageBrowseLinkUrl: '',
            filebrowserImageBrowseUrl: '',
            filebrowserImageUploadUrl: '',
            filebrowserUploadUrl: '',
            filebrowserWindowFeatures: '',
            filebrowserWindowHeight: '',
            filebrowserWindowWidth: '',
            fillEmptyBlocks: true,
            find_highlight: {
                element : 'span',
                styles : { 'background-color' : '#ff0', 'color' : '#00f' }
            },
            font_defaultLabel: 'Arial',
            font_names: 'Arial;Times New Roman;Verdana',
            font_style: {
                element		: 'span',
                styles		: { 'font-family' : '#(family)' },
                overrides	: [ { element : 'font', attributes : { 'face' : null } } ]
            },
            fontSize_defaultLabel: '12px',
            fontSize_sizes: '8/8px;9/9px;10/10px;11/11px;12/12px;14/14px;16/16px;18/18px;20/20px;22/22px;24/24px;26/26px;28/28px;36/36px;48/48px;72/72px',
            fontSize_style: {
                element		: 'span',
                styles		: { 'font-size' : '#(size)' },
                overrides	: [ { element : 'font', attributes : { 'size' : null } } ]
            },
            forceEnterMode: false,
            forcePasteAsPlainText: false,
            forceSimpleAmpersand: false,
            format_address: { element: 'address' },
            format_div: { element: 'div' },
            format_h1: { element: 'h1' },
            format_h2: { element: 'h2' },
            format_h3: { element: 'h3' },
            format_h4: { element: 'h4' },
            format_h5: { element: 'h5' },
            format_h6: { element: 'h6' },
            format_p: { element: 'p' },
            format_pre: { element: 'pre' },
            format_tags: 'p;h1;h2;h3;h4;h5;h6;pre;address;div',
            fullPage: false,
            height: 30,
            htmlEncodeOutput: false,
            ignoreEmptyParagraph: true,
            image_previewText: ' ',
            image_removeLinkByEmptyURL: true,
            indentClasses: null,
            indentOffset: 40,
            indentUnit: 'px',
            jqueryOverrideVal: true,
            justifyClasses: null,
            keystrokes: [
                [ CKEDITOR.ALT + 121 /*F10*/, 'toolbarFocus' ],
                [ CKEDITOR.ALT + 122 /*F11*/, 'elementsPathFocus' ],

                [ CKEDITOR.SHIFT + 121 /*F10*/, 'contextMenu' ],

                [ CKEDITOR.CTRL + 90 /*Z*/, 'undo' ],
                [ CKEDITOR.CTRL + 89 /*Y*/, 'redo' ],
                [ CKEDITOR.CTRL + CKEDITOR.SHIFT + 90 /*Z*/, 'redo' ],

                [ CKEDITOR.CTRL + 76 /*L*/, 'link' ],

                [ CKEDITOR.CTRL + 66 /*B*/, 'bold' ],
                [ CKEDITOR.CTRL + 73 /*I*/, 'italic' ],
                [ CKEDITOR.CTRL + 85 /*U*/, 'underline' ],

                [ CKEDITOR.ALT + 109 /*-*/, 'toolbarCollapse' ]
            ],
            language: '',
            menu_groups: 'clipboard,form,tablecell,tablecellproperties,tablerow,tablecolumn,table,anchor,link,image,flash,checkbox,radio,textfield,hiddenfield,imagebutton,button,select,textarea',
            newpage_html: '',
            pasteFromWordCleanupFile: 'default',
            pasteFromWordNumberedHeadingToList: false,
            pasteFromWordPromptCleanup: undefined,
            pasteFromWordRemoveFontStyles: false,
            pasteFromWordRemoveStyles: true,
            plugins: 'dialogui,dialog,about,a11yhelp,basicstyles,blockquote,clipboard,panel,floatpanel,menu,contextmenu,button,toolbar,enterkey,entities,popup,filebrowser,floatingspace,listblock,richcombo,format,horizontalrule,htmlwriter,wysiwygarea,image,indent,indentlist,fakeobjects,link,list,magicline,maximize,pastetext,pastefromword,removeformat,sourcearea,specialchar,menubutton,scayt,stylescombo,tab,table,tabletools,undo,wsc',
            protectedSource: [],
            readOnly: false,
            removeDialogTabs: 'link:advanced',
            removeFormatAttributes: 'class,style,lang,width,height,align,hspace,valign',
            removeFormatTags: 'b,big,code,del,dfn,em,font,i,ins,kbd,q,samp,small,span,strike,strong,sub,sup,tt,u,var',
            removePlugins: 'elementspath,resize,save,font',
            resize_dir: 'both',
            resize_enabled: true,
            resize_maxHeight: 800,
            resize_maxWidth: 0,
            resize_minHeight: 100,
            resize_minWidth: 290,
            scayt_autoStartup: false,
            scayt_contextCommands: 'all',
            scayt_contextMenuItemsOrder: 'suggest|moresuggest|control',
            scayt_customDictionaryIds: '',
            scayt_customerid: '',
            scayt_maxSuggestions: 5,
            scayt_moreSuggestions: 'on',
            scayt_sLang: 'en_US',
            scayt_srcUrl: '',
            scayt_uiTabs: '1,1,1',
            scayt_userDictionaryName: '',
            sharedSpaces: undefined,
            shiftEnterMode: CKEDITOR.ENTER_BR,
            skin: 'moono',
            smiley_columns: 8,
            smiley_descriptions: [
                'smiley', 'sad', 'wink', 'laugh', 'frown', 'cheeky', 'blush', 'surprise',
                'indecision', 'angry', 'angel', 'cool', 'devil', 'crying', 'enlightened', 'no',
                'yes', 'heart', 'broken heart', 'kiss', 'mail'
            ],
            smiley_images: [
                'regular_smile.gif','sad_smile.gif','wink_smile.gif','teeth_smile.gif','confused_smile.gif','tounge_smile.gif',
                'embaressed_smile.gif','omg_smile.gif','whatchutalkingabout_smile.gif','angry_smile.gif','angel_smile.gif','shades_smile.gif',
                'devil_smile.gif','cry_smile.gif','lightbulb.gif','thumbs_down.gif','thumbs_up.gif','heart.gif',
                'broken_heart.gif','kiss.gif','envelope.gif'
            ],
            smiley_path: CKEDITOR.basePath + 'plugins/smiley/images/',
            specialChars: [],
            startupFocus: false,
            startupMode: 'wysiwyg',
            startupOutlineBlocks: false,
            startupShowBorders: true,
            stylesheetParser_skipSelectors: /(^body\.|^\.)/i,
            stylesheetParser_validSelectors: /\w+\.\w+/,
            stylesSet: [],
            tabIndex: 0,
            tabSpaces: 0,
            templates: 'default',
            templates_files: [ 'plugins/templates/templates/default.js' ],
            templates_replaceContent: true,
//            theme: 'monoo',
            toolbar: 'Basic',
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
            toolbar_Full: [
                { name: 'document',    items : [ 'Source','-','Save','NewPage','DocProps','Preview','Print','-','Templates' ] },
                { name: 'clipboard',   items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
                { name: 'editing',     items : [ 'Find','Replace','-','SelectAll','-','SpellChecker', 'Scayt' ] },
                { name: 'forms',       items : [ 'Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField' ] },
                '/',
                { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ] },
                { name: 'paragraph',   items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','CreateDiv','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ] },
                { name: 'links',       items : [ 'Link','Unlink','Anchor' ] },
                { name: 'insert',      items : [ 'Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak' ] },
                '/',
                { name: 'styles',      items : [ 'Styles','Format','Font','FontSize' ] },
                { name: 'colors',      items : [ 'TextColor','BGColor' ] },
                { name: 'tools',       items : [ 'Maximize', 'ShowBlocks','-','About' ] }
            ],
            toolbarCanCollapse: false,
            toolbarGroupCycling: true,
            toolbarLocation: 'bottom',
            toolbarStartupExpanded: true,
            uiColor: '#EBEBEB',
            undoStackSize: 20,
            useComputedState: true,
            width: ''
        }
    });

    S.define('view.Commenting', Commenting);

}) (crafter.social);