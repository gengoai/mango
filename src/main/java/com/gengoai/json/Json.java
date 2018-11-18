package com.gengoai.json;

import com.gengoai.io.Resources;
import com.gengoai.io.resource.Resource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * <p>Convenience methods for serializing and deserializing objects to and from json and creating json reader and
 * writers.</p>
 *
 * @author David B. Bracewell
 */
public final class Json {

   private Json() {
      throw new IllegalAccessError();
   }

   /**
    * Creates a <code>JsonReader</code> to read from the given resource.
    *
    * @param resource the resource to read
    * @return the json reader
    * @throws IOException Something went wrong creating the reader
    */
   public static JsonReader createReader(Resource resource) throws IOException {
      return new JsonReader(resource);
   }

   /**
    * Creates a <code>JsonWriter</code> to write to the given resource.
    *
    * @param resource the resource
    * @return the json writer
    * @throws IOException something went wrong creating the writer
    */
   public static JsonWriter createWriter(Resource resource) throws IOException {
      return new JsonWriter(resource);
   }

   /**
    * Dumps the given object to the given output location in json format. This method is {@link JsonSerializable}
    * aware.
    *
    * @param object   the object to dump
    * @param resource the resource to write the dumped object in json format to.
    * @return the resource
    * @throws IOException Something went wrong writing to the given resource
    */
   public static Resource dump(Object object, Resource resource) throws IOException {
      try (JsonWriter writer = new JsonWriter(resource)) {
         JsonEntry objJson = JsonEntry.from(object);
         if (objJson.isPrimitive()) {
            writer.beginDocument(true);
         }
         writer.write(objJson);
         if (objJson.isPrimitive()) {
            writer.endDocument();
         }
      }
      return resource;
   }

   /**
    * Dumps the given object to a string in json format. This method is {@link JsonSerializable} aware.
    *
    * @param object the object to dump
    * @return the object as a json string
    */
   public static String dumps(Object object) {
      try {
         return dump(object, Resources.fromString()).readToString();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Loads an array of objects from the given resource in json format.
    *
    * @param resource the resource to read from
    * @return the list of objects read in from the resource
    * @throws IOException Something went wrong reading from the resource
    */
   public static List<JsonEntry> parseArray(Resource resource) throws IOException {
      return parse(resource).getAsArray();
   }

   /**
    * Parses a json string into a list of {@link JsonEntry}
    *
    * @param json the json string to load the array from
    * @return the list of objects parsed from the string
    * @throws IOException Something went wrong parsing the json string
    */
   public static List<JsonEntry> parseArray(String json) throws IOException {
      return parse(json).getAsArray();
   }

   /**
    * Quicker method for parsing a json string into a <code>Map</code> of <code>String</code> keys and
    * <code>Object</code> values.
    *
    * @param json the json to load from
    * @return the map of representing the json object in the string
    * @throws IOException Something went wrong parsing the json in the resource
    */
   public static Map<String, JsonEntry> parseObject(Resource json) throws IOException {
      return parse(json).getAsMap();
   }


   /**
    * Parses the json in the given resource creating an object of the given class type. This method is {@link
    * JsonSerializable}* aware and is useful for deserializing objects from json format.
    *
    * @param <T>      the class type parameter
    * @param resource the resource to read from
    * @param clazz    the class information for the object to deserialized
    * @return the deserialized object
    * @throws IOException something went wrong reading the json
    */
   public static <T> T parseObject(Resource resource, Type clazz) throws IOException {
      return parse(resource).getAs(clazz);
   }

   /**
    * Parses the json in the given json string creating an object of the given class type. This method is {@link
    * JsonSerializable}* aware and is useful for deserializing objects from json format.
    *
    * @param <T>   the class type parameter
    * @param json  the json to read
    * @param clazz the class information for the object to deserialized
    * @return the deserialized object
    * @throws IOException something went wrong reading the json
    */
   public static <T> T parseObject(String json, Type clazz) throws IOException {
      return parse(json).getAs(clazz);
   }

   /**
    * Quicker method for parsing a json string into a <code>Map</code> of <code>String</code> keys and
    * <code>Object</code> values.
    *
    * @param json the json to load from
    * @return the map of representing the json object in the string
    * @throws IOException Something went wrong parsing the json string
    */
   public static Map<String, JsonEntry> parseObject(String json) throws IOException {
      return parseObject(Resources.fromString(json));
   }

   /**
    * Parses the json string.
    *
    * @param json the json string to parse
    * @return the parsed string as a json entry
    * @throws IOException something went wrong parsing the json.
    */
   public static JsonEntry parse(String json) throws IOException {
      return parse(Resources.fromString(json));
   }

   /**
    * Parse the json in the given string returning the given type.
    *
    * @param <T>  the type parameter
    * @param json the json
    * @param type the type
    * @return the parsed object
    * @throws IOException Something went wrong parsing the json.
    */
   public static <T> T parse(String json, Type type) throws IOException {
      return parse(Resources.fromString(json)).getAs(type);
   }


   /**
    * Parses the given resource as json entry
    *
    * @param json the resource to read from
    * @return the parsed resource as a json entry
    * @throws IOException Something went wrong parsing the resource
    */
   public static JsonEntry parse(Resource json) throws IOException {
      try (JsonReader reader = createReader(json)) {
         return reader.nextElement();
      }
   }


   /**
    * Parse t.
    *
    * @param <T>  the type parameter
    * @param json the json
    * @param type the type
    * @return the t
    * @throws IOException the io exception
    */
   public static <T> T parse(Resource json, Type type) throws IOException {
      return parse(json).getAs(type);
   }

}//END OF Json
