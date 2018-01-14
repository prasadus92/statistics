package com.n26.statistics.validation;

import javax.annotation.Nullable;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;
import java.util.function.Supplier;

public class WithinLastMinuteValidator implements ConstraintValidator<WithinLast, Long> {

    public static Supplier<Long> CURRENT_MILLIS = System::currentTimeMillis;

    private WithinLast annotation;

    @Override
    public void initialize(WithinLast annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(@Nullable Long value, ConstraintValidatorContext context) {
        Duration age = Duration.of(annotation.duration(), annotation.unit());
        return value == null || CURRENT_MILLIS.get() - value <= age.toMillis();
    }
}
