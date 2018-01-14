package com.n26.statistics.validation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WithinLastMinuteValidatorTest {

    @Mock
    private WithinLast annotation;

    @Mock
    private ConstraintValidatorContext context;

    private WithinLastMinuteValidator validator;

    @Before
    public void setUp() throws Exception {
        WithinLastMinuteValidator.CURRENT_MILLIS = System::currentTimeMillis;
        validator = new WithinLastMinuteValidator();
        validator.initialize(annotation);
        when(annotation.duration()).thenReturn(1);
        when(annotation.unit()).thenReturn(SECONDS);
    }

    @Test
    public void shouldAcceptValidValue() throws Exception {
        boolean valid = validator.isValid(System.currentTimeMillis() - 500, context);
        assertThat(valid, is(true));
    }

    @Test
    public void shouldRejectInvalidValue() throws Exception {
        boolean valid = validator.isValid(System.currentTimeMillis() - 1500, context);
        assertThat(valid, is(false));
    }
}
