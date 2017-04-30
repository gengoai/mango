package com.davidbracewell.json;

import com.davidbracewell.io.Resources;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.resource.StringResource;
import com.davidbracewell.reflection.BeanMap;
import lombok.NonNull;

import java.io.IOException;

/**
 * The interface Json serializable.
 *
 * @author David B. Bracewell
 */
public interface JsonSerializable {

   /**
    * To json.
    *
    * @param jsonWriter the json writer
    * @throws IOException the io exception
    */
   default void toJson(@NonNull JsonWriter jsonWriter) throws IOException {
      jsonWriter.value(new BeanMap(this));
   }

   /**
    * To json.
    *
    * @param jsonWriter the json writer
    * @throws IOException the io exception
    */
   default void toJson(String name, @NonNull JsonWriter jsonWriter) throws IOException {
      boolean isArray = this instanceof JsonArraySerializable;
      if (isArray) {
         jsonWriter.beginArray(name);
      } else {
         jsonWriter.beginObject(name);
      }
      toJson(jsonWriter);
      if (isArray) {
         jsonWriter.endArray();
      } else {
         jsonWriter.endObject();
      }
   }

   /**
    * To json string.
    *
    * @return the string
    */
   default String toJson() throws IOException {
      Resource r = new StringResource();
      try (JsonWriter writer = new JsonWriter(r)) {
         writer.beginDocument(this instanceof JsonArraySerializable);
         toJson(writer);
         writer.endDocument();
      }
      return r.readToString().trim();
   }

   default void fromJson(@NonNull JsonReader reader) throws IOException {
      new BeanMap(this).putAll(reader.nextMap());
   }

   default void fromJson(String name, @NonNull JsonReader reader) throws IOException {
      boolean isArray = this instanceof JsonArraySerializable;
      if (isArray) {
         reader.beginArray(name);
      } else {
         reader.beginObject(name);
      }
      new BeanMap(this).putAll(reader.nextMap());
      if (isArray) {
         reader.endArray();
      } else {
         reader.endObject();
      }
   }

   default void fromJson(@NonNull String json) throws IOException {
      try (JsonReader reader = new JsonReader(Resources.fromString(json))) {
         new BeanMap(this).putAll(reader.nextMap());
      }
   }


}//END OF JsonSerializable
