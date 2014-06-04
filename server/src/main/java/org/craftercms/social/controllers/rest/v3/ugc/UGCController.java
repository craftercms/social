package org.craftercms.social.controllers.rest.v3.ugc;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.craftercms.commons.mongo.FileInfo;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.ugc.UGCService;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Rest Services for UGC's
 */
@Controller
@RequestMapping(value = "/api/3/ugc")
@Api(value = "Ugc Service", basePath = "/api/3/ugc", description = "Main UGC Services",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class UGCController<T extends UGC> {

    private static final String MAX_INTEGER_STRING = "2147483647";
    @Autowired
    private UGCService ugcService;
    private Logger log = LoggerFactory.getLogger(UGCController.class);

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Creates a new UGC based on system configuration",
        notes = "Depending on how the system is configure it will return a UGC based structure",
        position = 1, responseContainer = "Any Ugc Based Item.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400,
        message = "If any of the given parameters are not send or empty")})
    public T createUGG(@ApiParam(name = "content", required = true, value = "Text content of the UGC ") @NotBlank
                           @SafeHtml(whitelistType = SafeHtml.WhiteListType.RELAXED)
                       final String content, @ApiParam(value = "Subject of the UGC", name = "subject",
        required = true) @SafeHtml(whitelistType = SafeHtml.WhiteListType.RELAXED)
                       final String subject, @ApiParam(value = "Target Id of the UGC. Its always required but for " +
        "security and structure concerns if parent param is given , It will use the parent UGC targetId",
        name = "target") @NotBlank final String target, @ApiParam(name = "parent",
        value = "Parent Id of the UGC that will be created") @RequestParam(required = false,
        value = "parent") final String parent) throws SocialException {
        log.debug("Request for creating a new UGC");
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        return (T)ugcService.create(tenant, parent, target, content, subject);
    }

    @RequestMapping(value = "{ugcId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Returns all the UGC and all its children (and children of ) in a tree structure empty if "
        + "nothing is found.",
        notes = "This call can be SLOW if the given UGC have a big tree. Is highly recommended to use childrenCount "
            + "as low as possible to avoid slowness in the request (suggested max number 10)")
    public T readUgc(@ApiParam(name = "ugcId", value = " Id of the UGC to find") @NotBlank @PathVariable(value =
        "ugcId")
                     final String ugcId, @ApiParam(name = "includeChildren", value = " Should I include all my " +
        "children, if set false it will only return the UGC with the given Id",
        defaultValue = "true") @RequestParam(required = false,
        defaultValue = "true") final boolean includeChildren, @ApiParam(value = "deep of the Tree to be return (how "
        + "many levels of children should I return", name = "treeDeepLevel",
        defaultValue = MAX_INTEGER_STRING) @RequestParam(required = false,
        defaultValue = MAX_INTEGER_STRING) final int treeDeepLevel) throws SocialException {
        log.debug("Request for get a UGC with id {} with including Children {} and children deep {}", ugcId,
            includeChildren, treeDeepLevel);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        return (T)ugcService.read(ugcId, includeChildren, treeDeepLevel, tenant);
    }

    @ApiOperation(value = "Gets all the children of the given UGC (does not include it self in the response) also ")
    @RequestMapping(value = "{ugcId}/children", method = RequestMethod.GET)
    @ResponseBody
    public Iterable<T> readUgcChildren(@ApiParam(value = "Id of the UGC", name = "ugcId") @NotBlank @PathVariable
        (value = "ugcId")
                                       final String ugcId, @ApiParam(value = "Amount of results to get",
        name = "limit") @RequestParam(required = false,
        defaultValue = MAX_INTEGER_STRING)
                                       final int limit, @ApiParam(name = "skip",
        value = "Where to skip in the result" + ".") @RequestParam(required = false,
        defaultValue = "0") final int skip, @ApiParam(value = "Amount of sub-children to get, " +
        "" + "if set to >=1 it will only return on level (aka direct children)") @RequestParam(required = false,
        defaultValue = "0")
                                       final int childrenLevel) throws SocialException {
        log.debug("Request for get all children of {} with a limit of , and starting in ", ugcId, limit, skip);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        return ugcService.readChildren(tenant, ugcId, limit, skip, childrenLevel);
    }

    @RequestMapping(value = "/target", method = RequestMethod.GET)
    @ResponseBody()
    @ApiOperation(value = "Gets all the UGC (and sub-children) for the given target Id")
    public Iterable<T> readUgcByTargetId(@ApiParam(name = "targetId", value = " Id of the target to get all UGC (and " +
        "" + "its children(builds a tree for each children)") @NotEmpty @RequestParam(required = true,
        value = "targetId") final String targetId) throws SocialException {
        log.debug("Request for getting all UGC by targetID {}", targetId);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        return ugcService.readByTargetId(targetId, tenant);
    }

    @RequestMapping(value = "/{ugcId}/attributes/remove", method = RequestMethod.POST)
    @ResponseBody()
    @ApiOperation(value = "Deletes all the attributes from the given UGC", notes = "All attributes must be in dot " +
        "notation where nested values should be in its full path, to remove multiple attributes send them separated " +
        "by a ',' ")
    public boolean removeAttributes(@ApiParam(value = "Id of the UGC", name = "ugcId") @NotBlank @PathVariable(value
        = "ugcId") final String ugcId, @ApiParam(name = "attributes", value = "List of , " +
        "separated attributes name to delete. use dot " + "notation do delete nested attributes.") @RequestParam
        (required = true)
    final String attributes) throws SocialException {
        log.debug("Request for deleting form  UGC {} attributes {}", ugcId, attributes);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        ugcService.deleteAttribute(ugcId, attributes.split(","), tenant);
        return true;//Always true unless exception.
    }


    @RequestMapping(value = "/{ugcId}/attributes/add", method = RequestMethod.POST)
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
    public boolean addAttributes(@ApiParam(value = "Id of the UGC") @NotBlank @PathVariable(value = "ugcId") final
                                     String ugcId, @ApiParam(value = "Json of the attributes to be updated or created" +
        ". All values are " + "save as string (booleans,numbers,dates)") @RequestBody
    final Map<String, Object> attributes) throws SocialException {
        log.debug("Request for deleting form  UGC {} attributes {}", ugcId, attributes);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        ugcService.setAttributes(ugcId, tenant, attributes);
        return true;//Always true unless exception.
    }

    @RequestMapping(value = "/{ugcId}/delete", method = RequestMethod.POST)
    @ResponseBody()
    @ApiOperation(value = "Deletes the UGC and all it's tree", notes = "Be aware that this will delete the whole ugc " +
        "" + "tree")
    public boolean deleteUgc(@ApiParam(value = "Id of the UGC", name = "ugcId") @NotBlank @PathVariable(value =
        "ugcId") final String ugcId) throws SocialException {
        log.debug("Request for deleting  UGC {}", ugcId);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        ugcService.deleteUgc(ugcId, tenant);
        return true;//Always true unless exception.
    }

    @ApiOperation(value = "Searches Ugc's with the given Search Query and sort ", notes = "For more information about" +
        " " +
        "" + "the Search API see http://wiki.craftercms.org/display/CRAFTER/Search+Api")
    @RequestMapping(value = "/search", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody()
    public Iterable<T> search(@ApiParam(name = "query", value = "Query string to be use") @RequestParam(required =
        true) final String query, @RequestParam(required = false,
        defaultValue = "") @ApiParam(value = "Sort query to be use", name = "sort") final String sort,
                              @RequestParam(required = false,
        defaultValue = MAX_INTEGER_STRING) @ApiParam(value = "Amount of results to return", name = "limit")
                              final int limit, @ApiParam(value = "How many results to skip before limit and returning",
        name = "skip") @RequestParam(required = false,
        defaultValue = "0") final int skip) throws SocialException {
        log.debug("Searching using query {} sort {} limit {} skip {}", query, sort, limit, skip);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        return ugcService.search(tenant, query, sort, skip, limit);

    }

    @RequestMapping(value = "/{ugcId}/attachment/add", method = RequestMethod.POST)
    @ResponseBody()
    @ApiOperation(value = "Adds and attachment to the given UGC")
    public FileInfo addAttachment(@ApiParam(value = "Id of the UGC", name = "ugcId") @NotBlank @PathVariable(value =
        "ugcId") final String ugcId, @ApiParam(value = "File to upload, Do notice that the server will enforce ")
    @RequestParam(required = true) CommonsMultipartFile attachment) throws SocialException, IOException {
        log.debug("Adding Attachment for UGC {} ", ugcId);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        return ugcService.addAttachment(ugcId, tenant, attachment.getInputStream(), attachment.getOriginalFilename(),
            new MimetypesFileTypeMap().getContentType(attachment.getOriginalFilename()));
    }

    @RequestMapping(value = "/{ugcId}/attachment/remove/{attachmentId}", method = RequestMethod.POST)
    @ResponseBody()
    @ApiOperation("Deletes the given attachment for the UGC")
    public boolean removeAttachment(@ApiParam("Id of the UGC")@NotBlank @PathVariable(value = "ugcId") final String ugcId,
                                    @ApiParam("Id of the attachment to delete") @NotBlank @PathVariable(value =
                                        "attachmentId") final String
                                        attachmentId) throws
        SocialException, IOException {
        log.debug("Removing Attachment for UGC {} with Id {}", ugcId, attachmentId);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        ugcService.removeAttachment(ugcId, tenant, attachmentId);
        return true;
    }

    @RequestMapping(value = "/{ugcId}/attachment/{attachmentId}", method = RequestMethod.GET)
    @ResponseBody()
    @ApiOperation(value = "Sends the attachment to the client",notes = "This will send the headers  content-type " +
        "detected (based on extension)  and content-length")
    public void readAttachment(@ApiParam("Id of the UGC")@NotBlank @PathVariable(value = "ugcId") final String ugcId,
                               @ApiParam("Id of the attachment")@NotBlank @PathVariable(value = "attachmentId") final
                               String attachmentId,
                               final HttpServletResponse response) throws SocialException, IOException {
        log.debug("Reading Attachment for UGC {} with Id {}", ugcId, attachmentId);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        FileInfo fileInfo = ugcService.readAttachment(ugcId, tenant, attachmentId);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(fileInfo.getContentType());
        String realName = fileInfo.getFileName().substring(fileInfo.getFileName().lastIndexOf(File.separator));
        response.setHeader("Content-Disposition", "filename=\"" + realName + "\"");
        response.setContentLength((int)fileInfo.getFileSizeBytes());
        IOUtils.copy(fileInfo.getInputStream(), response.getOutputStream());
        response.getOutputStream().flush();
    }

}
