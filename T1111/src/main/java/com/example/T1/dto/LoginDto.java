package com.example.T1.dto;

public class LoginDto {
    private String username;
    private String password;

    public LoginDto(String userName, String password) {
        this.username = userName;
        this.password = password;
    }

    public LoginDto(){}

    public String getUsername() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginDto{" +
                "userName='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
