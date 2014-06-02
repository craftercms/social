package org.craftercms.social.controllers.rest.v3.ugc;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.craftercms.commons.mongo.FileInfo;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.ugc.UGCService;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UGCController<T> {

    private static final String MAX_INTEGER_STRING = "2147483647";
    @Autowired
    private UGCService ugcService;
    private Logger log = LoggerFactory.getLogger(UGCController.class);

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public T createUGG(@NotBlank @SafeHtml(whitelistType = SafeHtml.WhiteListType.RELAXED) final String content,
                       @SafeHtml(whitelistType = SafeHtml.WhiteListType.RELAXED)
    final String subject, @NotBlank final String target, @RequestParam(required = false,
        value = "parent") final String parent) throws SocialException {
        log.debug("Request for creating a new UGC");
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        return (T)ugcService.create(tenant, parent, target, content, subject);
    }

    @RequestMapping(value = "{ugcId}", method = RequestMethod.GET)
    @ResponseBody()
    public T readUgc(@NotBlank @PathVariable(value = "ugcId") final String ugcId, @RequestParam(required = false,
        defaultValue = "true") final boolean includeChildren, @RequestParam(required = false,
        defaultValue = MAX_INTEGER_STRING) final int childrenCount) throws SocialException {
        log.debug("Request for get a UGC with id {} with including Children {} and children deep {}", ugcId,
            includeChildren, childrenCount);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        return (T)ugcService.read(ugcId, includeChildren, childrenCount, tenant);
    }

    @RequestMapping(value = "{ugcId}/children", method = RequestMethod.GET)
    @ResponseBody()
    public Iterable<T> readUgcChilds(@NotBlank @PathVariable(value = "ugcId") final String ugcId,
                                     @RequestParam(required = false, defaultValue = MAX_INTEGER_STRING)
    final int limit, @RequestParam(required = false, defaultValue = "0") final int skip) throws SocialException {
        log.debug("Request for get all children of {} with a limit of , and starting in ", ugcId, limit,skip);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        return ugcService.readChildren(tenant, ugcId, limit,skip );
    }

    @RequestMapping(value = "/target", method = RequestMethod.GET)
    @ResponseBody()
    public Iterable<T> readUgcByTargetId(@NotEmpty @RequestParam(required = true,
        value = "targetId") final String targetId) throws SocialException {
        log.debug("Request for getting all UGC by targetID {}", targetId);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        return ugcService.readByTargetId(targetId, tenant);
    }

    @RequestMapping(value = "/{ugcId}/attributes/remove", method = RequestMethod.POST)
    @ResponseBody()
    public boolean removeAttributes(@NotBlank @PathVariable(value = "ugcId") final String ugcId,
                                    @RequestParam(required = true) final String attributes) throws SocialException {
        log.debug("Request for deleting form  UGC {} attributes {}", ugcId, attributes);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        ugcService.deleteAttribute(ugcId, attributes.split(","), tenant);
        return true;//Always true unless exception.
    }


    @RequestMapping(value = "/{ugcId}/attributes/add", method = RequestMethod.POST)
    @ResponseBody()
    public boolean addAttributes(@NotBlank @PathVariable(value = "ugcId") final String ugcId,
                                 @RequestBody final Map<String, Object> attributes) throws SocialException {
        log.debug("Request for deleting form  UGC {} attributes {}", ugcId, attributes);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        ugcService.setAttributes(ugcId, tenant, attributes);
        return true;//Always true unless exception.
    }

    @RequestMapping(value = "/{ugcId}/delete", method = RequestMethod.POST)
    @ResponseBody()
    public boolean deleteUgc(@NotBlank @PathVariable(value = "ugcId") final String ugcId) throws SocialException {
        log.debug("Request for deleting  UGC {}", ugcId);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        ugcService.deleteUgc(ugcId, tenant);
        return true;//Always true unless exception.
    }

    @RequestMapping(value = "/search", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody()
    public Iterable<T> search(@RequestParam(required = true) final String query, @RequestParam(required = false,
        defaultValue = "") final String sort, @RequestParam(required = false, defaultValue = MAX_INTEGER_STRING)
                              final int limit, @RequestParam(required = false, defaultValue = "0") final int skip)
        throws SocialException {
        log.debug("Searching using query {} sort {} limit {} skip {}", query, sort, limit, skip);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        return ugcService.search(tenant, query, sort, skip, limit);

    }

    @RequestMapping(value = "/{ugcId}/attachment/add", method = RequestMethod.POST)
    @ResponseBody()
    public FileInfo addAttachment(@NotBlank @PathVariable(value = "ugcId") final String ugcId,
                                  @RequestParam(required = true) CommonsMultipartFile attachment,
                                  HttpServletRequest request) throws SocialException, IOException {
        log.debug("Adding Attachment for UGC {} ", ugcId);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        MimetypesFileTypeMap typeMap = new MimetypesFileTypeMap();

        return ugcService.addAttachment(ugcId, tenant, attachment.getInputStream(), attachment.getOriginalFilename(),
            new MimetypesFileTypeMap().getContentType(attachment.getOriginalFilename()));
    }

    @RequestMapping(value = "/{ugcId}/attachment/remove/{attachmentId}", method = RequestMethod.POST)
    @ResponseBody()
    public boolean removeAttachment(@NotBlank @PathVariable(value = "ugcId") final String ugcId,
                                    @NotBlank @PathVariable(value = "attachmentId") final String attachmentId) throws
        SocialException, IOException {
        log.debug("Removing Attachment for UGC {} with Id {}", ugcId, attachmentId);
        String tenant = "testTenant"; //=ProfileUtils.getCurrentProfile().getTenant();
        ugcService.removeAttachment(ugcId, tenant, attachmentId);
        return true;
    }

    @RequestMapping(value = "/{ugcId}/attachment/{attachmentId}", method = RequestMethod.GET)
    @ResponseBody()
    public void readAttachment(@NotBlank @PathVariable(value = "ugcId") final String ugcId,
                               @NotBlank @PathVariable(value = "attachmentId") final String attachmentId,
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
