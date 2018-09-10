package com.gengoai;

import com.gengoai.collection.Iterators;
import com.gengoai.conversion.Cast;
import com.gengoai.conversion.Convert;
import com.gengoai.json.JsonEntry;
import com.gengoai.json.JsonSerializable;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import static com.gengoai.Validation.checkArgument;

/**
 * The interface Parameters.
 *
 * @param <K> the type parameter
 * @author David B. Bracewell
 */
public final class Parameters<K extends Enum<K> & ValueTypeInformation> implements Iterable<K>, Copyable<Parameters<K>>, JsonSerializable, Serializable {
   private static final long serialVersionUID = 1L;
   private final Class<K> keyClass;
   private final EnumMap<K, Object> parameters;


   public static <K extends Enum<K> & ValueTypeInformation> Parameters<K> params(K key, Object value, Object... other) {
      Parameters<K> toReturn = new Parameters<>(key.getDeclaringClass());
      toReturn.set(key, value);
      checkArgument(other.length % 2 == 0, "Must have an even number of arguments");
      for (int i = 0; i < other.length; i += 2) {
         toReturn.set(Cast.as(other[i]), other[i + 1]);
      }
      return toReturn;
   }

   public Parameters(Class<K> keyClass) {
      this.keyClass = keyClass;
      this.parameters = new EnumMap<>(keyClass);
   }

   public Parameters<K> copy() {
      return new Parameters<>(keyClass).setAll(parameters);
   }

   public <T> T get(K name) {
      return Cast.as(parameters.getOrDefault(name, name.defaultValue()));
   }

   public <T> T get(K name, Class<T> clazz) {
      return Convert.convert(parameters.getOrDefault(name, name.defaultValue()), clazz);
   }

   public int getInt(K name) {
      return get(name);
   }

   public float getFloat(K name) {
      return get(name);
   }

   public double getDouble(K name) {
      return get(name);
   }

   public boolean getBoolean(K name) {
      return get(name);
   }

   public char getCharacter(K name) {
      return get(name);
   }

   public String getString(K name) {
      return get(name);
   }

   public <T> T getOrDefault(K name, T value) {
      return Cast.as(parameters.getOrDefault(name, value));
   }


   public boolean contains(K name) {
      return parameters.containsKey(name);
   }

   public Map<K, Object> asMap() {
      return Collections.unmodifiableMap(parameters);
   }

   public Iterator<K> iterator() {
      return Iterators.unmodifiableIterator(parameters.keySet().iterator());
   }

   public Parameters<K> set(K name, Object value) {
      if (value == null) {
         parameters.remove(name);
         return this;
      }
      parameters.put(name, value);
      return this;
   }

   public Parameters<K> setAll(Map<K, Object> values) {
      values.forEach(this::set);
      return this;
   }

   public Class<K> getKeyClass() {
      return keyClass;
   }

   @Override
   public JsonEntry toJson() {
      JsonEntry object = JsonEntry.object();
      object.addProperty("key", getKeyClass().getName());
      JsonEntry params = JsonEntry.object();
      asMap().forEach((k, v) -> params.addProperty(k.toString(), v));
      object.addProperty("parameters", params);
      return object;
   }

   /**
    * From json e.
    *
    * @param <K>   the type parameter
    * @param entry the entry
    * @return the e
    */
   static <K extends Enum<K> & ValueTypeInformation> Parameters<K> fromJson(JsonEntry entry) {
      Class<K> keyClass = Cast.as(entry.getValProperty("key").asClass());
      Parameters<K> parameters = new Parameters<>(keyClass);
      entry.getProperty("parameters")
           .propertyIterator()
           .forEachRemaining(e -> {
              K key = Enum.valueOf(keyClass, e.getKey());
              Object value = e.getValue().getAs(key.getValueType());
              parameters.set(key, value);
           });
      return Cast.as(parameters);
   }

}//END OF Parameters
