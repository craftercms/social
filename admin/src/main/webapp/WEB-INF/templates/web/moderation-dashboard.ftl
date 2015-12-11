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
                 ng-src="{{profileAvatar(comment.user.id)}}"/>

            <p>{{comment.user.username | truncateIfTooLarge}}<p>
        </div>
        <div class="form-group col-sm-8">
            <div class="row">
                <div class="col-sm-8">

                    <h4 class="comment-header"><span class="text-muted">Thread:</span>
                      <span ng-if="!comment.attributes.commentUrl"> {{comment.targetId}} </span>
                      <a ng-if="comment.attributes.commentUrl" href="{{preferences.baseUrl}}{{comment.attributes
                      .commentUrl}}" target="_blank">
                          <span ng-if="!comment.attributes.commentThreadName"> {{comment.targetId}} </span>
                          <span ng-if="comment.attributes.commentThreadName"> {{comment.attributes.commentThreadName}} </span>
                      </a>
                    </h4>
                </div>
                <div class="col-sm-4"  ng-if="comment.flags.length>0">
                    <span class="label label-warning pull-right mouse-pointer" ng-click="showFlagsModal(comment)">
                       View {{comment.flags.length}} Flags
                    </span>
                </div>
            </div>
            <textarea ckeditor="editorOptions" class="form-control comment-body" ng-model="comment.body"></textarea>
            <div class="comment-footer">
                {{comment.lastModifiedDate | date:'MM/dd/yyyy @ h:mm a'}}
                <span ng-if="comment.attachments.length != 0">
                    / <a ng-click="showAttachmentsModal(comment)">See attachments</a>
                </span>
            </div>
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

<div id="flagModal" class="modal fade" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Commnent Flags</h4>
            </div>
            <div class="modal-body text-center">
                <div ng-repeat="flag in selectedComment.flags">
                    {{flag.reason}}
                    <hr/>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<div id="attachmentsModal" class="modal fade" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Attachments</h4>
            </div>
            <div class="modal-body">
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                        <tr>
                            <th>File Name</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr ng-repeat="attachment in selectedComment.attachments">
                            <td>
                                <a href="{{getAttachmentUrl(attachment)}}" target="_blank">
                                    {{attachment.fileName.substring(attachment.fileName.lastIndexOf('/') + 1)}}
                                </a>
                            </td>
                            <td>
                                <a ng-click="deleteAttachment(attachment)">Delete</a>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

