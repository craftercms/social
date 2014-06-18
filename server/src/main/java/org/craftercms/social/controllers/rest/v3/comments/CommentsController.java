package org.craftercms.social.controllers.rest.v3.comments;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import java.io.IOException;
import java.util.Map;



import org.apache.commons.lang3.StringUtils;
import org.craftercms.social.domain.social.Flag;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.IllegalUgcException;
import org.craftercms.social.exceptions.SocialException;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 */
@Controller
public class CommentsController<T extends SocialUgc> extends AbstractCommentsController {
    private Logger log = LoggerFactory.getLogger(CommentsController.class);

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Creates a new comment",consumes = MimeTypeUtils.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public T create(@ApiParam(value = "Body of the Comment,Some Html/scripts tags will be strip") @RequestParam() final String body, @ApiParam(name = "thread",
        value = "Id of the thread to attach this comment") @RequestParam(required = true) final String thread,
                    @ApiParam(value = "Id of the parent for the new comment", name = "parentId") @RequestParam
                        (required = false, defaultValue = "") final String parent,
                    @ApiParam(value = "Json String representing any extra attributes of the comment to create",
        name = "attributes") @RequestParam(required = false,
        defaultValue = "{}") final String attributes, MultipartFile attachment) throws SocialException,
        MissingServletRequestParameterException, IOException {
        Map<String, Object> attributesMap = null;
        if (!StringUtils.isBlank(attributes)) {
            attributesMap = parseAttributes(attributes);
        }
        T newUgc = (T)ugcService.create(tenant(), parent, thread, body,  "", attributesMap);

        if(attachment !=null){
            ugcService.addAttachment(newUgc.getId().toString(),tenant(), attachment.getInputStream(),
                attachment.getOriginalFilename(),getContentType(attachment.getOriginalFilename()));
        }
        return newUgc;
    }

    @RequestMapping(value = "{id}",method = RequestMethod.PUT)
    @ApiOperation(value = "Updates the given comment",notes = "As Create some HTML/scripts tags will be scripted")
    @ResponseBody
    public T update(@ApiParam(value = "Ugc id to update") @PathVariable("id")final String id,
                    @ApiParam(value = "New comment Body") @RequestParam() final String body,
                    @ApiParam(value = "Json String representing any extra attributes of the comment to create",
                    name = "attributes") @RequestParam(required = false,
    defaultValue = "{}") final String attributes ) throws SocialException,MissingServletRequestParameterException{
        Map<String, Object> attributesMap = null;
        if (!StringUtils.isBlank(attributes)) {
            attributesMap = parseAttributes(attributes);
        }
        return (T)ugcService.update(id, body,"",userId(),tenant(),attributesMap);
    }

    @RequestMapping(value = "{id}",method = RequestMethod.DELETE)
    @ApiOperation(value = "Deletes the comment",notes = "As Create some HTML/scripts tags will be scripted," +
        "Also All children will be deleted")
    @ResponseBody
    public boolean delete(@ApiParam(value = "Comment id to update") @PathVariable("id")final String id ) throws
        SocialException{
       ugcService.deleteUgc(id,tenant());
        return true;
    }

    @RequestMapping(value = "{id}",method = RequestMethod.GET)
    @ApiOperation(value = "Gets a the comment")
    @ResponseBody
    public T read(@ApiParam(value = "Comment id to update") @PathVariable("id") final String id ) throws
        SocialException{
      return (T)ugcService.read(id,tenant());
    }


    @RequestMapping(value = "{id}/attributes", method ={RequestMethod.POST,RequestMethod.PUT})
        @ResponseBody()
        @ApiOperation(value = "Adds or updates the given attributes with there new value " + "if attribute does not " +
            "exists it will be created Json is expected to by the POST body",
            notes = "This operation expects any " +
                "type of valid JSON" +
                " " +
                "object Do notice that there is a current limitation and all attributes will be converted into a 'String" +
                " there for its all non array-maps. this is valid for numbers,booleans and dates. keep this in mind where" +
                " " +
                "doing the search")
        public boolean addAttributes(@ApiParam(value = "Id of the UGC") @NotBlank @PathVariable(value = "id") final
                                         String id, @ApiParam(value = "Json of the attributes to be updated or created" +
            ". All values are " + "save as string (booleans,numbers,dates)") @RequestBody
        final Map<String, Object> attributes) throws SocialException {
            log.debug("Request for deleting form  UGC {} attributes {}", id, attributes);
            String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
            ugcService.setAttributes(id, tenant, attributes);
            return true;//Always true unless exception.
        }


    @RequestMapping(value = "{id}/attributes", method = RequestMethod.DELETE)
    @ResponseBody()
    @ApiOperation(value = "Deletes all the attributes from the given UGC", notes = "All attributes must be in dot " +
        "notation where nested values should be in its full path, to remove multiple attributes send them separated " +
        "by a ',' ")
    public boolean removeAttributes(@ApiParam(value = "Id of the comment", name = "id") @NotBlank @PathVariable(value
        = "id") final String id, @ApiParam(name = "attributes", value = "List of , " +
        "separated attributes name to delete. use dot " + "notation do delete nested attributes.") @RequestParam
        (required = true)
    final String attributes) throws SocialException {
        log.debug("Request for deleting form  UGC {} attributes {}", id, attributes);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        ugcService.deleteAttribute(id, attributes.split(","), tenant);
        return true;//Always true unless exception.
    }


    @RequestMapping(value = "{id}/flags",method = RequestMethod.POST)
    @ApiOperation(value = "Flags the UGC",notes = "Reason will be cleanup for any HTML/Script")
    public T flagUgc(@ApiParam(value = "Comment Id") @PathVariable(value = "id") final String id,
                     @ApiParam(value = "Reason why the comment is been flag") @RequestParam final String reason)
        throws SocialException{
        return (T)socialServices.flag(id,tenant(),reason,userId());
    }


    @RequestMapping(value = "{id}/flags",method = RequestMethod.GET)
    @ApiOperation(value = "Flags the UGC",notes = "Reason will be cleanup for any HTML/Script")
    public Iterable<Flag> flagUgc(@ApiParam(value = "Comment Id") @PathVariable(value = "id") final String id)
        throws SocialException{
        T ugc= (T)ugcService.read(id, tenant());
        if (ugc == null) {
            throw new IllegalUgcException("Given UGC does not exist for tenant");
        }
        return ugc.getFlags();
    }

    @RequestMapping(value = "{id}/flags/{flagId}",method = RequestMethod.DELETE)
    @ApiOperation(value = "Flags the UGC",notes = "Reason will be cleanup for any HTML/Script")
    public boolean unflagUgc(@ApiParam(value = "Comment Id") @PathVariable(value = "id") final String id,
                     @ApiParam(value = "Flag id to delete") @PathVariable(value = "flagId") final String flagId)
        throws SocialException{
        return socialServices.unFlag(id,flagId,userId(),tenant());
    }

}
