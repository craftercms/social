<h1 class="page-header">Context Preferences</h1>

<form class="form-inline" role="form">
    <div class="form-group">
        <label for="context">Context: </label>
        <select name="context" class="form-control" ng-model="selectedContext"
                ng-options="context.contextName for context in contexts">
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