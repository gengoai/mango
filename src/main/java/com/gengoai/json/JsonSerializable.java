package com.gengoai.json;

import com.gengoai.io.Resources;
import com.gengoai.io.resource.Resource;
import com.gengoai.io.resource.StringResource;
import com.gengoai.reflection.BeanMap;

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
   default void toJson(JsonWriter jsonWriter) throws IOException {
      jsonWriter.value(new BeanMap(this));
   }

   /**
    * To json.
    *
    * @param jsonWriter the json writer
    * @throws IOException the io exception
    */
   default void toJson(String name, JsonWriter jsonWriter) throws IOException {
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

   default void fromJson(JsonReader reader) throws IOException {
      new BeanMap(this).putAll(reader.nextMap());
   }

   default void fromJson(String name, JsonReader reader) throws IOException {
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

   default void fromJson(String json) throws IOException {
      try (JsonReader reader = new JsonReader(Resources.fromString(json))) {
         new BeanMap(this).putAll(reader.nextMap());
      }
   }


}//END OF JsonSerializable
