package com.davidbracewell.stream;

import lombok.NonNull;
import org.apache.spark.Accumulator;

import java.io.Serializable;

/**
 * @author David B. Bracewell
 */
public class SparkMAccumulator implements MAccumulator, Serializable {

  private final Accumulator<Double> accumulator;


  public SparkMAccumulator(@NonNull Accumulator<Double> accumulator) {
    this.accumulator = accumulator;
  }

  @Override
  public void increment(double amount) {
    accumulator.add(amount);
  }

  @Override
  public void decrement(double amount) {
    accumulator.add(-amount);
  }

  @Override
  public double value() {
    return accumulator.localValue();
  }

  @Override
  public void setValue(double value) {
    accumulator.setValue(value);
  }

  @Override
  public String name() {
    return accumulator.name().get();
  }
}// END OF SparkMAccumulator
