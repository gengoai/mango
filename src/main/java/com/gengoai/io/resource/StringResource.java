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

import com.gengoai.stream.MStream;
import com.gengoai.stream.StreamingContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * <p> A resource that wraps a String. </p>
 *
 * @author David B. Bracewell
 */
public class StringResource extends BaseResource implements NonTraversableResource {

   private static final long serialVersionUID = 8750046186020559958L;
   private final StringBuilder resource;

   /**
    * Instantiates a new string resource.
    */
   public StringResource() {
      this(null);
   }

   protected boolean canEqual(Object other) {
      return other instanceof StringResource;
   }

   public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof StringResource)) return false;
      final StringResource other = (StringResource) o;
      if (!other.canEqual((Object) this)) return false;
      if (!super.equals(o)) return false;
      final Object this$resource = this.resource;
      final Object other$resource = other.resource;
      if (this$resource == null ? other$resource != null : !this$resource.equals(other$resource)) return false;
      return true;
   }

   public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + super.hashCode();
      final Object $resource = this.resource;
      result = result * PRIME + ($resource == null ? 43 : $resource.hashCode());
      return result;
   }

   @Override
   public String readToString() throws IOException {
      return resource.toString();
   }

   @Override
   public byte[] readBytes() throws IOException {
      return resource.toString().getBytes();
   }

   @Override
   public Reader reader() throws IOException {
      return new StringReader(readToString());
   }

   @Override
   public List<String> readLines() throws IOException {
      return Arrays.asList(readToString().split("\r?\n"));
   }

   /**
    * <p> Creates a Resource that is a String. </p>
    *
    * @param resource The string contents.
    */
   public StringResource(String resource) {
      if (resource == null) {
         this.resource = new StringBuilder();
      } else {
         this.resource = new StringBuilder(resource);
      }
   }

   @Override
   public Resource append(String content) throws IOException {
      resource.append(content);
      return this;
   }

   @Override
   public Resource append(byte[] byteArray) throws IOException {
      return append(byteArray == null ? null : new String(byteArray, getCharset()));
   }

   @Override
   public boolean exists() {
      return true;
   }

   @Override
   public MStream<String> lines() throws IOException {
      return StreamingContext.local().stream(resource.toString().split("\r?\n"));
   }

   @Override
   protected InputStream createInputStream() throws IOException {
      return new ByteArrayInputStream(resource.toString().getBytes(StandardCharsets.UTF_8));
   }

   @Override
   protected OutputStream createOutputStream() throws IOException {
      this.resource.setLength(0);
      return new OutputStream() {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         @Override
         public void write(int b) throws IOException {
            baos.write(b);
         }

         @Override
         public void close() throws IOException {
            super.close();
            resource.setLength(0);
            resource.append(baos.toString("UTF-8"));
            baos.close();
         }
      };
   }

}// END OF StringResource
