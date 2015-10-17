
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.DoubleConsumer;

@FunctionalInterface
public interface SerializableDoubleConsumer extends DoubleConsumer, Serializable {

}//END OF SerializableDoubleConsumer
