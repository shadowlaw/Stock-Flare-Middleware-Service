package com.shadow.stock_flare_middleware_service.annotations;

import com.shadow.stock_flare_middleware_service.annotations.constraint.ValidEnumConstantValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ValidEnumConstantValidator.class})
@NotNull
public @interface ValidEnumConstant {
    String message() default "Choice Not valid. Valid choices include: %s";
    Class<? extends Enum<?>> enumClazz();
    //represents group of constraints
    public Class<?>[] groups() default {};
    //represents additional information about annotation
    public Class<? extends Payload>[] payload() default {};
}
