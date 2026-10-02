package com.mysociety.communication.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException e, HttpServletRequest r) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
        p.setType(java.net.URI.create("urn:mysociety:validation-error"));
        p.setProperty("path", r.getRequestURI());
        p.setProperty("errors", e.getBindingResult().getFieldErrors().stream().map(x -> x.getField() + ": " + x.getDefaultMessage()).toList());
        return p;
    }

    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail denied(HttpServletRequest r) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Tenant access denied");
        p.setProperty("path", r.getRequestURI());
        return p;
    }
}
