package com.example.T1.dto;

import java.time.LocalDateTime;

public class DataErrorDto {
    private String methodSignature;
    private String exceptionMessage;
    private LocalDateTime localDateTime;
    private String errorType;

    public DataErrorDto(){}

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    @Override
    public String toString() {
        return "ErrorDto{" +
                "methodSignature='" + methodSignature + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                ", localDateTime=" + localDateTime +
                ", errorType='" + errorType + '\'' +
                '}';
    }
}