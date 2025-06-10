package com.example.T1.model;

import com.example.T1.enums.AccountStatus;
import com.example.T1.enums.Type;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account")
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "acc_type")
    private Type type;
    @Column(name = "balance")
    private Long balance;
    @Column(name = "accountid", nullable = false)
    private Long accountId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;
    @Column(name = "frozen_amount")
    private Long frozenAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    @JsonBackReference
    private Client client;
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Transaction> transactions = new ArrayList<>();

    public Account(Type type, Long balance, Client client, Long accountId, Long frozenAmount) {
        this.type = type;
        this.balance = balance;
        this.client = client;
        this.accountId = accountId;
        this.frozenAmount = frozenAmount;
    }

    public Account(){}

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setAccount(this); // Устанавливаем обратную связь
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
        transaction.setAccount(null); // Разрываем обратную связь
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Long getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(Long frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", type=" + type +
                ", balance=" + balance +
                ", accountId=" + accountId +
                ", status=" + status +
                ", frozenAmount=" + frozenAmount +
                ", client=" + client +
                ", transactions=" + transactions +
                '}';
    }
}