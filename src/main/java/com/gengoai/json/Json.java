package com.gengoai.json;

import com.gengoai.io.Resources;
import com.gengoai.io.resource.Resource;
import com.gengoai.io.resource.StringResource;
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
    * The Map type.
    */
   static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {
   }.getType();
   /**
    * The List type.
    */
   static final Type LIST_TYPE = new TypeToken<List<Object>>() {
   }.getType();
   /**
    * The Gson.
    */
   static final Gson gson = new Gson();


   private Json() {
      throw new IllegalAccessError();
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
   public static Map<String, JsonEntry> load(Resource json) throws IOException {
      try (JsonReader reader = createReader(json)) {
         return reader.nextMap();
      }
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
   public static Map<String, JsonEntry> loads(String json) throws IOException {
      return load(Resources.fromString(json));
   }


   /**
    * Dumps a map in this format to a string.
    *
    * @param map the map to dump
    * @return the string representation of the map
    */
   public static String dumps(Map<String, ?> map) {
      try {
         Resource strResource = new StringResource();
         try (JsonWriter writer = new JsonWriter(strResource)) {
            writer.write(JsonEntry.from(map));
         }
         return strResource.readToString().trim();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
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


}//END OF Json
