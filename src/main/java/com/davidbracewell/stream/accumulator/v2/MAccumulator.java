package com.davidbracewell.stream.accumulator.v2;


import java.io.Serializable;
import java.util.Optional;

/**
 * The interface M accumulator.
 *
 * @param <IN>  the type parameter
 * @param <OUT> the type parameter
 * @author David B. Bracewell
 */
public interface MAccumulator<IN, OUT> extends Serializable {

  /**
   * Add.
   *
   * @param in the in
   */
  void add(IN in);

  /**
   * Value out.
   *
   * @return the out
   */
  OUT value();

  /**
   * Merge.
   *
   * @param other the other
   */
  void merge(MAccumulator<IN, OUT> other);

  /**
   * Name optional.
   *
   * @return the optional
   */
  Optional<String> name();

  /**
   * Reset.
   */
  void reset();

}// END OF MAcc
