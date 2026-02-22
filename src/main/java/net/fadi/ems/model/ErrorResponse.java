package net.fadi.ems.model;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private String error;
    private String exceptionMessage;
    private String path;
    private int statusCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now(ZoneId.of("UTC"));
    }

    public ErrorResponse(String error, String exceptionMessage, String path, int statusCode) {
        this();
        this.error = error;
        this.exceptionMessage = exceptionMessage;
        this.path = path;
        this.statusCode = statusCode;
    }
}
