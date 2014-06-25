package org.craftercms.social.controllers.rest.v3.comments;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import java.io.File;
import java.io.IOException;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.craftercms.commons.mongo.FileInfo;
import org.craftercms.social.controllers.rest.v3.comments.exceptions.UGCNotFound;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.SocialException;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Created by cortiz on 6/11/14.
 */
@Controller

public class AttachmentsController<T extends SocialUgc> extends AbstractCommentsController {


    private Logger log = LoggerFactory.getLogger(AttachmentsController.class);

    @RequestMapping(value = "/{id}/attachments", method = RequestMethod.POST)
    @ResponseBody()
    @ApiOperation(value = "Adds and attachment to the given UGC")
    public FileInfo addAttachment(@ApiParam(value = "Id of the UGC", name = "id") @NotBlank @PathVariable(value =
        "id") final String id, @ApiParam(value = "File to upload, Do notice that the server will enforce ")
    @RequestParam(required = true) CommonsMultipartFile attachment) throws SocialException, IOException {
        log.debug("Adding Attachment for UGC {} ", id);
        return ugcService.addAttachment(id, tenant(), attachment.getInputStream(), attachment.getOriginalFilename(),
            new MimetypesFileTypeMap().getContentType(attachment.getOriginalFilename()));
    }


    @RequestMapping(value = "/{id}/attachments/{attachmentId}", method = RequestMethod.DELETE)
    @ResponseBody()
    @ApiOperation("Deletes the given attachment for the UGC")
    public boolean removeAttachment(@ApiParam("Id of the UGC") @NotBlank @PathVariable(value = "id") final String
                                            id, @ApiParam("Id of the attachment to delete") @NotBlank
    @PathVariable(value = "attachmentId") final String attachmentId) throws SocialException, IOException {
        log.debug("Removing Attachment for UGC {} with Id {}", id, attachmentId);

        ugcService.removeAttachment(id, tenant(), attachmentId);
        return true;
    }

    @RequestMapping(value = "/{id}/attachments/{attachmentId}", method = RequestMethod.PUT)
    @ResponseBody()
    @ApiOperation("Updates the given attachment for the UGC")
    public boolean updateAttachment(@ApiParam("Id of the UGC") @NotBlank @PathVariable(value = "id") final String
                                            id, @ApiParam("Id of the attachment to delete") @NotBlank
    @PathVariable(value = "attachmentId") final String attachmentId, CommonsMultipartFile file) throws
        SocialException, IOException {
        log.debug("Removing Attachment for UGC {} with Id {}", id, attachmentId);

        ugcService.updateAttachment(id, tenant(), attachmentId, file.getInputStream());
        return true;
    }

    @RequestMapping(value = "/{id}/attachments", method = RequestMethod.GET)
    @ResponseBody()
    @ApiOperation(value = "Sends the information attachment to the client")
    public Iterable<FileInfo> listAttachments(@ApiParam("Id of the UGC") @NotBlank @PathVariable(value = "id")
                                                  final String id) throws SocialException, UGCNotFound {
        log.debug("Listing all Attachments for UGC {}", id);

        T ugc = (T)ugcService.read(id, tenant());
        if (ugc == null) {
            throw new UGCNotFound("Ugc with Id " + id + " does not Exists");
        }
        return ugc.getAttachments();
    }

    @RequestMapping(value = "/{id}/attachments/{attachmentId}", method = RequestMethod.GET)
    @ResponseBody()
    @ApiOperation(value = "Sends the attachment to the client", notes = "This will send the headers  content-type " +
        "(based on extension),content-length,and content-disposition")
    public void readAttachment(@ApiParam("Id of the UGC") @NotBlank @PathVariable(value = "id") final String
                                       id, @ApiParam("Id of the attachment") @NotBlank @PathVariable(value =
        "attachmentId") final String attachmentId, final HttpServletResponse response) throws SocialException,
        IOException {
        log.debug("Reading Attachment for UGC {} with Id {}", id, attachmentId);

        FileInfo fileInfo = ugcService.readAttachment(id, tenant(), attachmentId);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(fileInfo.getContentType());
        String realName = fileInfo.getFileName().substring(fileInfo.getFileName().lastIndexOf(File.separator));
        response.setHeader("Content-Disposition", "filename=\"" + realName + "\"");
        response.setContentLength((int)fileInfo.getFileSizeBytes());
        IOUtils.copy(fileInfo.getInputStream(), response.getOutputStream());
        response.getOutputStream().flush();
    }
}
