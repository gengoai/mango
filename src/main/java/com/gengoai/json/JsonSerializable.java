package com.gengoai.json;

import com.gengoai.reflection.BeanMap;

/**
 * <p>Interface defining that an object is capable of being serialized to a <code>JsonElement</code>. It is expected
 * that objects implementing this interface also implement a static <code>fromJson(JsonElement)</code> method in order
 * to deserialize the object from json.</p>
 *
 * @author David B. Bracewell
 */
public interface JsonSerializable {

   /**
    * To json json entry.
    *
    * @return the json entry
    */
   default JsonEntry toJson() {
      return JsonEntry.from(new BeanMap(this));
   }

   /**
    * From json t.
    *
    * @param <T>   the type parameter
    * @param entry the entry
    * @return the t
    */
   static <T> T fromJson(JsonEntry entry) {
      throw new IllegalStateException("Not Implemented");
   }


}//END OF JsonSerializable
