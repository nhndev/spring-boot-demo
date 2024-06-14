package com.nhn.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nhn.demo.dto.response.ErrorResponse;

@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return ErrorResponse.builder().errCode(404).errMsg(e.getMessage())
                            .build();
    }

    @ExceptionHandler(FuncErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFuncErrorException(final FuncErrorException e) {
        return ErrorResponse.builder().errCode(400).errMsg(e.getMessage())
                            .build();
    }
}
