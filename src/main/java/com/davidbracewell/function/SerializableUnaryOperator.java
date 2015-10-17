
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface SerializableUnaryOperator<T> extends UnaryOperator<T>, Serializable {

}//END OF SerializableUnaryOperator
