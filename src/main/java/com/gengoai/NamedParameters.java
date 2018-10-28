package com.gengoai;

import com.gengoai.collection.Iterators;
import com.gengoai.conversion.Cast;
import com.gengoai.conversion.Converter;
import com.gengoai.conversion.TypeConversionException;
import com.gengoai.json.JsonEntry;
import com.gengoai.json.JsonSerializable;
import com.gengoai.reflection.Types;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;

import static com.gengoai.Validation.checkArgument;
import static com.gengoai.reflection.Types.asClass;

/**
 * <p>Object representation of named parameters. Assuming, we have defined an enum <Code>MyFunctionParams</Code> that
 * is to be used by <code>myFunction</code>:</p>
 * <pre>
 * {@code
 *    myFunction(params(HEIGHT, 20,
 *                      WIDTH, 100,
 *                      DEPTH, 40));
 * }
 * </pre>
 * <p>The elements of the <Code>MyFunctionParams</Code> enum define the default values for the parameters allowing the
 * parameters to be optional. This class is an alternative to the builder pattern.</p>
 *
 * @param <K> the type parameter
 * @author David B. Bracewell
 */
public final class NamedParameters<K extends Enum<K> & NamedParameters.Value> implements Iterable<K>, Copyable<NamedParameters<K>>, JsonSerializable, Serializable {
   private static final long serialVersionUID = 1L;
   private final EnumMap<K, Object> parameters;

   /**
    * Static method for easily constructing a set of parameters
    *
    * @param <K>   the parameter type parameter
    * @param key   the parameter name
    * @param value the parameter value
    * @param other the other name value pairs to set. Their should be an even number of other with odd even entries
    *              being names and odd entries values.
    * @return the parameters
    */
   public static <K extends Enum<K> & Value> NamedParameters<K> params(K key, Object value, Object... other) {
      final Class<K> keyClass = key.getDeclaringClass();
      NamedParameters<K> toReturn = new NamedParameters<>(keyClass);
      toReturn.set(key, value);
      checkArgument(other.length % 2 == 0, "Must have an even number of arguments");
      for (int i = 0; i < other.length; i += 2) {
         toReturn.set(Cast.as(other[i], keyClass), other[i + 1]);
      }
      return toReturn;
   }

   private NamedParameters(Class<K> keyClass) {
      this.parameters = new EnumMap<>(keyClass);
   }

   private NamedParameters(EnumMap<K, Object> map) {
      this.parameters = map;
   }

   @Override
   public NamedParameters<K> copy() {
      return new NamedParameters<>(parameters.clone());
   }

   /**
    * Gets the value (or its default) for the given parameter name silently casting it as type <code>T</code>.
    *
    * @param <T>  the type parameter
    * @param name the parameter name
    * @return the parameter value
    */
   public <T> T get(K name) {
      return Cast.as(parameters.getOrDefault(name, name.defaultValue()));
   }

   /**
    * Gets the value (or its default) for the given parameter name converting it as type <code>T</code>.
    *
    * @param <T>   the type parameter
    * @param name  the parameter name
    * @param clazz the clazz
    * @return the parameter value
    */
   public <T> T get(K name, Class<T> clazz) {
      try {
         return Converter.convert(parameters.getOrDefault(name, name.defaultValue()), clazz);
      } catch (TypeConversionException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Gets the value of the given parameter as an int
    *
    * @param name the parameter name
    * @return the parameter value or default
    */
   public int getInt(K name) {
      return get(name);
   }

   /**
    * Gets the value of the given parameter as a float
    *
    * @param name the parameter name
    * @return the parameter value or default
    */
   public float getFloat(K name) {
      return get(name);
   }

   /**
    * Gets the value of the given parameter as a double
    *
    * @param name the parameter name
    * @return the parameter value or default
    */
   public double getDouble(K name) {
      return get(name);
   }

   /**
    * Gets the value of the given parameter as a boolean
    *
    * @param name the parameter name
    * @return the parameter value or default
    */
   public boolean getBoolean(K name) {
      return get(name);
   }

   /**
    * Gets the value of the given parameter as a character
    *
    * @param name the parameter name
    * @return the parameter value or default
    */
   public char getCharacter(K name) {
      return get(name);
   }

   /**
    * Gets the value of the given parameter as a String
    *
    * @param name the parameter name
    * @return the parameter value or default
    */
   public String getString(K name) {
      return get(name);
   }

   /**
    * Gets the value for the given parameter name or returns the default value if not set.
    *
    * @param <T>   the parameter type parameter
    * @param name  the parameter name
    * @param value the default parameter value
    * @return the parameter value or default
    */
   public <T> T getOrDefault(K name, T value) {
      return Cast.as(parameters.getOrDefault(name, value));
   }


   /**
    * Checks if the given parameter name was explicitly set, i.e. by {@link #set(Enum, Object)}
    *
    * @param name the parameter name
    * @return True it was explicitly set, False otherwise
    */
   public boolean isSet(K name) {
      return parameters.containsKey(name);
   }

   /**
    * Gets the set parameters as map
    *
    * @return the map of set parameters
    */
   public Map<K, Object> asMap() {
      return Collections.unmodifiableMap(parameters);
   }

   @Override
   public Iterator<K> iterator() {
      return Iterators.unmodifiableIterator(parameters.keySet().iterator());
   }

   /**
    * Sets the value of the given parameter name. Note that null values will result in the default value being returned
    * on subsequent {@link #get(Enum)} calls.
    *
    * @param name  the parameter name
    * @param value the parameter value
    * @return This set of parameters
    */
   public NamedParameters<K> set(K name, Object value) {
      if (value == null) {
         parameters.remove(name);
         return this;
      }
      checkArgument(
         Types.isAssignable(Primitives.wrap(asClass(name.getValueType())), Primitives.wrap(value.getClass())),
         () -> "Illegal Argument: " + value.getClass() + " is not of type " + name.getValueType());
      parameters.put(name, value);
      return this;
   }

   /**
    * Sets the value for each parameter in the map
    *
    * @param values the parameter names and their values to set
    * @return This set of parameters
    */
   public NamedParameters<K> setAll(Map<K, Object> values) {
      values.forEach(this::set);
      return this;
   }

   @Override
   public JsonEntry toJson() {
      JsonEntry object = JsonEntry.object();
      asMap().forEach((k, v) -> object.addProperty(k.toString(), v));
      return object;
   }

   /**
    * Static method to construct a parameter set from a json entry and key class.
    *
    * @param <K>   the key type parameter
    * @param entry the json entry to parse
    * @param types the parameter types
    * @return the parameter set
    */
   public static <K extends Enum<K> & Value> NamedParameters<K> fromJson(JsonEntry entry, Type... types) {
      Class<K> keyClass = Cast.as(asClass(types[0]));
      NamedParameters<K> parameters = new NamedParameters<>(keyClass);
      entry.propertyIterator()
           .forEachRemaining(e -> {
              K key = Enum.valueOf(keyClass, e.getKey());
              Object value = e.getValue().getAs(key.getValueType());
              parameters.set(key, value);
           });
      return Cast.as(parameters);
   }

   @Override
   public String toString() {
      return asMap().toString();
   }

   @Override
   public int hashCode() {
      return Objects.hash(parameters);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {return true;}
      if (obj == null || getClass() != obj.getClass()) {return false;}
      final NamedParameters other = (NamedParameters) obj;
      return Objects.equals(this.parameters, other.parameters);
   }

   /**
    * Interface for use with {@link NamedParameters} for defining the default value and value types of named
    * parameters.
    *
    * @author David B. Bracewell
    */
   public interface Value {


      /**
       * Gets value type.
       *
       * @return the value type
       */
      Type getValueType();

      /**
       * Default value for the parameter
       *
       * @return the default value (null by default)
       */
      default Object defaultValue() {
         return null;
      }

   }//END OF ParameterName
}//END OF Parameters
