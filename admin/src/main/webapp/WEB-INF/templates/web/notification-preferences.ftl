<form class="form-inline" role="form">
    <div class="form-group">
        <label for="context">Context: </label>
        <select name="context" class="form-control" ng-model="selectedContext"
                ng-options="context.contextName for context in contexts"
                ng-change="resetContextPreferences()">
        </select>
    </div>
</form>
<h2>Notification Email templates</h2>
<form class="form-horizontal" role="form">
    <div class="row">
        <div class="col-lg-4">
            <div class="form-group">
                <label for="selectedType">Context: </label>
                <select name="selectedType" class="form-control" ng-model="selectedType"
                        ng-options="type.name for type in emailTypes">
                </select>
            </div>
        </div>
        <div class="col-lg-6">
            <div class="form-group">
                <button type="button" id="btnSaveNotificationTemplate" ng-click="saveTemplate()" class="btn btn-primary"
                        >Save
                </button>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-lg-12">
            <div class="form-group">
                <label for="emailTemplate">Notification Email template</label>
                <textarea class="ng-ckeditor" ckeditor="editorOptions" ng-model="emailTemplate"></textarea>
            </div>
        </div>
    </div>
    </div>
</form>

<hr/>
<h1 class="page-header">Notification Email Preferences</h1>
<form class="form-horizontal" role="form">
    <div class="form-group">
        <label for="host" class="col-sm-2 control-label">Email Server Host</label>

        <div class="col-sm-10">
            <input type="text" class="form-control" id="host" ng-model="emailConfig.host" name="host">
        </div>
    </div>

    <div class="form-group">
        <label for="port" class="col-sm-2 control-label">Email <Server></Server> Port</label>

        <div class="col-sm-10">
            <input type="text" class="form-control" id="port" ng-model="emailConfig.port" name="port">
        </div>
    </div>

    <div class="form-group">
        <label for="auth" class="col-sm-2 control-label"> Use Authentication</label>

        <div class="col-sm-10">
            <input type="checkbox" class="form-control" id="auth" ng-model="emailConfig.auth" name="auth">
        </div>
    </div>

    <div class="form-group">
        <label for="username" class="col-sm-2 control-label">Username</label>

        <div class="col-sm-10">
            <input type="text" class="form-control" id="username" ng-model="emailConfig.username" name="username">
        </div>
    </div>
    <div class="form-group">
        <label for="password" class="col-sm-2 control-label">Password</label>

        <div class="col-sm-10">
            <input type="password" class="form-control" id="password" ng-model="emailConfig.password" name="password">
        </div>
    </div>

    <div class="form-group">
        <label for="tls" class="col-sm-2 control-label"> Use TLS</label>

        <div class="col-sm-10">
            <input type="checkbox" class="form-control" id="tls" ng-model="emailConfig.tls" name="tls">
        </div>
    </div>

    <div class="form-group">
        <label for="replyTo" class="col-sm-2 control-label">Reply To</label>

        <div class="col-sm-10">
            <input type="email" class="form-control" id="replyTo"  ng-model="emailConfig.replyTo" name="replyTo">
        </div>
    </div>

    <div class="form-group">
        <label for="from" class="col-sm-2 control-label">From</label>

        <div class="col-sm-10">
            <input type="email" class="form-control" id="from" ng-model="emailConfig.from" name="from">
        </div>
    </div>

    <div class="form-group">
        <label for="priority" class="col-sm-2 control-label">Email Priority</label>

        <div class="col-sm-10">
            <input type="text" class="form-control" id="priority" ng-model="emailConfig.priority" name="priority">
        </div>
    </div>

    <div class="form-group">
        <label for="subject" class="col-sm-2 control-label">Subject</label>

        <div class="col-sm-10">
            <input type="text" class="form-control" id="subject" ng-model="emailConfig.subject" name="subject">
        </div>
    </div>

    <div class="form-group">
        <label for="encoding" class="col-sm-2 control-label">Encoding</label>

        <div class="col-sm-10">
            <input type="text" class="form-control" id="encoding" ng-model="emailConfig.encoding" name="encoding">
        </div>
    </div>

    <div class="form-group pull-right">
        <button type="button" id="btnSavePreferences" class="btn btn-primary" ng-click="saveEmailConfig()">Save
            Preferences</button>
    </div>
</form>
