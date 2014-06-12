<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!doctype html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
    <head>

        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>ui</title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width">
        <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

        <link rel="stylesheet" href="static-assets/styles/86dd6fa4.index.css">

        <script>
            var crafterSocial_cfg = {
                'url.base': '/crafter-comments-ui-plugin/',
                'url.service': '/crafter-comments/api/2/',
                'url.templates': 'static-assets/templates/'
                // ...
            }
            function crafterSocial_onAppReady ( Director, CrafterSocial ) {
                console.log('Crafter Social is Ready!');

                Director.socialise({
                    target: '#jumbotron',
                    tenant: 'craftercms'
                });

            }
        </script>

    </head>
    <body>
        <!--[if lt IE 10]>
            <p class="browsehappy">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
        <![endif]-->


        <div class="container">
            <div class="header">
                <ul class="nav nav-pills pull-right">
                    <li class="active"><a href="#">Home</a></li>
                    <li><a href="#">About</a></li>
                    <li><a href="#">Contact</a></li>
                </ul>
                <h3 class="text-muted">ui</h3>
            </div>

            <div class="jumbotron" id="jumbotron">
                <h1><i class="glyphicon glyphicon-info-sign"></i> 'Allo, 'Allo!</h1>
                <p class="lead">Always a pleasure scaffolding your apps.</p>
                <p><a class="btn btn-lg btn-success" href="#">Splendid!</a></p>
            </div>

            <div class="row marketing">
                <div class="col-lg-6">
                    <h4>HTML5 Boilerplate</h4>
                    <p>HTML5 Boilerplate is a professional front-end template for building fast, robust, and adaptable web apps or sites.</p>

                    <h4>Bootstrap</h4>
                    <p>Sleek, intuitive, and powerful mobile first front-end framework for faster and easier web development.</p>

                    <h4>Modernizr</h4>
                    <p>Modernizr is an open-source JavaScript library that helps you build the next generation of HTML5 and CSS3-powered websites.</p>


                    <h4>RequireJS</h4>
                    <p>RequireJS is a JavaScript file and module loader. It is optimized for in-browser use, but it can be used in other JavaScript environments, like Rhino and Node.</p>

                </div>
            </div>

            <div class="footer">
                <p>♥ from the Yeoman team</p>
            </div>

        </div>

        <!-- bower:js -->
        <script src="static-assets/libs/modernizr/modernizr.js"></script>
        <!-- endbower -->

        <link rel="stylesheet" href="static-assets/styles/fd62ff72.main.css">

        <script src="static-assets/scripts/social.js"></script>

</body>
</html>