package com.n26.statistics.storage;

import com.n26.statistics.exceptions.InvalidTimestampException;
import com.n26.statistics.persistence.AtomicTransactionStorage;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AtomicTransactionStorageTest {

    private static final long NOW = System.currentTimeMillis();
    private static final long ONE_SECOND = Duration.ofSeconds(5).toMillis();
    private static final long ONE_MINUTE = Duration.ofMinutes(1).toMillis();
    private static final BinaryOperator<Integer> SUM = (a, b) -> a + b;

    private AtomicTransactionStorage<Integer> transactionStorage;

    @Before
    public void setUp() throws Exception {
        transactionStorage = new AtomicTransactionStorage<>(ChronoUnit.MINUTES, ChronoUnit.SECONDS, 64, () -> 0, () -> NOW);
    }

    @Test
    public void shouldAcceptSingleValue() throws Exception {
        transactionStorage.update(NOW, plus(5));

        Integer value = transactionStorage.getReference(NOW).getValue();

        assertThat(value, is(5));
    }

    @Test
    public void shouldAcceptMultipleValues() throws Exception {
        transactionStorage.update(NOW, plus(5));
        transactionStorage.update(NOW, plus(10));
        transactionStorage.update(NOW, plus(20));

        Integer value = transactionStorage.getReference(NOW).getValue();

        assertThat(value, is(35));
    }

    @Test(expected = InvalidTimestampException.class)
    public void shouldRejectOldValue() throws Exception {
        transactionStorage.update(NOW - ONE_MINUTE - ONE_SECOND, plus(5));
    }

    @Test(expected = InvalidTimestampException.class)
    public void shouldRejectFutureValue() throws Exception {
        transactionStorage.update(NOW + ONE_SECOND, plus(5));
    }

    @Test
    public void shouldReturnEmptyValues() throws Exception {
        Integer value = transactionStorage.getStatistics(SUM);

        assertThat(value, is(0));
    }

    @Test
    public void shouldReturnSingleValue() throws Exception {
        transactionStorage.update(NOW, plus(5));

        Integer value = transactionStorage.getStatistics(SUM);

        assertThat(value, is(5));
    }

    @Test
    public void shouldReturnMultipleValues() throws Exception {
        transactionStorage.update(NOW - ONE_MINUTE, plus(5));
        transactionStorage.update(NOW - ONE_SECOND, plus(10));
        transactionStorage.update(NOW, plus(20));

        Integer value = transactionStorage.getStatistics(SUM);

        assertThat(value, is(35));
    }

    @Test
    public void shouldReturnGroupedValues() throws Exception {
        transactionStorage.update(NOW, plus(5));
        transactionStorage.update(NOW, plus(10));
        transactionStorage.update(NOW, plus(20));

        Integer value = transactionStorage.getStatistics(SUM);

        assertThat(value, is(35));
    }

    private UnaryOperator<Integer> plus(int i) {
        return value -> value + i;
    }
}
