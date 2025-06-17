package com.example.T1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserAlreadyRegisteredException extends RuntimeException{
    public UserAlreadyRegisteredException(String userName){
        super("Юзер с userName {" + userName + "} уже существует!");
    }
}
