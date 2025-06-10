package com.example.T1.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dataSourceErrorLog")
public class DataSourceErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stackTrace")
    private String stackTrace;
    @Column(name = "message")
    private String message;
    @Column(name = "methodSignature")
    private String methodSignature;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    @Override
    public String toString() {
        return "DataSourceErrorLog{" +
                "id=" + id +
                ", stackTrace='" + stackTrace + '\'' +
                ", message='" + message + '\'' +
                ", methodSignature='" + methodSignature + '\'' +
                '}';
    }
}