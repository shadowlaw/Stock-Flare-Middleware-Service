package com.shadow.stock_flare_middleware_service.constants;

public class Validation {
    public static final String SYMBOL_ID_REGEX = "^(?=.*[A-Z])[\\w.]{2,15}$";
    public static final String MEDIUM_ID_REGEX = "^[0-9]{9}$";
    public static final String USER_ID_REGEX = "\\d+";
    public static final String PORTFOLIO_NICKNAME_REGEX = "^[a-zA-Z0-9_]+$";
    public static final String PORTFOLIO_IDENTIFIER_REGEX = "^[a-zA-Z0-9_]+$";
}
