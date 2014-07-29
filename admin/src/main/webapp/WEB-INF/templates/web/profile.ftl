<#import "spring.ftl" as spring/>

<h1 class="page-header">Profile</h1>

<div class="row">
    <div class="col-sm-4 col-avatar-large">
        <img ng-if="profile.attributes.avatarLink" class="avatar-img avatar-img-large"
             ng-src="{{profile.attributes.avatarLink}}"/>
        <img ng-if="!profile.attributes.avatarLink" class="avatar-img avatar-img-large"
             src="<@spring.url '/resources/image/profile.jpg'/>"/>
    </div>
    <div class="col-sm-8">
        <h3 class="profile-header">{{profile.username}}</h3>
        <div ng-if="profile.attributes.firstName" class="block">
            <strong>First Name:</strong> {{profile.attributes.firstName}}
        </div>
        <div ng-if="profile.attributes.lastName"  class="block">
            <strong>Last Name:</strong> {{profile.attributes.lastName}}
        </div>
        <div ng-if="profile.attributes.displayName"  class="block">
            <strong>Display Name:</strong> {{profile.attributes.displayName}}
        </div>
        <div class="block">
            <strong>Email:</strong> {{profile.email}}
        </div>
        <div class="block">
            <strong>Tenant:</strong> {{profile.tenant}}
        </div>
        <div class="block">
            <strong>Roles:</strong> {{profile.roles.join(', ')}}
        </div>
        <div class="panel panel-default">
            <div class="panel-heading">
                <span class="form-panel-title">Associated Contexts</span>
            </div>
            <div class="panel-body">
                <form class="form-inline">
                    <div class="form-group">
                        <label>Context:</label>
                        <select class="form-control" ng-model="selectedContext"
                                ng-options="context.contextName for context in contexts">
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Roles:</label>
                        <input style="width: 300px" class="form-control" ng-model="contextRoles" ng-list/>
                    </div>
                    <div class="form-group">
                        <button class="btn btn-default" type="button" ng-click="addProfileToContext()">Add</button>
                    </div>
                </form>
            </div>

            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Context</th>
                        <th>Roles</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <tr ng-repeat="context in profile.attributes.socialContexts">
                        <td style="vertical-align: middle">{{context.name}}</td>
                        <td style="vertical-align: middle">{{context.roles.join(', ')}}</td>
                        <td><button class="btn btn-default" type="button"
                                    ng-click="removeProfileFromContext(context.id)">Delete</button></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>