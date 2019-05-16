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

package com.gengoai.persistence;

import com.gengoai.conversion.Cast;
import com.gengoai.json.JsonEntry;
import com.gengoai.math.NumericComparison;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author David B. Bracewell
 */
public class Filters {

   public static Filter eq(String field, Object value) {
      return db -> {
         Set<Long> ids;
         if (db.hasIndex(field)) {
            ids = db.getIndex(field).lookup(value).boxed().collect(Collectors.toSet());
         } else {
            JsonEntry je = JsonEntry.from(value);
            ids = db.stream()
                    .filter(doc -> doc.contains(field) && doc.get(field).equals(je))
                    .map(DBDocument::getId)
                    .collect(Collectors.toSet());
         }
         return ids.stream().map(db::get);
      };
   }

   public static Filter between(String field, Object lower, Object upper) {
      return db -> {
         Set<Long> ids;
         if (db.hasIndex(field)) {
            ids = db.getIndex(field).range(lower, upper).boxed().collect(Collectors.toSet());
         } else {
            ids = db.stream()
                    .filter(doc -> {
                       if (doc.contains(field)) {
                          Object o = doc.getAsObject(field);
                          if (o instanceof Number) {
                             Number n = Cast.as(o);
                             return NumericComparison.GTE.apply(n, Cast.as(lower)) &&
                                       NumericComparison.LT.apply(n, Cast.as(upper));
                          } else if (o instanceof Comparable) {
                             Comparable c = Cast.as(o);
                             return c.compareTo(lower) >= 0 && c.compareTo(upper) < 0;
                          }
                          throw new IllegalArgumentException(field + " is not range queryable");
                       }
                       return false;
                    })
                    .map(DBDocument::getId)
                    .collect(Collectors.toSet());
         }
         return ids.stream().map(db::get);
      };
   }

}//END OF Filters
