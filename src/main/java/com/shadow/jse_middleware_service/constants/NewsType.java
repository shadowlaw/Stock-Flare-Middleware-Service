package com.shadow.jse_middleware_service.constants;

import java.util.Arrays;

public enum NewsType {
    DIVDEC;

    public static String getNames() {
        return Arrays.toString(NewsType.values()).replaceAll("^.|.$", "");
    }
}
