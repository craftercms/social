/* jshint -W015 */
(function (S) {
    'use strict';

    var $ = S.$,
        join = function (array, chr) { return array.join(chr || ''); },
        tmpl = S.util.template,
        isJSON = S.util.checkJSON;

    var TMPL = join([
            '<div class="{{theme.fileDisplay}}>">',
                '<strong>{{file.name}}</strong>\n',
                '({{file.type}}, {{divide file.size 1024}} KB) - ',
                '<span class="progress">0%</span>',
            '</div>'
        ]),
        TMPL_ERROR = join([
            '<div class="{{theme.fileError}}">',
                '<span>',
                    'An error has occurred trying to read the dropped file. ',
                    '({{#if error}} {{error.info}} {{/if}})',
                '</span>',
            '</div>'
        ]),
        TMPL_UPLOAD_ERROR = join([
            '<div class="{{theme.uploadError}}">',
                '<span>An error has occurred trying to upload <i>"{{file.name}}"</i>.</span>', ' ',
                // TODO dismissing the message keeps the file UI container, remove it?
                '<small><a onclick="this.parentNode.parentNode.removeChild(this.parentNode)">Dismiss Message</a></small>',
            '</div>'
        ]),

        // Upload events
        SUCCESS = 'success',
        ERROR = 'error';

    TMPL = join([
        '<div class="{{ theme.fileDisplay }}">',
            '<div class="image shadow clearfix">',
            '<img src="{{file.src}}" alt="{{ file.name }}"',
                 'title="{{ file.name }}"',
                 'class="pull-left"/>',
            '<div class="title">{{ file.name }}</div>',
                '<div class="details">{{ file.type }} @ {{divide file.size 1024}} KB</div>',
                '<div class="progress">',
                    '<div class="progress-bar" style="width: {{divide file.size 1024}}%;">',
                        '<span class="sr-only">{{divide file.size 1024}}% Complete</span>',
                    '</div>',
                '</div>',
            '</div>',
        '</div>'
    ]);

    var extend = $.extend,
        getElem = function (value) {
            var elem;
            if (value instanceof HTMLElement) {
                elem = value;
            } else if (value instanceof S.$) {
                elem = value.get(0);
            } else if (typeof value === 'string') {
                elem = document.querySelector(value);
                !elem && (elem = document.getElementById(value));
            } else {
                elem = getElem (value.element);
            }
            return elem;
        };

    function FileManager () {
        this.files = {  };
        this.complete = {  };
    }

    $.extend(FileManager.prototype, {
        get: function (id) {
            return this.files[id];
        },
        push: function (id, file) {
            this.files[id] = file;
        },
        setCompleted: function (id) {
            this.complete[id] = true;
        }
    });

    var defaults = {
        /**
         * Element that will serve as the dropbox */
        element: '#dropbox',

        /**
         * Element that will display dropped
         * files and their status */
        display: '#dropbox',

        /**
         * File display UI element child that will serve as the
         * upload progress reporting element. Progress element is
         * passed to the showUploadProgress function */
        progress: '.progress',

        /**
         * The file in queue template */
        template: TMPL,
        /**
         * The file error template */
        templateError: TMPL_ERROR,
        /**
         * The file upload error template. Template is
         * called provided the file info, theme object,
         * HTTP response status code and the response text */
        templateUploadError: TMPL_UPLOAD_ERROR,

        /**
         * Theming css classes for the UI */
        theme: {
            over: 'dropbox-dragover',
            main: 'crafter-social-view crafter-social-dropbox-element',
            fileDisplay: 'dropbox-file-display',
            fileError: 'dropbox-file-error',
            uploadError: 'dropbox-upload-error'
        },

        /**
         * The URL to upload files to */
        target: '/url/to/upload/files',
        /**
         * Uploaded file name in the submitted form data */
        uploadPostKey: 'submitted-file',
        /**
         * Additional parameters to be sent with the form data */
        formData: { },

        /**
         * Newly dropped elements may go on top or at the bottom of the queue.
         * This proper controls whether the file list display
         * behaves as a stack or a queue */
        newOnTop: false,

        /**
         * Flag to determine whether to start uploading immediately or
         * on demand */
        immediateUpload: true
    };

    function Dropbox () {

        // Default Dropbox settings
        this.oCfg = defaults;

        this.manager = new FileManager ();
        this.subscribers = {  };

        var id = 'DRPBX_' + (++ids);
        this.getID = function () { return id; };
        this.init.apply(this, arguments);

    }

    var DP = Dropbox.prototype,
    // unique instance id counter
        ids = 0,
    // private members container object
        _;

    _ = {
        addClass: function (c) {
            $(this.element).addClass(c);
        },
        removeClass: function (c) {
            $(this.element).removeClass(c);
        },
        dragover: function (e) {
            e.dataTransfer.dropEffect = 'copy';
            _.addClass.call(this, this.oCfg.theme.over);
        },
        dragleave: function (/*e*/) {
            _.removeClass.call(this, this.oCfg.theme.over);
        },
        drop: function (e) {

            // instance settings
            var cfg = this.oCfg;
            _.removeClass.call(this, cfg.theme.over);

            // Initialise the instance file collection if it has not
            // previously been initialised
            var /*id = this.getID(),*/
                manager = this.manager, // instance files
                files = e.dataTransfer.files, // drag/dropped files
                display = this.display,
                me = this; // instance

            var onloadfn = function (aFile) {
                return function (evt) {

                    var fileID = 'FILE_' + (++ids),
                        data = {
                            'theme': cfg.theme,
                            'file': extend(aFile, {
                                // It seems like extending native type File
                                // does not create problems and the properties
                                // are usable
                                'id': fileID,
                                'src': (function (target) {
                                    try {
                                        return target.result;
                                    } catch (ex) {
                                        return null;
                                    }
                                }) (evt.target)
                            })
                        };

                    // Render thumbnail template with the file info (data object).
                    $(display)[cfg.newOnTop ? 'prepend' : 'append'](join([
                        '<div data-dropbox-file-id="', fileID ,'" class="' + cfg.theme.fileDisplay + '">',
                            tmpl(cfg.template, data),
                        '</div>'
                    ]));

                    // Store the file in this instance's file manager
                    manager.push(fileID, data.file);

                    if (cfg.immediateUpload) {
                        me.upload(fileID);
                    }

                };
            };

            var onerrorfn = function (/*aFile*/) {
                return function (evt) {

                    var error = evt.target.error,
                        data = {
                            'theme': cfg.theme,
                            'error': {
                                info: error.code || error.name
                            }
                        };

                    // Render thumbnail template with the file info (data object).
                    $(cfg.display)[cfg.newOnTop ? 'prepend' : 'append'](tmpl(cfg.templateError, data));

                };
            };

            // Loop through list of files user dropped.
            /* jshint -W084 */
            for (var i = 0, file; file = files[i]; i++) {

                // TODO verify file is not repeated (by name?)

                // TODO implement selective file type accept/reject
                /* For example for only processing images files */
                /*
                 var imageType = /image.* /; <-- REMOVE THE SPACE BETWEEN * & /
                 if (!file.type.match(imageType)) {
                 continue;
                 }
                 */

                var reader = new FileReader ();

                reader.onerror = onerrorfn(file);

                // Create a closure to capture the file information.
                reader.onload = onloadfn(file);

                // Read in the image file as a data url.
                reader.readAsDataURL(file);
            }

            return false;
        },

        fire: function (event, data) {
            var me = this,
                subs = this.subscribers[event];
            if (subs) {
                for (var i = 0, l = subs.length; i < l; i++) {
                    if (typeof data === 'object') {
                        // Is 'data' an array?
                        if ('concat' in data) {
                            subs[i].apply(me, data);
                        } else {
                            subs[i].call(data.context || me, data);
                        }
                    } else {
                        subs[i].call(me, data);
                    }
                }
            }
        }
    };

    DP.init = function (oCfg) {

        // Verify that the browser has the necessary
        // features for the Dropbox to work
        if ((function () {
            return join([
                typeof window.FileReader !== 'undefined',
                'draggable' in document.createElement('span'),
                !!window.FormData,
                'upload' in new XMLHttpRequest()
            ], ' ').indexOf('false') !== -1;
        }) ()) {
            // TODO fallback to input[type="file"]?
            // TODO use modal instead of an alert?
            alert('Your browser does not support the necessary features to use drag and drop file uploading');
        }

        var me = this,
            settings = this.oCfg,
            elem;

        elem = getElem (oCfg);

        if (!elem) {
            throw ('Dropbox: Unable to select dropbox element');
        }

        this.element = elem;

        // Default the file display to the same dropbox element
        // Override the default configuration settings with the provided settings
        if (typeof oCfg === 'string') {
            settings.element = oCfg;
            settings.display = oCfg;
        } else {
            settings.display = oCfg instanceof HTMLElement ? oCfg : oCfg.element;
            // Extend the default configuration settings with the provided instance settings
            extend (true, settings, oCfg);
        }

        this.display = getElem (settings.display);

        if (!this.display)  {
            throw ('Dropbox: Unable to select display element');
        }

        var eventHandlerFn = function (e) {
            e.stopPropagation();
            e.preventDefault();
            var fn = _[e.type];
            fn && fn.call(me, e);
        };

        // Setup drag and drop handlers.
        elem.addEventListener('dragenter', eventHandlerFn, false);
        elem.addEventListener('dragover', eventHandlerFn, false);
        elem.addEventListener('dragleave', eventHandlerFn, false);
        elem.addEventListener('drop', eventHandlerFn, false);

        // Add theme class
        _.addClass.call(this, settings.theme.main);

    };

    DP.upload = function (fileID) {

        /* References
         * @see http://www.w3.org/TR/FileAPI/
         * @see http://www.w3.org/TR/progress-events/
         * @see http://hacks.mozilla.org/2009/12/uploading-files-with-xmlhttprequest/
         * */

        var me = this,
            cfg = me.oCfg,
            display = this.display,
            file = this.manager.get(fileID);

        var xhr = new XMLHttpRequest(),
            fileUI = display.querySelector('[data-dropbox-file-id="' + file.id + '"]'),
            elemProgress = fileUI.querySelector(cfg.progress);

        var error = function () {
            var response = xhr.responseText;
            fileUI.innerHTML = tmpl(cfg.templateUploadError, {
                file: file,
                theme: cfg.theme,
                statusCode: xhr.status,
                /* jshint -W014 */
                /* jshint -W061 */
                responseText: response !== '' && isJSON(response)
                    ? eval('(' + xhr.responseText + ')')
                    : xhr.responseText
            });
        };

        xhr.upload.addEventListener('progress', function (e) {
            if (e.lengthComputable) {
                me.showUploadProgress(
                    elemProgress,
                    Math.round((e.loaded * 100) / e.total),
                    fileUI);
            }
        }, false);

        xhr.addEventListener('load', function (e) {
            var success = (xhr.status === 200);
            if (success) {
                me.manager.setCompleted(file.id);
                me.showUploadProgress( elemProgress, 100, fileUI );
            } else {
                error();
            }
            // fire the respective event
            _.fire.call(me, ((success) ? SUCCESS : ERROR),
                { ui: fileUI, file: file, e: e });
        });

        xhr.addEventListener('error', function (/*e*/) {
            error();
            _.fire.call(me, ERROR, { ui: fileUI, file: file });
        }, false);

        // TODO:
        // what to do about dropped files with error? remove them
        // from manager or keep them and allow some form of retry

        var fd = new FormData(),
            auxFormData = cfg.formData;

        fd.append(cfg.uploadPostKey, file);
        if (auxFormData) {
            for (var key in auxFormData) {
                fd.append(key, auxFormData[key]);
            }
        }

        xhr.open('POST', cfg.target);
        xhr.send(fd);

    };

    /**
     * Adds parameters to the form data or overrides the value
     * if the parameter name preexisted on the form data
     * @param key
     * @param value
     */
    DP.addFormData = function (key, value) {
        if (!this.oCfg.formData) {
            this.oCfg.formData = { };
        }
        this.oCfg.formData[key] = value;
    };

    /**
     * Removes a key/value from the form data parameters
     * @param key the FormData key to remove
     * @return {*}
     */
    DP.removeFormData = function (key) {
        var fd = this.oCfg.formData,
            value;
        if (fd) {
            value = fd[key];
            delete fd[key];
        }
        return value;
    };

    /**
     * TODO this function could be overwritten through inheritance
     * TODO need inheritace mechanism [use Ember's?]
     */
    DP.showUploadProgress = function (elem, progress/*, fileUI*/) {
        // TODO need some way to cache the progress element, this may impact performance
        // Alternative: specify the progress element selector throught the settings/config
        // object and receive the cached progress element instead of the whole file display UI
        // (or both)
        // elem.innerHTML = progress;
        $(elem).find('.progress-bar').width(progress + '%');
        // $('.progress-bar', elem)
    };

    /**
     * Sets the accepted file types
     * @param fileTypes {Array} List of strings of file types e.g. ['image/gif', 'application/pdf']
     */
    DP.accept = function (/*fileTypes*/) {

    };

    /**
     * Sets the accepted file types through rejecting a set of types
     * @param fileTypes {Array} List of strings of file types e.g. ['image/gif', 'application/pdf']
     */
    DP.reject = function (/*fileTypes*/) {

    };

    DP.clear = function () {

    };

    DP.set = function (property, value) {
        // TODO set nested properties (objects inside the settings object)
        this.oCfg[property] = value;
    };

    DP.get = function (property) {
        return this.oCfg[property];
    };

    DP.on = function (eventName, fn) {
        var subs = this.subscribers,
            event = subs[eventName] || (subs[eventName] = []);
        event.push(fn);
    };

    Dropbox.UPLOAD_SUCCESS_EVENT = SUCCESS;
    Dropbox.UPLOAD_ERROR_EVENT = ERROR;

    S.define('component.Dropbox', Dropbox);

}) (crafter.social);

/*
 'image/gif' // .gif
 'application/pdf' // .pdf
 'application/x-javascript' // .js
 'text/css' // .css
 'application/zip' // .zip
 'application/x-gzip' // .tar.gz
 'text/plain' // .txt
 'text/html' // .html
 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' // .docx
 'application/msword' // .doc
 'application/x-diskcopy' // .dmg
 */