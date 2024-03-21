package com.shadow.stock_flare_middleware_service.constants;

public class Validation {
    public static final String SYMBOL_ID_REGEX = "^(?=.*[A-Z])[\\w.]{2,15}$";
    public static final String MEDIUM_ID_REGEX = "^[0-9]{9}$";
}