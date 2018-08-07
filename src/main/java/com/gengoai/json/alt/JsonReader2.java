package com.gengoai.json.alt;

import com.gengoai.Validation;
import com.gengoai.conversion.Val;
import com.gengoai.io.resource.Resource;
import com.gengoai.json.Json;
import com.gengoai.tuple.Tuple2;
import com.google.gson.*;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import static com.gengoai.tuple.Tuples.$;

/**
 * @author David B. Bracewell
 */
public class JsonReader2 implements AutoCloseable, Closeable {

   private final com.google.gson.stream.JsonReader reader;

   public JsonReader2(Resource resource) throws IOException {
      this.reader = new JsonReader(resource.reader());
   }

   public boolean hasNext() throws IOException {
      return reader.hasNext() && peek() != JsonToken.END_DOCUMENT;
   }

   public JsonReader2 beginObject() throws IOException {
      reader.beginObject();
      return this;
   }

   public JsonReader2 beginObject(String name) throws IOException {
      String nextName = nextName();
      Validation.checkState(nextName.equals(name), "Next Object is named (" +
                                                      nextName + "), but was expecting (" + name + ")");
      reader.beginObject();
      return this;
   }

   public JsonReader2 beginArray() throws IOException {
      reader.beginArray();
      return this;
   }

   public JsonReader2 beginArray(String name) throws IOException {
      String nextName = nextName();
      Validation.checkState(nextName.equals(name), "Next Array is named (" +
                                                      nextName + "), but was expecting (" + name + ")");
      reader.beginArray();
      return this;
   }

   public JsonToken beginDocument() throws IOException {
      if (peek() == JsonToken.BEGIN_OBJECT) {
         reader.beginObject();
         return JsonToken.BEGIN_OBJECT;
      } else if (peek() == JsonToken.BEGIN_ARRAY) {
         reader.beginArray();
         return JsonToken.BEGIN_ARRAY;
      }
      throw new IOException("Unable to start document");
   }

   public void endDocument() throws IOException {
      if (peek() != JsonToken.END_DOCUMENT) {
         throw new IOException("Unable to end document");
      }
   }

   public Tuple2<String, Val> nextProperty() throws IOException {
      return $(nextName(), nextValue());
   }

   public Tuple2<String, Boolean> nextBooleanProperty() throws IOException {
      return $(nextName(), nextBoolean());
   }

   public Tuple2<String, String> nextStringProperty() throws IOException {
      return $(nextName(), nextString());
   }

   public Tuple2<String, Double> nextDoubleProperty() throws IOException {
      return $(nextName(), nextDouble());
   }

   public Tuple2<String, Long> nextLongProperty() throws IOException {
      return $(nextName(), nextLong());
   }

   public Tuple2<String, JsonElement> nextElementProperty() throws IOException {
      return $(nextName(), nextElement());
   }

   public Val nextValue() throws IOException {
      switch (reader.peek()) {
         case BOOLEAN:
            return Val.of(nextBoolean());
         case STRING:
            return Val.of(nextString());
         case NULL:
            return Val.NULL;
         case NUMBER:
            double val = reader.nextDouble();
            if (val == (int) val) {
               return Val.of((int) val);
            } else if (val == (long) val) {
               return Val.of((long) val);
            } else {
               return Val.of(val);
            }
         case BEGIN_OBJECT:
            return Val.of(Json.gson.fromJson(nextElement(), Json.type));
         case BEGIN_ARRAY:
            return Val.of(Json.gson.fromJson(nextElement(), new TypeToken<List<Object>>() {
            }.getType()));
      }
      throw new IOException();
   }

   public JsonToken peek() throws IOException {
      return reader.peek();
   }

   public boolean nextBoolean() throws IOException {
      return reader.nextBoolean();
   }

   public double nextDouble() throws IOException {
      return reader.nextDouble();
   }

   public long nextLong() throws IOException {
      return reader.nextLong();
   }

   public String nextName() throws IOException {
      return reader.nextName();
   }

   public String nextString() throws IOException {
      return reader.nextString();
   }

   public JsonElement nextElement() throws IOException {
      JsonToken nextToken = reader.peek();
      switch (nextToken) {
         case NUMBER:
            String number = reader.nextString();
            return new JsonPrimitive(new LazilyParsedNumber(number));
         case BOOLEAN:
            return new JsonPrimitive(reader.nextBoolean());
         case STRING:
            return new JsonPrimitive(reader.nextString());
         case NULL:
            reader.nextNull();
            return JsonNull.INSTANCE;
         case BEGIN_ARRAY:
            JsonArray array = new JsonArray();
            reader.beginArray();

            while (reader.hasNext() && reader.peek() != JsonToken.END_ARRAY) {
               array.add(nextElement());
            }

            reader.endArray();
            return array;
         case BEGIN_OBJECT:
            JsonObject object = new JsonObject();
            reader.beginObject();

            while (reader.hasNext() && reader.peek() != JsonToken.END_OBJECT) {
               object.add(reader.nextName(), nextElement());
            }

            reader.endObject();
            return object;
         case END_DOCUMENT:
         case NAME:
            JsonObject kv = new JsonObject();
            kv.add(reader.nextName(), nextElement());
            return kv;
         case END_OBJECT:
         case END_ARRAY:
         default:
            throw new IllegalArgumentException(nextToken + " is invalid");
      }
   }

   @Override
   public void close() throws IOException {
      reader.close();
   }
}//END OF JsonReader2
