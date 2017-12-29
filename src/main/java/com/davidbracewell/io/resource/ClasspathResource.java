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

package com.davidbracewell.io.resource;

import com.davidbracewell.io.FileUtils;
import com.davidbracewell.io.JarUtils;
import com.davidbracewell.io.Resources;
import com.davidbracewell.io.resource.spi.ClasspathResourceProvider;
import com.davidbracewell.logging.Logger;
import com.davidbracewell.string.StringUtils;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * <p> A <code>Resource</code> implementation for resources that exist on the classpath. Resources are loaded either
 * using the supplied class loader or the system class loader if none is supplied. </p>
 *
 * @author David B. Bracewell
 */
public class ClasspathResource extends BaseResource {

   private static final Logger log = Logger.getLogger(ClasspathResource.class);
   private static final long serialVersionUID = -1977592698953910323L;
   private final String resource;
   private final ClassLoader classLoader;


   /**
    * Constructs a ClasspathResource resource with a given charset, compression and encoding setting
    *
    * @param resource The resource
    */
   public ClasspathResource(String resource) {
      this(resource, ClasspathResource.class.getClassLoader());
   }

   /**
    * <p> Creates a classpath resource that uses the given class loader to load the resource. </p>
    *
    * @param resource    The path to the resource.
    * @param classLoader The class loader to use.
    */
   public ClasspathResource(@NonNull String resource, @NonNull ClassLoader classLoader) {
      this.resource = FileUtils.toUnix(resource);
      this.classLoader = classLoader;
   }

   @Override
   public Resource append(byte[] byteArray) throws IOException {
      Preconditions.checkState(canWrite(), "Unable to write to this resource");
      new FileResource(asFile().get()).append(byteArray);
      return this;
   }

   @Override
   public Optional<File> asFile() {
      return asURL()
                .filter(u -> u.getProtocol() != null && u.getProtocol().equalsIgnoreCase("file"))
                .map(u -> {
                   try {
                      return new File(u.toURI());
                   } catch (Exception e) {
                      return null;
                   }
                })
                .filter(Objects::nonNull);
   }

   @Override
   public String descriptor() {
      return ClasspathResourceProvider.PROTOCOL + ":" + resource;
   }

   @Override
   public Optional<URI> asURI() {
      return asURL().map(url -> {
         try {
            return url.toURI();
         } catch (URISyntaxException e) {
            return null;
         }
      });
   }

   @Override
   public Optional<URL> asURL() {
      return Optional.ofNullable(classLoader.getResource(resource));
   }

   @Override
   public boolean isDirectory() {
      if (asFile().isPresent()) {
         return asFile().get().isDirectory();
      }
      return !canRead();
   }

   @Override
   public String path() {
      return FileUtils.path(resource);
   }

   @Override
   public String baseName() {
      if (asFile().isPresent()) {
         return asFile().get().getName();
      }
      String path = path();
      int index = Math.max(0, Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\')) + 1);
      return path.substring(index);
   }

   @Override
   public boolean canWrite() {
      return asFile().map(File::canWrite).orElse(false);
   }

   @Override
   public boolean canRead() {
      if (asFile().isPresent()) {
         return !asFile().get().isDirectory() && asFile().get().canRead();
      }
      try (InputStream is = inputStream()) {
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   @Override
   public Resource getChild(String relativePath) {
      if (relativePath == null) {
         relativePath = StringUtils.EMPTY;
      }
      relativePath = relativePath.replaceFirst("^[\\\\/]+", "");
      if (resource.endsWith("/")) {
         return new ClasspathResource(resource + relativePath, classLoader);
      } else {
         return new ClasspathResource(resource + "/" + relativePath, classLoader);
      }
   }

   @Override
   public boolean exists() {
      return classLoader.getResource(resource) != null;
   }

   @Override
   public List<Resource> getChildren(Pattern filePattern, boolean recursive) {
      List<Resource> rval = new ArrayList<>();

      if (!isDirectory()) {
         return rval;
      }

      if (asFile().isPresent()) {
         return Resources.fromFile(asFile().get()).getChildren(filePattern, recursive);
      }

      String matchText = path();
      matchText = matchText.endsWith("/") ? matchText : matchText + "/";
      String path = path() + "/";

      for (Resource resource : JarUtils.getClasspathResources()) {
         String rName = resource.baseName();
         String rPath = resource.path() + "/";
         String rParent = resource.getParent().path() + "/";
         if (rPath.startsWith(matchText) && (recursive || rParent.equals(path)) && filePattern.matcher(rName).find()) {
            rval.add(resource);
         }
      }
      return rval;
   }

   @Override
   public Resource getParent() {
      String parent = FileUtils.parent(resource);
      if (parent == null) {
         return EmptyResource.INSTANCE;
      }
      return new ClasspathResource(parent);
   }


   @Override
   public InputStream inputStream() throws IOException {
      InputStream rawis = createInputStream();
      Preconditions.checkState(rawis != null, "This resource cannot be read from.");
      PushbackInputStream is = new PushbackInputStream(rawis, 2);
      if (FileUtils.isCompressed(is)) {
         setIsCompressed(true);
         return new GZIPInputStream(is);
      }
      return is;
   }

   @Override
   public InputStream createInputStream() throws IOException {
      return classLoader.getResourceAsStream(resource);
   }

   @Override
   public OutputStream createOutputStream() throws IOException {
      Preconditions.checkState(canWrite(), "Unable to write to this resource");
      return new FileOutputStream(this.asFile().get());
   }

   @Override
   public boolean mkdirs() {
      return asFile().map(File::mkdirs).orElse(false);
   }

   @Override
   public boolean mkdir() {
      return asFile().map(File::mkdir).orElse(false);
   }

   @Override
   public int hashCode() {
      return Objects.hash(classLoader, resource);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ClasspathResource other = (ClasspathResource) obj;
      return Objects.equals(classLoader, other.classLoader) &&
                Objects.equals(resource, other.resource);
   }

}// END OF ClasspathResource
