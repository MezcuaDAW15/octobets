package com.pfc.octobets.common;

import java.net.URI;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ProblemDetail handleApiException(ApiException ex,
            HttpServletRequest request) {

        ProblemDetail pd = ProblemDetail
                .forStatusAndDetail(ex.getCode().getStatus(), ex.getMessage());

        // Campos estándar
        pd.setTitle(ex.getCode().name());
        pd.setType(URI.create("https://octobets.com/errors/" + ex.getCode()));
        pd.setInstance(URI.create(request.getRequestURI()));

        // Campos personalizados
        pd.setProperty("code", ex.getCode().name());
        if (ex.getDetails() != null) {
            pd.setProperty("details", ex.getDetails());
        }

        return pd;
    }
}