package com.gengoai.json;

import com.gengoai.collection.Iterables;
import com.gengoai.collection.Iterators;
import com.gengoai.collection.Lists;
import com.gengoai.collection.Sets;
import com.gengoai.conversion.Cast;
import com.gengoai.conversion.Convert;
import com.gengoai.conversion.Val;
import com.gengoai.reflection.Reflect;
import com.google.gson.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.gengoai.Validation.checkState;
import static com.gengoai.tuple.Tuples.$;

/**
 * <p>A convenience wrapper around <code>JsonElement</code> allowing a single interface for objects, arrays, and
 * primitives that understands {@link JsonSerializable} objects</p>
 */
public class JsonEntry {
   private static final Gson gson = new Gson();
   private final JsonElement element;

   private JsonEntry(JsonElement element) {
      this.element = element;
   }

   /**
    * Creates a new empty array
    *
    * @return the json entry
    */
   public static JsonEntry array() {
      return new JsonEntry(new JsonArray());
   }

   /**
    * Creates an entry from the given object. This method is {@link JsonSerializable} aware and will also correctly
    * handle serializable values in maps, arrays, and collections.
    *
    * @param v the value to create the entry from.
    * @return the json entry
    */
   public static JsonEntry from(Object v) {
      return new JsonEntry(toElement(v));
   }

   private static JsonElement iterableToElement(Iterable<?> iterable) {
      JsonArray array = new JsonArray();
      if (iterable != null) {
         iterable.forEach(item -> array.add(from(item).element));
      }
      return array;
   }

   private static JsonElement mapToElement(Map<?, ?> map) {
      JsonObject obj = new JsonObject();
      if (map != null) {
         map.forEach((k, v) -> obj.add(Convert.convert(k, String.class), from(v).element));
      }
      return obj;
   }

   /**
    * Creates a null valued entry
    *
    * @return the null valued entry
    */
   public static JsonEntry nullValue() {
      return new JsonEntry(JsonNull.INSTANCE);
   }

   /**
    * Creates a new empty object
    *
    * @return the json entry
    */
   public static JsonEntry object() {
      return new JsonEntry(new JsonObject());
   }

   private static JsonElement toElement(Object v) {
      if (v == null) {
         return JsonNull.INSTANCE;
      } else if (v instanceof JsonEntry) {
         return Cast.<JsonEntry>as(v).element;
      } else if (v instanceof Boolean) {
         return new JsonPrimitive((Boolean) v);
      } else if (v instanceof Number) {
         return new JsonPrimitive((Number) v);
      } else if (v instanceof Character) {
         return new JsonPrimitive((Character) v);
      } else if (v instanceof JsonSerializable) {
         return Cast.<JsonSerializable>as(v).toJson().getElement();
      } else if (v instanceof Map) {
         return mapToElement(Cast.<Map<?, ?>>as(v));
      } else if (v instanceof Iterable) {
         return iterableToElement(Cast.<Iterable<?>>as(v));
      } else if (v instanceof Iterator) {
         return iterableToElement(Iterables.asIterable(Cast.as(v)));
      } else if (v.getClass().isArray() && v.getClass().getComponentType().isPrimitive()) {
         return gson.toJsonTree(v);
      } else if (v.getClass().isArray()) {
         return iterableToElement(Arrays.asList((Object[]) v));
      }
      return gson.toJsonTree(v);
   }

   /**
    * Adds a property to the entry checking that it is a json object
    *
    * @param name  the property name
    * @param value the property value
    * @return this json entry
    * @throws IllegalStateException if the entry's element is not a json object
    */
   public JsonEntry addProperty(String name, Object value) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      element.getAsJsonObject().add(name, toElement(value));
      return this;
   }

   /**
    * Adds a value to the entry checking that it is a json array
    *
    * @param value the value
    * @return this json entry
    * @throws IllegalStateException if the entry's element is not a json array
    */
   public JsonEntry addValue(Object value) {
      element.getAsJsonArray().add(toElement(value));
      return this;
   }

   /**
    * Gets an iterator over the elements in this element checking if the underlying entry is a json array.
    *
    * @return the iterator
    * @throws IllegalStateException if the entry's element is not a json array
    */
   public Iterator<JsonEntry> elementIterator() {
      checkState(element.isJsonArray(), "Entry (" + element.getClass().getName() + ") is not an array.");
      return Iterators.transform(element.getAsJsonArray().iterator(), JsonEntry::new);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {return true;}
      if (obj == null || getClass() != obj.getClass()) {return false;}
      final JsonEntry other = (JsonEntry) obj;
      return Objects.equals(this.element, other.element);
   }

   /**
    * Performs the given action for entry in this array.
    *
    * @param consumer the action to perform
    * @throws IllegalStateException if the entry's element is not a json array
    */
   public void forEachElement(Consumer<JsonEntry> consumer) {
      elementIterator().forEachRemaining(consumer);
   }

   /**
    * Performs the given action for property name and value in this object.
    *
    * @param consumer the action to perform
    * @throws IllegalStateException if the entry's element is not a json object
    */
   public void forEachProperty(BiConsumer<String, JsonEntry> consumer) {
      propertyIterator().forEachRemaining(e -> consumer.accept(e.getKey(), e.getValue()));
   }

   /**
    * Gets the value of this entry as the given class. This method is {@link JsonSerializable} aware and will look for a
    * static <code>fromJson(JsonEntry)</code> method on the class if it is a subclass.
    *
    * @param <T>   the type parameter
    * @param clazz the class information for the type to be generated
    * @return the value
    */
   public <T> T getAs(Class<T> clazz) {
      if (clazz == null) {
         return getAsVal().cast();
      }
      if (JsonSerializable.class.isAssignableFrom(clazz)) {
         Reflect reflected = Reflect.onClass(clazz).allowPrivilegedAccess();
         Optional<Method> staticRead = reflected.getMethods("fromJson", 1).stream()
                                                .filter(m -> JsonEntry.class.isAssignableFrom(
                                                   m.getParameterTypes()[0]))
                                                .filter(m -> Modifier.isStatic(m.getModifiers()))
                                                .findFirst();
         if (staticRead.isPresent()) {
            try {
               return Cast.as(staticRead.get().invoke(null, this));
            } catch (IllegalAccessException | InvocationTargetException e) {
               throw new RuntimeException(e);
            }
         }
      }
      return getAsVal().as(clazz);
   }

   /**
    * Converts the entry into a list of elements checking if the underlying entry is a json array.
    *
    * @return the list
    * @throws IllegalStateException if the entry's element is not a json array
    */
   public List<JsonEntry> getAsArray() {
      checkState(element.isJsonArray(), "Entry (" + element.getClass().getName() + ") is not an array.");
      return new ElementList(element.getAsJsonArray());
   }

   /**
    * Converts the entry into a list of elements checking if the underlying entry is a json array.
    *
    * @param <T>   the type parameter
    * @param clazz the clazz
    * @return the list
    * @throws IllegalStateException if the entry's element is not a json array
    */
   public <T> List<T> getAsArray(Class<T> clazz) {
      checkState(element.isJsonArray(), "Entry (" + element.getClass().getName() + ") is not an array.");
      return Lists.transform(getAsArray(), entry -> entry.getAs(clazz));
   }

   /**
    * Gets this entry as a boolean value.
    *
    * @return the as boolean value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public boolean getAsBoolean() {
      checkState(element.isJsonPrimitive(), "Entry (" + element.getClass().getName() + ") is not a primitive.");
      return element.getAsBoolean();
   }

   /**
    * Gets this entry as a byte value.
    *
    * @return the as byte value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public byte getAsByte() {
      checkState(element.isJsonPrimitive(), "Entry (" + element.getClass().getName() + ") is not a primitive.");
      return element.getAsByte();
   }

   /**
    * Gets this entry as a Character value.
    *
    * @return the as Character value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public Character getAsCharacter() {
      checkState(element.isJsonPrimitive(), "Entry (" + element.getClass().getName() + ") is not a primitive.");
      return element.getAsCharacter();
   }

   /**
    * Gets this entry as a double value.
    *
    * @return the as double value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public double getAsDouble() {
      checkState(element.isJsonPrimitive(), "Entry (" + element.getClass().getName() + ") is not a primitive.");
      return element.getAsDouble();
   }

   /**
    * Gets this entry as a float value.
    *
    * @return the as float value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public float getAsFloat() {
      checkState(element.isJsonPrimitive(), "Entry (" + element.getClass().getName() + ") is not a primitive.");
      return element.getAsFloat();
   }

   /**
    * Gets this entry as a int value.
    *
    * @return the as int value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public int getAsInt() {
      checkState(element.isJsonPrimitive(), "Entry (" + element.getClass().getName() + ") is not a primitive.");
      return element.getAsInt();
   }

   /**
    * Gets this entry as a long value.
    *
    * @return the as long value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public long getAsLong() {
      checkState(element.isJsonPrimitive(), "Entry (" + element.getClass().getName() + ") is not a primitive.");
      return element.getAsLong();
   }

   /**
    * Converts the entry into a map of string keys and entry elements checking if the underlying entry is a json
    * object.
    *
    * @return the map
    * @throws IllegalStateException if the entry's element is not a json object
    */
   public Map<String, JsonEntry> getAsMap() {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      return new ElementMap(element.getAsJsonObject());
   }

   /**
    * Converts the entry into a map of string keys and entry elements checking if the underlying entry is a json
    * object.
    *
    * @param <T>   the type parameter
    * @param clazz the clazz
    * @return the map
    * @throws IllegalStateException if the entry's element is not a json object
    */
   public <T> Map<String, T> getAsMap(Class<T> clazz) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      Map<String, T> map = new HashMap<>();
      propertyIterator().forEachRemaining(e -> map.put(e.getKey(), e.getValue().getAs(clazz)));
      return map;
   }

   /**
    * Gets this entry as a Number value.
    *
    * @return the as Number value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public Number getAsNumber() {
      checkState(element.isJsonPrimitive(), "Entry (" + element.getClass().getName() + ") is not a primitive.");
      return element.getAsNumber();
   }

   /**
    * Gets this entry as a short value.
    *
    * @return the as short value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public short getAsShort() {
      checkState(element.isJsonPrimitive(), "Entry (" + element.getClass().getName() + ") is not a primitive.");
      return element.getAsShort();
   }

   /**
    * Gets this entry as a String value.
    *
    * @return the as String value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public String getAsString() {
      checkState(element.isJsonPrimitive(), "Entry (" + element.getClass().getName() + ") is not a primitive.");
      return element.getAsString();
   }

   /**
    * Gets this entry as a Val value.
    *
    * @return the as Val value
    */
   public Val getAsVal() {
      if (element.isJsonNull()) {
         return Val.NULL;
      }
      if (element.isJsonPrimitive()) {
         JsonPrimitive primitive = Cast.as(element);
         if (primitive.isBoolean()) {
            return Val.of(primitive.getAsBoolean());
         }
         if (primitive.isNumber()) {
            return Val.of(primitive.getAsNumber());
         }
         return Val.of(primitive.getAsString());
      }
      if (element.isJsonArray()) {
         return Val.of(getAsArray());
      }
      return Val.of(getAsMap());
   }

   /**
    * Gets the value of the given property name as a boolean
    *
    * @param propertyName the property name
    * @return the boolean property
    * @throws IllegalStateException if the entry is not a json object
    */
   public boolean getBooleanProperty(String propertyName) {
      return getProperty(propertyName).element.getAsBoolean();
   }

   /**
    * Gets the value of the given property name as a boolean
    *
    * @param propertyName the property name
    * @param defaultValue the default value if the property does not exist
    * @return the boolean property
    * @throws IllegalStateException if the entry is not a json object
    */
   public boolean getBooleanProperty(String propertyName, boolean defaultValue) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      if (element.getAsJsonObject().has(propertyName)) {
         return element.getAsJsonObject().get(propertyName).getAsBoolean();
      }
      return defaultValue;
   }

   /**
    * Gets the value of the given property name as a Character
    *
    * @param propertyName the property name
    * @return the Character property
    * @throws IllegalStateException if the entry is not a json object
    */
   public Character getCharacterProperty(String propertyName) {
      return getProperty(propertyName).element.getAsCharacter();
   }

   /**
    * Gets the value of the given property name as a Character
    *
    * @param propertyName the property name
    * @param defaultValue the default value if the property does not exist
    * @return the Character property
    * @throws IllegalStateException if the entry is not a json object
    */
   public Character getCharacterProperty(String propertyName, Character defaultValue) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      if (element.getAsJsonObject().has(propertyName)) {
         return element.getAsJsonObject().get(propertyName).getAsCharacter();
      }
      return defaultValue;
   }


   /**
    * Gets the value of the given property name as a double
    *
    * @param propertyName the property name
    * @return the double property
    * @throws IllegalStateException if the entry is not a json object
    */
   public double getDoubleProperty(String propertyName) {
      return getProperty(propertyName).element.getAsDouble();
   }

   /**
    * Gets the value of the given property name as a double
    *
    * @param propertyName the property name
    * @param defaultValue the default value if the property does not exist
    * @return the double property
    * @throws IllegalStateException if the entry is not a json object
    */
   public double getDoubleProperty(String propertyName, double defaultValue) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      if (element.getAsJsonObject().has(propertyName)) {
         return element.getAsJsonObject().get(propertyName).getAsDouble();
      }
      return defaultValue;
   }

   /**
    * Gets the underlying JsonElement.
    *
    * @return the element
    */
   public JsonElement getElement() {
      return element;
   }


   /**
    * Gets the value of the given property name as a float
    *
    * @param propertyName the property name
    * @return the float property
    * @throws IllegalStateException if the entry is not a json object
    */
   public float getFloatProperty(String propertyName) {
      return getProperty(propertyName).element.getAsFloat();
   }

   /**
    * Gets the value of the given property name as a float
    *
    * @param propertyName the property name
    * @param defaultValue the default value if the property does not exist
    * @return the float property
    * @throws IllegalStateException if the entry is not a json object
    */
   public float getFloatProperty(String propertyName, float defaultValue) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      if (element.getAsJsonObject().has(propertyName)) {
         return element.getAsJsonObject().get(propertyName).getAsFloat();
      }
      return defaultValue;
   }


   /**
    * Gets the value of the given property name as a int
    *
    * @param propertyName the property name
    * @return the int property
    * @throws IllegalStateException if the entry is not a json object
    */
   public int getIntProperty(String propertyName) {
      return getProperty(propertyName).element.getAsInt();
   }

   /**
    * Gets the value of the given property name as a int
    *
    * @param propertyName the property name
    * @param defaultValue the default value if the property does not exist
    * @return the int property
    * @throws IllegalStateException if the entry is not a json object
    */
   public int getIntProperty(String propertyName, int defaultValue) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      if (element.getAsJsonObject().has(propertyName)) {
         return element.getAsJsonObject().get(propertyName).getAsInt();
      }
      return defaultValue;
   }


   /**
    * Gets the value of the given property name as a long
    *
    * @param propertyName the property name
    * @return the long property
    * @throws IllegalStateException if the entry is not a json object
    */
   public long getLongProperty(String propertyName) {
      return getProperty(propertyName).element.getAsLong();
   }

   /**
    * Gets the value of the given property name as a long
    *
    * @param propertyName the property name
    * @param defaultValue the default value if the property does not exist
    * @return the long property
    * @throws IllegalStateException if the entry is not a json object
    */
   public long getLongProperty(String propertyName, long defaultValue) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      if (element.getAsJsonObject().has(propertyName)) {
         return element.getAsJsonObject().get(propertyName).getAsLong();
      }
      return defaultValue;
   }


   /**
    * Gets the value of the given property name as a Number
    *
    * @param propertyName the property name
    * @return the Number property
    * @throws IllegalStateException if the entry is not a json object
    */
   public Number getNumberProperty(String propertyName) {
      return getProperty(propertyName).element.getAsNumber();
   }

   /**
    * Gets the value of the given property name as a Number
    *
    * @param propertyName the property name
    * @param defaultValue the default value if the property does not exist
    * @return the Number property
    * @throws IllegalStateException if the entry is not a json object
    */
   public Number getNumberProperty(String propertyName, Number defaultValue) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      if (element.getAsJsonObject().has(propertyName)) {
         return element.getAsJsonObject().get(propertyName).getAsNumber();
      }
      return defaultValue;
   }


   /**
    * Gets the value of the given property name as a JsonEntry
    *
    * @param propertyName the property name
    * @return the JsonEntry property
    * @throws IllegalStateException if the entry is not a json object
    */
   public JsonEntry getProperty(String propertyName) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      return new JsonEntry(element.getAsJsonObject().get(propertyName));
   }

   /**
    * Gets the value of the given property name as a JsonEntry
    *
    * @param propertyName the property name
    * @param defaultValue the default value if the property does not exist
    * @return the JsonEntry property
    * @throws IllegalStateException if the entry is not a json object
    */
   public JsonEntry getProperty(String propertyName, Object defaultValue) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      if (element.getAsJsonObject().has(propertyName)) {
         return new JsonEntry(element.getAsJsonObject().get(propertyName));
      }
      return from(defaultValue);
   }

   /**
    * Gets the value of the given property name as the type of the given class
    *
    * @param <T>          the type parameter
    * @param propertyName the property name
    * @param clazz        Class information for the desired type
    * @return the property value
    * @throws IllegalStateException if the entry is not a json object
    */
   public <T> T getProperty(String propertyName, Class<T> clazz) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      return new JsonEntry(element.getAsJsonObject().get(propertyName)).getAs(clazz);
   }

   /**
    * Gets the value of the given property name as the type of the given class
    *
    * @param <T>          the type parameter
    * @param propertyName the property name
    * @param clazz        Class information for the desired type
    * @param defaultValue the default value if the property does not exist
    * @return the property value
    * @throws IllegalStateException if the entry is not a json object
    */
   public <T> T getProperty(String propertyName, Class<T> clazz, T defaultValue) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      if (element.getAsJsonObject().has(propertyName)) {
         return new JsonEntry(element.getAsJsonObject().get(propertyName)).getAs(clazz);
      }
      return defaultValue;
   }

   /**
    * Gets the value of the given property name as a String
    *
    * @param propertyName the property name
    * @return the String property
    * @throws IllegalStateException if the entry is not a json object
    */
   public String getStringProperty(String propertyName) {
      return getProperty(propertyName).element.getAsString();
   }

   /**
    * Gets the value of the given property name as a String
    *
    * @param propertyName the property name
    * @param defaultValue the default value if the property does not exist
    * @return the String property
    * @throws IllegalStateException if the entry is not a json object
    */
   public String getStringProperty(String propertyName, String defaultValue) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      if (element.getAsJsonObject().has(propertyName)) {
         return element.getAsJsonObject().get(propertyName).getAsString();
      }
      return defaultValue;
   }

   /**
    * Gets the value of the given property name as a Val
    *
    * @param propertyName the property name
    * @return the Val property
    * @throws IllegalStateException if the entry is not a json object
    */
   public Val getValProperty(String propertyName) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      if (element.getAsJsonObject().has(propertyName)) {
         return new JsonEntry(element.getAsJsonObject().get(propertyName)).getAsVal();
      }
      return Val.NULL;
   }

   /**
    * Gets the value of the given property name as a Val
    *
    * @param propertyName the property name
    * @param defaultValue the default value if the property does not exist
    * @return the Val property
    * @throws IllegalStateException if the entry is not a json object
    */
   public Val getValProperty(String propertyName, Object defaultValue) {
      checkState(element.isJsonObject(), "Entry (" + element.getClass().getName() + ") is not an object.");
      if (element.getAsJsonObject().has(propertyName)) {
         return new JsonEntry(element.getAsJsonObject().get(propertyName)).getAsVal();
      }
      return Val.of(defaultValue);
   }

   /**
    * Checks if this entry has the given property
    *
    * @param propertyName the property name to check
    * @return true if this is an object and has the property otherwise false
    */
   public boolean hasProperty(String propertyName) {
      if (element.isJsonObject()) {
         return element.getAsJsonObject().has(propertyName);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hash(element);
   }

   /**
    * Checks if this entry is a json array
    *
    * @return true if a json array, false otherwise
    */
   public boolean isArray() {
      return element.isJsonArray();
   }

   /**
    * Checks if this entry is a json null value
    *
    * @return true if a json null value, false otherwise
    */
   public boolean isNull() {
      return element.isJsonNull();
   }

   /**
    * Checks if this entry is a json object
    *
    * @return true if a json object, false otherwise
    */
   public boolean isObject() {
      return element.isJsonObject();
   }

   /**
    * Checks if this entry is a json primitive
    *
    * @return true if a json primitive value, false otherwise
    */
   public boolean isPrimitive() {
      return element.isJsonPrimitive();
   }

   /**
    * Gets the keys (property names).
    *
    * @return the set of property names (keys) or empty set if not an object
    */
   public Set<String> keySet() {
      if (element.isJsonObject()) {
         return element.getAsJsonObject().keySet();
      }
      return Collections.emptySet();
   }

   /**
    * Gets an iterator over the elements in this element.
    *
    * @return the iterator of properties if an object, empty iterator otherwise
    */
   public Iterator<Map.Entry<String, JsonEntry>> propertyIterator() {
      if (element.isJsonObject()) {
         return Iterators.transform(element.getAsJsonObject().entrySet().iterator(),
                                    e -> $(e.getKey(), new JsonEntry(e.getValue())));
      }
      return Collections.emptyIterator();
   }

   @Override
   public String toString() {
      return element.toString();
   }

   private static class ElementList extends AbstractList<JsonEntry> {
      private final JsonArray array;

      private ElementList(JsonArray array) {
         this.array = array;
      }

      @Override
      public boolean add(JsonEntry entry) {
         array.add(entry.element);
         return true;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {return true;}
         if (obj == null || getClass() != obj.getClass()) {return false;}
         if (!super.equals(obj)) {return false;}
         final ElementList other = (ElementList) obj;
         return Objects.equals(this.array, other.array);
      }

      @Override
      public JsonEntry get(int index) {
         return new JsonEntry(array.get(index));
      }

      @Override
      public int hashCode() {
         return 31 * super.hashCode() + Objects.hash(array);
      }

      @Override
      public JsonEntry remove(int index) {
         return new JsonEntry(array.remove(index));
      }

      @Override
      public boolean remove(Object o) {
         if (o instanceof JsonEntry) {
            return array.remove(Cast.<JsonEntry>as(o).element);
         }
         return false;
      }

      @Override
      public int size() {
         return array.size();
      }

      @Override
      public String toString() {
         return array.toString();
      }
   }

   private static class ElementMap extends AbstractMap<String, JsonEntry> {
      private final JsonObject object;

      /**
       * Instantiates a new Element map.
       *
       * @param object the object
       */
      ElementMap(JsonObject object) {
         this.object = object;
      }

      @Override
      public boolean containsKey(Object key) {
         return object.has(key.toString());
      }

      @Override
      public Set<Entry<String, JsonEntry>> entrySet() {
         return Sets.transform(object.entrySet(),
                               e -> $(e.getKey(), new JsonEntry(e.getValue())));
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {return true;}
         if (obj == null || getClass() != obj.getClass()) {return false;}
         if (!super.equals(obj)) {return false;}
         final ElementMap other = (ElementMap) obj;
         return Objects.equals(this.object, other.object);
      }

      @Override
      public JsonEntry get(Object key) {
         return getOrDefault(key, null);
      }

      @Override
      public JsonEntry getOrDefault(Object key, JsonEntry defaultValue) {
         if (containsKey(key)) {
            return new JsonEntry(object.get(key.toString()));
         }
         return defaultValue;
      }

      @Override
      public int hashCode() {
         return 31 * super.hashCode() + Objects.hash(object);
      }

      @Override
      public Set<String> keySet() {
         return object.keySet();
      }

      @Override
      public JsonEntry put(String key, JsonEntry value) {
         JsonEntry toReturn = get(key);
         object.add(key, value.element);
         return toReturn;
      }

      @Override
      public void putAll(Map<? extends String, ? extends JsonEntry> m) {
         super.putAll(m);
      }

      @Override
      public JsonEntry remove(Object key) {
         return new JsonEntry(object.remove(key.toString()));
      }

      @Override
      public int size() {
         return object.size();
      }

      @Override
      public String toString() {
         return object.toString();
      }
   }


}//END OF JElement
