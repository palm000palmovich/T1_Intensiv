package com.example.T1.model;

import jakarta.persistence.*;

@Entity
@Table(name = "time_limit_exceed_log")
public class TimeLimitExceedLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "method_signature")
    private String methodSignature;
    @Column(name = "difference")
    private Long difference;
    @Column(name = "units_of_measurement")
    private String unitsOfMeasurement;

    public TimeLimitExceedLog(Long id, String methodSignature, Long difference, String unitsOfMeasurement) {
        this.id = id;
        this.methodSignature = methodSignature;
        this.difference = difference;
    }

    public TimeLimitExceedLog() {
        this.unitsOfMeasurement = "милисекунды";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public Long getDifference() {
        return difference;
    }

    public void setDifference(Long difference) {
        this.difference = difference;
    }

    public String getUnitsOfMeasurement() {
        return unitsOfMeasurement;
    }

    public void setUnitsOfMeasurement(String unitsOfMeasurement) {
        this.unitsOfMeasurement = unitsOfMeasurement;
    }

    @Override
    public String toString() {
        return "TimeLimitExceedLog{" +
                "id=" + id +
                ", method_signature='" + methodSignature + '\'' +
                ", difference=" + difference +
                ", unitsOfMeasurement='" + unitsOfMeasurement + '\'' +
                '}';
    }
}