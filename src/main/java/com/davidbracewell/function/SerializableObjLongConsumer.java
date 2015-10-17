
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ObjLongConsumer;

@FunctionalInterface
public interface SerializableObjLongConsumer<T> extends ObjLongConsumer<T>, Serializable {

}//END OF SerializableObjLongConsumer
