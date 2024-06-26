package com.shadow.stock_flare_middleware_service.annotations.constraint;

import com.shadow.stock_flare_middleware_service.annotations.ValidEnumConstant;
import lombok.SneakyThrows;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.ArrayList;

public class ValidEnumConstantValidator  implements ConstraintValidator<ValidEnumConstant, String> {

    private ArrayList<String> enumClazzList;
    private String message;

    @SneakyThrows
    @Override
    public void initialize(ValidEnumConstant constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        enumClazzList = new ArrayList<>();

        for (@SuppressWarnings("rawtypes") Enum enumVal : constraintAnnotation.enumClazz().getEnumConstants()) {
            enumClazzList.add(enumVal.toString());
        }

        String defaultValue = (String) ValidEnumConstant.class.getDeclaredMethod("message").getDefaultValue();

        message = defaultValue.equals(constraintAnnotation.message()) ?
                String.format(defaultValue, String.join(", ", enumClazzList)):
                constraintAnnotation.message();

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return enumClazzList.stream().anyMatch( value -> value.equalsIgnoreCase(s));
    }
}
