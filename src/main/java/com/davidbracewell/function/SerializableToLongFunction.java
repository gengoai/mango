
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ToLongFunction;

@FunctionalInterface
public interface SerializableToLongFunction<T> extends ToLongFunction<T>, Serializable {

}//END OF SerializableToLongFunction
