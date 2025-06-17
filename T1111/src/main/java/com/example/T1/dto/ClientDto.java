package com.example.T1.dto;

import com.example.T1.enums.ClientStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ClientDto {
    @NotNull(message = "Имя клиента не может быть пустым")
    private String first;
    @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
    private String last;
    private String middle;
    private Long clientId;
    private ClientStatus status;

    public ClientDto(String first, String last,
                     String middle, Long clientId,
                     ClientStatus status) {
        this.first = first;
        this.last = last;
        this.middle = middle;
        this.clientId = clientId;
        this.status = status;
    }

    public ClientDto(){}

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClientDto{" +
                "first='" + first + '\'' +
                ", last='" + last + '\'' +
                ", middle='" + middle + '\'' +
                ", clientId=" + clientId +
                ", status=" + status +
                '}';
    }
}