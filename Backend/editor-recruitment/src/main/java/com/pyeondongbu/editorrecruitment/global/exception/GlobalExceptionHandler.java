package com.pyeondongbu.editorrecruitment.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException e,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request
    ) {
        log.warn(e.getMessage(), e);

        final String errMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(INVALID_REQUEST.getStatus(), errMessage));
    }

    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<ExceptionResponse> handleSizeLimitExceededException(final SizeLimitExceededException e) {
        log.warn(e.getMessage(), e);

        final String message = EXCEED_IMAGE_CAPACITY.getMessage()
                + " 입력된 이미지 용량은 " + e.getActualSize() + " byte 입니다. "
                + "(제한 용량: " + e.getPermittedSize() + " byte)";
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(EXCEED_IMAGE_CAPACITY.getStatus(), message));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponse> handleAuthException(final AuthException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(e.getStatus(), e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(final BadRequestException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(e.getStatus(), e.getMessage()));
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ExceptionResponse> handleMemberException(final MemberException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(e.getStatus(), e.getMessage()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(final Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse(INTERNAL_SEVER_ERROR.getStatus(), INTERNAL_SEVER_ERROR.getMessage()));
    }


    /*
    사용자가 엔드포인트가 없는걸 호출할 경우 404 페이지 대신
     */

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(NOT_FOUND.getStatus(), NOT_FOUND.getMessage()));
    }
}

