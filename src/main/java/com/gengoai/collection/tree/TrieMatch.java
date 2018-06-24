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

package com.gengoai.collection.tree;

import java.io.Serializable;

/**
 * @author David B. Bracewell
 */
public class TrieMatch<V> implements Serializable {
   private static final long serialVersionUID = 1L;
   public final int start;
   public final int end;
   public final V value;

   public TrieMatch(int start, int end, V value) {
      this.end = end;
      this.start = start;
      this.value = value;
   }

   public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof TrieMatch)) return false;
      final TrieMatch other = (TrieMatch) o;
      if (this.getStart() != other.getStart()) return false;
      if (this.getEnd() != other.getEnd()) return false;
      final Object this$value = this.getValue();
      final Object other$value = other.getValue();
      if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
      return true;
   }

   public int getEnd() {
      return this.end;
   }

   public String getMatch(String content) {
      return content.substring(start, end);
   }

   public int getStart() {
      return this.start;
   }

   public V getValue() {
      return this.value;
   }

   public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + this.getStart();
      result = result * PRIME + this.getEnd();
      final Object $value = this.getValue();
      result = result * PRIME + ($value == null ? 43 : $value.hashCode());
      return result;
   }

   public String toString() {
      return "TrieMatch(start=" + this.getStart() + ", end=" + this.getEnd() + ", value=" + this.getValue() + ")";
   }
}//END OF TrieMatch
