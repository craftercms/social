<h1 class="page-header">Search Profiles</h1>

<form class="form-inline" role="form">
    <div class="form-group">
        <label for="tenant">Tenant:</label>
        <select name="tenant" class="form-control"
                ng-model="selectedTenantName" ng-options="tenantName for tenantName in tenantNames">
        </select>
    </div>
    <div class="form-group">
        <input type="text" class="form-control search-box" placeholder="Search by username" ng-model="searchText"/>
        <button class="btn btn-default" type="button" ng-disabled="searchText == null || searchText == ''"
                ng-click="doSearch()">Search</button>
    </div>
    <div class="form-group pull-right">
        <pagination ng-if="profiles" total-items="totalItems" items-per-page="itemsPerPage" class="no-margin"
                    ng-model="currentPage" ng-change="getCurrentPage()">
        </pagination>
    </div>
</form>

<div class="table-responsive" style="margin-top: 20px;">
    <table class="table table-striped">
        <thead>
            <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Contexts</th>
            </tr>
        </thead>
        <tbody>
            <tr ng-repeat="profile in profiles">
                <td>
                    <a href="#/profile/{{profile.id}}">{{profile.id}}</a>
                </td>
                <td>
                    {{profile.username}}
                </td>
                <td>
                    {{profile.email}}
                </td>
                <td>
                    {{getSocialContextNames(profile).join(', ')}}
                </td>
            </tr>
        </tbody>
    </table>
</div>