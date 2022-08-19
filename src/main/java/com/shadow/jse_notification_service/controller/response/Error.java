package com.shadow.jse_notification_service.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error {
    String error;
    String message;
}
