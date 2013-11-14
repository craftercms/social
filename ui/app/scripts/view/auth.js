(function (S) {
    'use strict';

    var Auth,
        Base        = S.view.Modal,
        C           = S.Constants,
        Director    = S.getDirector(),
        Profile     = S.model.Profile,
        $           = S.$,
        U           = S.util;

    Auth = Base.extend({

        className: [
            Base.prototype.className,
            'crafter-social-auth-view'
        ].join(' '),

        events: {
            'click #submitBtn_%@': 'submit'
        },

        initialize: function () {
            Base.prototype.initialize.apply(this, arguments);
            (!this.model) && (this.model = new Profile());
        },

        listen: function () {

            Base.prototype.listen.apply(this, arguments);

            var me = this;
            this.$el.on('shown.bs.modal', function () {
                me.$('input:first').select();
            });

            this.listenTo(Director, C.get('EVENT_USER_AUTHENTICATION_SUCCESS'), this.destroy);
            this.listenTo(Director, C.get('EVENT_USER_AUTHENTICATION_FAILED'), this.failure);

        },

        createUI: function () {

            Base.prototype.createUI.apply(this, arguments);

            this.$('.modal-title:first').text('Login');

            this.$('.modal-body:first').html(
                U.template(this.getTemplate('form'), {
                    guid: this.guid,
                    action: S.Cfg('url.security'),
                    method: 'post'
                }));

            this.$('.modal-footer:first').html(
                U.template(this.getTemplate('submit'), this));

        },

        render: function () {

            Base.prototype.render.apply(this, arguments);

            var data = this.model.toJSON();
            this.setFormData(data);

            return this;

        },

        setFormData: function ( data ) {
            this.$('form').find('input, textarea, select').each(function () {
                var $el = $(this);
                $el.val(data[$el.attr('name')]);
            });
        },
        getFormData: function () {
            var data = {};
            this.$('form').find('input, textarea, select').each(function () {
                var $el = $(this);
                data[$el.attr('name')] = $el.val();
            });
            return data;
        },

        submit: function () {
            this.setMessage({
                type: 'info',
                icon: 'info-sign',
                message: 'Loading, please wait&hellip;'
            });
            var data = this.getFormData();
            this.model.set(data);
            this.model.authenticate();
        },

        failure: function () {
            this.setMessage({
                type: 'danger',
                icon: 'warning-sign',
                message: 'Your credentials were not recognized'
            });
            this.$('input:first').select();
        },

        setMessage: function ( cfg ) {

            var $bd = this.$('.modal-body');
            $bd.find('.alert').remove();
            if (cfg) {
                $bd.prepend(U.template(this.getTemplate('message'), cfg));
            }

        }

    });

    Auth.DEFAULTS = $.extend(true, {}, Base.DEFAULTS, {
        templates: {
            form: ('%@auth-form.hbs').fmt(S.Cfg('url.templates')),
            submit: '<button data-indentifyme="submitBtn" class="btn btn-primary">Submit</button>',
            /* jshint -W015 */
            message: [
                '<div class="alert alert-{{type}}">',
                    '<i class="crafter-social-icon cs-icon-{{icon}}"></i> {{html message}}',
                '</div>'
            ].join('')
        }
    });

    S.define('view.Auth', Auth);

}) (crafter.social);