package com.shadow.jse_middleware_service.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SubscriptionType {

    ALL,
    NEWS,
    PRICE;

    public static List<String> getSubTypes(SubscriptionType subscriptionType) {
        switch (subscriptionType) {
            case NEWS:
                return Arrays.stream(NewsType.values()).map(Enum::toString).collect(Collectors.toList());
            case PRICE:
                return Arrays.stream(PriceTargetType.values()).map(Enum::toString).collect(Collectors.toList());
            default:
                List<String> all = new ArrayList<>();
                all.addAll(Arrays.stream(NewsType.values()).map(Enum::toString).collect(Collectors.toList()));
                all.addAll(Arrays.stream(PriceTargetType.values()).map(Enum::toString).collect(Collectors.toList()));
                return all;
        }
    }
}
