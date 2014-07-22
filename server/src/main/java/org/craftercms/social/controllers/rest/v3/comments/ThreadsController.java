package org.craftercms.social.controllers.rest.v3.comments;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.security.SocialSecurityUtils;
import org.craftercms.social.services.ugc.UGCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Controller
@RequestMapping("/api/3/threads")
public class ThreadsController {

    public static final String MAX_INT = "666";
    @Autowired
    private UGCService ugcService;



    @RequestMapping(value = "{id}/comments", method = RequestMethod.GET)
    @ApiOperation(value = "Gets all the comments for the given thread", notes = "The pageNumber and page size will " +
        "only work for top level comments. to restrict the amount of children for level for comment use childrenCount" +
        " Sort will apply for all levels")
    @ResponseBody
    public Thread thread(@ApiParam(value = "Id of the thread") @PathVariable final String id,
                         @ApiParam(value = "Levels of comments to return") @RequestParam(required = false,
        defaultValue = MAX_INT) final int recursive, @ApiParam("Page number to return") @RequestParam(required =
        false, defaultValue = "0") final int pageNumber, @ApiParam("Comments per Page") @RequestParam(required = false,
        defaultValue = MAX_INT) final int pageSize, @ApiParam(value = "Amount of Children to return") @RequestParam
        (required = false, defaultValue = MAX_INT) final int childrenCount, @ApiParam("List of fields to order by")
    @RequestParam(required = false) final List<String> sortBy, @ApiParam("Sort Order") @RequestParam(required =
        false) final List<SocialSortOrder> sortOrder) throws SocialException {
        int start = 0;
        if (pageNumber > 0 && pageSize > 0) {
            start = getStart(pageNumber, pageSize);
        }
        Thread thread = new Thread();
        final int upToLevel;
        if (recursive < 0) {
            upToLevel = Integer.MAX_VALUE;
        } else {
            upToLevel = recursive;
        }
        thread.setComments(ugcService.read(id, SocialSecurityUtils.getContext(), start, pageSize,
            getSortOrder(sortBy, sortOrder), upToLevel, childrenCount));
        thread.setPageNumber(pageNumber);
        thread.setPageSize(pageSize);
        thread.setTotal(ugcService.count(id, SocialSecurityUtils.getContext()));
        return thread;
    }

    @RequestMapping(value = "{id}/comments/{commentId}/children", method = RequestMethod.GET)
    @ApiOperation(value = "Gets all the comments for the given thread", notes = "The pageNumber and page size will " +
        "only work for top level comments. to restrict the amount of children for level for comment use childrenCount" +
        " Sort will apply for all levels")
    @ResponseBody
    public Thread comments(@ApiParam(value = "Id of the thread") @PathVariable final String id,
                           @ApiParam(value = "Id of the Comment") @PathVariable final String commentId,
                           @ApiParam(value = "Levels of comments to return") @RequestParam(required = false,
        defaultValue = MAX_INT) final int recursive, @ApiParam("Page number to return") @RequestParam(required =
        false, defaultValue = "0") final int pageNumber, @ApiParam("Comments per Page") @RequestParam(required = false,
        defaultValue = MAX_INT) final int pageSize, @ApiParam(value = "Amount of Children to return") @RequestParam
        (required = false, defaultValue = MAX_INT) final int childrenCount, @ApiParam("List of fields to order by")
    @RequestParam(required = false) final List<String> sortBy, @ApiParam("Sort Order") @RequestParam(required =
        false) final List<SocialSortOrder> sortOrder) throws SocialException {
        int start = 0;
        if (pageNumber > 0 && pageSize > 0) {
            start = getStart(pageNumber, pageSize);
        }
        Thread thread = new Thread();
        final int upToLevel;
        if (recursive < 0) {
            upToLevel = Integer.MAX_VALUE;
        } else {
            upToLevel = recursive;
        }
        thread.setComments(ugcService.readChildren(commentId, id, SocialSecurityUtils.getContext(), start, pageSize,
            getSortOrder(sortBy, sortOrder), upToLevel, childrenCount));
        thread.setPageNumber(pageNumber);
        thread.setPageSize(pageSize);
        thread.setTotal(ugcService.countChildren(commentId,SocialSecurityUtils.getContext()));
        return thread;
    }

    public static List<DefaultKeyValue<String, Boolean>> getSortOrder(final List<String> sortFields,
                                                                final List<SocialSortOrder> sortOrder) {
        if (CollectionUtils.isEmpty(sortFields)) {
            return null;
        }
        List<DefaultKeyValue<String, Boolean>> toReturn = new ArrayList<>();
        for (int i = 0; i < sortFields.size(); i++) {
            DefaultKeyValue<String, Boolean> mapSort;
            if (CollectionUtils.isEmpty(sortOrder) || i >= sortOrder.size()) {
                mapSort = new DefaultKeyValue(sortFields.get(i), SocialSortOrder.DESC.value());
            } else {
                mapSort = new DefaultKeyValue(sortFields.get(i), sortOrder.get(i).value());
            }
            toReturn.add(mapSort);
        }
        return toReturn;
    }

    public static int getStart(int page, int pageSize) {
        if (page <= 0) {
            return 0;
        }
        return (page - 1) * pageSize;
    }

    //    if (page > 0 && pageSize > 0) {
    //        int start = getStart(page, pageSize);
    //        int end = pageSize;
    //        q.skip(start);
    //        q.limit(end);
    //
    //    }
}
