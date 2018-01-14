package com.n26.statistics.persistence;

import com.n26.statistics.exceptions.InvalidTimestampException;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MILLIS;

public class AtomicTransactionStorage<T> implements TransactionStorage<T> {

    private final Supplier<Long> currentMillis;
    /**
     * This supplies transaction amount {@code Double}.
     * Initially initialized to 0.0.
     */
    private final Supplier<T> factory;

    private final TemporalUnit targetUnit;
    private final TemporalUnit groupingUnit;
    /**
     * The array buffer into which transaction amount of the transactions are stored.
     * Capacity is initialized to 64.
     * {@link AtomicReferenceArray} is thread safe, initializes to {@code null}.
     */
    private final AtomicReferenceArray<Reference<T>> dataStore;

    private AtomicTransactionStorage(Supplier<T> factory) {
        this(ChronoUnit.MINUTES,
                ChronoUnit.SECONDS,
                64, factory, System::currentTimeMillis);
    }

    public AtomicTransactionStorage(TemporalUnit targetUnit,
                                    TemporalUnit groupingUnit,
                                    int bufferSize,
                                    Supplier<T> factory,
                                    Supplier<Long> currentMillis) {
        this.currentMillis = currentMillis;
        this.factory = factory;
        this.targetUnit = targetUnit;
        this.groupingUnit = groupingUnit;
        this.dataStore = new AtomicReferenceArray<>(bufferSize);
    }

    public static <T> AtomicTransactionStorage<T> initialize(Supplier<T> factory) {
        return new AtomicTransactionStorage<>(
                factory);
    }

    @Override
    public void update(long timestamp, UnaryOperator<T> updater) {
        getReference(timestamp).update(updater);
    }


    @Override
    public T getStatistics(BinaryOperator<T> reducer) {

        return getReferenceStream().reduce(factory.get(), reducer);
    }

    /**
     * Return the new {@link Reference<T>} object for received timestamp
     * or stored object from {@link AtomicReferenceArray}
     *
     * @param timestamp the transaction timestamp
     * @return {@link Reference<T>}
     */
    public Reference<T> getReference(long timestamp) {
        int index = checkedIndexFor(timestamp);
        int offset = offset(index);
        return dataStore.updateAndGet(offset, value -> actual(index, value));
    }

    /**
     * Calculates upperIndex and lowerIndex within last 60 seconds.
     * maps the {@link AtomicReference} from {@link AtomicReferenceArray}, filters {@code null} objects
     * build a stream of transaction amount.
     *
     * @return {@link Stream<T>}
     */
    private Stream<T> getReferenceStream() {

        long now = this.currentMillis.get();

        int firstIndex = lowerIndexFor(now);
        int lastIndex = currentIndexFor(now);

        return IntStream.rangeClosed(firstIndex, lastIndex)
                .mapToObj(index -> historical(index, dataStore.get(offset(index))))
                .filter(Objects::nonNull)
                .map(Reference::getValue);
    }

    /**
     * Method checks for {@link Reference<T>} object with specified index or {@code null}.
     *
     * @param index     {@link AtomicReferenceArray} index.
     * @param reference object.
     * @return the result either {@code null} & {@link Reference<T>}
     */
    private Reference<T> historical(int index, @Nullable Reference<T> reference) {
        return reference != null && reference.getIndex() == index ? reference : null;
    }

    /**
     * @param index for {@link AtomicReferenceArray}
     * @param value of {@link Reference<T>}
     * @return either new {@code  new Reference<>(index, factory.get())} or current stored value.
     */
    private Reference<T> actual(int index, @Nullable Reference<T> value) {
        return value == null || value.getIndex() < index ? new Reference<>(index, factory.get()) : value;
    }

    /**
     * Calculate the index withing {@code dataStore} range.
     *
     * @param index the index in seconds
     * @return the result.
     */
    private int offset(int index) {
        return index % dataStore.length();
    }

    /**
     * Return seconds for received transaction timestamp.
     *
     * @param timestamp the transaction timestamp received.
     * @return the result.
     */
    private int currentIndexFor(long timestamp) {
        return (int) Duration.of(timestamp, MILLIS).get(groupingUnit);
    }

    /**
     * Return seconds for received transaction after subtracting one minute.
     *
     * @param timestamp the transaction timestamp received.
     * @return the result.
     */
    private int lowerIndexFor(long timestamp) {
        return (int) Duration.of(timestamp, MILLIS).minus(1, targetUnit).get(groupingUnit);
    }

    /**
     * Return the index if its valid i.e within the limit of {@code lowerIndex} and {@code upperIndex}.
     * Checks for {@link InvalidTimestampException}
     *
     * @param timestamp the transaction timestamp
     * @return the result
     */
    private int checkedIndexFor(long timestamp) {
        long now = this.currentMillis.get();

        int lowerIndex = lowerIndexFor(now);
        int upperIndex = currentIndexFor(now);

        int index = currentIndexFor(timestamp);

        InvalidTimestampException.check(index >= lowerIndex, "Old Timestamp");
        InvalidTimestampException.check(index <= upperIndex, "Young Timestamp");

        return index;
    }

    /**
     * Inner class wraps transaction amount in {@link AtomicReference} a thread safe.
     * every {@link AtomicReferenceArray} element is this class object with transaction amount and its
     * index in the array.
     *
     * @param <E>
     */
    protected static class Reference<E> {
        private final long index;
        private final AtomicReference<E> value;

        public Reference(long index, E value) {
            this.index = index;
            this.value = new AtomicReference<>(value);
        }

        public void update(UnaryOperator<E> updater) {
            value.updateAndGet(updater);
        }

        public E getValue() {
            return value.get();
        }

        public long getIndex() {
            return index;
        }
    }
}
