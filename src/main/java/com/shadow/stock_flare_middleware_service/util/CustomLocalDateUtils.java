package com.shadow.stock_flare_middleware_service.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class CustomLocalDateUtils {
    private CustomLocalDateUtils() {}

    public static LocalDate addDays(LocalDate date, long daysToAdd) {
        try{
            log.debug("adding [{}] days to [{}]", daysToAdd, date);
            return date.plusDays(daysToAdd);
        } catch (Exception e) {
            LocalDate currentDate = LocalDate.now();
            log.warn("Unable to add [{}] days to [{}]", daysToAdd, date);
            log.info("adding [{}] days to current date [{}]", daysToAdd, currentDate);
            log.error("{}: {}", e.getClass(), e.getMessage());
            return currentDate.plusDays(daysToAdd);
        }
    }
}
