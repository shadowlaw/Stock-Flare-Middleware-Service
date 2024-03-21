package com.shadow.stock_flare_middleware_service.annotations.constraint;

import com.shadow.stock_flare_middleware_service.annotations.ValidEnumConstant;
import com.shadow.stock_flare_middleware_service.constants.NewsType;
import com.shadow.stock_flare_middleware_service.constants.NotificationMediumType;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.hibernate.validator.internal.util.annotation.AnnotationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class ValidEnumConstantValidatorTest {

    private ConstraintValidatorContext context;

    @BeforeEach
    public void setup() {
        context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(context.buildConstraintViolationWithTemplate(any()))
                .thenReturn(builder);
    }


    @ParameterizedTest
    @MethodSource("argumentProvider")
    void test_isValid_givenString_MatchEnumClassConstant_thenReturnExpected(String stringConstant, Class<? extends Enum<?>> enumClazz, boolean expectedValue) {
        ValidEnumConstantValidator validator = new ValidEnumConstantValidator();
        validator.initialize(createValidEnumConstantValidator(enumClazz));

        assertEquals(expectedValue, validator.isValid(stringConstant, context));
    }

    private ValidEnumConstant createValidEnumConstantValidator(Class<? extends Enum<?>> enumClass) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("enumClazz", enumClass);

        return AnnotationFactory.create(
                new AnnotationDescriptor.Builder<>(ValidEnumConstant.class, attributes).build()
        );
    }

    private static Stream<Arguments> argumentProvider() {
        return Stream.of(
                Arguments.of("TELEGRAM", NotificationMediumType.class, true),
                Arguments.of("SIGNAL", NewsType.class, false)
        );
    }
}