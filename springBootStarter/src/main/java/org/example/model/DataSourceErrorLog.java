package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "data_source_error_log")
public class DataSourceErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "message")
    private String message;
    @Column(name = "method_signature")
    private String methodSignature;
    @Column(name = "stack_trace")
    private String stackTrace;

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
