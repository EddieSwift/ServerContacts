package com.eddie.servercontacts;

public class DeleteResponse {
    private String status;

    public DeleteResponse() {
    }

    public DeleteResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DeleteResponse{" +
                "status='" + status + '\'' +
                '}';
    }
}
