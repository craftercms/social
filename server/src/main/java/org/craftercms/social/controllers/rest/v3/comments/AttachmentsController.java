package org.craftercms.social.controllers.rest.v3.comments;

import java.io.File;
import java.io.IOException;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.craftercms.commons.mongo.FileInfo;
import org.craftercms.social.controllers.rest.v3.comments.exceptions.UGCNotFound;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;


/**
 *
 */
@Controller

public class AttachmentsController<T extends SocialUgc> extends AbstractCommentsController {


    private Logger log = LoggerFactory.getLogger(AttachmentsController.class);

    @Value("${studio.social.web.mimeFile}")
    protected Resource mimeFile;

    @RequestMapping(value = "/{id}/attachments", method = RequestMethod.POST)
    @ResponseBody()
    @ApiOperation(value = "Adds an attachment to the given UGC")
    public FileInfo addAttachment(@ApiParam(value = "Id of the UGC", name = "id") @NotBlank @PathVariable(value =
        "id") final String id, @ApiParam(value = "File to upload, Do notice that the server will enforce ")
    @RequestParam() MultipartFile attachment) throws SocialException, IOException {
        log.debug("Adding Attachment for UGC {} ", id);
        final FileInfo fileInfo =ugcService.addAttachment(id, context(), attachment.getInputStream(), attachment
            .getOriginalFilename(), new MimetypesFileTypeMap(mimeFile.getInputStream()).getContentType(attachment
            .getOriginalFilename().toLowerCase()));
        if(fileInfo==null){
            throw new UGCException("Given File contains a virus");
        }else{
            return fileInfo;
        }
    }


    @RequestMapping(value = "/{id}/attachments/{attachmentId}", method = RequestMethod.DELETE)
    @ResponseBody()
    @ApiOperation("Deletes the given attachment for the UGC")
    public boolean removeAttachment(@ApiParam("Id of the UGC") @NotBlank @PathVariable(value = "id") final String
                                            id, @ApiParam("Id of the attachment to delete") @NotBlank
    @PathVariable(value = "attachmentId") final String attachmentId) throws SocialException, IOException {
        log.debug("Removing Attachment for UGC {} with Id {}", id, attachmentId);

        ugcService.removeAttachment(id, context(), attachmentId);
        return true;
    }

    @RequestMapping(value = "/{id}/attachments/{attachmentId}/delete", method = RequestMethod.POST)
    @ResponseBody()
    @ApiOperation("Deletes the given attachment for the UGC")
    public boolean removeAttachmentPost(@ApiParam("Id of the UGC") @NotBlank @PathVariable(value = "id") final String
                                        id, @ApiParam("Id of the attachment to delete") @NotBlank
                                    @PathVariable(value = "attachmentId") final String attachmentId) throws SocialException, IOException {
        return this.removeAttachment(id,attachmentId);
    }

    @RequestMapping(value = "/{id}/attachments/{attachmentId}/update", method = RequestMethod.POST)
    @ResponseBody()
    @ApiOperation("Updates the given attachment for the UGC")
    public boolean updateAttachmentPost(@ApiParam("Id of the UGC") @NotBlank @PathVariable(value = "id") final String
                                        id, @ApiParam("Id of the attachment to delete") @NotBlank
                                        @PathVariable(value = "attachmentId") final String attachmentId,
                                        @RequestParam MultipartFile file) throws
        SocialException, IOException {
        log.debug("Removing Attachment for UGC {} with Id {}", id, attachmentId);
        ugcService.updateAttachment(id, context(), attachmentId, file.getInputStream());
        return true;
    }


    @RequestMapping(value = "/{id}/attachments", method = RequestMethod.GET)
    @ResponseBody()
    @ApiOperation(value = "Sends the information attachment to the client")
    public Iterable<FileInfo> listAttachments(@ApiParam("Id of the UGC") @NotBlank @PathVariable(value = "id")
                                                  final String id) throws SocialException, UGCNotFound {
        log.debug("Listing all Attachments for UGC {}", id);

        T ugc = (T)ugcService.read(id, context());
        if (ugc == null) {
            throw new UGCNotFound("Ugc with Id " + id + " does not Exist");
        }
        return ugc.getAttachments();
    }

    @RequestMapping(value = "/{id}/attachments/{attachmentId}", method = RequestMethod.GET)
    @ResponseBody()
    @ApiOperation(value = "Sends the attachment to the client", notes = "This will send the headers  content-type " +
        "(based on extension), content-length, and content-disposition")
    public void readAttachment(@ApiParam("Id of the UGC") @NotBlank @PathVariable(value = "id") final String
                                       id, @ApiParam("Id of the attachment") @NotBlank @PathVariable(value =
        "attachmentId") final String attachmentId, final HttpServletResponse response) throws SocialException,
        IOException {
        log.debug("Reading Attachment for UGC {} with Id {}", id, attachmentId);

        FileInfo fileInfo = ugcService.readAttachment(id, context(), attachmentId);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(fileInfo.getContentType());
        String realName = fileInfo.getStoreName().substring(fileInfo.getStoreName().lastIndexOf(File.separator));
        response.setHeader("Content-Disposition", "filename=\"" + realName + "\"");
        response.setContentLength((int)fileInfo.getFileSizeBytes());
        IOUtils.copy(fileInfo.getInputStream(), response.getOutputStream());
        response.getOutputStream().flush();
    }
}
