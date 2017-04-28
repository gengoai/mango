package com.davidbracewell.io.structured;

import com.davidbracewell.DynamicEnum;
import com.davidbracewell.EnumValue;
import com.davidbracewell.collection.counter.Counter;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;
import lombok.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a class for writing data in a structured format, e.g. xml, json, yaml, etc. Individual implementations
 * may provide extra functionality (e.g. read xml attributes).
 *
 * @author David B. Bracewell
 */
public abstract class StructuredWriter implements Closeable {

   /**
    * Begin document as an object structure
    *
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public StructuredWriter beginDocument() throws IOException {
      return beginDocument(false);
   }

   /**
    * Begin document
    *
    * @param isArray True the document is an array structure, false is an object structure
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public abstract StructuredWriter beginDocument(boolean isArray) throws IOException;

   /**
    * End document.
    *
    * @throws IOException Something went wrong writing
    */
   public abstract void endDocument() throws IOException;

   /**
    * Begins a new object with a given name
    *
    * @param name the name
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public abstract StructuredWriter beginObject(String name) throws IOException;

   /**
    * Begins a new object
    *
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public abstract StructuredWriter beginObject() throws IOException;

   /**
    * Ends the current object
    *
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public abstract StructuredWriter endObject() throws IOException;

   /**
    * Begins a new array with given name
    *
    * @param name the name
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public abstract StructuredWriter beginArray(String name) throws IOException;

   /**
    * Begins a new array
    *
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public abstract StructuredWriter beginArray() throws IOException;

   /**
    * Ends the current array
    *
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public abstract StructuredWriter endArray() throws IOException;

   /**
    * Determines if the writer is currently in an array
    *
    * @return True if in an array, False if not
    */
   public abstract boolean inArray();

   /**
    * Determines if the writer is currently in an object
    *
    * @return True if in an object, False if not
    */
   public abstract boolean inObject();

   /**
    * Flushes the writer.
    *
    * @throws IOException Something went wrong writing
    */
   public abstract void flush() throws IOException;

   /**
    * Writes a  key value pair
    *
    * @param key    the key
    * @param object the value
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public abstract StructuredWriter writeKeyValue(String key, Object object) throws IOException;

   /**
    * Writes an array value
    *
    * @param value the value
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public StructuredWriter writeValue(Object value) throws IOException {
      return writeObject(value);
   }

   /**
    * Writes a  null value
    *
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected abstract StructuredWriter writeNull() throws IOException;

   /**
    * Writes a number
    *
    * @param number the number
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected abstract StructuredWriter writeNumber(Number number) throws IOException;

   /**
    * Writes a string
    *
    * @param string the string
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected abstract StructuredWriter writeString(String string) throws IOException;

   /**
    * Writes a boolean.
    *
    * @param value the value
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected abstract StructuredWriter writeBoolean(boolean value) throws IOException;


   /**
    * Serializes an object
    *
    * @param object the object to serialize
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected StructuredWriter writeObject(Object object) throws IOException {
      if (object == null) {
         writeNull();
      } else if (object instanceof StructuredSerializable) {
         StructuredSerializable structuredSerializable = Cast.as(object);
         if (object instanceof ArrayValue) {
            beginArray();
         } else {
            beginObject();
         }
         structuredSerializable.write(this);
         if (object instanceof ArrayValue) {
            endArray();
         } else {
            endObject();
         }
      } else if (object instanceof Number) {
         writeNumber(Cast.as(object));
      } else if (object instanceof String) {
         writeString(Cast.as(object));
      } else if (object instanceof Boolean) {
         writeBoolean(Cast.as(object));
      } else if (object instanceof Enum || object instanceof EnumValue) {
         writeString(Convert.convert(object, String.class));
      } else if (object instanceof Collection) {
         writeCollection(Cast.as(object));
      } else if (object instanceof Map) {
         writeMap(Cast.as(object));
      } else if (object.getClass().isArray()) {
         writeArray(Cast.as(object));
      } else if (object instanceof Multimap) {
         writeMap(Cast.<Multimap>as(object).asMap());
      } else if (object instanceof Counter) {
         writeMap(Cast.<Counter>as(object).asMap());
      } else if (object instanceof Iterable) {
         writeCollection(new AbstractCollection<Object>() {
            @Override
            public Iterator<Object> iterator() {
               return Cast.<Iterable<Object>>as(object).iterator();
            }

            @Override
            public int size() {
               return Iterators.size(iterator());
            }
         });
      } else if (object instanceof Iterator) {
         writeCollection(new AbstractCollection<Object>() {
            @Override
            public Iterator<Object> iterator() {
               return Cast.as(object);
            }

            @Override
            public int size() {
               return Iterators.size(Cast.as(object));
            }
         });
      } else {
         writeValue(Convert.convert(object, String.class));
      }
      return this;
   }

   /**
    * Writes a map
    *
    * @param map the map to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected StructuredWriter writeMap(@NonNull Map<?, ?> map) throws IOException {
      boolean inObject = inObject();
      if (!inObject) beginObject();
      for (Map.Entry<?, ?> entry : map.entrySet()) {
         writeKeyValue(Convert.convert(entry.getKey(), String.class), entry.getValue());
      }
      if (!inObject) endObject();
      return this;
   }

   /**
    * Writes a collection
    *
    * @param collection the collection to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected StructuredWriter writeCollection(@NonNull Collection<?> collection) throws IOException {
      beginArray();
      for (Object o : collection) {
         writeValue(o);
      }
      endArray();
      return this;
   }

   /**
    * Writes an array
    *
    * @param array the array to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected StructuredWriter writeArray(@NonNull Object[] array) throws IOException {
      beginArray();
      for (Object o : array) {
         writeValue(o);
      }
      endArray();
      return this;
   }

   /**
    * Writes a map with the given key name
    *
    * @param key the key name for the map
    * @param map the map to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected StructuredWriter writeMap(String key, @NonNull Map<?, ?> map) throws IOException {
      boolean inObject = inObject();
      if (!inObject) beginObject(key);
      for (Map.Entry<?, ?> entry : map.entrySet()) {
         writeKeyValue(Convert.convert(entry.getKey(), String.class), entry.getValue());
      }
      if (!inObject) endObject();
      return this;
   }

   /**
    * Writes a collection with the given key name
    *
    * @param key        the key name for the collection
    * @param collection the collection to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected StructuredWriter writeCollection(String key, @NonNull Collection<?> collection) throws IOException {
      beginArray(key);
      for (Object o : collection) {
         writeValue(o);
      }
      endArray();
      return this;
   }

   /**
    * Writes an array with the given key name
    *
    * @param key   the key name for the array
    * @param array the array to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected StructuredWriter writeArray(String key, @NonNull Object[] array) throws IOException {
      beginArray(key);
      for (Object o : array) {
         writeValue(o);
      }
      endArray();
      return this;
   }

}//END OF StructuredWriter
