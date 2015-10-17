
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongBinaryOperator;

@FunctionalInterface
public interface CheckedLongBinaryOperator extends Serializable {

	long apply(long t, long u) throws Throwable;

}//END OF CheckedLongBinaryOperator
