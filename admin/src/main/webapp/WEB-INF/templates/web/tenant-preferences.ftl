<form class="form-inline" role="form">
    <div class="form-group">
        <label for="context">Context: </label>
        <select name="context" class="form-control" ng-model="selectedContext"
                ng-options="context.contextName for context in contexts"
                ng-change="resetContextPreferences()"
        >
        </select>
    </div>
</form>
<h2>Tenant Preferences</h2>
<div class="row">
    <div class="col-lg-8">
        <button class="btn btn-primary pull-left" ng-click="savePreferences()"><span class="fa fa-save">&nbsp;
            Save</span></button>
        <button class="btn btn-default pull-right" ng-click="showNewPreferenceModal()"><span class="fa fa-plus">&nbsp;
            Add</span></button>
    </div>
    <hr/>
    <div class="col-lg-10" ng-repeat="(key, value) in preferences track by $index" id="tenatnPrefSection">
        <hr/>
        <form class="form-horizontal" accept-charset="utf-8" role="form" >
            <div class="form-group" >
                <div class="col-sm-4">
                    <input type="text"
                           id="{{key}}" class="form-control" value="{{key}}" readonly>
                </div>
                <div class="col-sm-4">
                    <input type="text" class="form-control" ng-model='preferences[key]' id="{{key}}Value"
                           placeholder="">
                </div>
                <div class="col-sm-4">
                    <button class="btn btn-danger" ng-click="removeKey(key)"><span class="fa fa-minus">&nbsp;
                        Remove</span></button>
                </div>
            </div>
        </form>
    </div>
</div>

<div id="addNewModal" class="modal fade" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Add new Property</h4>
            </div>
            <div class="modal-body text-center">
                <form class="form-inline">
                    <div class="form-group">
                        <label for="newPname">Name</label>
                        <input type="text" class="form-control" id="newPname" ng-model="newPkey">
                    </div>
                    <div class="form-group">
                        <label for="newPValue">Value</label>
                        <input type="text" class="form-control" id="newPValue" ng-model="newPval">
                    </div>
                </form>
                <br/>
                <span class="label label-warning pull-right">Don't forget to save your new properties</span>
            </div>
            <div class="modal-footer">
                <button class="btn btn-success" ng-click="addPreferenceField()"><span class="fa fa-plus">&nbsp;
            Add and close</span></button>
                <button type="button" class="btn btn-danger" data-dismiss="modal">
                    <span class="fa fa-close"> Cancel</span></button>
            </div>
        </div>
    </div>
</div>
