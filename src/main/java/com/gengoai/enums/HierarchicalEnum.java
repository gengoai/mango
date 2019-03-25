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

package com.gengoai.enums;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author David B. Bracewell
 */
public abstract class HierarchicalEnum<T extends HierarchicalEnum> extends EnumValue {
   protected final int depth;

   /**
    * Instantiates a new enum value.
    *
    * @param name the name of the enum value
    */
   protected HierarchicalEnum(String name) {
      super(name);
      this.depth = (int) name().chars().filter(i -> i == ':').count();
   }

   protected abstract Registry<T> registry();

   public int depth() {
      return depth;
   }

   public List<T> children() {
      final String target = name() + ".";
      return registry().values()
                       .parallelStream()
                       .filter(v -> v.depth() == (depth() + 1) && v.name().startsWith(target))
                       .collect(Collectors.toList());
   }

   public boolean isLeaf() {
      return children().isEmpty();
   }

   public T parent() {
      int idx = name().indexOf(':');
      if (idx < 0) {
         return null;
      }
      return registry().valueOf(name().substring(0, idx));
   }

}//END OF HierarchicalEnum
