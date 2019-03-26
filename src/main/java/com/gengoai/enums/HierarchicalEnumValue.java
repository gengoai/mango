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

import com.gengoai.Tag;
import com.gengoai.conversion.Cast;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Hierarchical enum value.
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public abstract class HierarchicalEnumValue<T extends HierarchicalEnumValue> extends EnumValue {
   public static final char SEPARATOR = '$';
   private final int depth;

   /**
    * Instantiates a new enum value.
    *
    * @param name the name of the enum value
    */
   protected HierarchicalEnumValue(String name) {
      super(name);
      this.depth = (int) name().chars().filter(i -> i == SEPARATOR).count();
   }

   /**
    * Children list.
    *
    * @return the list
    */
   public List<T> children() {
      final String target = name() + SEPARATOR;
      return registry().values()
                       .parallelStream()
                       .filter(v -> v.depth() == (depth() + 1) && v.name().startsWith(target))
                       .collect(Collectors.toList());
   }

   /**
    * Depth int.
    *
    * @return the int
    */
   public int depth() {
      return depth;
   }

   @Override
   public final boolean isInstance(Tag value) {
      HierarchicalEnumValue<T> hev = this;
      while (hev != null && hev != registry().ROOT) {
         if (hev.equals(value)) {
            return true;
         }
         hev = Cast.as(hev.parent());
      }
      return false;
   }

   /**
    * Is leaf boolean.
    *
    * @return the boolean
    */
   public boolean isLeaf() {
      return children().isEmpty();
   }

   @Override
   public String label() {
      int index = name().lastIndexOf(SEPARATOR);
      if (index > 0) {
         return name().substring(index + 1);
      }
      return name();
   }

   /**
    * Parent t.
    *
    * @return the t
    */
   public T parent() {
      if (this == registry().ROOT) {
         return null;
      }
      int idx = name().indexOf(SEPARATOR);
      if (idx < 0) {
         return registry().ROOT;
      }
      return registry().valueOf(name().substring(0, idx));
   }

   public boolean isRoot() {
      return parent() == null;
   }

   public String[] path() {
      return name().split(Character.toString(SEPARATOR));
   }

   /**
    * Registry registry.
    *
    * @return the registry
    */
   protected abstract HierarchicalRegistry<T> registry();

}//END OF HierarchicalEnum
