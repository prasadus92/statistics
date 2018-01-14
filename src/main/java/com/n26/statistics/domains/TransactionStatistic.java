package com.n26.statistics.domains;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.n26.statistics.utils.JsonSnakeStretegy;
import org.immutables.value.Value;

@Value.Immutable
@JsonSnakeStretegy
@JsonSerialize(as = ImmutableTransactionStatistic.class)
@JsonDeserialize(as = ImmutableTransactionStatistic.class)
public interface TransactionStatistic {

    TransactionStatistic INITIAL_VALUE = ImmutableTransactionStatistic.builder()
            .count(0)
            .sum(0.0)
            .max(Double.NaN)
            .min(Double.NaN)
            .build();

    long getCount();

    double getSum();

    double getMax();

    double getMin();

    default double getAvg() {
        return getCount() > 0 ? getSum() / getCount() : Double.NaN;
    }

    default TransactionStatistic record(double amount) {
        return this.equals(INITIAL_VALUE) ?
                ImmutableTransactionStatistic.builder()
                        .count(1)
                        .sum(amount)
                        .min(amount)
                        .max(amount)
                        .build() :
                ImmutableTransactionStatistic.builder()
                        .count(getCount() + 1)
                        .sum(getSum() + amount)
                        .min(Math.min(getMin(), amount))
                        .max(Math.max(getMax(), amount))
                        .build();
    }

    default TransactionStatistic add(TransactionStatistic that) {
        if (this.equals(INITIAL_VALUE)) {
            return that;
        }
        if (that.equals(INITIAL_VALUE)) {
            return this;
        }
        return ImmutableTransactionStatistic.builder()
                .count(this.getCount() + that.getCount())
                .sum(this.getSum() + that.getSum())
                .min(Math.min(this.getMin(), that.getMin()))
                .max(Math.max(this.getMax(), that.getMax()))
                .build();
    }
}