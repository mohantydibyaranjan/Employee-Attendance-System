package com.attendance.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private boolean success;
    private String message;
    private List<Error> errors;
    private LocalDateTime timestamp;
    private String path;

    @Data
    @NoArgsConstructor
    public static class Error {
        private String field;
        private String error;

        public Error(String field, String error) {
            this.field = field;
            this.error = error;
        }
    }

    public ErrorResponse(boolean success, String message, List<Error> errors, String path) {
        this.success = success;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }
}
