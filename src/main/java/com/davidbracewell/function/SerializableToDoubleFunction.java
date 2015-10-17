
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface SerializableToDoubleFunction<T> extends ToDoubleFunction<T>, Serializable {

}//END OF SerializableToDoubleFunction
