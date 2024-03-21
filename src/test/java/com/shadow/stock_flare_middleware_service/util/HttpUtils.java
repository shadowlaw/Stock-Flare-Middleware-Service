package com.shadow.stock_flare_middleware_service.util;

import java.util.Base64;

public class HttpUtils {
    private HttpUtils() {}

    public static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
