package com.gengoai;

import com.gengoai.function.SerializableSupplier;
import lombok.NonNull;

import java.io.Serializable;

/**
 * <p>Lazily create a value in a thread safe manner. Common usage is as follows:</p>
 * <pre>
 * {@code
 *    //Declare a lazy initialized object.
 *    Lazy<ExpensiveObject> lazy = new Lazy(() -> createExpensiveObject());
 *
 *    //Now we will actually create the object.
 *    lazy.get().operation();
 *
 *    //Successive calls will use the already created object.
 *    lazy.get().operation_2();
 * }
 * </pre>
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public final class Lazy<T> implements Serializable {
  private static final long serialVersionUID = 1L;
  private volatile T object;
  private volatile SerializableSupplier<? extends T> supplier;

  /**
   * Instantiates a new Lazy created object.
   *
   * @param supplier the supplier used to create the object
   */
  public Lazy(@NonNull SerializableSupplier<? extends T> supplier) {
    this.supplier = supplier;
  }

  /**
   * <p>Gets or creates the object as needed.</p>
   *
   * @return the object wrapped by Lazy
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
