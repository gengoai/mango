package com.davidbracewell.stream.accumulator;

import com.davidbracewell.function.SerializableSupplier;
import lombok.NonNull;

import java.util.Map;

/**
 * @author David B. Bracewell
 */
public class MapAccumulatable<K, V> implements Accumulatable<Map<K, V>> {
  private static final long serialVersionUID = 1L;
  final SerializableSupplier<Map<K, V>> supplier;

  public MapAccumulatable(@NonNull SerializableSupplier<Map<K, V>> supplier) {
    this.supplier = supplier;
  }

  @Override
  public Map<K, V> addInPlace(Map<K, V> t1, Map<K, V> t2) {
    if (t1 == null) {
      t1 = supplier.get();
    }
    t1.putAll(t2);
    return t1;
  }

  @Override
  public Map<K, V> addAccumulator(Map<K, V> t1, Map<K, V> t2) {
    Map<K, V> map = supplier.get();
    map.putAll(t1);
    map.putAll(t2);
    return map;
  }

  @Override
  public Map<K, V> zero(Map<K, V> zeroValue) {
    if (zeroValue == null) {
      return supplier.get();
    }
    return zeroValue;
  }

}// END OF MapAccumulatable
