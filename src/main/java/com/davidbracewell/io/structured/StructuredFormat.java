/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.davidbracewell.io.structured;

import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.resource.StringResource;
import com.davidbracewell.io.structured.json.JSONReader;
import com.davidbracewell.io.structured.json.JSONWriter;
import com.davidbracewell.io.structured.xml.XMLReader;
import com.davidbracewell.io.structured.xml.XMLWriter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines a common interface for creating structured readers and writers
 *
 * @author David B. Bracewell
 */
public interface StructuredFormat extends Serializable {

   /**
    * Creates a new Structured Reader reading from the given resource.
    *
    * @param resource the resource to read from
    * @return the structured reader
    * @throws IOException Something went wrong initializing the reader
    */
   StructuredReader createReader(Resource resource) throws IOException;


   /**
    * Creates a new Structured Writer writing to the given resource.
    *
    * @param resource the resource to write from
    * @return the structured writer
    * @throws IOException Something went wrong initializing the writer
    */
   StructuredWriter createWriter(Resource resource) throws IOException;


   /**
    * Reads the resource in the format to a map.
    *
    * @param resource the resource
    * @return the data in the resource as a map
    * @throws IOException something went wrong reading the resource
    */
   default Map<String, ?> loads(Resource resource) throws IOException {
      return loads(resource.readToString());
   }

   /**
    * Reads the resource in the format to a map.
    *
    * @param data the data
    * @return the data in the resource as a map
    */
   @SneakyThrows
   default Map<String, ?> loads(String data) {
      Map<String, Object> r = new HashMap<>();
      try (StructuredReader reader = createReader(new StringResource(data))) {
         reader.beginDocument();
         while (reader.peek() != ElementType.END_DOCUMENT) {
            String name = reader.peekName();
            switch (reader.peek()) {
               case BEGIN_OBJECT:
                  r.put(name, reader.nextMap());
                  break;
               case BEGIN_ARRAY:
                  r.put(name, reader.nextCollection(ArrayList::new));
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
   default String dumps(@NonNull Map<String, ?> map) {
      Resource strResource = new StringResource();
      try (StructuredWriter writer = createWriter(strResource)) {
         writer.beginDocument();
         for (Map.Entry<String, ?> entry : map.entrySet()) {
            writer.writeKeyValue(entry.getKey(), entry.getValue());
         }
         writer.endDocument();
      }
      return strResource.readToString().trim();
   }


   /**
    * XML Format
    */
   StructuredFormat XML = new StructuredFormat() {

      private static final long serialVersionUID = 1297100190960314727L;

      @Override
      public StructuredReader createReader(Resource resource) throws IOException {
         return new XMLReader(resource);
      }

      @Override
      public StructuredWriter createWriter(Resource resource) throws IOException {
         return new XMLWriter(resource);
      }
   };


   /**
    * JSON Format
    */
   StructuredFormat JSON = new StructuredFormat() {
      private static final long serialVersionUID = 1297100190960314727L;

      @Override
      public StructuredReader createReader(Resource resource) throws IOException {
         return new JSONReader(resource);
      }

      @Override
      public StructuredWriter createWriter(Resource resource) throws IOException {
         return new JSONWriter(resource);
      }

   };

}//END OF StructuredFormat
