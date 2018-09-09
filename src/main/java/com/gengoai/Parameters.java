package com.gengoai;

import com.gengoai.conversion.Cast;

import java.util.Map;

/**
 * @author David B. Bracewell
 */
public interface Parameters extends Copyable<Parameters> {

   boolean contains(String name);

   Object get(String name);

   default <T> T getAs(String name, Class<T> tClass) {
      return Cast.as(name, tClass);
   }

   default <T> T getAs(String name) {
      return Cast.as(name);
   }

   default boolean getBoolean(String name) {
      return Cast.as(get(name));
   }

   default Number getNumber(String name) {
      return Cast.as(get(name));
   }

   <E> E getOrDefault(String name, E defaultValue);

   Map<String, Object> getParameters();

   default String getString(String name) {
      return Cast.as(get(name));
   }

   Parameters set(String name, Object value);

}//END OF ParameterMap
