package com.shadow.template.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

@Schema(name = "Error Response")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    String timestamp;
    int status;
    List<Error> errors;
    String path;

    public ErrorResponse(int status, List<Error> errors, String path) {
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Timestamp(System.currentTimeMillis()));
        this.status = status;
        this.errors = errors;
        this.path = path;
    }
}
