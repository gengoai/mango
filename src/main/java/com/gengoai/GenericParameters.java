package com.gengoai;

import com.gengoai.conversion.Cast;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author David B. Bracewell
 */
public class GenericParameters implements Parameters, Serializable {
   private static final long serialVersionUID = 1L;
   private final Map<String, Object> parameters = new HashMap<>();

   @Override
   public Parameters set(String name, Object value) {
      parameters.put(name.toUpperCase(), value);
      return this;
   }

   @Override
   public Parameters copy() {
      GenericParameters toReturn = new GenericParameters();
      getParameters().forEach(toReturn::set);
      return toReturn;
   }

   @Override
   public Object get(String name) {
      return parameters.get(name.toUpperCase());
   }

   @Override
   public <E> E getOrDefault(String name, E defaultValue) {
      return Cast.as(parameters.getOrDefault(name.toUpperCase(), defaultValue));
   }

   @Override
   public boolean contains(String name) {
      return parameters.containsKey(name.toUpperCase());
   }

   @Override
   public Map<String, Object> getParameters() {
      return Collections.unmodifiableMap(parameters);
   }

}//END OF ParameterMap
