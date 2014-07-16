<h1 class="page-header">Security Actions</h1>

<form class="form-inline" role="form">
    <div class="form-group">
        <label for="context">Context: </label>
        <select name="context" class="form-control" ng-model="selectedContext"
                ng-options="context.contextName for context in contexts"
                ng-change="getActions()">
        </select>
    </div>
</form>

<div class="table-responsive" style="margin-top: 20px;">
    <form>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Action Name</th>
                <th>Roles</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr ng-repeat="action in actions | orderBy:actionsOrderedBy">
                <td>
                    {{action.actionName}}
                </td>
                <td>
                    <div class="form-group">
                        <input class="form-control" ng-model="action.roles"
                               ng-disabled="action.actionName.indexOf('system.') == 0" ng-list/>
                    </div>
                </td>
                <td>
                    <div class="form-group">
                        <button type="button" class="btn btn-default"
                                ng-click="updateAction(action)"
                                ng-if="action.actionName.indexOf('system.') != 0">Update</button>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
    </form>
</div>