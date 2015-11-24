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
import com.davidbracewell.io.QuietIO;
import com.davidbracewell.stream.MStream;
import com.davidbracewell.string.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

/**
 * <p> A <code>Resource</code> wrapper for a URL. </p>
 *
 * @author David B. Bracewell
 */
@EqualsAndHashCode
public class URLResource extends BaseResource {

  private static final long serialVersionUID = -5874490341557934277L;
  private URL url;
  private String userAgent = "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0";
  private int connectionTimeOut = 30000;

  /**
   * Instantiates a new uRL resource.
   *
   * @param url the url
   * @throws java.net.MalformedURLException the malformed url exception
   */
  public URLResource(@NonNull String url) throws MalformedURLException {
    this.url = new URL(url);
  }

  public URLResource(@NonNull URL url) {
    this.url = url;
  }

  @Override
  public String descriptor() {
    return url.toString();
  }

  @Override
  public Optional<File> asFile() {
    if (url.getProtocol().equalsIgnoreCase("file")) {
      return Optional.of(new File(url.getFile()));
    }
    return super.asFile();
  }

  @Override
  public String path() {
    return url.getPath();
  }

  @Override
  public String baseName() {
    return url.getFile();
  }

  @Override
  public Optional<URL> asURL() {
    return Optional.of(url);
  }

  @Override
  public Resource getChild(String relativePath) {
    try {
      return new URLResource(new URL(url, relativePath));
    } catch (MalformedURLException e) {
      return EmptyResource.INSTANCE;
    }
  }

  @Override
  public Resource getParent() {
    try {
      return new URLResource(FileUtils.parent(url.toString()));
    } catch (MalformedURLException e) {
      return EmptyResource.INSTANCE;
    }
  }

  @Override
  public Resource append(String content) throws IOException {
    throw new UnsupportedOperationException("URLResource does not support appending");
  }

  @Override
  public Resource append(byte[] byteArray) throws IOException {
    throw new UnsupportedOperationException("URLResource does not support appending");
  }

  @Override
  public boolean exists() {
    boolean exists = true;
    InputStream is = null;
    try {
      URLConnection connection = createConnection();
      connection.setConnectTimeout(5 * 1000);
      is = connection.getInputStream();
    } catch (Exception e) {
      exists = false;
    } finally {
      QuietIO.closeQuietly(is);
    }
    return exists;
  }

  @Override
  public MStream<String> lines() throws IOException {
    return null;
  }

  @Override
  public InputStream createInputStream() throws IOException {
    return createConnection().getInputStream();
  }

  private URLConnection createConnection() throws IOException {
    URLConnection connection = url.openConnection();
    if (!StringUtils.isNullOrBlank(userAgent)) {
      connection.setRequestProperty("User-Agent", userAgent);
    }
    connection.setConnectTimeout(connectionTimeOut);
    return connection;
  }

  @Override
  public OutputStream createOutputStream() throws IOException {
    return createConnection().getOutputStream();
  }

  /**
   * @return The user agent string to pass along to the web server
   */
  public String getUserAgent() {
    return userAgent;
  }

  /**
   * Sets The user agent string to pass along to the web server
   *
   * @param userAgent the user agent
   */
  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  /**
   * @return the amount of time to wait in connecting to the host before giving up
   */
  public int getConnectionTimeOut() {
    return connectionTimeOut;
  }

  /**
   * Sets the amount of time to wait in connecting to the host before giving up
   *
   * @param connectionTimeOut The connectionTimeOut
   */
  public void setConnectionTimeOut(int connectionTimeOut) {
    this.connectionTimeOut = connectionTimeOut;
  }


}//END OF URLResource
