package com.n26.statistics.domains;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.n26.statistics.utils.JsonSnakeStretegy;
import com.n26.statistics.validation.WithinLast;
import org.immutables.value.Value;

import javax.validation.constraints.Min;

import static java.time.temporal.ChronoUnit.SECONDS;

@Value.Immutable
@JsonSnakeStretegy
@JsonSerialize(as = ImmutableTransaction.class)
@JsonDeserialize(as = ImmutableTransaction.class)
public interface Transaction {

    @Min(0)
    @Value.Parameter
    double getAmount();

    @Value.Parameter
    @WithinLast(duration = 60, unit = SECONDS)
    long getTimestamp();
}
