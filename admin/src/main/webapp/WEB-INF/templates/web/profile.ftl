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
            <strong>Roles:</strong> {{profile.roles.join(', ')}}
        </div>
        <div class="panel panel-default">
            <div class="panel-heading">
                <span class="form-panel-title">Associated Contexts</span>
            </div>
            <div class="panel-body">
                <form class="form-inline" role="form">
                    <div class="form-group">
                        <label for="contextName">Context:</label>
                        <input type="text" style="width: 125px" name="contextName" class="form-control"
                               ng-model="contextName"/>
                    </div>
                    <div class="form-group">
                        <label for="roles">Roles:</label>
                        <input type="text" name="roles" style="width: 300px" class="form-control"
                               ng-model="contextRoles"/>
                    </div>
                    <div class="form-group">
                        <button class="btn btn-default" type="button">Add</button>
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
                    <tr ng-repeat="context in profile.attributes.socialTenants">
                        <td style="vertical-align: middle">{{context.tenant}}</td>
                        <td style="vertical-align: middle">{{context.roles.join(', ')}}</td>
                        <td><button class="btn btn-default" type="button">Delete</button></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>