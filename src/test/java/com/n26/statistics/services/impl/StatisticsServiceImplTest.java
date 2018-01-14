package com.n26.statistics.services.impl;

import com.n26.statistics.domains.ImmutableTransaction;
import com.n26.statistics.domains.TransactionStatistic;
import com.n26.statistics.persistence.TransactionStorage;
import com.n26.statistics.validation.ClassCasts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsServiceImplTest {

    private static final Class<UnaryOperator<TransactionStatistic>> STATISTIC_UPDATER = ClassCasts.cast(UnaryOperator.class);
    private static final Class<BinaryOperator<TransactionStatistic>> STATISTIC_REDUCER = ClassCasts.cast(BinaryOperator.class);
    private static final Long TIMESTAMP = new Random().nextLong();
    private static final double AMOUNT = new Random().nextDouble();

    @Mock
    private TransactionStorage<TransactionStatistic> transactionStorage;

    private StatisticsServiceImpl statisticsService;

    @Mock
    private TransactionStatistic statistic;

    @Mock
    private TransactionStatistic anotherStatistic;

    @Mock
    private TransactionStatistic combinedStatistic;


    @Before
    public void setUp() throws Exception {
        statisticsService = new StatisticsServiceImpl(transactionStorage);
    }

    @Test
    public void shouldRegisterTransaction() throws Exception {
        doAnswer(invocation -> invocation.getArgumentAt(1, STATISTIC_UPDATER).apply(statistic))
                .when(transactionStorage).update(eq(TIMESTAMP), any(STATISTIC_UPDATER));

        statisticsService.record(ImmutableTransaction.of(AMOUNT, TIMESTAMP));

        verify(statistic).record(AMOUNT);
    }

    @Test
    public void shouldReturnTransactionStatistics() throws Exception {
        doAnswer(invocation -> {
            invocation.getArgumentAt(0, STATISTIC_REDUCER).apply(statistic, anotherStatistic);
            return combinedStatistic;
        }).when(transactionStorage).getStatistics(any(STATISTIC_REDUCER));

        TransactionStatistic actual = statisticsService.getTransactionStatistics();

        assertThat(actual, is(combinedStatistic));

        verify(statistic).add(anotherStatistic);
    }
}
