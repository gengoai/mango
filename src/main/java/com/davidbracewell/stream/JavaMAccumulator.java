package com.davidbracewell.stream;

import com.google.common.util.concurrent.AtomicDouble;

import java.io.Serializable;

/**
 * The type Java m accumulator.
 *
 * @author David B. Bracewell
 */
public class JavaMAccumulator implements MAccumulator, Serializable {

  private final String name;
  private final AtomicDouble accumulator;

  /**
   * Instantiates a new Java m accumulator.
   *
   * @param name the name
   */
  public JavaMAccumulator(String name) {
    this(name, 0d);
  }

  /**
   * Instantiates a new Java m accumulator.
   *
   * @param name         the name
   * @param initialValue the initial value
   */
  public JavaMAccumulator(String name, double initialValue) {
    this.name = name;
    this.accumulator = new AtomicDouble(initialValue);
  }

  @Override
  public void increment(double amount) {
    accumulator.addAndGet(amount);
  }

  @Override
  public void decrement(double amount) {
    accumulator.addAndGet(-amount);
  }

  @Override
  public double value() {
    return accumulator.get();
  }

  @Override
  public void setValue(double value) {
    accumulator.set(value);
  }

  @Override
  public String name() {
    return name;
  }
}// END OF JavaMAccumulator
