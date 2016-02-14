package com.davidbracewell.stream;

/**
 * The interface M accumulator.
 *
 * @author David B. Bracewell
 */
public interface MAccumulator {

  /**
   * Increment.
   */
  default void increment() {
    increment(1d);
  }

  /**
   * Decrement.
   */
  default void decrement() {
    decrement(1d);
  }

  default void incrementIf(boolean result) {
    if (result) {
      increment(1d);
    }
  }

  default void decrementIf(boolean result) {
    if (result) {
      decrement(1d);
    }
  }

  default void incrementUnless(boolean result) {
    if (!result) {
      increment(1d);
    }
  }

  default void decrementUnless(boolean result) {
    if (!result) {
      decrement(1d);
    }
  }

  /**
   * Increment.
   *
   * @param amount the amount
   */
  void increment(double amount);

  /**
   * Decrement.
   *
   * @param amount the amount
   */
  void decrement(double amount);

  /**
   * Value double.
   *
   * @return the double
   */
  double value();

  /**
   * Sets value.
   *
   * @param value the value
   */
  void setValue(double value);


  String name();


}// END OF MAccumulator
