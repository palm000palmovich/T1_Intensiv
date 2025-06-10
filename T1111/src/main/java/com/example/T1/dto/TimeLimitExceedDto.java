package com.example.T1.dto;

public class TimeLimitExceedDto {
    private String methodSignature;
    private Long duration;
    private String errorType;

    public TimeLimitExceedDto(){}

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    @Override
    public String toString() {
        return "TimeLimitExceedDto{" +
                "methodSignature='" + methodSignature + '\'' +
                ", duration=" + duration +
                ", errorType=" + errorType +
                '}';
    }
}