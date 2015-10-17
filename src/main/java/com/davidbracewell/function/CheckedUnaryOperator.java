
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface CheckedUnaryOperator<T> extends Serializable {

	T apply(T t) throws Throwable;

}//END OF CheckedUnaryOperator
