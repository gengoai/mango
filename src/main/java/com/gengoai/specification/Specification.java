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
 *
 */

package com.gengoai.specification;

import com.gengoai.Validation;
import com.gengoai.collection.Iterables;
import com.gengoai.collection.multimap.ArrayListMultimap;
import com.gengoai.collection.multimap.Multimap;
import com.gengoai.conversion.Cast;
import com.gengoai.conversion.Converter;
import com.gengoai.conversion.TypeConversionException;
import com.gengoai.reflection.Reflect;
import com.gengoai.reflection.ReflectionException;
import com.gengoai.reflection.TypeUtils;
import com.gengoai.string.Strings;
import lombok.*;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gengoai.string.Re.*;

/**
 * The type Specification.
 *
 * @author David B. Bracewell
 */
@EqualsAndHashCode(callSuper = false)
@Builder
public class Specification implements Serializable {
   private static final Pattern SPEC_PATTERN = Pattern.compile(re("^",
                                                                  namedGroup("SCHEMA",
                                                                             oneOrMore(chars(LETTER, "_")),
                                                                             zeroOrMore(q("::"),
                                                                                        oneOrMore(chars(LETTER, "_")))
                                                                            ),
                                                                  q("::"),
                                                                  namedGroup("PATH",
                                                                             zeroOrMore(chars(true, q(";")))),
                                                                  namedGroup("QUERY",
                                                                             zeroOrOne(q(";"), oneOrMore(ANY))),
                                                                  "$"), Pattern.CASE_INSENSITIVE);
   private static final long serialVersionUID = 1L;
   @NonNull
   @Getter
   private final String path;
   @NonNull
   @Getter
   private final String protocol;
   @NonNull
   private final ArrayListMultimap<String, String> queryParameters = new ArrayListMultimap<>();
   @NonNull
   @Getter
   private final String schema;
   @NonNull
   private final List<String> subProtocol;

   private Specification(@NonNull String schema,
                         @NonNull String protocol,
                         @NonNull List<String> subProtocol,
                         @NonNull String path) {
      this.schema = schema;
      this.protocol = protocol;
      this.subProtocol = subProtocol;
      this.path = path;
   }

   private static void getQueryParameters(String query, Multimap<String, String> map) {
      if (Strings.isNotNullOrBlank(query)) {
         Pattern.compile(";")
                .splitAsStream(query.substring(1))
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .forEach(s -> map.put(s[0].trim(), s[1].trim()));
      }
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder(schema).append("::");
      if (Strings.isNotNullOrBlank(protocol)) {
         builder.append(protocol).append("::");
      }
      if (subProtocol != null) {
         subProtocol.forEach(sp -> builder.append(sp).append("::"));
      }
      if (Strings.isNotNullOrBlank(path)) {
         builder.append(path);
      }
      for (Map.Entry<String, String> entry : queryParameters.entries()) {
         builder.append(";").append(entry.getKey()).append("=").append(entry.getValue());
      }
      return builder.toString();
   }

   public static <T extends Specifiable> T parse(String specification,
                                                 @NonNull Class<T> tClass) throws ReflectionException {
      Specification spec = parse(specification);
      Reflect r = Reflect.onClass(tClass).create().allowPrivilegedAccess();
      Validation.checkArgument(r.<Specifiable>get().getSchema().equals(spec.getSchema()),
                               "Invalid Schema: " + specification);
      for (Field field : r.getFields()) {
         for (Annotation annotation : field.getAnnotations()) {
            if (annotation instanceof Protocol) {
               setField(r, field, spec.getProtocol());
            } else if (annotation instanceof Path) {
               setField(r, field, spec.getPath());
            } else if (annotation instanceof SubProtocol) {
               SubProtocol subProtocol = Cast.as(annotation);
               if (subProtocol.value() >= 0) {
                  setField(r, field, spec.getSubProtocol(subProtocol.value()));
               } else {
                  setField(r, field, spec.getSubProtocol());
               }
            } else if (annotation instanceof QueryParameter) {
               QueryParameter qp = Cast.as(annotation);
               String key = Strings.isNullOrBlank(qp.value()) ? field.getName() : qp.value();
               Type type = field.getGenericType();
               if (TypeUtils.isAssignable(Iterable.class, type) || TypeUtils.asClass(type).isArray()) {
                  setField(r, field, spec.getQueryParameterValues(key));
               } else {
                  setField(r, field, spec.getQueryParameterValue(key, null));
               }
            }
         }
      }
      return r.get();
   }

   /**
    * Parse specification.
    *
    * @param specificationString the specification string
    * @return the specification
    */
   public static Specification parse(String specificationString) {
      Matcher m = SPEC_PATTERN.matcher(Validation.notNullOrBlank(specificationString));
      if (m.find()) {
         String[] schema = m.group("SCHEMA").split("::");
         String protocol = schema.length > 1 ? schema[1] : Strings.EMPTY;
         List<String> subProtocol = schema.length > 2
                                    ? Arrays.asList(Arrays.copyOfRange(schema, 2, schema.length))
                                    : Collections.emptyList();
         Specification specification = new Specification(schema[0],
                                                         protocol,
                                                         subProtocol,
                                                         m.group("PATH"));
         getQueryParameters(m.group("QUERY"), specification.queryParameters);
         return specification;
      }
      throw new IllegalArgumentException("Invalid Specification: " + specificationString);
   }

   private static void setField(Reflect r, Field field, Object value) throws ReflectionException {
      if (value == null) {
         return;
      }
      try {
         r.set(field.getName(), Converter.convert(value, field.getGenericType()));
      } catch (TypeConversionException e) {
         throw new ReflectionException(e.getMessage());
      }
   }

   /**
    * Gets query parameter value.
    *
    * @param parameter    the parameter
    * @param defaultValue the default value
    * @return the query parameter value
    */
   public String getQueryParameterValue(String parameter, String defaultValue) {
      return Iterables.getFirst(queryParameters.get(parameter), defaultValue);
   }

   /**
    * Gets query parameter values.
    *
    * @param parameter the parameter
    * @return the query parameter values
    */
   public List<String> getQueryParameterValues(String parameter) {
      return Collections.unmodifiableList(queryParameters.get(parameter));
   }

   /**
    * Gets sub protocol.
    *
    * @param index the index
    * @return the sub protocol
    */
   public String getSubProtocol(int index) {
      return index >= 0 && index < subProtocol.size()
             ? subProtocol.get(index)
             : null;
   }

   /**
    * Get sub protocol string [ ].
    *
    * @return the string [ ]
    */
   public List<String> getSubProtocol() {
      return Collections.unmodifiableList(subProtocol);
   }

   /**
    * Gets sub protocol count.
    *
    * @return the sub protocol count
    */
   public int getSubProtocolCount() {
      return subProtocol.size();
   }

   /**
    * The type Specification builder.
    */
   public static class SpecificationBuilder {
      private Multimap<String, String> parameters = new ArrayListMultimap<>();
      private List<String> subProtocol = new ArrayList<>();


      public SpecificationBuilder subProtocol(int index, String subProtocol) {
         this.subProtocol.add(index, subProtocol);
         return this;
      }

      public SpecificationBuilder subProtocol(String subProtocol) {
         this.subProtocol.add(subProtocol);
         return this;
      }

      public SpecificationBuilder subProtocol(Collection<String> subProtocol) {
         this.subProtocol.addAll(subProtocol);
         return this;
      }

      /**
       * Build specification.
       *
       * @return the specification
       */
      public Specification build() {
         Specification specification = new Specification(schema, protocol, subProtocol, path);
         specification.queryParameters.putAll(parameters);
         return specification;
      }

      /**
       * Clear query parameters specification builder.
       *
       * @return the specification builder
       */
      public SpecificationBuilder clearQueryParameters() {
         parameters.clear();
         return this;
      }

      /**
       * Query parameter specification builder.
       *
       * @param name  the name
       * @param value the value
       * @return the specification builder
       */
      public SpecificationBuilder queryParameter(String name, String value) {
         parameters.put(name, value);
         return this;
      }

   }


}//END OF Specification
