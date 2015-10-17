
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ToLongBiFunction;

@FunctionalInterface
public interface SerializableToLongBiFunction<T,U> extends ToLongBiFunction<T,U>, Serializable {

}//END OF SerializableToLongBiFunction
