package com.giba.validation;

import jakarta.validation.Constraint;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CpfValidoTest {

    @Test
    void declaraMetadadosDeConstraintBeanValidation() {
        assertEquals(CpfValidator.class, CpfValido.class.getAnnotation(Constraint.class).validatedBy()[0]);
        assertEquals(RetentionPolicy.RUNTIME, CpfValido.class.getAnnotation(Retention.class).value());
        assertArrayEquals(new ElementType[]{ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT},
                Arrays.stream(CpfValido.class.getAnnotation(Target.class).value()).sorted().toArray(ElementType[]::new));
    }
}
