/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.controllers.rest.v3.comments;

import java.io.File;
import java.io.IOException;
import jakarta.activation.MimetypesFileTypeMap;
import jakarta.servlet.http.HttpServletResponse;

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
    public FileInfo addAttachment(@NotBlank @PathVariable(value = "id") final String id,
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
    public boolean removeAttachment(@NotBlank @PathVariable(value = "id") final String id,
                                    @NotBlank @PathVariable(value = "attachmentId") final String attachmentId)
            throws SocialException, IOException {
        log.debug("Removing Attachment for UGC {} with Id {}", id, attachmentId);

        ugcService.removeAttachment(id, context(), attachmentId);
        return true;
    }

    @RequestMapping(value = "/{id}/attachments/{attachmentId}/delete", method = RequestMethod.POST)
    @ResponseBody()
    public boolean removeAttachmentPost(@NotBlank @PathVariable(value = "id") final String id,
                                        @NotBlank @PathVariable(value = "attachmentId") final String attachmentId)
            throws SocialException, IOException {
        return this.removeAttachment(id,attachmentId);
    }

    @RequestMapping(value = "/{id}/attachments/{attachmentId}/update", method = RequestMethod.POST)
    @ResponseBody()
    public boolean updateAttachmentPost(@NotBlank @PathVariable(value = "id") final String id,
                                        @NotBlank @PathVariable(value = "attachmentId") final String attachmentId,
                                        @RequestParam MultipartFile file) throws
        SocialException, IOException {
        log.debug("Removing Attachment for UGC {} with Id {}", id, attachmentId);
        ugcService.updateAttachment(id, context(), attachmentId, file.getInputStream());
        return true;
    }


    @RequestMapping(value = "/{id}/attachments", method = RequestMethod.GET)
    @ResponseBody()
    public Iterable<FileInfo> listAttachments(@NotBlank @PathVariable(value = "id") final String id)
            throws SocialException, UGCNotFound {
        log.debug("Listing all Attachments for UGC {}", id);

        T ugc = (T)ugcService.read(id, context());
        if (ugc == null) {
            throw new UGCNotFound("Ugc with Id " + id + " does not Exist");
        }
        return ugc.getAttachments();
    }

    @RequestMapping(value = "/{id}/attachments/{attachmentId}", method = RequestMethod.GET)
    @ResponseBody()
    public void readAttachment(@NotBlank @PathVariable(value = "id") final String id,
                               @NotBlank @PathVariable(value = "attachmentId") final String attachmentId,
                               final HttpServletResponse response) throws SocialException, IOException {
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
