package com.davidbracewell.stream.accumulator.v2;

import lombok.NonNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author David B. Bracewell
 */
public class LocalLongAccumulator implements MLongAccumulator {
  private static final long serialVersionUID = 1L;
  private final String name;
  private final AtomicLong longValue;

  public LocalLongAccumulator() {
    this(0, null);
  }

  public LocalLongAccumulator(String name) {
    this(0L, name);
  }

  public LocalLongAccumulator(long longValue) {
    this(longValue, null);
  }

  public LocalLongAccumulator(long longValue, String name) {
    this.name = name;
    this.longValue = new AtomicLong(longValue);
  }


  @Override
  public void add(long value) {
    longValue.addAndGet(value);
  }

  @Override
  public Long value() {
    return longValue.longValue();
  }

  @Override
  public void merge(@NonNull MAccumulator<Long, Long> other) {
    longValue.addAndGet(other.value());
  }

  @Override
  public Optional<String> name() {
    return Optional.of(name);
  }

  @Override
  public void reset() {
    longValue.set(0);
  }
}// END OF LocalMLongAccumulator
