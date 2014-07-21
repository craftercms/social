<#import "spring.ftl" as spring/>

<h1 class="page-header">Moderation Dashboard</h1>

<form class="form-inline" role="form">
    <div class="form-group">
        <label for="context">Context: </label>
        <select name="context" class="form-control" ng-model="selectedContext"
                ng-options="context.contextName for context in contexts"
                ng-change="resetStatusAndGetComments()">
        </select>
    </div>
    <div class="form-group">
        <label for="status">Status: </label>
        <select name="status" class="form-control" ng-model="selectedStatus"
                ng-options="status.value as status.label for status in moderationStatus"
                ng-change="getComments()">
        </select>
    </div>
    <div class="form-group pull-right">
        <pagination total-items="totalItems" items-per-page="itemsPerPage" class="no-margin" ng-model="currentPage"
                    ng-change="getCurrentPage()">
        </pagination>
    </div>
</form>

<div class="comments">
    <div class="row comment" ng-repeat="comment in comments">
        <div class="col-sm-2 col-avatar-small">
            <img ng-if="comment.user.attributes.avatarLink" class="avatar-img avatar-img-small"
                 ng-src="{{comment.user.attributes.avatarLink}}"/>
            <img ng-if="!comment.user.attributes.avatarLink" class="avatar-img avatar-img-small"
                 src="<@spring.url '/resources/image/profile.jpg'/>"/>
            <p>{{comment.user.username | truncateIfTooLarge}}<p>
        </div>
        <div class="form-group col-sm-8">
            <h4 class="comment-header"><span class="text-muted">Thread:</span> {{comment.targetId}}</h4>
            <textarea class="form-control comment-body" ng-model="comment.body"></textarea>
            <div class="comment-date">{{comment.lastModifiedDate | date:'MM/dd/yyyy @ h:mm a'}}</div>
            <div class="comment-action-btns">
                <button type="button" class="btn btn-primary"
                        ng-repeat="action in moderationStatusActions[selectedStatus]"
                        ng-click="executeAction(action, comment)">
                    {{action.label}}
                </button>
            </div>
        </div>
    </div>
</div>