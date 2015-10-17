
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.BinaryOperator;

@FunctionalInterface
public interface CheckedBinaryOperator<T> extends Serializable {

	T apply(T t, T u) throws Throwable;

}//END OF CheckedBinaryOperator
