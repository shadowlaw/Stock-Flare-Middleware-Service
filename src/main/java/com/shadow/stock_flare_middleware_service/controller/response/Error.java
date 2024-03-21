package com.shadow.stock_flare_middleware_service.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error {
    String error;
    String message;
}
