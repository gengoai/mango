
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.DoublePredicate;

@FunctionalInterface
public interface SerializableDoublePredicate extends DoublePredicate, Serializable {

}//END OF SerializableDoublePredicate
