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

package com.gengoai.io.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Resource that wraps a <code>Reader</code>
 *
 * @author David B. Bracewell
 */
public class ReaderResource extends BaseResource implements ReadOnlyResource, NonTraversableResource {

   private static final long serialVersionUID = 6220239496470153511L;
   private final Reader reader;

   /**
    * Instantiates a new Reader resource.
    *
    * @param reader the reader to wrap
    */
   public ReaderResource(Reader reader) {
      this.reader = reader;
   }

   protected boolean canEqual(Object other) {
      return other instanceof ReaderResource;
   }

   public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof ReaderResource)) return false;
      final ReaderResource other = (ReaderResource) o;
      if (!other.canEqual((Object) this)) return false;
      if (!super.equals(o)) return false;
      final Object this$reader = this.reader;
      final Object other$reader = other.reader;
      if (this$reader == null ? other$reader != null : !this$reader.equals(other$reader)) return false;
      return true;
   }

   public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + super.hashCode();
      final Object $reader = this.reader;
      result = result * PRIME + ($reader == null ? 43 : $reader.hashCode());
      return result;
   }

   @Override
   public Reader reader() throws IOException {
      return reader;
   }

   @Override
   public boolean exists() {
      return true;
   }

   @Override
   public byte[] readBytes() throws IOException {
      try (BufferedReader bufferedReader = new BufferedReader(reader)) {
         StringBuilder builder = new StringBuilder();
         char[] buffer = new char[1024];
         int read;
         while ((read = bufferedReader.read(buffer, 0, buffer.length)) > 0) {
            builder.append(buffer, 0, read);
         }
         return builder.toString().getBytes();
      }
   }
}//END OF ReaderResource
