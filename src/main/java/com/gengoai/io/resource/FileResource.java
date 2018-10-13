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

import com.gengoai.io.QuietIO;
import com.gengoai.io.resource.spi.FileResourceProvider;
import com.gengoai.stream.LocalStream;
import com.gengoai.stream.MStream;
import com.gengoai.string.StringUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * <p> A <code>Resource</code> implementation that wraps a <code>File</code>. </p>
 *
 * @author David B. Bracewell
 */
public class FileResource extends BaseResource {

   private static final long serialVersionUID = 1L;
   private final File file;

   /**
    * <p> Constructs a Resource backed by a File </p>
    *
    * @param file The file to be wrapped.
    */
   public FileResource(File file) {
      this.file = file.getAbsoluteFile();
   }

   /**
    * <p> Constructs a Resource backed by a File </p>
    *
    * @param path The path of the file
    */
   public FileResource(String path) {
      this(new File(path));
   }

   @Override
   public Resource append(byte[] byteArray) throws IOException {
      try (FileOutputStream outputStream = new FileOutputStream(file, true)) {
         outputStream.write(byteArray);
      }
      return this;
   }

   @Override
   public Optional<File> asFile() {
      return Optional.of(file.getAbsoluteFile());
   }

   @Override
   public Optional<URI> asURI() {
      return Optional.of(file.toURI());
   }

   @Override
   public Optional<URL> asURL() {
      try {
         return Optional.of(file.toURI().toURL());
      } catch (MalformedURLException e) {
         return Optional.empty();
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof FileResource;
   }


   @Override
   public String descriptor() {
      return FileResourceProvider.PROTOCOL + ":" + file.getAbsolutePath();
   }

   public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof FileResource)) return false;
      final FileResource other = (FileResource) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$file = this.file;
      final Object other$file = other.file;
      if (this$file == null ? other$file != null : !this$file.equals(other$file)) return false;
      return true;
   }

   public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $file = this.file;
      result = result * PRIME + ($file == null ? 43 : $file.hashCode());
      return result;
   }

   @Override
   public String path() {
      return file.getAbsolutePath();
   }

   @Override
   public String baseName() {
      return file.getName();
   }

   @Override
   public boolean canWrite() {
      return (!file.isDirectory() && !file.exists()) || file.canWrite();
   }

   @Override
   public boolean canRead() {
      return file.canRead();
   }

   @Override
   public Resource getChild(String relativePath) {
      if (relativePath == null) {
         relativePath = StringUtils.EMPTY;
      }
      return new FileResource(new File(file, relativePath.trim()));
   }

   @Override
   public boolean exists() {
      return file.exists();
   }

   @Override
   public boolean isDirectory() {
      return file.isDirectory();
   }

   @Override
   public MStream<String> lines() throws IOException {
      return new LocalStream<>(lineStream());
//      return new LocalStream<>(Files.lines(asPath().orElse(null)));
   }

   private Stream<String> lineStream() {
      try {
         BufferedReader reader = new BufferedReader(reader());
         return reader.lines().onClose(() -> QuietIO.closeQuietly(reader));
      } catch (IOException ioe) {
         throw new UncheckedIOException(ioe);
      }
   }

   @Override
   public List<Resource> getChildren(Pattern pattern, boolean recursive) {
      List<Resource> rval = new ArrayList<>();
      File[] files = file.listFiles();
      if (files != null) {
         for (File f : files) {
            if (pattern.matcher(f.getName()).find()) {
               FileResource r = new FileResource(f);
               rval.add(r);
               if (recursive) {
                  rval.addAll(r.getChildren(pattern, true));
               }
            }
         }
      }
      return rval;
   }

   @Override
   public Resource getParent() {
      File p = file.getAbsoluteFile().getParentFile();
      if (p == null) {
         return EmptyResource.INSTANCE;
      }
      return new FileResource(p);
   }

   @Override
   public InputStream createInputStream() throws IOException {
      return new FileInputStream(file);
   }

   @Override
   public OutputStream createOutputStream() throws IOException {
      return new FileOutputStream(file);
   }

   @Override
   public boolean delete(boolean recursively) {
      return delete(file);
   }

   private boolean delete(File file) {
      if (file.isDirectory()) {

         if (file.list().length == 0) {
            //Empty dir can delete
            file.delete();
         } else {
            for (File child : file.listFiles()) {
               delete(child);
            }
            file.delete();
         }

      } else {
         file.delete();
      }

      return !file.exists();
   }


   @Override
   public Resource deleteOnExit() {
      if (file.isDirectory()) {
         Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
               delete(true);
            }
         });
      } else {
         file.deleteOnExit();
      }
      return this;
   }

   @Override
   public boolean mkdirs() {
      return file.mkdirs();
   }

   @Override
   public boolean mkdir() {
      return file.mkdir();
   }

}// END OF FileResource
