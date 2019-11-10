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

import com.gengoai.conversion.Cast;
import com.gengoai.conversion.Converter;
import com.gengoai.conversion.TypeConversionException;
import com.gengoai.reflection.Reflect;
import com.gengoai.reflection.ReflectionException;
import com.gengoai.string.Strings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;

import static com.gengoai.reflection.TypeUtils.parameterizedType;

/**
 * The interface Specifiable.
 *
 * @author David B. Bracewell
 */
public interface Specifiable {

   /**
    * Gets schema.
    *
    * @return the schema
    */
   String getSchema();

   default String toSpecification() {
      Specification.SpecificationBuilder b = Specification.builder();
      b.schema(getSchema());
      try {
         Reflect r = Reflect.onObject(this).allowPrivilegedAccess();
         for (Field field : r.getFields()) {
            for (Annotation annotation : field.getAnnotations()) {
               if (annotation instanceof Protocol) {
                  b.protocol(Converter.convert(r.get(field.getName()).get(), String.class));
               } else if (annotation instanceof Path) {
                  b.path(Converter.convert(r.get(field.getName()).get(), String.class));
               } else if (annotation instanceof SubProtocol) {
                  SubProtocol subProtocol = Cast.as(annotation);
                  if (subProtocol.value() >= 0) {
                     b.subProtocol(subProtocol.value(),
                                   Converter.convert(r.get(field.getName()).get(), String.class));
                  } else {
                     b.subProtocol(Converter.<Collection<String>>convert(r.get(field.getName()).get(),
                                                                         parameterizedType(Collection.class,
                                                                                           String.class)));
                  }
               } else if (annotation instanceof QueryParameter) {
                  QueryParameter qp = Cast.as(annotation);
                  String key = Strings.isNullOrBlank(qp.value()) ? field.getName() : qp.value();
                  Object o = r.get(field.getName()).get();
                  if (o == null) {
                     continue;
                  }
                  if (o instanceof Iterable) {
                     for (Object a : Cast.<Iterable<?>>as(o)) {
                        b.queryParameter(key, Converter.convert(a, String.class));
                     }
                  } else if (o.getClass().isArray()) {
                     for (int i = 0; i < Array.getLength(o); i++) {
                        b.queryParameter(key, Converter.convert(Array.get(o, i), String.class));
                     }
                  } else {
                     b.queryParameter(key, Converter.convert(o, String.class));
                  }
               }
            }
         }
      } catch (ReflectionException | TypeConversionException e) {
         throw new RuntimeException(e);
      }
      return b.build().toString();
   }

}//END OF Specifiable
