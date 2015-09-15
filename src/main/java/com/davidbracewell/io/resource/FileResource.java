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

import com.davidbracewell.io.resource.spi.FileResourceProvider;
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
 * <p> A <code>Resource</code> implementation that wraps a <code>File</code>. </p>
 *
 * @author David B. Bracewell
 */
public class FileResource extends Resource {

  private static final long serialVersionUID = 430887560147494514L;
  private final File file;

  /**
   * <p> Constructs a Resource backed by a File </p>
   *
   * @param file The file to be wrapped.
   */
  public FileResource(File file) {
    Preconditions.checkNotNull(file);
    this.file = file.getAbsoluteFile();
  }

  /**
   * <p> Constructs a Resource backed by a File </p>
   *
   * @param path The path of the file
   */
  public FileResource(String path) {
    this(new File(Preconditions.checkNotNull(path)));
  }

  @Override
  public void append(byte[] byteArray) throws IOException {
    try (FileOutputStream outputStream = new FileOutputStream(file, true)) {
      outputStream.write(byteArray);
    }
  }

  @Override
  public File asFile() {
    return file.getAbsoluteFile();
  }

  @Override
  public URL asURL() throws MalformedURLException {
    return file.toURI().toURL();
  }


  @Override
  public String resourceDescriptor() {
    return FileResourceProvider.PROTOCOL + ":" + file.getAbsolutePath();
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
    return file.canWrite();
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
    relativePath = relativePath.trim();
    return new FileResource(new File(file, relativePath));
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
  protected List<Resource> getChildren(Pattern pattern, boolean recursive) {
    List<Resource> rval = Lists.newArrayList();
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
    return new FileResource(asFile().getAbsoluteFile().getParent());
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
  public void deleteOnExit() {
    file.deleteOnExit();
  }

  @Override
  public boolean mkdirs() {
    return file.mkdirs();
  }

  @Override
  public boolean mkdir() {
    return file.mkdir();
  }

  @Override
  public int hashCode() {
    return file.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    return obj instanceof FileResource && Objects.equals(file, ((FileResource) obj).file);
  }


}// END OF FileResource
