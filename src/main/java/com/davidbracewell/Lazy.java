package com.davidbracewell;

import com.davidbracewell.function.SerializableSupplier;
import lombok.NonNull;

import java.io.Serializable;

/**
 * <p>Lazily create a value in a thread safe manner.</p>
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public final class Lazy<T> implements Serializable {
  private static final long serialVersionUID = 1L;
  private volatile T object;
  private volatile SerializableSupplier<? extends T> supplier;

  /**
   * Instantiates a new Lazy.
   *
   * @param supplier the supplier used to generate the value
   */
  public Lazy(@NonNull SerializableSupplier<? extends T> supplier) {
    this.supplier = supplier;
  }

  /**
   * Gets the value lazily
   *
   * @return the value
   */
  public T get() {
    T value = object;
    if (value == null) {
      synchronized (this) {
        value = object;
        if (object == null) {
          object = value = supplier.get();
        }
      }
    }
    return value;
  }

}// END OF Lazy
