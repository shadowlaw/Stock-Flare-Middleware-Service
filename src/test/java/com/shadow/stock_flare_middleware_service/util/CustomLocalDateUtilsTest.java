package com.shadow.stock_flare_middleware_service.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CustomLocalDateUtilsTest {

    @ParameterizedTest
    @MethodSource("addDaysTestParams")
    void test_addDays_givenTestParams_thenReturnExpectedResponse(LocalDate date, long daysToAdd, LocalDate expectedDate) {
        LocalDate actualDate = CustomLocalDateUtils.addDays(date, daysToAdd);
        assertEquals(expectedDate.toString(), actualDate.toString());
    }

    public static Stream<Arguments> addDaysTestParams() {
        return Stream.of(
                Arguments.of(LocalDate.of(2023, 4, 8), 3, LocalDate.of(2023, 4, 11)),
                Arguments.of(LocalDate.of(2023, 4, 8), -7, LocalDate.of(2023, 4, 1)),
                Arguments.of(null, 1, LocalDate.now().plusDays(1))
        );
    }
}