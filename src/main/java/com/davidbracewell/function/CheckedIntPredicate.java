
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntPredicate;

@FunctionalInterface
public interface CheckedIntPredicate extends Serializable {

	boolean test(int t) throws Throwable;

}//END OF CheckedIntPredicate
