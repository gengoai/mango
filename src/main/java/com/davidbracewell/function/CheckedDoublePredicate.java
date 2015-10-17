
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.DoublePredicate;

@FunctionalInterface
public interface CheckedDoublePredicate extends Serializable {

	boolean test(double t) throws Throwable;

}//END OF CheckedDoublePredicate
