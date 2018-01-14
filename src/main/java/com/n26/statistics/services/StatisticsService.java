package com.n26.statistics.services;

import com.n26.statistics.domains.Transaction;
import com.n26.statistics.domains.TransactionStatistic;

public interface StatisticsService {

    void record(Transaction transaction);

    TransactionStatistic getTransactionStatistics();
}
