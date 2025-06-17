package org.example.dto;

public class BlackListCheckResponse {
    private String status;

    public BlackListCheckResponse(String status) {
        this.status = status;
    }

    public BlackListCheckResponse(){}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BlackListCheckResponse{" +
                "status='" + status + '\'' +
                '}';
    }
}
