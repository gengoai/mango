package com.davidbracewell.stream.accumulator.v2;


import java.io.Serializable;
import java.util.Optional;

/**
 * @author David B. Bracewell
 */
public interface MAccumulator<IN, OUT> extends Serializable {

  void add(IN in);

  OUT value();

  void merge(MAccumulator<IN, OUT> other);

  Optional<String> name();

  void reset();

}// END OF MAcc
