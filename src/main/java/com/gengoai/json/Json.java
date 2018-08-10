package com.gengoai.json;

import com.gengoai.io.Resources;
import com.gengoai.io.resource.Resource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * The type Json.
 *
 * @author David B. Bracewell
 */
public final class Json {
   /**
    * The List type.
    */
   static final Type LIST_TYPE = new TypeToken<List<Object>>() {
   }.getType();
   /**
    * The Map type.
    */
   static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {
   }.getType();
   /**
    * The Gson.
    */
   static final Gson gson = new Gson();


   private Json() {
      throw new IllegalAccessError();
   }

   /**
    * Create reader json reader.
    *
    * @param resource the resource
    * @return the json reader
    * @throws IOException the io exception
    */
   public static JsonReader createReader(Resource resource) throws IOException {
      return new JsonReader(resource);
   }

   /**
    * Create writer json writer.
    *
    * @param resource the resource
    * @return the json writer
    * @throws IOException the io exception
    */
   public static JsonWriter createWriter(Resource resource) throws IOException {
      return new JsonWriter(resource);
   }

   public static Resource dump(Object object, Resource out) throws IOException {
      try (JsonWriter writer = new JsonWriter(out)) {
         writer.write(JsonEntry.from(object));
      }
      return out;
   }

   /**
    * Dumps a map in this format to a string.
    *
    * @param object the object to dump
    * @return the string representation of the map
    */
   public static String dumps(Object object) {
      try {
         return dump(object, Resources.fromString()).readToString();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public static List<JsonEntry> loadArray(Resource json) throws IOException {
      return parse(json).getAsArray();
   }

   public static List<JsonEntry> loadArray(String json) throws IOException {
      return parse(json).getAsArray();
   }

   /**
    * Quicker method for loading a json string into a <code>Map</code> of <code>String</code> keys and
    * <code>Object</code> values. This method does not use information about {@link JsonSerializable} and instead loads
    * into basic data types.
    *
    * @param json the json to load from
    * @return the map of representing the json object in the string
    * @throws IOException Something went wrong in loading
    */
   public static Map<String, JsonEntry> loadObject(Resource json) throws IOException {
      return parse(json).getAsMap();
   }

   /**
    * Quicker method for loading a json string into a <code>Map</code> of <code>String</code> keys and
    * <code>Object</code> values. This method does not use information about {@link JsonSerializable} and instead loads
    * into basic data types.
    *
    * @param json the json to load from
    * @return the map of representing the json object in the string
    */
   public static Map<String, JsonEntry> loadObject(String json) throws IOException {
      return loadObject(Resources.fromString(json));
   }

   public static JsonEntry parse(String json) throws IOException {
      return parse(Resources.fromString(json));
   }

   public static JsonEntry parse(Resource json) throws IOException {
      try (JsonReader reader = createReader(json)) {
         return reader.nextElement();
      }
   }


}//END OF Json
