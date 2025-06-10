package com.example.T1.exceptions;

public class ValueTooBigException extends RuntimeException {
    public ValueTooBigException() {
        super("На счете недостаточно средств");
    }
}