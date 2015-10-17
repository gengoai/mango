
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongUnaryOperator;

@FunctionalInterface
public interface CheckedLongUnaryOperator extends Serializable {

	long applyAsLong(long t) throws Throwable;

}//END OF CheckedLongUnaryOperator
