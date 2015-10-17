
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface SerializableToIntFunction<T> extends ToIntFunction<T>, Serializable {

}//END OF SerializableToIntFunction
