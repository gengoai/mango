
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntPredicate;

@FunctionalInterface
public interface SerializableIntPredicate extends IntPredicate, Serializable {

}//END OF SerializableIntPredicate
