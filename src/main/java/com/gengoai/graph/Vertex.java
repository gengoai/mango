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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

   public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Vertex)) return false;
      final Vertex other = (Vertex) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$label = this.label;
      final Object other$label = other.label;
      if (this$label == null ? other$label != null : !this$label.equals(other$label)) return false;
      final Object this$properties = this.properties;
      final Object other$properties = other.properties;
      if (this$properties == null ? other$properties != null : !this$properties.equals(other$properties)) return false;
      return true;
   }

   public String getLabel() {
      return this.label;
   }

   public Map<String, String> getProperties() {
      return this.properties;
   }

   public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $label = this.label;
      result = result * PRIME + ($label == null ? 43 : $label.hashCode());
      final Object $properties = this.properties;
      result = result * PRIME + ($properties == null ? 43 : $properties.hashCode());
      return result;
   }

   public String toString() {
      return "Vertex(label=" + this.label + ", properties=" + this.properties + ")";
   }

   public static class VertexBuilder {
      private String label;
      private ArrayList<String> properties$key;
      private ArrayList<String> properties$value;

      VertexBuilder() {
      }

      public Vertex build() {
         Map<String, String> properties = new HashMap<>();
         for (int $i = 0; $i < this.properties$key.size(); $i++)
            properties.put(this.properties$key.get($i), this.properties$value.get($i));
         properties = java.util.Collections.unmodifiableMap(properties);
         return new Vertex(label, properties);
      }

      public VertexBuilder clearProperties() {
         if (this.properties$key != null) {
            this.properties$key.clear();
            this.properties$value.clear();
         }

         return this;
      }

      public VertexBuilder label(String label) {
         this.label = label;
         return this;
      }

      public VertexBuilder properties(Map<? extends String, ? extends String> properties) {
         if (this.properties$key == null) {
            this.properties$key = new ArrayList<String>();
            this.properties$value = new ArrayList<String>();
         }
         for (final Map.Entry<? extends String, ? extends String> $lombokEntry : properties.entrySet()) {
            this.properties$key.add($lombokEntry.getKey());
            this.properties$value.add($lombokEntry.getValue());
         }
         return this;
      }

      public VertexBuilder property(String propertyKey, String propertyValue) {
         if (this.properties$key == null) {
            this.properties$key = new ArrayList<String>();
            this.properties$value = new ArrayList<String>();
         }
         this.properties$key.add(propertyKey);
         this.properties$value.add(propertyValue);
         return this;
      }

      public String toString() {
         return "Vertex.VertexBuilder(label=" + this.label + ", properties$key=" + this.properties$key + ", properties$value=" + this.properties$value + ")";
      }
   }
}//END OF Vertex
