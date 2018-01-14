package com.n26.statistics.controllers;

import com.n26.statistics.exceptions.InvalidTimestampException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@ControllerAdvice
public class DefaultController {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultController.class);

    @ResponseStatus(NO_CONTENT)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void methodArgumentNotValidExceptionHandler() {
        LOG.error("Invalid Argument exception handler");
    }

    @ResponseStatus(NO_CONTENT)
    @ExceptionHandler(InvalidTimestampException.class)
    public void invalidTimestampExceptionHandler() {
        LOG.error("Invalid Timestamp exception handler");
    }

}
