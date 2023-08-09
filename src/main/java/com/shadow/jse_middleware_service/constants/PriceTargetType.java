package com.shadow.jse_middleware_service.constants;

import java.util.Arrays;

public enum PriceTargetType {
    PRC_VAL_UP_ALL;

    public static String getNames() {
        return Arrays.toString(PriceTargetType.values()).replaceAll("^.|.$", "");
    }
}
