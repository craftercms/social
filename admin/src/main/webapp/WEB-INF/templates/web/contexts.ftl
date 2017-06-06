<h1 class="page-header">Contexts</h1>
<#if isSuperAdmin>
<form class="form-inline" role="form">
    <div class="form-group">
        <label for="contextName">Context:</label>
        <input type="text" name="contextName" class="form-control" ng-model="contextName"/>
    </div>
    <div class="form-group">
        <button class="btn btn-default" type="button" ng-disabled="contextName == null || contextName == ''"
                ng-click="createContext()">Add</button>
    </div>
</form>
</#if>
<table class="table table-striped form-panel-table">
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
    </tr>
    </thead>
    <tbody>
    <tr ng-repeat="context in contexts">
        <td>
            {{context._id}}
        </td>
        <td>
            {{context.contextName}}
        </td>
    </tr>
    </tbody>
</table>
