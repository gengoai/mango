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

import com.gengoai.Validation;
import com.gengoai.io.FileUtils;
import com.gengoai.string.Strings;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Abstract base resource
 *
 * @author David B. Bracewell
 */
public abstract class BaseResource implements Resource, Serializable {
   private static final long serialVersionUID = 1L;

   private Charset charset = StandardCharsets.UTF_8;
   private boolean isCompressed = false;

   @Override
   public final Charset getCharset() {
      if (charset == null) {
         return StandardCharsets.UTF_8;
      }
      return charset;
   }

   @Override
   public final boolean isCompressed() {
      return isCompressed;
   }

   @Override
   public final Resource setIsCompressed(boolean isCompressed) {
      this.isCompressed = isCompressed;
      return this;
   }

   @Override
   public final Resource setCharset(Charset charset) {
      this.charset = charset;
      return this;
   }

   @Override
   public final Resource compressed() {
      return setIsCompressed(true);
   }

   @Override
   public final Resource uncompressed() {
      return setIsCompressed(false);
   }

   @Override
   public InputStream inputStream() throws IOException {
      Validation.checkState(canRead(), "This is resource cannot be read from.");
      PushbackInputStream is = new PushbackInputStream(createInputStream(), 2);
      if (FileUtils.isCompressed(is)) {
         setIsCompressed(true);
         return new GZIPInputStream(is);
      }
      return is;
   }

   @Override
   public OutputStream outputStream() throws IOException {
      Validation.checkState(canWrite(), "This is resource cannot be written to.");
      if (isCompressed) {
         return new GZIPOutputStream(createOutputStream());
      }
      return createOutputStream();
   }

   /**
    * Create output stream output stream.
    *
    * @return the output stream
    * @throws IOException the io exception
    */
   protected OutputStream createOutputStream() throws IOException {
      if (asFile().isPresent()) {
         return new FileOutputStream(asFile().orElseThrow(NullPointerException::new));
      }
      throw new UnsupportedOperationException();
   }

   /**
    * Create input stream input stream.
    *
    * @return the input stream
    * @throws IOException the io exception
    */
   protected InputStream createInputStream() throws IOException {
      if (asFile().isPresent()) {
         return new FileInputStream(asFile().orElseThrow(NullPointerException::new));
      }
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean canRead() {
      return true;
   }

   @Override
   public boolean canWrite() {
      return true;
   }

   @Override
   public final String toString() {
      return descriptor();
   }

   @Override
   public Optional<File> asFile() {
      return Optional.empty();
   }

   @Override
   public Optional<URI> asURI() {
      return Optional.empty();
   }

   @Override
   public String baseName() {
      return Strings.EMPTY;
   }

   @Override
   public String descriptor() {
      return super.toString();
   }

   @Override
   public boolean isDirectory() {
      return false;
   }

   @Override
   public String path() {
      return Strings.EMPTY;
   }

}//END OF BaseResource
