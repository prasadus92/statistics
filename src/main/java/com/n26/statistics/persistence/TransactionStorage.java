package com.n26.statistics.persistence;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public interface TransactionStorage<T> {

    void update(long timestamp, UnaryOperator<T> updater);

    T getStatistics(BinaryOperator<T> reducer);

}
