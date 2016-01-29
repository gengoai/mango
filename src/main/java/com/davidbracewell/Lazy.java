package com.davidbracewell;

import com.davidbracewell.function.SerializableSupplier;
import lombok.NonNull;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * @author David B. Bracewell
 */
public final class Lazy<T> implements Serializable {
  private static final long serialVersionUID = 1L;
  private volatile T lazyObject = null;
  private volatile SerializableSupplier<T> supplier;

  public Lazy(@NonNull SerializableSupplier<T> supplier) {
    this.supplier = supplier;
  }

  public T get() {
    final T result = lazyObject;
    if (result == null) {
      return compute(supplier);
    }
    return result;
  }


  private synchronized T compute(Supplier<T> supplier) {
    if (lazyObject == null) {
      lazyObject = supplier.get();
    }
    return lazyObject;
  }


}// END OF Lazy
