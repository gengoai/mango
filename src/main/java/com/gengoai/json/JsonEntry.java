package com.gengoai.json;

import com.gengoai.collection.Iterators;
import com.gengoai.collection.Lists;
import com.gengoai.collection.Sets;
import com.gengoai.collection.Streams;
import com.gengoai.conversion.Cast;
import com.gengoai.conversion.Converter;
import com.gengoai.conversion.Val;
import com.gengoai.string.Strings;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.gengoai.json.Json.MAPPER;
import static com.gengoai.tuple.Tuples.$;

/**
 * <p>A convenience wrapper around <code>JsonElement</code> allowing a single interface for objects, arrays, and
 * primitives</p>
 */
public class JsonEntry implements Serializable {
   private JsonElement element;

   private void writeObject(ObjectOutputStream oos) throws IOException {
      oos.writeUTF(element.toString());
   }

   private void readObject(ObjectInputStream ois) throws IOException {
      element = Json.parse(ois.readUTF()).element;
   }


   private JsonEntry(JsonElement element) {
      this.element = element;
   }


   public JsonEntry mergeObject(JsonEntry entry) {
      if (entry.isObject()) {
         entry.propertyIterator()
              .forEachRemaining(e -> this.addProperty(e.getKey(), e.getValue()));
         return this;
      }
      throw new IllegalArgumentException("Object expected");
   }

   public String pprint() {
      return pprint(3);
   }

   public String pprint(int indent) {
      StringWriter sw = new StringWriter();
      try (JsonWriter jw = MAPPER.newJsonWriter(sw)) {
         jw.setIndent(Strings.repeat(' ', indent));
         MAPPER.toJson(element, jw);
      } catch (Exception e) {
         //ignore
      }
      try {
         sw.close();
         return sw.getBuffer().toString();
      } catch (IOException e) {
         return null;
      }
   }

   /**
    * Array json entry.
    *
    * @param items the items
    * @return the json entry
    */
   public static JsonEntry array(Iterable<?> items) {
      JsonEntry entry = new JsonEntry(new JsonArray());
      items.forEach(entry::addValue);
      return entry;
   }

   /**
    * Creates a new array
    *
    * @param items Items to add to the array
    * @return the json entry
    */
   public static JsonEntry array(Object... items) {
      JsonEntry entry = new JsonEntry(new JsonArray());
      if (items != null) {
         for (Object item : items) {
            entry.addValue(item);
         }
      }
      return entry;
   }

   /**
    * Creates an entry from the given object.
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
         map.forEach((k, v) -> obj.add(Converter.convertSilently(k, String.class), from(v).element));
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
      }
      return MAPPER.toJsonTree(v);
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
      return Iterators.transform(element.getAsJsonArray().iterator(), JsonEntry::new);
   }

   public Stream<JsonEntry> elementStream() {
      return Streams.asStream(elementIterator());
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

//   public <T> T getAs(ParameterizedType pt) {
//      return MAPPER.fromJson(element, pt);
//   }

   public Object get() {
      if (isString()) {
         return getAsString();
      }
      if (isNumber()) {
         return getAsNumber();
      }
      if (isBoolean()) {
         return getAsBoolean();
      }
      if (isNull()) {
         return getAsNumber();
      }
      if (isObject()) {
         return getAsMap();
      }
      if (isArray()) {
         return getAsArray();
      }
      return getAs(Object.class);
   }

   /**
    * Gets the value of this entry as the given class.
    *
    * @param <T>  the type parameter
    * @param type the type information for the type to be generated
    * @return the value
    */
   public <T> T getAs(Type type) {
      return MAPPER.fromJson(element, type);
   }

   /**
    * Converts the entry into a list of elements checking if the underlying entry is a json array.
    *
    * @return the list
    * @throws IllegalStateException if the entry's element is not a json array
    */
   public List<JsonEntry> getAsArray() {
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
   public <T> List<T> getAsArray(Type clazz) {
      return Lists.transform(getAsArray(), entry -> entry.getAs(clazz));
   }

   public <T> List<T> getAsArray(Class<? extends T> clazz) {
      return Cast.as(getAsArray(Cast.<Type>as(clazz)));
   }

   public <T, E extends Collection<T>> E getAsArray(Type clazz, E collection) {
      elementIterator().forEachRemaining(je -> collection.add(je.getAs(clazz)));
      return collection;
   }

   /**
    * Gets this entry as a boolean value.
    *
    * @return the as boolean value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public boolean getAsBoolean() {
      return element.getAsBoolean();
   }

   public boolean[] getAsBooleanArray() {
      boolean[] out = new boolean[element.getAsJsonArray().size()];
      int i = 0;
      for (JsonElement jsonElement : element.getAsJsonArray()) {
         out[i] = jsonElement.getAsBoolean();
         i++;
      }
      return out;
   }

   /**
    * Gets this entry as a byte value.
    *
    * @return the as byte value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public byte getAsByte() {
      return element.getAsByte();
   }

   /**
    * Gets this entry as a Character value.
    *
    * @return the as Character value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public Character getAsCharacter() {
      return element.getAsCharacter();
   }

   /**
    * Gets this entry as a double value.
    *
    * @return the as double value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public double getAsDouble() {
      return element.getAsDouble();
   }

   public double[] getAsDoubleArray() {
      double[] out = new double[element.getAsJsonArray().size()];
      int i = 0;
      for (JsonElement jsonElement : element.getAsJsonArray()) {
         out[i] = jsonElement.getAsDouble();
         i++;
      }
      return out;
   }

   /**
    * Gets this entry as a float value.
    *
    * @return the as float value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public float getAsFloat() {
      return element.getAsFloat();
   }

   /**
    * Gets this entry as a int value.
    *
    * @return the as int value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public int getAsInt() {
      return element.getAsInt();
   }

   public int[] getAsIntArray() {
      int[] out = new int[element.getAsJsonArray().size()];
      int i = 0;
      for (JsonElement jsonElement : element.getAsJsonArray()) {
         out[i] = jsonElement.getAsInt();
         i++;
      }
      return out;
   }

   /**
    * Gets this entry as a long value.
    *
    * @return the as long value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public long getAsLong() {
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
   public <T> Map<String, T> getAsMap(Type clazz) {
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
      return element.getAsNumber();
   }

   /**
    * Gets this entry as a short value.
    *
    * @return the as short value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public short getAsShort() {
      return element.getAsShort();
   }

   /**
    * Gets this entry as a String value.
    *
    * @return the as String value
    * @throws IllegalStateException if the entry's element is not a json primitive
    */
   public String getAsString() {
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
    * Is boolean boolean.
    *
    * @return the boolean
    */
   public boolean isBoolean() {
      return isPrimitive() && element.getAsJsonPrimitive().isBoolean();
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
    * Is number boolean.
    *
    * @return the boolean
    */
   public boolean isNumber() {
      return isPrimitive() && element.getAsJsonPrimitive().isNumber();
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
    * Is string boolean.
    *
    * @return the boolean
    */
   public boolean isString() {
      return isPrimitive() && element.getAsJsonPrimitive().isString();
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

   public int size() {
      if (element.isJsonObject()) {
         return element.getAsJsonObject().size();
      } else if (element.isJsonArray()) {
         return element.getAsJsonArray().size();
      } else if (element.isJsonNull()) {
         return 0;
      }
      return 1;
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
