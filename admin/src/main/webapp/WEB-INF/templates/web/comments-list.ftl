<#import "spring.ftl" as spring/>

<h1 class="page-header">Comments List</h1>

<form class="form-inline" role="form">
    <div class="form-group">
        <label for="tenant">Tenant:</label>
        <select name="tenant" class="form-control">
        </select>
    </div>
    <div class="form-group">
        <label for="status">Status:</label>
        <select name="status" class="form-control"
                ng-model="selectedStatus" ng-options="status.value as status.label for status in moderationStatus"
                ng-change="resetCommentList(selectedStatus)">
        </select>
    </div>
    <div class="form-group pull-right">
        <pagination total-items="totalItems" class="no-margin" ng-model="currentPage" ng-change="pageChanged()">
        </pagination>
    </div>
</form>

<div class="comments">
    <form>
        <div class="row comment" ng-repeat="comment in comments">
            <div class="col-sm-2 col-avatar">
                <img class="avatar-img" src="<@spring.url '/resources/image/profile.jpg'/>"/>
                <p>{{comment.profile.attributes.displayName | truncateIfTooLarge}}<p>
            </div>
            <div class="form-group col-sm-8">
                <textarea class="form-control comment-body" ng-model="comment.body"></textarea>
                <div class="comment-date">{{comment.lastModifiedDate | date:'MM/dd/yyyy @ h:mm a'}}</div>
                <div class="comment-action-btns">
                    <button type="button" class="btn btn-primary"
                            ng-repeat="action in moderationStatusActions[selectedStatus]"
                            ng-click="action.action(comment, commentsService)">{{action.label}}</button>
                </div>
            </div>
        </div>
    </form>
</div>