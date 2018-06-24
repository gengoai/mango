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

package com.gengoai.graph;

import com.gengoai.Validation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.gengoai.Validation.notNull;

/**
 * @author David B. Bracewell
 */
public class Vertex implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String label;
   private final Map<String, String> properties;

   public Vertex(String label, Map<String, String> properties) {
      Validation.notNullOrBlank(label, "label cannot be null");
      this.label = label;
      this.properties = notNull(properties);
   }

   public static VertexBuilder builder() {
      return new VertexBuilder();
   }

   protected boolean canEqual(Object other) {
      return other instanceof Vertex;
   }

   public String getLabel() {
      return this.label;
   }

   public Map<String, String> getProperties() {
      return this.properties;
   }

   @Override
   public int hashCode() {
      return Objects.hash(label, properties);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {return true;}
      if (obj == null || getClass() != obj.getClass()) {return false;}
      final Vertex other = (Vertex) obj;
      return Objects.equals(this.label, other.label)
                && Objects.equals(this.properties, other.properties);
   }

   public String toString() {
      return "Vertex(label=" + this.label + ", properties=" + this.properties + ")";
   }

   public static class VertexBuilder {
      private String label;
      private Map<String, String> properties = new HashMap<>();

      VertexBuilder() {
      }

      public Vertex build() {
         return new Vertex(label, new HashMap<>(properties));
      }

      public VertexBuilder clearProperties() {
         this.properties.clear();
         return this;
      }

      public VertexBuilder label(String label) {
         this.label = label;
         return this;
      }

      public VertexBuilder properties(Map<? extends String, ? extends String> properties) {
         this.properties.clear();
         this.properties.putAll(properties);
         return this;
      }

      public VertexBuilder property(String propertyKey, String propertyValue) {
         this.properties.putIfAbsent(propertyKey, propertyValue);
         return this;
      }

      @Override
      public String toString() {
         return "VertexBuilder{" +
                   "label='" + label + '\'' +
                   ", properties=" + properties +
                   '}';
      }
   }
}//END OF Vertex
