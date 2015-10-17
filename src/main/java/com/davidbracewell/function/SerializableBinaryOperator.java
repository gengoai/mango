
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.BinaryOperator;

@FunctionalInterface
public interface SerializableBinaryOperator<T> extends BinaryOperator<T>, Serializable {

}//END OF SerializableBinaryOperator
