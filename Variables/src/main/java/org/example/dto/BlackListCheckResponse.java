package org.example.dto;

import org.example.enums.ClientStatus;

public class BlackListCheckResponse {
    private ClientStatus status;

    public BlackListCheckResponse(ClientStatus status) {
        this.status = status;
    }

    public BlackListCheckResponse(){}

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BlackListCheckResponse{" +
                "status=" + status +
                '}';
    }
}
