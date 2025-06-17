package com.example.T1.dto;

import com.example.T1.enums.UserRoles;

public class UserFullInfo {
    private Long userPrimaryId;
    private String userName;
    private String password;
    private UserRoles role;
    private Long clientPrimaryKey;

    public UserFullInfo(Long userPrimaryId, String userName,
                        String password, UserRoles role, Long clientPrimaryKey) {
        this.userPrimaryId = userPrimaryId;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.clientPrimaryKey = clientPrimaryKey;
    }

    public UserFullInfo(){}

    public Long getUserPrimaryId() {
        return userPrimaryId;
    }

    public void setUserPrimaryId(Long userPrimaryId) {
        this.userPrimaryId = userPrimaryId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRoles getRole() {
        return role;
    }

    public void setRole(UserRoles role) {
        this.role = role;
    }

    public Long getClientPrimaryKey() {
        return clientPrimaryKey;
    }

    public void setClientPrimaryKey(Long clientPrimaryKey) {
        this.clientPrimaryKey = clientPrimaryKey;
    }

    @Override
    public String toString() {
        return "UserFullInfo{" +
                "userPrimaryId=" + userPrimaryId +
                ", userName='" + userName + '\'' +
                ", role=" + role +
                ", clientPrimaryKey=" + clientPrimaryKey +
                '}';
    }
}

