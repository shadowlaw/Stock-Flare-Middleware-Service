package com.shadow.stock_flare_middleware_service.constants;

public class Validation {
    public static final String SYMBOL_ID_REGEX = "^(?=.*[A-Z])[\\w.]{2,15}$";
    public static final String SYMBOL_ID_REGEX_MESSAGE = "Symbol id must be alphanumeric and 2-15 characters in length";
    public static final String MEDIUM_ID_REGEX = "^[0-9]{9}$";
    public static final String USER_ID_REGEX = "\\d+";
    public static final String USER_ID_REGEX_MESSAGE = "must be numeric";
    public static final String PORTFOLIO_NICKNAME_REGEX = "^[a-zA-Z0-9_]+$";
    public static final String PORTFOLIO_IDENTIFIER_REGEX = "^[a-zA-Z0-9_]+$";
    public static final String PORTFOLIO_ID_REGEX = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
    public static final String PORTFOLIO_ID_REGEX_MESSAGE = "Invalid portfolio ID";
}
