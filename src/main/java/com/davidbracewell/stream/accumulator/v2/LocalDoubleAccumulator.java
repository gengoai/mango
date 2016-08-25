package com.davidbracewell.stream.accumulator.v2;

import lombok.NonNull;

import java.util.Optional;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * @author David B. Bracewell
 */
public class LocalDoubleAccumulator implements MDoubleAccumulator {
  private static final long serialVersionUID = 1L;
  private final String name;
  private final DoubleAdder value;

  public LocalDoubleAccumulator(double value) {
    this(value, null);
  }

  public LocalDoubleAccumulator(double value, String name) {
    this.name = name;
    this.value = new DoubleAdder();
    this.value.add(value);
  }

  @Override
  public Double value() {
    return value.doubleValue();
  }

  @Override
  public void merge(@NonNull MAccumulator<Double, Double> other) {
    add(other.value());
  }

  @Override
  public Optional<String> name() {
    return Optional.of(name);
  }

  @Override
  public void reset() {
    value.reset();
  }

  @Override
  public void add(double value) {
    this.value.add(value);
  }

}// END OF LocalMDoubleAccumulator
