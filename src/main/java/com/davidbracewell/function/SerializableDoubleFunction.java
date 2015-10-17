
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.DoubleFunction;

@FunctionalInterface
public interface SerializableDoubleFunction<R> extends DoubleFunction<R>, Serializable {

}//END OF SerializableDoubleFunction
