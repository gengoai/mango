package com.davidbracewell.stream.accumulator.v2;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author David B. Bracewell
 */
public class LocalListAccumulator<T> implements MListAccumulator<T> {
  private static final long serialVersionUID = 1L;
  private final List<T> list = new CopyOnWriteArrayList<>();
  private final String name;

  public LocalListAccumulator() {
    this(null);
  }

  public LocalListAccumulator(String name) {
    this.name = name;
  }

  @Override
  public void add(T t) {
    list.add(t);
  }

  @Override
  public List<T> value() {
    return list;
  }

  @Override
  public void merge(MAccumulator<T, List<T>> other) {
    if (other != null) {
      this.list.addAll(other.value());
    }
  }

  @Override
  public Optional<String> name() {
    return Optional.of(name);
  }

  @Override
  public void reset() {
    this.list.clear();
  }


}// END OF LocalMListAccumulator
