package com.davidbracewell.json;

import com.davidbracewell.conversion.Val;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.resource.StringResource;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Json.
 *
 * @author David B. Bracewell
 */
public final class Json {

   private Json() {
      throw new IllegalAccessError();
   }


   /**
    * Reads the resource in the format to a map.
    *
    * @param resource the resource
    * @return the data in the resource as a map
    * @throws IOException something went wrong reading the resource
    */
   public static Map<String, Val> loads(Resource resource) throws IOException {
      return loads(resource.readToString());
   }

   /**
    * Reads the resource in the format to a map.
    *
    * @param data the data
    * @return the data in the resource as a map
    */
   @SneakyThrows
   public static Map<String, Val> loads(String data) {
      Map<String, Val> r = new HashMap<>();
      try (JsonReader reader = new JsonReader(new StringResource(data))) {
         reader.beginDocument();
         while (reader.peek() != JsonTokenType.END_DOCUMENT) {
            String name = reader.peekName();
            switch (reader.peek()) {
               case BEGIN_OBJECT:
                  r.put(name, Val.of(reader.nextMap()));
                  break;
               case BEGIN_ARRAY:
                  r.put(name, Val.of(reader.nextCollection(ArrayList::new)));
                  break;
               case NAME:
                  r.put(name, reader.nextKeyValue(name));
                  break;
               default:
                  reader.skip();
            }
         }
         reader.endDocument();
      }
      return r;
   }

   /**
    * Dumps a map in this format to a string.
    *
    * @param map the map to dump
    * @return the string representation of the map
    */
   @SneakyThrows
   public static String dumps(@NonNull Map<String, ?> map) {
      Resource strResource = new StringResource();
      try (JsonWriter writer = new JsonWriter(strResource)) {
         writer.beginDocument();
         for (Map.Entry<String, ?> entry : map.entrySet()) {
            writer.property(entry.getKey(), entry.getValue());
         }
         writer.endDocument();
      }
      return strResource.readToString().trim();
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
