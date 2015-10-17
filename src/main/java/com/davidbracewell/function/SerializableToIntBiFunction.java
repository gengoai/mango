
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ToIntBiFunction;

@FunctionalInterface
public interface SerializableToIntBiFunction<T,U> extends ToIntBiFunction<T,U>, Serializable {

}//END OF SerializableToIntBiFunction
