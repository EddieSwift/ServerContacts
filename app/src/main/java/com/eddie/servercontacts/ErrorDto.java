package com.eddie.servercontacts;

public class ErrorDto {
    private int code;
    private String details;
    private String message;
    private String timestamp;

    public ErrorDto() {
    }

    public ErrorDto(int code, String details, String message, String timestamp) {
        this.code = code;
        this.details = details;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getCode() {
        return code;
    }

    public String getDetails() {
        return details;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(String timesmap) {
        this.timestamp = timesmap;
    }

    @Override
    public String toString() {
        return "ErrorDto{" +
                "code=" + code +
                ", details='" + details + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}

