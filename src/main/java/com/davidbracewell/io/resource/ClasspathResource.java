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
import com.google.common.collect.Lists;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * <p> A <code>Resource</code> implementation for resources that exist on the classpath. Resources are loaded either
 * using the supplied class loader or the system class loader if none is supplied. </p>
 *
 * @author David B. Bracewell
 */
public class ClasspathResource extends Resource {

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
  public ClasspathResource(String resource, ClassLoader classLoader) {
    Preconditions.checkNotNull(resource);
    Preconditions.checkNotNull(classLoader);
    this.resource = FileUtils.toUnix(resource);
    this.classLoader = classLoader;
  }

  @Override
  public File asFile() {
    try {
      URL url = asURL();
      if (url.getProtocol() != null && url.getProtocol().equals("file")) {
        return new File(url.toURI());
      }
      return null;
    } catch (Exception e) {
      log.fine(e);
      return null;
    }
  }

  @Override
  public String resourceDescriptor() {
    return ClasspathResourceProvider.PROTOCOL + ":" + resource;
  }

  @Override
  public URL asURL() throws MalformedURLException {
    return classLoader.getResource(resource);
  }

  @Override
  public boolean isDirectory() {
    if (asFile() != null) {
      return asFile().isDirectory();
    }
    return (FileUtils.extension(resource) != null) || canRead();
  }

  @Override
  public String path() {
    return FileUtils.path(resource);
  }

  @Override
  public String baseName() {
    if (asFile() != null) {
      return asFile().getName();
    }
    String path = path();
    int index = Math.max(0, Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\')) + 1);
    return path.substring(index);
  }

  @Override
  public boolean canWrite() {
    return asFile() != null && asFile().canWrite();
  }

  @Override
  public boolean canRead() {
    return (asFile() != null && asFile().canRead()) || exists();
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
  protected List<Resource> getChildren(Pattern filePattern, boolean recursive) {
    List<Resource> rval = Lists.newArrayList();

    if (!isDirectory()) {
      return rval;
    }

    if( asFile() != null ){
      return Resources.fromFile(asFile()).getChildren(filePattern,recursive);
    }

    String matchText = (asFile() == null) ? path() : asFile().getAbsolutePath();
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
    if( parent == null ){
      return this;
    }
    return new ClasspathResource(parent);
  }

  @Override
  public InputStream createInputStream() throws IOException {
    return classLoader.getResourceAsStream(resource);
  }

  @Override
  public OutputStream createOutputStream() throws IOException {
    return new FileOutputStream(this.asFile());
  }

  @Override
  public boolean mkdirs() {
    File file = asFile();
    return file != null && file.mkdirs();
  }

  @Override
  public boolean mkdir() {
    File file = asFile();
    return file != null && file.mkdir();

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

  @Override
  public String toString() {
    return "classpath:" + resource;
  }

}// END OF ClasspathResource
