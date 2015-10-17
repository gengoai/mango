
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ObjDoubleConsumer;

@FunctionalInterface
public interface SerializableObjDoubleConsumer<T> extends ObjDoubleConsumer<T>, Serializable {

}//END OF SerializableObjDoubleConsumer
