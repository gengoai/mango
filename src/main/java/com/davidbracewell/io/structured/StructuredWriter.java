package com.davidbracewell.io.structured;

import com.davidbracewell.DynamicEnum;
import com.davidbracewell.collection.counter.Counter;
import com.davidbracewell.collection.counter.MultiCounter;
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
 * The interface Structured writer.
 *
 * @author David B. Bracewell
 */
public abstract class StructuredWriter implements Closeable {

   /**
    * Begin document structured writer.
    *
    * @return the structured writer
    * @throws IOException the io exception
    */
   public StructuredWriter beginDocument() throws IOException {
      return beginDocument(false);
   }

   /**
    * Begin document structured writer.
    *
    * @param isArray the is array
    * @return the structured writer
    * @throws IOException the io exception
    */
   public abstract StructuredWriter beginDocument(boolean isArray) throws IOException;

   /**
    * End document.
    *
    * @throws IOException the io exception
    */
   public abstract void endDocument() throws IOException;

   /**
    * Begin object structured writer.
    *
    * @param name the name
    * @return the structured writer
    * @throws IOException the io exception
    */
   public abstract StructuredWriter beginObject(String name) throws IOException;

   /**
    * Begin object structured writer.
    *
    * @return the structured writer
    * @throws IOException the io exception
    */
   public abstract StructuredWriter beginObject() throws IOException;

   /**
    * End object structured writer.
    *
    * @return the structured writer
    * @throws IOException the io exception
    */
   public abstract StructuredWriter endObject() throws IOException;

   /**
    * Begin array structured writer.
    *
    * @param name the name
    * @return the structured writer
    * @throws IOException the io exception
    */
   public abstract StructuredWriter beginArray(String name) throws IOException;

   /**
    * Begin array structured writer.
    *
    * @return the structured writer
    * @throws IOException the io exception
    */
   public abstract StructuredWriter beginArray() throws IOException;

   /**
    * End array structured writer.
    *
    * @return the structured writer
    * @throws IOException the io exception
    */
   public abstract StructuredWriter endArray() throws IOException;

   /**
    * In array boolean.
    *
    * @return the boolean
    */
   public abstract boolean inArray();

   /**
    * In object boolean.
    *
    * @return the boolean
    */
   public abstract boolean inObject();

   /**
    * Flush.
    *
    * @throws IOException the io exception
    */
   public abstract void flush() throws IOException;

   /**
    * Write key value structured writer.
    *
    * @param key    the key
    * @param object the value
    * @return the structured writer
    * @throws IOException the io exception
    */
   public abstract StructuredWriter writeKeyValue(String key, Object object) throws IOException;

   /**
    * Write value structured writer.
    *
    * @param value the value
    * @return the structured writer
    * @throws IOException the io exception
    */
   public StructuredWriter writeValue(Object value) throws IOException {
      return writeObject(value);
   }

   /**
    * Write null structured writer.
    *
    * @return the structured writer
    * @throws IOException the io exception
    */
   protected abstract StructuredWriter writeNull() throws IOException;

   /**
    * Write number structured writer.
    *
    * @param number the number
    * @return the structured writer
    * @throws IOException the io exception
    */
   protected abstract StructuredWriter writeNumber(Number number) throws IOException;

   /**
    * Write string structured writer.
    *
    * @param string the string
    * @return the structured writer
    * @throws IOException the io exception
    */
   protected abstract StructuredWriter writeString(String string) throws IOException;

   /**
    * Write boolean structured writer.
    *
    * @param value the value
    * @return the structured writer
    * @throws IOException the io exception
    */
   protected abstract StructuredWriter writeBoolean(boolean value) throws IOException;


   /**
    * Write object structured writer.
    *
    * @param object the object
    * @return the structured writer
    * @throws IOException the io exception
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
      } else if (object instanceof Enum || object instanceof DynamicEnum) {
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
      } else if (object instanceof MultiCounter) {
         writeMap(Cast.<MultiCounter>as(object).asMap());
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
    * Write map structured writer.
    *
    * @param map the map
    * @return the structured writer
    * @throws IOException the io exception
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
    * Write collection structured writer.
    *
    * @param collection the collection
    * @return the structured writer
    * @throws IOException the io exception
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
    * Write array structured writer.
    *
    * @param array the array
    * @return the structured writer
    * @throws IOException the io exception
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
    * Write map structured writer.
    *
    * @param map the map
    * @return the structured writer
    * @throws IOException the io exception
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
    * Write collection structured writer.
    *
    * @param collection the collection
    * @return the structured writer
    * @throws IOException the io exception
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
    * Write array structured writer.
    *
    * @param array the array
    * @return the structured writer
    * @throws IOException the io exception
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
