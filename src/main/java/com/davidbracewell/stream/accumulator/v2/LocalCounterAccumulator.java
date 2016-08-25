package com.davidbracewell.stream.accumulator.v2;

import com.davidbracewell.collection.counter.Counter;
import com.davidbracewell.collection.counter.Counters;
import com.davidbracewell.collection.counter.HashMapCounter;
import lombok.NonNull;

import java.util.Optional;

/**
 * @author David B. Bracewell
 */
public class LocalCounterAccumulator<T> implements MCounterAccumulator<T> {
  private static final long serialVersionUID = 1L;
  private final String name;
  private final Counter<T> counter = Counters.synchronizedCounter(new HashMapCounter<T>());


  public LocalCounterAccumulator() {
    this(null);
  }


  public LocalCounterAccumulator(String name) {
    this.name = name;
  }

  @Override
  public void add(T t) {
    counter.increment(t);
  }

  @Override
  public Counter<T> value() {
    return counter;
  }

  @Override
  public void merge(@NonNull MAccumulator<T, Counter<T>> other) {
    counter.merge(other.value());
  }

  @Override
  public Optional<String> name() {
    return Optional.ofNullable(name);
  }

  @Override
  public void reset() {
    counter.clear();
  }

}// END OF LocalCounterAccumulator
