package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "blackList")
public class BlackList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "clientid")
    private Long clientId;
    @Column(name = "accountid")
    private Long accountId;

    public BlackList(Long id, Long clientId, Long accountId) {
        this.id = id;
        this.clientId = clientId;
        this.accountId = accountId;
    }

    public BlackList(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        return "BlacList{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", accountId=" + accountId +
                '}';
    }
}
