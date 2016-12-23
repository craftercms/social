package org.craftercms.social.controllers.rest.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.file.FileUtils;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.security.exception.AuthenticationRequiredException;
import org.craftercms.social.controllers.rest.v3.comments.exceptions.UGCNotFound;
import org.craftercms.social.exceptions.IllegalSocialQueryException;
import org.craftercms.social.exceptions.IllegalUgcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Handles Exceptions.
 */
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    private Logger log = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);
    @Value("${studio.social.web.system.throwStacktrace}")
    private boolean throwStacktrace;
    private ObjectWriter converter = new ObjectMapper().writer();

    public GlobalDefaultExceptionHandler() {

    }

    @ExceptionHandler(value = Throwable.class)
    public void defaultErrorHandler(HttpServletRequest req, HttpServletResponse resp, Exception e) throws Exception {
        log.error("Request: " + req.getRequestURL() + " raised and error {}", e);
        serializeError(e, resp, HttpStatus.INTERNAL_SERVER_ERROR, req);
    }

    @ExceptionHandler(value = ActionDeniedException.class)
    public void ActionDeniedHandler(HttpServletRequest req, HttpServletResponse resp, Exception e) throws Exception {
        log.error("Request: " + req.getRequestURL() + " raised and error {}", e);
        serializeError(e, resp, HttpStatus.FORBIDDEN, req);
    }

    @ExceptionHandler(value = UGCNotFound.class)
    public void ugcNotFound(HttpServletRequest req, HttpServletResponse resp, Exception e) throws IOException {
        log.debug("Request {} for a non existent UGC (or does not belong to context)",req.getRequestURL());
        serializeError(e, resp, HttpStatus.NOT_FOUND, req);
    }

    @ExceptionHandler(value = {MissingServletRequestParameterException.class, IllegalSocialQueryException.class,
        IllegalArgumentException.class, FileExistsException.class, IllegalUgcException.class})
    public void missingParameterHandler(HttpServletRequest req, HttpServletResponse resp,
                                        Exception e) throws Exception {
        serializeError(e, resp, HttpStatus.BAD_REQUEST, req);
    }


    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public void requestMethodNotSupportedException(HttpServletRequest req, HttpServletResponse resp,
                                        Exception e) throws Exception {
        serializeError(e, resp, HttpStatus.NOT_ACCEPTABLE, req);
    }



    @ExceptionHandler(value = AuthenticationRequiredException.class)
    public void authenticationRequiredExceptionHandler(HttpServletRequest req, HttpServletResponse resp,
                                        Exception e) throws Exception {
        log.error("Request: " + req.getRequestURL() + " raised and error {}", e);
        serializeError(e, resp, HttpStatus.UNAUTHORIZED, req);
    }


    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public void sizeLimitExceededException(HttpServletRequest req, HttpServletResponse response,
                                           Exception ex) throws Exception {
        log.error("Request: " + req.getRequestURL() + " raised and error {}", ex.toString());
        FileUploadBase.SizeLimitExceededException realEx = (FileUploadBase.SizeLimitExceededException)ex.getCause();
        String maxSize = FileUtils.readableFileSize(realEx.getPermittedSize());
        String fileSize = FileUtils.readableFileSize(realEx.getActualSize());
        Map<String, Object> error = new HashMap<>();
        error.put("message", String.format("Unable to upload file due size limit is %s and upload size is %s",
            maxSize, fileSize));
        error.put("maxSize", maxSize);
        error.put("fileSize", fileSize);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        converter.writeValue(response.getOutputStream(), error);
    }

    private void serializeError(Exception ex, HttpServletResponse response, HttpStatus status,
                                final HttpServletRequest req) throws IOException {
        log.error("Request: " + req.getRequestURL() + " raised and error {}", ex.toString());
        log.error("Error processing request",ex);
        Map<String, Object> error = new HashMap<>();

        if (StringUtils.isBlank(ex.getMessage())) {
            error.put("error","Unknown Error");
        } else {
            error.put("error", ex.getMessage());
            error.put("message",ex.getMessage());
        }

        if (throwStacktrace) {
            error.put("stacktrace", getStackTrace(ex));
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        converter.writeValue(response.getOutputStream(), error);
    }

    public String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
