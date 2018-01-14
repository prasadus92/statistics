package com.n26.statistics.services.impl;

import com.n26.statistics.domains.Transaction;
import com.n26.statistics.domains.TransactionStatistic;
import com.n26.statistics.persistence.AtomicTransactionStorage;
import com.n26.statistics.persistence.TransactionStorage;
import com.n26.statistics.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final TransactionStorage<TransactionStatistic> transactions;

    @Autowired
    public StatisticsServiceImpl() {
        this(AtomicTransactionStorage.initialize(() -> TransactionStatistic.INITIAL_VALUE));
    }

    protected StatisticsServiceImpl(TransactionStorage<TransactionStatistic> transactions) {
        this.transactions = transactions;
    }

    @Override
    public void record(Transaction transaction) {
        transactions.update(transaction.getTimestamp(),
                (transactionStatistic) -> transactionStatistic.record(transaction.getAmount()));
    }

    @Override
    public TransactionStatistic getTransactionStatistics() {
        return transactions.getStatistics(TransactionStatistic::add);
    }
}
