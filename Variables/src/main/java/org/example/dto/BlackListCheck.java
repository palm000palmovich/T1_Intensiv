package org.example.dto;

public class BlackListCheck {
    private Long clientId;
    private Long accountId;

    public BlackListCheck(Long clientId, Long accountId) {
        this.clientId = clientId;
        this.accountId = accountId;
    }

    public BlackListCheck(){}

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "BlackListCheck{" +
                "clientId=" + clientId +
                ", accountId=" + accountId +
                '}';
    }
}
