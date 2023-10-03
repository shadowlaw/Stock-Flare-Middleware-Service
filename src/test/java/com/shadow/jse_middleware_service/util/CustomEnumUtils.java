package com.shadow.jse_middleware_service.util;

import org.apache.commons.lang3.EnumUtils;

import java.util.Arrays;
import java.util.stream.Collectors;


public class CustomEnumUtils extends EnumUtils {
    public static String getNames(Class<? extends Enum<?>> enumClazz) {
        return Arrays.stream(enumClazz.getEnumConstants()).map(Enum::toString).collect(Collectors.joining(", "));
    }
}
